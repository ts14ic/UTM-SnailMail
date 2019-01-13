package md.ti181m.snailmail.network;

import android.content.Context;

import com.android.volley.Response;

import java.util.List;

import md.ti181m.snailmail.network.model.MailJson;

public class SnailMailApiImpl extends BaseApi implements SnailMailApi {

    public SnailMailApiImpl(Context context) {
        super(context);
    }

    @Override
    public void getMail(
            Object tag,
            String mailboxId,
            Response.Listener<List<MailJson>> onSuccessListener,
            Response.ErrorListener onErrorListener
    ) {
        enqueueGetRequest(
                tag,
                "history?id=" + mailboxId,
                listParser(MailJson.class),
                onSuccessListener,
                onErrorListener
        );
    }

    @Override
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

    @Override
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
