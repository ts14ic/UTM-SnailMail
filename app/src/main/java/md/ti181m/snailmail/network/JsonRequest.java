package md.ti181m.snailmail.network;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

class JsonRequest<T> extends BaseRequest<T> {
    private final Class<T> responseClass;

    JsonRequest(
            Object tag,
            int method,
            String url,
            Class<T> responseClass,
            Response.Listener<T> onSuccessListener,
            Response.ErrorListener onErrorListener
    ) {
        super(tag, method, url, onSuccessListener, onErrorListener);
        this.responseClass = responseClass;
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            ObjectMapper mapper = new ObjectMapper()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            String content = new String(response.data);
            T result = mapper.readValue(content, responseClass);
            return Response.success(result, /*cache*/null);
        } catch (IOException e) {
            return Response.error(new VolleyError(e));
        }
    }
}
