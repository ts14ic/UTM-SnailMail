package md.ti181m.snailmail.app.di;

import android.app.Application;
import android.content.Context;

import md.ti181m.snailmail.network.SnailMailApi;
import md.ti181m.snailmail.utils.Prefs;
import toothpick.config.Module;

public class ApplicationModule extends Module {

    public ApplicationModule(Application application) {
        bind(Application.class).toInstance(application);
        bind(Context.class).toInstance(application);

        bind(SnailMailApi.class).toInstance(new SnailMailApi(application));

        bind(Prefs.class).toInstance(Prefs.get(application));
    }
}
