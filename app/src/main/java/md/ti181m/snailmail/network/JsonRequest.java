package md.ti181m.snailmail.network;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.io.IOException;

class JsonRequest<T> extends BaseRequest<T> {
    private final Parser<T> parser;

    JsonRequest(
            Object tag,
            int method,
            String url,
            Parser<T> parser,
            Response.Listener<T> onSuccessListener,
            Response.ErrorListener onErrorListener
    ) {
        super(tag, method, url, onSuccessListener, onErrorListener);
        this.parser = parser;
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            T result = parser.parse(new String(response.data));
            return Response.success(result, /*cache*/null);
        } catch (IOException e) {
            return Response.error(new VolleyError(e));
        }
    }

    interface Parser<T> {
        T parse(String json) throws IOException;
    }
}
