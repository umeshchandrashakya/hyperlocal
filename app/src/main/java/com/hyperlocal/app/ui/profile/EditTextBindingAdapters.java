package com.hyperlocal.app.ui.profile;

import android.databinding.BindingAdapter;
import android.text.TextWatcher;
import android.widget.EditText;

/**
 * @author ${Umesh} on 01-05-2018.
 */

public class EditTextBindingAdapters {

    @BindingAdapter("textChangedListener")
    public static void bindTextWatcher(EditText editText, TextWatcher textWatcher) {
        editText.addTextChangedListener(textWatcher);
    }
}
