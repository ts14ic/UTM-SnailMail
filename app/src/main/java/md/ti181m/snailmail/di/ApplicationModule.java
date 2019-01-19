package md.ti181m.snailmail.di;

import android.app.Application;

import md.ti181m.snailmail.SnailMailApplication;
import toothpick.config.Module;

public class ApplicationModule extends Module {

    public ApplicationModule(SnailMailApplication application) {
        bind(Application.class).toInstance(application);
    }
}
