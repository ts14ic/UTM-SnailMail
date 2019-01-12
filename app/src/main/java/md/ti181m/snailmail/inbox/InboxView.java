package md.ti181m.snailmail.inbox;

import java.util.List;

interface InboxView {

    void close();

    void displayLoadingText();

    void displayErrorText();

    void displayEmptyText();

    void displayMailItems(List<Mail> mail);

    void setProgressVisible(boolean isVisible);

    void setMailboxNumber(String mailboxNumber);

    void updateUnseenCount(long count);

    void updateDeletedPercentage(double deletedPercentage);

    void askMarkAsSeenConfirmation();

    void askMailDeletionConfirmation(Mail mail);
}
