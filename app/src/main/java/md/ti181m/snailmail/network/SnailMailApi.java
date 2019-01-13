package md.ti181m.snailmail.network;

import com.android.volley.Response;

import java.util.List;

import md.ti181m.snailmail.network.model.MailJson;

public interface SnailMailApi {

    void close();

    void getMail(
            Object tag,
            String mailboxId,
            Response.Listener<List<MailJson>> onSuccessListener,
            Response.ErrorListener onErrorListener
    );

    void markAllAsSeen(
            Object tag,
            String mailboxId,
            Response.Listener<Void> onSuccessListener,
            Response.ErrorListener onErrorListener
    );

    void deleteMail(
            Object tag,
            long mailId,
            Response.Listener<Void> successListener,
            Response.ErrorListener errorListener
    );
}
