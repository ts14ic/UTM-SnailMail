package md.ti181m.snailmail.di;

import android.app.Application;

import md.ti181m.snailmail.SnailMailApplication;
import md.ti181m.snailmail.network.SnailMailApi;
import md.ti181m.snailmail.utils.Prefs;
import toothpick.config.Module;

public class ApplicationModule extends Module {

    public ApplicationModule(SnailMailApplication application) {
        bind(Application.class).toInstance(application);

        bind(SnailMailApi.class).toInstance(new SnailMailApi(application));

        bind(Prefs.class).toInstance(Prefs.get(application));
    }
}
