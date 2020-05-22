package md.ti181m.snailmail.app.di;


import androidx.fragment.app.Fragment;

import toothpick.config.Module;

class FragmentModule extends Module {

    FragmentModule(Fragment fragment) {
        bind(Fragment.class).toInstance(fragment);
    }
}
