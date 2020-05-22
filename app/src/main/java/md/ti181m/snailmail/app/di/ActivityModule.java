package md.ti181m.snailmail.app.di;

import android.app.Activity;
import android.content.Context;

import toothpick.config.Module;

class ActivityModule extends Module {

    ActivityModule(Activity activity) {
        bind(Context.class).toInstance(activity);
        bind(Activity.class).toInstance(activity);
    }
}
