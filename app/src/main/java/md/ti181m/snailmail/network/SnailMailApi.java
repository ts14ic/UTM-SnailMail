package md.ti181m.snailmail.network;

import android.content.Context;

import com.android.volley.Response;

import java.util.List;

import md.ti181m.snailmail.network.model.MailJson;

public class SnailMailApi extends BaseApi {

    public SnailMailApi(Context context) {
        super(context);
    }

    public void getInbox(
            Object tag,
            String mailboxId,
            Response.Listener<List<MailJson>> onSuccessListener,
            Response.ErrorListener onErrorListener
    ) {
        enqueueGetListRequest(
                tag,
                "history?id=" + mailboxId,
                MailJson.class,
                onSuccessListener,
                onErrorListener
        );
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
}
