package md.ti181m.snailmail.network;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

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
            JsonRequest.Parser<T> parser,
            Response.Listener<T> onSuccessListener,
            Response.ErrorListener onErrorListener
    ) {
        queue.add(new JsonRequest<>(
                tag,
                Request.Method.GET,
                baseUrl + url,
                parser,
                onSuccessListener,
                onErrorListener
        ));
    }

    <T> JsonRequest.Parser<T> objectParser(Class<T> objectClass) {
        return json -> {
            ObjectMapper mapper = new ObjectMapper()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            return mapper.readValue(json, objectClass);
        };
    }

    <T> JsonRequest.Parser<List<T>> listParser(Class<T> objectClass) {
        return json -> {
            ObjectMapper mapper = new ObjectMapper()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            CollectionType typeToken = mapper.getTypeFactory()
                    .constructCollectionType(List.class, objectClass);
            return mapper.readValue(json, typeToken);
        };
    }

    void enqueueBodylessPostRequest(
            Object tag,
            String url,
            Response.Listener<Void> onSuccessListener,
            Response.ErrorListener onErrorListener
    ) {
        queue.add(new JsonRequest<>(
                tag,
                Request.Method.POST,
                baseUrl + url,
                json -> null,
                onSuccessListener,
                onErrorListener
        ));
    }
}
