package md.ti181m.snailmail.network;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import java.util.List;

import md.ti181m.snailmail.BuildConfig;

class BaseApi {
    private final RequestQueue queue;
    private final String baseUrl;

    BaseApi(Context context) {
        this.queue = Volley.newRequestQueue(context);
        this.baseUrl = BuildConfig.API_BASE_URL;
    }

    public void cancelRequests(Object tag) {
        queue.cancelAll(tag);
    }

    public void cancelAllRequests() {
        RequestQueue.RequestFilter filter = request -> /*every request*/true;
        queue.cancelAll(filter);
    }

    <T> void enqueueGetRequest(
            Object tag,
            String url,
            Class<T[]> responseClass,
            Response.Listener<T[]> onSuccessListener,
            Response.ErrorListener onErrorListener
    ) {
        queue.add(new JsonRequest<>(
                tag,
                Request.Method.GET,
                baseUrl + url,
                responseClass,
                onSuccessListener,
                onErrorListener
        ));
    }

    <T> void enqueueGetListRequest(
            Object tag,
            String url,
            Class<T> responseClass,
            Response.Listener<List<T>> onSuccessListener,
            Response.ErrorListener onErrorListener
    ) {
        queue.add(new JsonListRequest<>(
                tag,
                Request.Method.GET,
                baseUrl + url,
                responseClass,
                onSuccessListener,
                onErrorListener
        ));
    }

    void enqueueBodylessPostRequest(
            Object tag,
            String url,
            Response.Listener<Void> onSuccessListener,
            Response.ErrorListener onErrorListener
    ) {
        queue.add(new BodylessPostRequest(
                tag,
                baseUrl + url,
                onSuccessListener,
                onErrorListener
        ));
    }
}
