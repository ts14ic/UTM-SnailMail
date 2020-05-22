package md.ti181m.snailmail.app;

import android.content.Context;

import androidx.fragment.app.Fragment;

import md.ti181m.snailmail.app.di.Dependencies;

/**
 * Serves as the base class for all fragments by providing only what is required by all fragments.
 * <p>
 * Auto injects all dependencies on attachment and releases them when detached.
 */
public abstract class BaseFragment extends Fragment {

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Dependencies.inject(this);
    }

    @Override
    public void onDetach() {
        Dependencies.release(this);
        super.onDetach();
    }
}
