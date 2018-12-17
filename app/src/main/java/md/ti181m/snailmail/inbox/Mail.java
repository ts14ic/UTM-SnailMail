package md.ti181m.snailmail.inbox;

import java.util.Objects;

public class Mail {
    private long id;
    private long whenReceived;
    private Long whenSeen;

    public Mail(long id,
                long whenReceived,
                Long whenSeen) {
        this.id = id;
        this.whenReceived = whenReceived;
        this.whenSeen = whenSeen;
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

    long getWhenSeen() {
        return Objects.requireNonNull(whenSeen, "check `hasBeenSeen` before using this");
    }
}
