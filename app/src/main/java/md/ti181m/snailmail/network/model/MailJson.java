package md.ti181m.snailmail.network.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

import androidx.annotation.NonNull;
import md.ti181m.snailmail.inbox.Mail;

public class MailJson {
    @JsonProperty("id")
    private long id;
    @JsonProperty("whenAdded")
    private Date whenAdded;
    @JsonProperty("whenSeen")
    private Date whenSeen;

    public Mail toMail() {
        return new Mail(
                id,
                whenAdded.getTime(),
                whenSeen != null
                        ? whenSeen.getTime()
                        : null
        );
    }

    @NonNull
    @Override
    public String toString() {
        return "MailJson{" +
                "id=" + id +
                ", whenAdded=" + whenAdded +
                ", whenSeen=" + whenSeen +
                '}';
    }
}
