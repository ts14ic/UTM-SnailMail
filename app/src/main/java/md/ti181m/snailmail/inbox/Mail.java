package md.ti181m.snailmail.inbox;

import java.util.Objects;

public class Mail {
    private long id;
    private long whenReceived;
    private Long whenSeen;
    private Long whenDeleted;

    public Mail(long id,
                long whenReceived,
                Long whenSeen,
                Long whenDeleted) {
        this.id = id;
        this.whenReceived = whenReceived;
        this.whenSeen = whenSeen;
        this.whenDeleted = whenDeleted;
    }

    long getId() {
        return id;
    }

    long getWhenReceived() {
        return whenReceived;
    }

    boolean hasBeenSeen() {
        return whenSeen != null;
    }

    boolean hasBeenDeleted() {
        return whenDeleted != null;
    }

    long getWhenSeen() {
        return Objects.requireNonNull(whenSeen, "check `hasBeenSeen` before using this");
    }
}
