package md.ti181m.snailmail.splash;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import md.ti181m.snailmail.R;
import md.ti181m.snailmail.inbox.InboxActivity;
import md.ti181m.snailmail.login.LoginActivity;

public class SplashActivity extends AppCompatActivity {

    private static final int LAYOUT = R.layout.activity_splash;

    public static Intent getStartIntent(Context context) {
        return new Intent(context, SplashActivity.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);

        startActivity(getNextActivityIntent());
        finish();
    }

    private Intent getNextActivityIntent() {
        if (PrefsImpl.get(this).getMailboxId().isEmpty()) {
            return LoginActivity.getStartIntent(this);
        } else {
            return InboxActivity.getStartIntent(this);
        }
    }
}
