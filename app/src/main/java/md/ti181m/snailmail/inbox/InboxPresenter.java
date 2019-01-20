package md.ti181m.snailmail.inbox;

import com.annimon.stream.Stream;

import java.util.List;

import javax.inject.Inject;

import md.ti181m.snailmail.network.SnailMailApi;
import md.ti181m.snailmail.network.model.MailJson;
import md.ti181m.snailmail.utils.Prefs;
import timber.log.Timber;

class InboxPresenter {

    private final SnailMailApi api;
    private final Prefs prefs;

    private InboxView view;

    @Inject
    InboxPresenter(SnailMailApi api, Prefs prefs) {
        this.api = api;
        this.prefs = prefs;
    }

    void setView(InboxView view) {
        this.view = view;
    }

    void onCreate() {
        if (view == null) {
            return;
        }
        view.setMailboxNumber(prefs.getMailboxId());
        view.updateUnseenCount(0);
    }

    void onStart() {
        downloadMailForDisplay();
    }

    private void downloadMailForDisplay() {
        if (view == null) {
            return;
        }
        view.setProgressVisible(true);
        view.displayLoadingText();

        api.getMail(
                this,
                prefs.getMailboxId(),
                response -> {
                    if (view == null) {
                        return;
                    }
                    view.setProgressVisible(false);

                    List<Mail> mail = mapJsonToMail(response);
                    if (mail.isEmpty()) {
                        view.displayEmptyText();
                    } else {
                        displayMail(mail);
                    }
                },
                error -> {
                    Timber.w("Failed to download mail");
                    if (view == null) {
                        return;
                    }
                    view.setProgressVisible(false);
                    view.displayErrorText();
                }
        );
    }

    private List<Mail> mapJsonToMail(List<MailJson> response) {
        return Stream.of(response)
                .map(MailJson::toMail)
                .sorted((left, right) -> {
                    // Descending date order
                    return Long.compare(
                            right.getWhenReceived(),
                            left.getWhenReceived()
                    );
                })
                .toList();
    }

    private void displayMail(List<Mail> allMail) {
        if (view == null) {
            return;
        }

        List<Mail> visibleMail = filterVisibleMail(allMail);

        view.displayMailItems(visibleMail);

        view.updateUnseenCount(countUnseen(allMail));

        view.updateDeletedPercentage(getDeletedPercentage(allMail));
    }

    private List<Mail> filterVisibleMail(List<Mail> mails) {
        return Stream.of(mails)
                .filterNot(Mail::hasBeenDeleted)
                .toList();
    }

    // todo: test
    private long countUnseen(List<Mail> mails) {
        return Stream.of(mails)
                .filterNot(Mail::hasBeenSeen)
                .filterNot(Mail::hasBeenDeleted)
                .count();
    }

    // todo: test
    private double getDeletedPercentage(List<Mail> mails) {
        int allCount = mails.size();
        if (allCount == 0) {
            return 0;
        }

        long deletedCount = Stream.of(mails)
                .filter(Mail::hasBeenDeleted)
                .count();
        return 100.0 * deletedCount / allCount;
    }

    void close() {
        view = null;
        api.cancelRequests(this);
    }

    void onExitClicked() {
        prefs.removeMailboxId();

        if (view != null) {
            view.close();
        }
    }

    void onRefresh() {
        if (view != null) {
            downloadMailForDisplay();
        }
    }

    void onMarkAllSeenClicked() {
        if (view != null) {
            view.askMarkAsSeenConfirmation();
        }
    }

    void onMarkAllSeenConfirmed() {
        if (view == null) {
            return;
        }
        view.setProgressVisible(true);

        api.markAllAsSeen(
                this,
                prefs.getMailboxId(),
                ok -> {
                    Timber.d("Successfully marked all as seen");
                    if (view == null) {
                        return;
                    }
                    view.setProgressVisible(false);

                    downloadMailForDisplay();
                },
                error -> {
                    Timber.w("Failed to reset history");
                    if (view != null) {
                        view.setProgressVisible(false);
                    }
                }
        );
    }

    void onMailDeleteClicked(Mail mail) {
        if (view != null) {
            view.askMailDeletionConfirmation(mail);
        }
    }

    void onMailDeleteConfirmed(Mail mail) {
        deleteMail(mail);
    }

    private void deleteMail(Mail mail) {
        if (view == null) {
            return;
        }
        view.setProgressVisible(true);

        api.deleteMail(
                this,
                mail.getId(),
                ok -> {
                    Timber.d("Successfully marked all as seen");
                    if (view == null) {
                        return;
                    }
                    view.setProgressVisible(false);
                    downloadMailForDisplay();
                },
                error -> {
                    Timber.w("Failed to reset history");
                    if (view == null) {
                        return;
                    }
                    view.setProgressVisible(false);
                }
        );
    }

}
