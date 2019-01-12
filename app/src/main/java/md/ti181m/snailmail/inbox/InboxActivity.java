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
import md.ti181m.snailmail.splash.SplashActivity;
import md.ti181m.snailmail.utils.ToolbarActivity;

public class InboxActivity
        extends AppCompatActivity
        implements ToolbarActivity, MailItem.Listener, InboxView {

    private static final int LAYOUT = R.layout.activity_inbox;

    @BindView(R.id.toolbar_title_text_view) TextView toolbarTitleTextView;
    @BindView(R.id.swipe_refresh_layout) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.mail_recycler_view) RecyclerView mailRecyclerView;
    @BindView(R.id.content_description_text_view) TextView contentDescriptionTextView;
    @BindView(R.id.drawer_layout) DrawerLayout drawerLayout;
    @BindView(R.id.drawer_view) NavigationView drawerView;
    private TextView unseenCounterTextView;
    private TextView deletedPercentageTextView;
    private TextView inboxIdTextView;

    private MailAdapter mailAdapter;
    private List<Dialog> dialogs = new ArrayList<>();
    private InboxPresenter presenter;

    public static Intent getStartIntent(Context context) {
        return new Intent(context, InboxActivity.class);
    }

    // region lifecycle
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);
        ButterKnife.bind(this);

        presenter = new InboxPresenter(this);
        presenter.setView(this);

        mailAdapter = new MailAdapter();
        mailRecyclerView.setAdapter(mailAdapter);

        swipeRefreshLayout.setOnRefreshListener(presenter::onRefresh);

        drawerView.setNavigationItemSelectedListener(menuItem -> {
            if (menuItem.getItemId() == R.id.exit) {
                presenter.onExitClicked();
                return true;
            }
            return false;
        });

        View drawerHeaderView = drawerView.getHeaderView(0);
        inboxIdTextView = drawerHeaderView.findViewById(R.id.inbox_id_text_view);
        unseenCounterTextView = drawerHeaderView.findViewById(R.id.inbox_unseen_text_view);
        deletedPercentageTextView = drawerHeaderView.findViewById(R.id.inbox_deleted_percentage_text_view);

        presenter.onCreate();
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.onStart();
    }

    @Override
    public void close() {
        startActivity(SplashActivity.getStartIntent(this));
        finish();
    }

    @Override
    protected void onDestroy() {
        presenter.close();
        dismissAllDialogs();
        super.onDestroy();
    }

    private void dismissAllDialogs() {
        for (Dialog dialog : dialogs) {
            dialog.dismiss();
        }
    }
    // endregion

    // region click listeners
    @OnClick(R.id.toolbar_exit_button)
    void onExitClicked() {
        presenter.onExitClicked();
    }

    @OnClick(R.id.toolbar_drawer_button)
    void onDrawerButtonClicked() {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    @OnClick(R.id.mark_all_as_seen_button)
    void onMarkAllAsSeenClicked() {
        presenter.onMarkAllSeenClicked();
    }

    @Override
    public void onDeleteMailClicked(Mail mail) {
        presenter.onMailDeleteClicked(mail);
    }
    // endregion

    // region content
    @Override
    public void displayMailItems(List<Mail> inbox) {
        List<MailItem> items = Stream.of(inbox)
                .map(mail -> new MailItem(mail, this))
                .toList();
        mailAdapter.setItems(items);
        mailAdapter.notifyDataSetChanged();

        contentDescriptionTextView.setVisibility(View.GONE);
    }

    @Override
    public void displayLoadingText() {
        if (mailAdapter.getItemCount() == 0) {
            contentDescriptionTextView.setText(R.string.inbox__loading);
            setToolbarTitle(R.string.inbox__title);
        }
    }

    @Override
    public void displayErrorText() {
        if (mailAdapter.getItemCount() == 0) {
            contentDescriptionTextView.setText(R.string.inbox__failed_to_load);
            setToolbarTitle(R.string.inbox__title);
        }
    }

    @Override
    public void displayEmptyText() {
        contentDescriptionTextView.setVisibility(View.VISIBLE);
        contentDescriptionTextView.setText(R.string.inbox__no_content);

        setToolbarTitle(R.string.inbox__title);
    }
    // endregion

    // region info
    @Override
    public void setProgressVisible(boolean isVisible) {
        swipeRefreshLayout.setRefreshing(isVisible);
    }

    @Override
    public void setToolbarTitle(String title) {
        toolbarTitleTextView.setText(title);
    }

    @Override
    public void setToolbarTitle(@StringRes int titleRes, Object... fmtArgs) {
        setToolbarTitle(getString(titleRes, fmtArgs));
    }

    @Override
    public void setMailboxNumber(String mailboxNumber) {
        String inboxIdText = getString(R.string.inbox__inbox_id_s, presenter.getMailboxId());
        inboxIdTextView.setText(inboxIdText);
    }

    @Override
    public void updateUnseenCount(long count) {
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

    @Override
    public void updateDeletedPercentage(double percentage) {
        if (deletedPercentageTextView != null) {
            String deletedPercentageText = getString(R.string.inbox__deleted_percentage_d_pct, percentage);
            deletedPercentageTextView.setText(deletedPercentageText);
        }
    }
    // endregion

    // region dialogs
    @Override
    public void askMarkAsSeenConfirmation() {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setMessage(R.string.inbox__sure_dismiss)
                .setPositiveButton(R.string.all__yes, (dialog, which) -> {
                    presenter.onMarkAllSeenConfirmed();
                })
                .setNegativeButton(R.string.all__no, (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
        dialogs.add(alertDialog);
    }

    @Override
    public void askMailDeletionConfirmation(Mail mail) {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setMessage(R.string.inbox__delete_confirmation)
                .setPositiveButton(R.string.all__yes, (dialog, which) -> {
                    presenter.onMailDeleteConfirmed(mail);
                })
                .setNegativeButton(R.string.all__no, (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
        dialogs.add(alertDialog);
    }
    // endregion
}
