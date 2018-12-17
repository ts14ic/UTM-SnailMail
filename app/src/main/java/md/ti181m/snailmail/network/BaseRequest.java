package md.ti181m.snailmail.network;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import timber.log.Timber;

abstract class BaseRequest<T> extends Request<T> {
    private final Response.Listener<T> onSuccessListener;

    BaseRequest(Object tag,
                int method,
                String url,
                Response.Listener<T> onSuccessListener,
                Response.ErrorListener listener) {
        super(method, url, listener);
        this.onSuccessListener = onSuccessListener;
        setTag(tag);
    }

    @Override
    protected void deliverResponse(T response) {
        Timber.w("Server response for %s: %s", getRequestDescription(), response);
        onSuccessListener.onResponse(response);
    }

    private String getRequestDescription() {
        return (isCanceled() ? "[X] " : "[ ] ")
                + getMethodName()
                + " "
                + getUrl();
    }

    private String getMethodName() {
        int method = getMethod();
        switch (method) {
            case Method.GET: {
                return "GET";
            }

            case Method.POST: {
                return "POST";
            }

            default: {
                return "[" + method + "]";
            }
        }
    }

    @Override
    public void deliverError(VolleyError error) {
        super.deliverError(error);
        Timber.w(error, "Server error, %s", getRequestDescription());
    }
}
