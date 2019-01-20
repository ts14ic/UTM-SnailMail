package md.ti181m.snailmail.di;

import androidx.appcompat.app.AppCompatActivity;
import md.ti181m.snailmail.SnailMailApplication;
import toothpick.Scope;
import toothpick.Toothpick;
import toothpick.config.Module;

public final class Dependencies {

    private Dependencies() {
    }

    public static void inject(SnailMailApplication application, Module... modules) {
        Scope scope = Toothpick.openScope(application);
        scope.installModules(modules);
        Toothpick.inject(application, scope);
    }

    public static void inject(AppCompatActivity activity, Module... modules) {
        Scope scope = Toothpick.openScopes(activity.getApplication(), activity);
        scope.installModules(modules);
        Toothpick.inject(activity, scope);
    }
}
