package md.ti181m.snailmail.app.di;

import android.app.Activity;
import android.app.Application;

import androidx.fragment.app.Fragment;
import toothpick.Scope;
import toothpick.Toothpick;

/**
 * Utility methods to inject dependencies into main android components
 * <p>
 * Most of the methods come in {@link #inject} and {@link #release} pairs.
 * That is, when an object starts it's lifecycle, call {@code inject}, and when it's lifecycle ends,
 * call {@code release}.
 * <p>
 * This is both to minimize boilerplate and decouple the project from a concrete DI framework.
 * <p>
 * If you want some special treatment for your activity or fragment, just provide an inject overload.
 */
public class Dependencies {

    /**
     * Injects dependencies into application scope.
     * <p>
     * Call this in {@link Application#onCreate}.
     *
     * @param application application to be injected with dependencies.
     */
    public static void inject(Application application) {
        Scope scope = Toothpick.openScope(application);
        scope.installModules(new ApplicationModule(application));
        Toothpick.inject(application, scope);
    }

    /**
     * Injects dependencies into activity scope.
     * <p>
     * Call this in {@link Activity#onCreate} before the call to super.
     * If you call this after, fragment attachment will crash the app, because
     * they are attached in the call to super, and dependencies are not ready yet.
     * <p>
     * Call {@link #release(Activity)} inside {@link Activity#onDestroy}
     *
     * @param activity activity to be injected with dependencies
     * @see #release(Activity)
     */
    public static void inject(Activity activity) {
        Scope scope = Toothpick.openScopes(activity.getApplication(), activity);
        scope.installModules(new ActivityModule(activity));
        Toothpick.inject(activity, scope);
    }

    /**
     * Releases scoped dependencies to make them eligible for garbage collection.
     * <p>
     * Call this in {@link Activity#onDestroy}
     *
     * @param activity activity that had dependencies injected
     * @see #inject(Activity)
     */
    public static void release(Activity activity) {
        Toothpick.closeScope(activity);
    }

    /**
     * Injects dependencies into fragment scope.
     * <p>
     * Call this in {@link Fragment#onAttach}.
     * <p>
     * Call {@link #release(Fragment)} inside {@link Fragment#onDetach()}.
     *
     * @param fragment fragment to be injected with dependencies
     * @see #release(Fragment)
     */
    public static void inject(Fragment fragment) {
        Scope scope = Toothpick.openScopes(
                fragment.getActivity().getApplication(),
                fragment.getActivity(),
                fragment
        );
        scope.installModules(new FragmentModule(fragment));
        Toothpick.inject(fragment, scope);
    }

    /**
     * Releases scope dependencies to make them eligible for garbage collection.
     * <p>
     * Call this in {@link Fragment#onDetach()}
     *
     * @param fragment fragment that had dependencies injected
     * @see #inject(Fragment)
     */
    public static void release(Fragment fragment) {
        Toothpick.closeScope(fragment);
    }
}
