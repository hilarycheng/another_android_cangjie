package com.diycircuits.cangjie;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.*;
import android.graphics.*;
import android.util.Log;

public class CandidateView extends LinearLayout {

    private int width = 0;
    private int height = 0;
    private char match[] = null;
    private int total = 0;
    private ImageButton mLeftArrow;
    private ImageButton mRightArrow;
    private CandidateSelect mSelect;

    public CandidateView(Context context, AttributeSet attrs) {
	super(context, attrs);

    }

    @Override
    protected void onFinishInflate() {
	super.onFinishInflate();

	mSelect = (CandidateSelect) findViewById(R.id.match_view);
	
	mLeftArrow = (ImageButton) findViewById(R.id.arrow_left);
	if (mLeftArrow != null) {
	    mLeftArrow.setOnClickListener(new View.OnClickListener() {
		    public void onClick(View v) {
			mSelect.showPrevPage();
		    }
		});
	}
         
	mRightArrow = (ImageButton) findViewById(R.id.arrow_right);
	if (mRightArrow != null) {
	    mRightArrow.setOnClickListener(new View.OnClickListener() {
		    public void onClick(View v) {
			mSelect.showNextPage();
		    }
		});
	}

    }

}
