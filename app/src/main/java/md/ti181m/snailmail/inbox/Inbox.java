package md.ti181m.snailmail.inbox;

import com.annimon.stream.Stream;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import javax.inject.Inject;

import md.ti181m.snailmail.network.SnailMailApi;
import md.ti181m.snailmail.network.model.MailJson;
import md.ti181m.snailmail.utils.Prefs;

import static java.util.Objects.requireNonNull;

public class Inbox {

    private final SnailMailApi api;
    private final Prefs prefs;
    private final List<Observer> observers;

    private Progress progress;
    private Content content;
    private List<Mail> allMail;

    @Inject
    public Inbox(SnailMailApi api, Prefs prefs) {
        this.observers = new ArrayList<>();
        this.api = api;
        this.prefs = prefs;

        this.allMail = new ArrayList<>();
        this.progress = Progress.GONE;
        this.content = Content.EMPTY;
    }

    void registerObserver(Observer observer) {
        observers.add(requireNonNull(observer));
    }

    void unregisterObserver(Observer observer) {
        requireNonNull(observer);

        observers.remove(observer);
        api.cancelRequests(observer);
    }

    private void notifyObservers() {
        ListIterator<Observer> iterator = observers.listIterator();
        while (iterator.hasNext()) {
            // prevent recursion by temporarily unregistering before update
            Observer observer = iterator.next();
            iterator.remove();

            observer.update();

            iterator.add(observer);
        }
    }

    String getMailBoxNumber() {
        return prefs.getMailboxId();
    }

    int getUnseenCount() {
        return countUnseen(allMail);
    }

    double getDeletedPercentage() {
        return getDeletedPercentage(allMail);
    }

    List<Mail> getVisibleMail() {
        return filterVisibleMail(allMail);
    }

    Progress getProgress() {
        return progress;
    }

    Content getContent() {
        return content;
    }

    void downloadMailForDisplay() {
        progress = Progress.VISIBLE;
        content = Content.LOADING;
        notifyObservers();

        api.getMail(
                this,
                prefs.getMailboxId(),
                response -> {
                    progress = Progress.GONE;
                    content = Content.VISIBLE;
                    allMail = mapJsonToMail(response);
                    notifyObservers();
                },
                error -> {
                    progress = Progress.GONE;
                    content = Content.ERROR;
                    notifyObservers();
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

    private List<Mail> filterVisibleMail(List<Mail> mails) {
        return Stream.of(mails)
                .filterNot(Mail::hasBeenDeleted)
                .toList();
    }

    private int countUnseen(List<Mail> mails) {
        return (int) Stream.of(mails)
                .filterNot(Mail::hasBeenSeen)
                .filterNot(Mail::hasBeenDeleted)
                .count();
    }

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

    void markAllAsSeen() {
        progress = Progress.VISIBLE;
        notifyObservers();

        api.markAllAsSeen(
                this,
                prefs.getMailboxId(),
                ok -> {
                    progress = Progress.GONE;
                    notifyObservers();

                    downloadMailForDisplay();
                },
                error -> {
                    progress = Progress.GONE;
                    notifyObservers();
                }
        );
    }

    void deleteMail(Mail mail) {
        progress = Progress.VISIBLE;
        notifyObservers();

        api.deleteMail(
                this,
                mail.getId(),
                ok -> {
                    progress = Progress.GONE;
                    notifyObservers();

                    downloadMailForDisplay();
                },
                error -> {
                    progress = Progress.GONE;
                    notifyObservers();
                }
        );
    }

    public enum Progress {
        GONE,
        VISIBLE
    }

    public enum Content {
        EMPTY,
        LOADING,
        ERROR,
        VISIBLE
    }

    public interface Observer {

        void update();
    }
}
