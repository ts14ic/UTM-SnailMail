package md.ti181m.snailmail.utils;

import androidx.annotation.StringRes;

public interface ToolbarActivity {
    void setToolbarTitle(String title);

    void setToolbarTitle(@StringRes int titleRes, Object... fmtArgs);
}
