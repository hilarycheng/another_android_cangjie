package com.diycircuits.cangjie;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.graphics.*;
import android.util.Log;

public class CandidateView extends LinearLayout {

    private int width = 0;
    private int height = 0;
    private char match[] = null;
    private int total = 0;
    
    public CandidateView(Context context, AttributeSet attrs) {
	super(context, attrs);
    }

}
