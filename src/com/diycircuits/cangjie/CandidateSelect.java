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
    private float[] textWidth = new float[21529];
    private int offset = 10;
    private int charOffset = 0;
    private int spacing = 10;
    private float charWidth = 0;
    private int topOffset = 0;
    private Context context = null;
    
    private final static int SPACING            = 4;
    private final static int STARTING_FONT_SIZE = 12;
    private final static int ENDING_FONT_SIZE   = 128;

    private CandidateListener listener = null;
    
    public static interface CandidateListener {
	void characterSelected(char c);
    }
    
    public CandidateSelect(Context context, AttributeSet attrs) {
	super(context, attrs);

	this.context = context;
	
	paint = new Paint();
	paint.setColor(Color.BLACK);
	paint.setAntiAlias(true);
	paint.setTextSize(50);
	paint.setStrokeWidth(0);
    }

    public void setCandidateListener(CandidateListener listen) {
	listener = listen;
    }
    
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
	super.onSizeChanged(w, h, oldw, oldh);

	width  = w;
	height = h;

	Rect rect = new Rect();
	for (int fontsize = STARTING_FONT_SIZE; fontsize < ENDING_FONT_SIZE; fontsize += 2) {
	    paint.setTextSize(fontsize);

	    Paint.FontMetrics metrics = paint.getFontMetrics();
	    int totalHeight = (int) (metrics.bottom - metrics.top);

	    if (totalHeight > height) {
		paint.setTextSize(fontsize - 7);
		paint.getTextBounds(context.getString(R.string.cangjie), 0, 1, rect);

		topOffset = (int) Math.abs(rect.top) + (int) (Math.abs(rect.bottom) + 9 );
		break;
	    }
	}

    }

    @Override
    protected void onDraw(Canvas canvas) {
	super.onDraw(canvas);
	if (canvas == null) {
	    return;
	}

	paint.setColor(Color.BLACK);
	canvas.drawRect(0, 0, width, height - 0, paint);
	paint.setColor(0xff33B5E5);
	
	if (match != null) {
	    int _width = total > textWidth.length ? textWidth.length : total;
	    int measured = paint.getTextWidths(match, 0, _width, textWidth);

	    int start = offset, index = charOffset;
	    while (start < width && index < total) {
		canvas.drawText(match, index, 1, start, topOffset, paint);
		start = start + (int) textWidth[index] + spacing;
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
	int left = offset;
	char c = 0;

	for (int count = charOffset; count < textWidth.length - 1; count++) {
	    if (count >= total) continue;
	    if (select > left && select < left + textWidth[count]) {
		c = match[count];
		Log.i("Cangjie", "Matched " + c);
		break;
	    }
	    left = left + (int) textWidth[count] + spacing;
	}

	switch (action) {
	case MotionEvent.ACTION_DOWN:
	case MotionEvent.ACTION_MOVE:
	    break;
	case MotionEvent.ACTION_UP:
	    if (listener != null && c != 0) listener.characterSelected(c);
	    break;
	}

	return true;
    }

    public void showNextPage() {
	if (match == null) return;
	if (total > 1 && charOffset < (total - 1)) {
	    charOffset++;
	    invalidate();
	}
    }
    
    public void showPrevPage() {
	if (match == null) return;
	if (charOffset > 0) {
	    charOffset--;
	    invalidate();
	}
    }
    
    public void updateMatch(char[] _match, int _total) {
	match = _match;
	total = _total;
        charOffset = 0;
	offset = 10;
	invalidate();
    }

}
