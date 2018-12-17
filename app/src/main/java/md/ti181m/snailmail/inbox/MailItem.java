package md.ti181m.snailmail.inbox;

class MailItem extends BaseItem {

    private final Mail mail;

    MailItem(Mail mail) {
        this.mail = mail;
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
}
