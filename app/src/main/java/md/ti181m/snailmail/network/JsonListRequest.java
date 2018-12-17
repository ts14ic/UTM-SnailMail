package md.ti181m.snailmail.network;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

import java.io.IOException;
import java.util.List;

class JsonListRequest<T> extends BaseRequest<List<T>> {
    private final Class<T> responseClass;

    JsonListRequest(
            Object tag,
            int method,
            String url,
            Class<T> responseClass,
            Response.Listener<List<T>> onSuccessListener,
            Response.ErrorListener onErrorListener
    ) {
        super(tag, method, url, onSuccessListener, onErrorListener);
        this.responseClass = responseClass;
    }

    @Override
    protected Response<List<T>> parseNetworkResponse(NetworkResponse response) {
        try {
            ObjectMapper mapper = new ObjectMapper()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            String content = new String(response.data);
            CollectionType typeToken = mapper.getTypeFactory()
                    .constructCollectionType(List.class, responseClass);
            List<T> results = mapper.readValue(content, typeToken);
            return Response.success(results, /*cache*/null);
        } catch (IOException e) {
            return Response.error(new VolleyError(e));
        }
    }
}
