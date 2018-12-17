package md.ti181m.snailmail.inbox;

import android.view.ViewGroup;

abstract class BaseHolder extends md.ti181m.snailmail.adapter.BaseHolder {
    BaseHolder(ViewGroup parent, int layout) {
        super(parent, layout);
    }

    abstract void bind(BaseItem baseItem);
}
