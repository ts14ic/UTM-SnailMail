package md.ti181m.snailmail;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.multidex.MultiDexApplication;
import md.ti181m.snailmail.di.ApplicationModule;
import timber.log.Timber;
import toothpick.Scope;
import toothpick.Toothpick;

public class SnailMailApplication extends MultiDexApplication {
    @Override
    public void onCreate() {
        super.onCreate();

        Scope scope = Toothpick.openScope(this);
        scope.installModules(new ApplicationModule(this));

        configureLogging();
        enableVectorSupportOnPreLollipop();
    }

    private void configureLogging() {
        Timber.plant(new Timber.DebugTree());
    }

    private void enableVectorSupportOnPreLollipop() {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }
}
