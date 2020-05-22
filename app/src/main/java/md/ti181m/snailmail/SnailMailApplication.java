package md.ti181m.snailmail;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.multidex.MultiDexApplication;

import md.ti181m.snailmail.app.di.Dependencies;
import timber.log.Timber;

public class SnailMailApplication extends MultiDexApplication {
    @Override
    public void onCreate() {
        Dependencies.inject(this);
        super.onCreate();

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
