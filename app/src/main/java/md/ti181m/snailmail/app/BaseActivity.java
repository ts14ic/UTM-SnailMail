package md.ti181m.snailmail.app;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import md.ti181m.snailmail.app.di.Dependencies;

/**
 * Serves as the base class for activities by providing only what is required by all activities.
 * <p>
 * Auto injects all dependencies and releases them when dies.
 */
public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Dependencies.inject(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        Dependencies.release(this);
        super.onDestroy();
    }
}
