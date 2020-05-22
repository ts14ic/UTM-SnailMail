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

import javax.inject.Inject;

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
import md.ti181m.snailmail.app.BaseActivity;
import md.ti181m.snailmail.app.di.Dependencies;
import md.ti181m.snailmail.splash.SplashActivity;
import md.ti181m.snailmail.utils.Prefs;
import md.ti181m.snailmail.utils.ToolbarActivity;

public class InboxActivity
        extends BaseActivity
        implements ToolbarActivity,
        MailItem.Listener,
        Inbox.Observer {

    private static final int LAYOUT = R.layout.activity_inbox;

    @BindView(R.id.toolbar_title_text_view) TextView toolbarTitleTextView;
    @BindView(R.id.swipe_refresh_layout) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.mail_recycler_view) RecyclerView mailRecyclerView;
    @BindView(R.id.content_description_text_view) TextView contentDescriptionTextView;
    @BindView(R.id.drawer_layout) DrawerLayout drawerLayout;
    @BindView(R.id.drawer_view) NavigationView drawerView;

    @Inject Prefs prefs;
    @Inject Inbox inbox;

    private TextView unseenCounterTextView;
    private TextView deletedPercentageTextView;
    private TextView inboxIdTextView;

    private MailAdapter mailAdapter;
    private List<Dialog> dialogs = new ArrayList<>();

    public static Intent getStartIntent(Context context) {
        return new Intent(context, InboxActivity.class);
    }

    // region lifecycle
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);
        ButterKnife.bind(this);

        mailAdapter = new MailAdapter();
        mailRecyclerView.setAdapter(mailAdapter);

        View drawerHeaderView = drawerView.getHeaderView(0);
        inboxIdTextView = drawerHeaderView.findViewById(R.id.inbox_id_text_view);
        unseenCounterTextView = drawerHeaderView.findViewById(R.id.inbox_unseen_text_view);
        deletedPercentageTextView = drawerHeaderView.findViewById(R.id.inbox_deleted_percentage_text_view);
    }

    @Override
    protected void onStart() {
        super.onStart();

        swipeRefreshLayout.setOnRefreshListener(inbox::downloadMailForDisplay);

        drawerView.setNavigationItemSelectedListener(menuItem -> {
            if (menuItem.getItemId() == R.id.exit) {
                onExitClicked();
                return true;
            }
            return false;
        });


        inbox.registerObserver(this);
        inbox.downloadMailForDisplay();
    }

    @Override
    protected void onStop() {
        swipeRefreshLayout.setOnRefreshListener(null);
        drawerView.setNavigationItemSelectedListener(null);
        inbox.unregisterObserver(this);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        dismissAllDialogs();
        super.onDestroy();
    }

    private void dismissAllDialogs() {
        for (Dialog dialog : dialogs) {
            dialog.dismiss();
        }
    }
    // endregion lifecycle

    // region click listeners
    @OnClick(R.id.toolbar_exit_button)
    void onExitClicked() {
        prefs.removeMailboxId();
        close();
    }

    private void close() {
        startActivity(SplashActivity.getStartIntent(this));
        finish();
    }

    @OnClick(R.id.toolbar_drawer_button)
    void onDrawerButtonClicked() {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    @OnClick(R.id.mark_all_as_seen_button)
    void onMarkAllAsSeenClicked() {
        askMarkAsSeenConfirmation();
    }

    @Override
    public void onDeleteMailClicked(Mail mail) {
        askMailDeletionConfirmation(mail);
    }
    // endregion click listeners

    // region content
    @Override
    public void update() {
        setMailboxNumber(inbox.getMailBoxNumber());

        setUnseenCount(inbox.getUnseenCount());

        setDeletedPercentage(inbox.getDeletedPercentage());

        setProgressVisible(inbox.getProgress());

        setContentVisible(inbox.getContent());
    }

    private void setContentVisible(Inbox.Content content) {
        switch (content) {
            case EMPTY:
                displayEmptyText();
                break;
            case LOADING:
                displayLoadingText();
                break;
            case ERROR:
                displayErrorText();
                break;
            case VISIBLE:
                displayMailItems(inbox.getVisibleMail());
                break;
        }
    }

    private void displayMailItems(List<Mail> inbox) {
        List<MailItem> items = Stream.of(inbox)
                .map(mail -> new MailItem(mail, this))
                .toList();
        mailAdapter.setItems(items);
        mailAdapter.notifyDataSetChanged();

        contentDescriptionTextView.setVisibility(View.GONE);
    }

    private void displayLoadingText() {
        if (mailAdapter.getItemCount() == 0) {
            contentDescriptionTextView.setText(R.string.inbox__loading);
            setToolbarTitle(R.string.inbox__title);
        }
    }

    private void displayErrorText() {
        if (mailAdapter.getItemCount() == 0) {
            contentDescriptionTextView.setText(R.string.inbox__failed_to_load);
            setToolbarTitle(R.string.inbox__title);
        }
    }

    private void displayEmptyText() {
        contentDescriptionTextView.setVisibility(View.VISIBLE);
        contentDescriptionTextView.setText(R.string.inbox__no_content);

        setToolbarTitle(R.string.inbox__title);
    }
    // endregion content

    // region info
    private void setProgressVisible(Inbox.Progress progress) {
        swipeRefreshLayout.setRefreshing(progress == Inbox.Progress.VISIBLE);
    }

    @Override
    public void setToolbarTitle(String title) {
        toolbarTitleTextView.setText(title);
    }

    @Override
    public void setToolbarTitle(@StringRes int titleRes, Object... fmtArgs) {
        setToolbarTitle(getString(titleRes, fmtArgs));
    }

    private void setMailboxNumber(String mailboxNumber) {
        String inboxIdText = getString(R.string.inbox__inbox_id_s, mailboxNumber);
        inboxIdTextView.setText(inboxIdText);
    }

    private void setUnseenCount(long count) {
        if (count == 0) {
            setToolbarTitle(R.string.inbox__title);
        } else {
            setToolbarTitle(R.string.inbox__title_with_counter, count);
        }

        if (unseenCounterTextView != null) {
            String unseenText = getString(R.string.inbox__unseen_s, String.valueOf(count));
            unseenCounterTextView.setText(unseenText);
        }
    }

    private void setDeletedPercentage(double percentage) {
        if (deletedPercentageTextView != null) {
            String deletedPercentageText = getString(R.string.inbox__deleted_percentage_d_pct, percentage);
            deletedPercentageTextView.setText(deletedPercentageText);
        }
    }
    // endregion info

    // region dialogs
    private void askMarkAsSeenConfirmation() {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setMessage(R.string.inbox__sure_dismiss)
                .setPositiveButton(R.string.all__yes, (dialog, which) -> {
                    inbox.markAllAsSeen();
                })
                .setNegativeButton(R.string.all__no, (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
        dialogs.add(alertDialog);
    }

    private void askMailDeletionConfirmation(Mail mail) {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setMessage(R.string.inbox__delete_confirmation)
                .setPositiveButton(R.string.all__yes, (dialog, which) -> {
                    inbox.deleteMail(mail);
                })
                .setNegativeButton(R.string.all__no, (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
        dialogs.add(alertDialog);
    }
    // endregion dialogs
}
