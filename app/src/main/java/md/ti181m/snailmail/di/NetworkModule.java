package md.ti181m.snailmail.di;

import android.content.Context;

import md.ti181m.snailmail.network.SnailMailApi;
import toothpick.config.Module;

public class NetworkModule extends Module {

    public NetworkModule(Context context) {
        bind(SnailMailApi.class).toInstance(new SnailMailApi(context.getApplicationContext()));
    }
}
