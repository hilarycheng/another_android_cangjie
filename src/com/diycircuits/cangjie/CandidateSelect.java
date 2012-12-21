package com.diycircuits.cangjie;

import android.content.Context;
import android.app.Activity;
import android.util.AttributeSet;
import android.view.*;
import android.widget.*;
import android.graphics.*;
import android.util.Log;
import android.os.Handler.Callback;
import android.os.Handler;
import android.os.Message;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;

public class CandidateSelect extends View implements Handler.Callback {

    public static final int CHARACTER = 0;
    
    private int width = 0;
    private int height = 0;
    private char match[] = null;
    private int total = 0;
    private Paint paint = null;
    private float[] textWidth = new float[21529];
    private int offset = 10;
    private int charOffset = 0;
    private int spacing = 17;
    private float charWidth = 0;
    private int topOffset = 0;
    private int mFontSize = 0;
    private Context context = null;
    private PopupWindow mPopup = null;
    private Handler mHandler = null;

    private final static int SPACING            = 4;
    private final static int STARTING_FONT_SIZE = 12;
    private final static int ENDING_FONT_SIZE   = 128;

    private CandidateListener listener = null;
    
    public static interface CandidateListener {
	void characterSelected(char c, int idx);
    }
    
    public CandidateSelect(Context context, AttributeSet attrs) {
	super(context, attrs);

	this.context = context;

	mFontSize = 50;
	paint = new Paint();
	paint.setColor(Color.BLACK);
	paint.setAntiAlias(true);
	paint.setTextSize(50);
	paint.setStrokeWidth(0);

	mHandler = new Handler(this);
    }

    public void setCandidateListener(CandidateListener listen) {
	listener = listen;
    }

    private class CandidateItem {
    }
    
    public class CandidateAdapter extends ArrayAdapter<CandidateItem> {

	private Context context    = null;
	private char[]  match      = null;
	private int     total      = 0;
	private int     layoutRes  = 0;
	private int     fontSize   = 0;
	private int     topOffset  = 0;
	private int     leftOffset = 0;
	private int     columnc    = 0;

	public CandidateAdapter(Context context, int layoutRes, CandidateItem[] row, char[] match, int columnc, int total, int fs, int to, int lo) {
	    super(context, layoutRes, row);
	    this.context    = context;
	    this.match      = match;
	    this.layoutRes  = layoutRes;
	    this.match      = match;
	    this.total      = total;
	    this.fontSize   = fs;
	    this.topOffset  = to;
	    this.leftOffset = lo;
	    this.columnc    = columnc;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	    View row = convertView;
	    CandidateHolder holder = null;

	    if (row == null) {
		LayoutInflater inflater = (LayoutInflater)
		    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		row = inflater.inflate(layoutRes, parent, false);

		holder = new CandidateHolder();
		holder.row = (CandidateRow) row.findViewById(R.id.candidateRow);
		row.setTag(holder);
	    } else {
		holder = (CandidateHolder) row.getTag();
	    }

	    holder.row.setHandler(mHandler);
	    holder.row.setFontSize(fontSize, topOffset, leftOffset);
	    holder.row.setMatch(match, position * columnc, total - (position * columnc) >= columnc ? columnc : (total - (position * columnc)), total);

	    return row;
	}

	class CandidateHolder {
	    CandidateRow row;
	}
	
    }

    public void closePopup() {
	if (mPopup == null) return;
	mPopup.dismiss();
	mPopup = null;
    }
    
    public boolean handleMessage(Message msg) {

	if (msg.what == CHARACTER) {
	    closePopup();
	    if (listener != null && msg.arg1 != 0) listener.characterSelected((char) msg.arg1, msg.arg2);
	}
	
	return true;
    }

    public void showCandidatePopup(View mParent, int w, int h) {
	if (total == 0) return;
	if (mPopup == null) {
	    mPopup = new PopupWindow(context);
	    LayoutInflater inflate = (LayoutInflater)
		context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    View view = inflate.inflate(R.layout.popup, null);

	    int measured = paint.getTextWidths(context.getString(R.string.cangjie), 0, 1, textWidth);
	    int columnc = (w / ((int) textWidth[0] + spacing));

	    int rowc = total / columnc;
	    if ((total % columnc) > 0) rowc++;
	    int leftOffset = (columnc * (int) textWidth[0]) +
		((columnc - 1) * spacing);

	    leftOffset = (w - leftOffset) / 2;

	    CandidateItem[] row = new CandidateItem[rowc];
	    CandidateAdapter adapter = new CandidateAdapter(context, R.layout.candidate, row, match, columnc, total, mFontSize, topOffset, leftOffset);
	    
	    Button mButton = (Button) view.findViewById(R.id.cancelButton);
	    mButton.setOnClickListener(new View.OnClickListener() {
		    public void onClick(View v) {
			closePopup();
		    }
		});

	    ScrollView sv = (ScrollView) view.findViewById(R.id.sv);
	    sv.setFillViewport(true);
	    ListView lv = (ListView) view.findViewById(R.id.candidateExpanded);
	    lv.setAdapter(adapter);

	    mPopup.setContentView(view);
	}

	mPopup.setWidth(w);
	mPopup.setHeight(300);
	mPopup.showAsDropDown(mParent, 0, -h);
    }

    public int fontSize() {
	return mFontSize;
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
		mFontSize = fontsize - 8;
		paint.setTextSize(mFontSize);
		paint.getTextBounds(context.getString(R.string.cangjie), 0, 1, rect);

		topOffset = rect.height() - rect.bottom;
		topOffset += (h - rect.height()) / 2;

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

	paint.setColor(0xff282828);
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
	int idx = -1;

	offset = 0;
	for (int count = charOffset; count < textWidth.length - 1; count++) {
	    if (count >= total) continue;
	    if (select > left && select < left + textWidth[count]) {
		c = match[count];
		idx = count;
		break;
	    }
	    left = left + (int) textWidth[count] + spacing;
	}

	switch (action) {
	case MotionEvent.ACTION_DOWN:
	case MotionEvent.ACTION_MOVE:
	    break;
	case MotionEvent.ACTION_UP:
	    if (listener != null && c != 0) listener.characterSelected(c, idx);
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
	offset = 13;
	invalidate();
    }

}
