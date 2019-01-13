package md.ti181m.snailmail.utils;

import android.content.Context;

public class PrefsImpl extends BasePrefs implements Prefs {

    private static final String KEY_MAILBOX_ID = prefixKey("KEY_MAILBOX_ID");

    PrefsImpl(Context context) {
        super(context);
    }

    @Override
    public String getMailboxId() {
        return preferences.getString(KEY_MAILBOX_ID, "");
    }

    @Override
    public void setMailboxId(String mailboxId) {
        commit(editor -> editor.putString(KEY_MAILBOX_ID, mailboxId));
    }

    @Override
    public void removeMailboxId() {
        remove(KEY_MAILBOX_ID);
    }

}
