package md.ti181m.snailmail.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.annimon.stream.function.Consumer;

import androidx.annotation.NonNull;
import md.ti181m.snailmail.BuildConfig;

class BasePrefs {
    final SharedPreferences preferences;

    BasePrefs(Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    static String prefixKey(String key) {
        return BuildConfig.APPLICATION_ID + "." + key;
    }

    public static Prefs get(@NonNull Context context) {
        return new Prefs(context);
    }

    void remove(String key) {
        commit(editor -> editor.remove(key));
    }

    @SuppressLint("ApplySharedPref")
    void commit(Consumer<SharedPreferences.Editor> editorConsumer) {
        SharedPreferences.Editor editor = preferences.edit();
        editorConsumer.accept(editor);
        editor.commit();
    }
}
