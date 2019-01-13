package md.ti181m.snailmail.utils;

import android.content.Context;

public class Prefs extends BasePrefs {

    private static final String KEY_MAILBOX_ID = prefixKey("KEY_MAILBOX_ID");

    Prefs(Context context) {
        super(context);
    }

    public String getMailboxId() {
        return preferences.getString(KEY_MAILBOX_ID, "");
    }

    public void setMailboxId(String mailboxId) {
        commit(editor -> editor.putString(KEY_MAILBOX_ID, mailboxId));
    }

    public void removeMailboxId() {
        remove(KEY_MAILBOX_ID);
    }
}
