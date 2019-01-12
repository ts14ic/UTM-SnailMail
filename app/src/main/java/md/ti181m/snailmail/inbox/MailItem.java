package md.ti181m.snailmail.inbox;

class MailItem extends BaseItem {

    private final Mail mail;
    private final Listener listener;

    MailItem(Mail mail, Listener listener) {
        this.mail = mail;
        this.listener = listener;
    }

    long getWhenReceived() {
        return mail.getWhenReceived();
    }

    boolean hasBeenSeen() {
        return mail.hasBeenSeen();
    }

    long getWhenSeen() {
        return mail.getWhenSeen();
    }

    void onDeleteMailClicked() {
        listener.onDeleteMailClicked(mail);
    }

    public interface Listener {

        void onDeleteMailClicked(Mail mail);
    }
}
