package md.ti181m.snailmail.network.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

import androidx.annotation.NonNull;
import md.ti181m.snailmail.inbox.Mail;

public class MailJson {
    @JsonProperty("id")
    public long id;
    @JsonProperty("whenAdded")
    public Date whenAdded;
    @JsonProperty("whenSeen")
    public Date whenSeen;
    @JsonProperty("whenDeleted")
    public Date whenDeleted;

    public Mail toMail() {
        return new Mail(
                id,
                whenAdded.getTime(),
                whenSeen != null
                        ? whenSeen.getTime()
                        : null,
                whenDeleted != null
                        ? whenDeleted.getTime()
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
                ", whenDeleted=" + whenDeleted +
                '}';
    }
}
