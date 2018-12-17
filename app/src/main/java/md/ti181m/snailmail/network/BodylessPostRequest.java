package md.ti181m.snailmail.network;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;

class BodylessPostRequest extends BaseRequest<Void> {
    BodylessPostRequest(
            Object tag,
            String url,
            Response.Listener<Void> onSuccessListener,
            Response.ErrorListener onErrorListener
    ) {
        super(tag, Method.POST, url, onSuccessListener, onErrorListener);
    }

    @Override
    protected Response<Void> parseNetworkResponse(NetworkResponse response) {
        return Response.success(/*body*/null, /*cache*/null);
    }
}
