package md.ti181m.snailmail.di;

import android.app.Application;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import toothpick.Scope;
import toothpick.Toothpick;
import toothpick.config.Module;

public final class Dependencies {

    private Dependencies() {
    }

    public static void inject(Application application, Module... modules) {
        Scope scope = Toothpick.openScope(application);
        scope.installModules(modules);
        Toothpick.inject(application, scope);
    }

    public static void inject(AppCompatActivity activity, Module... modules) {
        Scope scope = Toothpick.openScopes(activity.getApplication(), activity);
        scope.installModules(modules);
        Toothpick.inject(activity, scope);

        activity.getLifecycle().addObserver(new ScopeCloser(activity));
    }

    private static class ScopeCloser implements LifecycleObserver {

        private final Object name;

        private ScopeCloser(Object name) {
            this.name = name;
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        public void onDestroy() {
            Toothpick.closeScope(name);
        }
    }
}
