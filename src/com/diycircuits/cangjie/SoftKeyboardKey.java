package com.diycircuits.cangjie;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.Keyboard.Key;

public class SoftKeyboardKey extends Keyboard.Key {

    // functional normal state (with properties)
    private final int[] KEY_STATE_FUNCTIONAL_NORMAL = {
	android.R.attr.state_single
    };

    // functional pressed state (with properties)
    private final int[] KEY_STATE_FUNCTIONAL_PRESSED = {
	android.R.attr.state_single,
	android.R.attr.state_pressed
    };

    public SoftKeyboardKey(Resources res, Keyboard.Row parent, int x, int y,
			   XmlResourceParser parser) {
	super(res, parent, x, y, parser);
    }

    @Override
    public int[] getCurrentDrawableState() {
	if (codes.length > 0 && codes[0] < 0) {
	    if (pressed) {
		return KEY_STATE_FUNCTIONAL_PRESSED;
	    } else {
		return KEY_STATE_FUNCTIONAL_NORMAL;
	    }
	}
	return super.getCurrentDrawableState();
    }

}
