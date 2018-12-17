package md.ti181m.snailmail.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.ButterKnife;

public class BaseHolder extends RecyclerView.ViewHolder {

    protected BaseHolder(ViewGroup parent, @LayoutRes int layout) {
        super(inflateView(parent, layout));
        ButterKnife.bind(this, itemView);
    }

    private static View inflateView(ViewGroup parent, @LayoutRes int layout) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return inflater.inflate(layout, parent, /*attach to parent*/false);
    }
}
