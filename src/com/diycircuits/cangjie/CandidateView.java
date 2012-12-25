package com.diycircuits.cangjie;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.*;
import android.graphics.*;
import android.view.LayoutInflater;
import android.util.Log;

public class CandidateView extends LinearLayout {

    private int width = 0;
    private int height = 0;
    private char match[] = null;
    private int total = 0;
    private ImageButton mLeftArrow = null;
    private ImageButton mRightArrow = null;
    private CandidateSelect mSelect = null;
    private PopupWindow mPopup = null;
    private Context mContext = null;
    private CandidateView mCandidate = this;
    private int mWidth = 0;
    private int mHeight = 0;
    private View mParent = null;

    public CandidateView(Context context, AttributeSet attrs) {
	super(context, attrs);
	mContext = context;
    }

    public void setParent(View view) {
	this.mParent = view;
    }
    
    public void setDimension(int w, int h) {
	mWidth = w;
	mHeight = h;
    }
    
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
	super.onSizeChanged(w, h, oldw, oldh);

	mWidth = w;
	mHeight = h;

	mSelect.setParentWidth(mWidth);
    }

    @Override
    protected void onFinishInflate() {
	super.onFinishInflate();

	mSelect = (CandidateSelect) findViewById(R.id.match_view);
         
	mRightArrow = (ImageButton) findViewById(R.id.arrow_right);
	if (mRightArrow != null) {
	    mRightArrow.setOnClickListener(new View.OnClickListener() {
		    public void onClick(View v) {
			synchronized(mSelect) {
			    mSelect.showCandidatePopup(mParent, mWidth, mHeight);
			}
		    }
		});
	}

    }

}
