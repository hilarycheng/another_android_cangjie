package com.diycircuits.cangjie;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.inputmethodservice.Keyboard;

public class SoftKeyboard extends Keyboard {

    public SoftKeyboard(Context context, int xmlLayoutResId) {
	super(context, xmlLayoutResId);
    }

    @Override
    protected Key createKeyFromXml(Resources res, Row parent, int x, int y,
				   XmlResourceParser parser) {

	return new SoftKeyboardKey(res, parent, x, y, parser);
    }

}
