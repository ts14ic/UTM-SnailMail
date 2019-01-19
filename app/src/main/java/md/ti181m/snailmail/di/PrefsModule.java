package md.ti181m.snailmail.di;

import android.content.Context;

import md.ti181m.snailmail.utils.Prefs;
import toothpick.config.Module;

public class PrefsModule extends Module {

    public PrefsModule(Context context) {
        bind(Prefs.class).toInstance(Prefs.get(context.getApplicationContext()));
    }
}
