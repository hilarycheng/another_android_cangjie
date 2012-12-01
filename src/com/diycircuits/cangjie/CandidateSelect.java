package com.diycircuits.cangjie;

import android.content.Context;
import android.util.AttributeSet;
import android.view.*;
import android.widget.LinearLayout;
import android.graphics.*;
import android.util.Log;

public class CandidateSelect extends View {

    private int width = 0;
    private int height = 0;
    private char match[] = null;
    private int total = 0;
    private Paint paint = null;
    private float[] textWidth = new float[24];
    private int offset = 10;

    private CandidateListener listener = null;
    
    public static interface CandidateListener {
	void characterSelected(char c);
    }
    
    public CandidateSelect(Context context, AttributeSet attrs) {
	super(context, attrs);

	paint = new Paint();
	paint.setColor(Color.BLACK);
	paint.setAntiAlias(true);
	paint.setTextSize(64);
	paint.setStrokeWidth(0);


	Log.i("Cangjie", "Candidate Select Create");
    }

    public void setCandidateListener(CandidateListener listen) {
	listener = listen;
    }
    
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
	super.onSizeChanged(w, h, oldw, oldh);

	width  = w;
	height = h;
	
	Log.i("Cangjie", "Candidate Select onSizeChanged " + w + " " + h + " " + oldw + " " + oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
	Log.i("Cangjie", "Candidate Select on Draw ");
	super.onDraw(canvas);
	if (canvas == null) {
	    return;
	}
	Log.i("Cangjie", "Candidate Select on Draw " + paint);

	paint.setColor(Color.DKGRAY);
	canvas.drawRect(0, 0, width, height - 10, paint);
	paint.setColor(Color.WHITE);
	
	if (match != null) {
	    int _width = total > textWidth.length ? textWidth.length : total;
	    int measured = paint.getTextWidths(match, 0, _width, textWidth);
	    Log.i("Cangjie", "Candidate Select Text Width " + measured + " " + textWidth[0] + " " + textWidth[1]);

	    int start = offset, index = 0;
	    while (start < width && index < total) {
		canvas.drawText(match, index, 1, start, 56, paint);
		Log.i("Cangjie", "Candidate Select Text Width " + start + " " + index + " " + textWidth[index] + " " + match[index]);
		start = start + (int) textWidth[index] + 10;
		index++;
	    }
	}
    }

    @Override
    public boolean onTouchEvent(MotionEvent me) {
	int action = me.getAction();
	int x = (int) me.getX();
	int y = (int) me.getY();
	int select = x - offset;
	int left = 0;
	char c = 0;

	for (int count = 0; count < textWidth.length - 1; count++) {
	    if (count >= total) continue;
	    if (select > left && select < left + textWidth[count]) {
		c = match[count];
		break;
	    }
	    left = left + (int) textWidth[count];
	}

	switch (action) {
	case MotionEvent.ACTION_DOWN:
	case MotionEvent.ACTION_MOVE:
	    Log.i("Cangjie", "Cadidate Select OnTouch Event Action Down/Move " + x + " " + y);
	    break;
	case MotionEvent.ACTION_UP:
	    Log.i("Cangjie", "Cadidate Select OnTouch Event Action Up " + x + " " + y);
	    if (listener != null && c != 0) listener.characterSelected(c);
	    break;
	}

	return true;
    }

    public void updateMatch(char[] _match, int _total) {
	match = _match;
	total = _total;
	offset = 10;
	Log.i("Cangjie", "Update Match " + _total);
	invalidate();
    }

}
