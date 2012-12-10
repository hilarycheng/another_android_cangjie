package com.diycircuits.cangjie;

import java.util.*;
import android.util.*;

public class MostUsedKey implements Comparable {

    private char mKey[] = new char[5];
    private int mTotal = 0;
    private StringBuffer sb = new StringBuffer();

    public MostUsedKey(char key[], int len) {
	for (int count = 0; count < mKey.length; count++) mKey[count] = 0;
	for (int count = 0; count < len; count++) mKey[count] = key[count];
	mTotal = len;
    }
    
    public int compareTo(Object obj) throws ClassCastException {
	if (!(obj instanceof MostUsedKey))
	    throw new ClassCastException("Cannot Compare MostUsedChar Object");

	MostUsedKey charobj = (MostUsedKey) obj;
	Log.i("Cangjie", "Compare " + mTotal + " " + charobj.mTotal);
	if (this.mTotal > charobj.mTotal)
	    return 1;
	boolean match = true;
	for (int count = 0; count < charobj.mTotal; count++) {
	    match = match & (mKey[count] == charobj.mKey[count]);
	}
	Log.i("Cangjie", "Compare " + mTotal + " " + charobj.mTotal + " " + match);
	if (match)
	    return 0;
	else
	    return -1;
    }

    public String toString() {
	sb.setLength(0);
	sb.append("[ Len: ");
	sb.append(Integer.toString(mTotal));
	sb.append(", ");
	for (int count = 0; count < mTotal; count++) {
	    sb.append(Integer.toString(mKey[count]));
	    sb.append(", ");
	}
	sb.append("], ");

	return sb.toString();
    }

}

