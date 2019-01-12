package md.ti181m.snailmail.inbox;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.annimon.stream.Stream;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import md.ti181m.snailmail.R;
import md.ti181m.snailmail.network.SnailMailApi;
import md.ti181m.snailmail.network.model.MailJson;
import md.ti181m.snailmail.splash.SplashActivity;
import md.ti181m.snailmail.utils.Prefs;
import md.ti181m.snailmail.utils.ToolbarActivity;
import timber.log.Timber;

public class InboxActivity extends AppCompatActivity implements ToolbarActivity, MailItem.Listener {

    private static final int LAYOUT = R.layout.activity_inbox;

    @BindView(R.id.toolbar_title_text_view) TextView toolbarTitleTextView;
    @BindView(R.id.swipe_refresh_layout) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.mail_recycler_view) RecyclerView mailRecyclerView;
    @BindView(R.id.content_description_text_view) TextView contentDescriptionTextView;
    @BindView(R.id.drawer_layout) DrawerLayout drawerLayout;
    @BindView(R.id.drawer_view) NavigationView drawerView;
    private TextView unseenCounterTextView;

    private SnailMailApi api;
    private MailAdapter mailAdapter;
    private List<Dialog> dialogs = new ArrayList<>();

    public static Intent getStartIntent(Context context) {
        return new Intent(context, InboxActivity.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);
        ButterKnife.bind(this);

        setToolbarTitle(R.string.inbox__title);

        api = new SnailMailApi(this);

        mailAdapter = new MailAdapter();
        mailRecyclerView.setAdapter(mailAdapter);

        swipeRefreshLayout.setOnRefreshListener(this::downloadInboxForDisplay);


        drawerView.setNavigationItemSelectedListener(menuItem -> {
            if (menuItem.getItemId() == R.id.exit) {
                onExitClicked();
                return true;
            }
            return false;
        });

        View drawerHeaderView = drawerView.getHeaderView(0);

        TextView inboxIdTextView = drawerHeaderView.findViewById(R.id.inbox_id_text_view);
        String inboxIdText = getString(R.string.inbox__inbox_id_s, Prefs.get(this).getMailboxId());
        inboxIdTextView.setText(inboxIdText);

        unseenCounterTextView = drawerHeaderView.findViewById(R.id.inbox_unseen_text_view);
        updateUnreadCount(0);
    }

    @Override
    public void setToolbarTitle(@StringRes int titleRes, Object... fmtArgs) {
        setToolbarTitle(getString(titleRes, fmtArgs));
    }

    @OnClick(R.id.toolbar_exit_button)
    void onExitClicked() {
        Prefs.get(this).removeMailboxId();

        startActivity(SplashActivity.getStartIntent(this));
        finish();
    }

    @Override
    public void setToolbarTitle(String title) {
        toolbarTitleTextView.setText(title);
    }

    @Override
    protected void onStart() {
        super.onStart();
        downloadInboxForDisplay();
    }

    private void downloadInboxForDisplay() {
        setProgressVisible(true);
        setLoadingPerspective();

        api.getInbox(
                this,
                Prefs.get(this).getMailboxId(),
                inbox -> {
                    setProgressVisible(false);

                    List<Mail> mails = Stream.of(inbox)
                            .map(MailJson::toMail)
                            .sorted((left, right) -> {
                                // Descending date order
                                return Long.compare(
                                        right.getWhenReceived(),
                                        left.getWhenReceived()
                                );
                            })
                            .toList();

                    setContentPerspective(mails);
                },
                error -> {
                    Timber.w("Failed to download inbox");
                    setProgressVisible(false);
                    setErrorPerspective();
                }
        );
    }

    private void setLoadingPerspective() {
        if (mailAdapter.getItemCount() == 0) {
            contentDescriptionTextView.setText(R.string.inbox__loading);
            setToolbarTitle(R.string.inbox__title);
        }
    }

    private void setErrorPerspective() {
        if (mailAdapter.getItemCount() == 0) {
            contentDescriptionTextView.setText(R.string.inbox__failed_to_load);
            setToolbarTitle(R.string.inbox__title);
        }
    }

    private void setProgressVisible(boolean isVisible) {
        if (isVisible) {
            swipeRefreshLayout.setRefreshing(true);
        } else {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void setContentPerspective(List<Mail> inbox) {
        if (inbox.isEmpty()) {
            setEmptyPerspective();
        } else {
            displayInbox(inbox);
        }
    }

    private void setEmptyPerspective() {
        contentDescriptionTextView.setVisibility(View.VISIBLE);
        contentDescriptionTextView.setText(R.string.inbox__no_content);

        setToolbarTitle(R.string.inbox__title);
    }

    private void displayInbox(List<Mail> mails) {
        List<MailItem> items = Stream.of(mails)
                .filterNot(Mail::hasBeenDeleted)
                .map(mail -> new MailItem(mail, this))
                .toList();

        mailAdapter.setItems(items);
        mailAdapter.notifyDataSetChanged();

        contentDescriptionTextView.setVisibility(View.GONE);

        long unseenCount = Stream.of(mails)
                .filterNot(Mail::hasBeenSeen)
                .filterNot(Mail::hasBeenDeleted)
                .count();
        updateUnreadCount(unseenCount);
    }

    private void updateUnreadCount(long unseenCount) {
        if (unseenCount == 0) {
            setToolbarTitle(R.string.inbox__title);
        } else {
            setToolbarTitle(R.string.inbox__title_with_counter, unseenCount);
        }

        if (unseenCounterTextView != null) {
            String unseenText = getString(R.string.inbox__unseen_s, String.valueOf(unseenCount));
            unseenCounterTextView.setText(unseenText);
        }
    }


    @OnClick(R.id.toolbar_drawer_button)
    void onDrawerButtonClicked() {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    @Override
    protected void onDestroy() {
        api.close();
        dismissAllDialogs();
        super.onDestroy();
    }

    private void dismissAllDialogs() {
        for (Dialog dialog : dialogs) {
            dialog.dismiss();
        }
    }

    @OnClick(R.id.mark_all_as_seen_button)
    void onMarkAllAsSeenClicked() {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setMessage(R.string.inbox__sure_dismiss)
                .setPositiveButton(R.string.all__yes, (dialog, which) -> {
                    markAllAsSeen();
                    dialog.dismiss();
                })
                .setNegativeButton(R.string.all__no, (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
        dialogs.add(alertDialog);
    }

    private void markAllAsSeen() {
        setProgressVisible(true);
        api.markAllAsSeen(
                this,
                Prefs.get(this).getMailboxId(),
                ok -> {
                    Timber.d("Successfully marked all as seen");
                    setProgressVisible(false);
                    downloadInboxForDisplay();
                },
                error -> {
                    Timber.w("Failed to reset history");
                    setProgressVisible(false);
                }
        );
    }

    @Override
    public void onDeleteMailClicked(Mail mail) {
        new AlertDialog.Builder(this)
                .setMessage(R.string.inbox__delete_confirmation)
                .setPositiveButton(R.string.all__yes, (dialog, which) -> {
                    deleteMail(mail);
                })
                .setNegativeButton(R.string.all__no, (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }

    private void deleteMail(Mail mail) {
        setProgressVisible(true);
        api.deleteMail(
                this,
                mail.getId(),
                ok -> {
                    Timber.d("Successfully marked all as seen");
                    setProgressVisible(false);
                    downloadInboxForDisplay();
                },
                error -> {
                    Timber.w("Failed to reset history");
                    setProgressVisible(false);
                }
        );
    }
}
