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
    private Paint paint = null;
    private float[] textWidth = new float[20];
    
    public CandidateView(Context context, AttributeSet attrs) {
	super(context, attrs);

	paint = new Paint();
	paint.setColor(Color.BLACK);
	paint.setAntiAlias(true);
	paint.setTextSize(28);
	paint.setStrokeWidth(0);


	Log.i("Cangjie", "Candidate View Create");
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
	super.onSizeChanged(w, h, oldw, oldh);

	width  = w;
	height = h;
	
	Log.i("Cangjie", "Candidate View onSizeChanged " + w + " " + h + " " + oldw + " " + oldh);
    }

    // @Override
    // protected void onDraw(Canvas canvas) {
    // 	Log.i("Cangjie", "on Draw ");
    // 	if (canvas == null) {
    // 	    return;
    // 	}
    // 	super.onDraw(canvas);

    // 	// int _width = total > 20 ? 20 : total;
    // 	// int measured = paint.getTextWidths(match, 0, _width, textWidth);
    // 	// Log.i("Cangjie", "Text Width " + measured + " " + textWidth[0] + " " + textWidth[1]);
    // }

    public void updateMatch(char[] _match, int _total) {
	// match = _match;
	// total = _total;
	// Log.i("Cangjie", "Update Match " + _total);
	// invalidate();
    }

}
