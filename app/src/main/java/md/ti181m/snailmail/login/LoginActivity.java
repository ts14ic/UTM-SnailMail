package md.ti181m.snailmail.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import md.ti181m.snailmail.R;
import md.ti181m.snailmail.inbox.InboxActivity;
import md.ti181m.snailmail.utils.Prefs;

public class LoginActivity extends AppCompatActivity {

    private static final int LAYOUT = R.layout.activity_login;

    @BindView(R.id.id_text_input) TextInputLayout idTextInput;
    @BindView(R.id.id_edit_text) TextInputEditText idEditText;

    public static Intent getStartIntent(Context context) {
        return new Intent(context, LoginActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.register_button)
    public void onRegisterButtonClicked() {
        updateIdInputErrors();

        if (getIdValidation().isOk()) {
            String enteredId = idEditText.getText().toString();
            Prefs.get(this).setMailboxId(enteredId);

            startActivity(InboxActivity.getStartIntent(this));
            finish();
        }
    }

    private void updateIdInputErrors() {
        switch (getIdValidation()) {
            case EMPTY: {
                idTextInput.setError(getString(R.string.login__error__empty_id));
                break;
            }

            case INVALID: {
                idTextInput.setError(getString(R.string.login__error__invalid_id));
                break;
            }

            default: {
                idTextInput.setErrorEnabled(false);
                break;
            }
        }
    }

    private IdValidation getIdValidation() {
        String enteredId = idEditText.getText().toString();
        if (enteredId.isEmpty()) {
            return IdValidation.EMPTY;
        }

        try {
            Long.parseLong(enteredId);
        } catch (NumberFormatException e) {
            return IdValidation.INVALID;
        }

        return IdValidation.OK;
    }

    enum IdValidation {
        OK,
        EMPTY,
        INVALID;

        public boolean isOk() {
            return this == OK;
        }
    }
}
