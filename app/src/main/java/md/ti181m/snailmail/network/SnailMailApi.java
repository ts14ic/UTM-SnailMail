package md.ti181m.snailmail.network;

import android.content.Context;

import com.android.volley.Response;

import org.joda.time.DateTime;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import md.ti181m.snailmail.network.model.MailJson;

public class SnailMailApi extends BaseApi {

    public SnailMailApi(Context context) {
        super(context);
    }

    public void getMail(
            Object tag,
            String mailboxId,
            Response.Listener<List<MailJson>> onSuccessListener,
            Response.ErrorListener onErrorListener
    ) {
        MailJson unseen = new MailJson();
        unseen.id = 1;
        unseen.whenAdded = DateTime.now().minusHours(1).toDate();

        MailJson seen = new MailJson();
        seen.id = 1;
        seen.whenAdded = DateTime.now().minusHours(1).toDate();
        seen.whenSeen = new Date();

        MailJson deleted = new MailJson();
        deleted.id = 1;
        deleted.whenAdded = DateTime.now().minusHours(1).toDate();
        deleted.whenDeleted = new Date();

        onSuccessListener.onResponse(Arrays.asList(
                unseen,
                seen,
                deleted
        ));
    }

    public void markAllAsSeen(
            Object tag,
            String mailboxId,
            Response.Listener<Void> onSuccessListener,
            Response.ErrorListener onErrorListener
    ) {
        enqueueBodylessPostRequest(
                tag,
                "dismiss/" + mailboxId,
                onSuccessListener,
                onErrorListener
        );
    }

    public void deleteMail(
            Object tag,
            long mailId,
            Response.Listener<Void> successListener,
            Response.ErrorListener errorListener
    ) {
        enqueueBodylessPostRequest(
                tag,
                "delete/" + mailId,
                successListener,
                errorListener
        );
    }
}
