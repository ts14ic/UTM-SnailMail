package md.ti181m.snailmail.inbox;

import android.view.ViewGroup;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

class MailAdapter extends RecyclerView.Adapter<BaseHolder> {
    private List<? extends BaseItem> items = Collections.emptyList();

    void setItems(List<? extends BaseItem> items) {
        this.items = Objects.requireNonNull(items);
    }

    @NonNull
    @Override
    public BaseHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MailHolder(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
