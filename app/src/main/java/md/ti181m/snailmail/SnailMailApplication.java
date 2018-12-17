package md.ti181m.snailmail;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.multidex.MultiDexApplication;
import timber.log.Timber;

public class SnailMailApplication extends MultiDexApplication {
    @Override
    public void onCreate() {
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
