package com.diycircuits.cangjie;

import java.util.*;
import android.util.*;

public class MostUsedChar implements Comparable {

    private int mOftenUse = 0;
    private char mKey[] = new char[5];
    private char mFinalChar = 0;
    private StringBuffer sb = new StringBuffer();

    public MostUsedChar(char input[], int input_len, char ch) {
	for (int count = 0; count < mKey.length; count++) mKey[count] = 0;
	for (int count = 0; count < input_len; count++) mKey[count] = input[count];
	mFinalChar = ch;
	mOftenUse = 1;
    }

    public char[] getKey() {
	return mKey;
    }

    public char getFinalChar() {
	return mFinalChar;
    }

    public int getOftenUse() {
	return mOftenUse;
    }

    public void charUsed() {
	mOftenUse++;
    }

    public void resetToOne() {
	mOftenUse = 1;
    }

    public int compareTo(Object obj) throws ClassCastException {
	if (!(obj instanceof MostUsedChar))
	    throw new ClassCastException("Cannot Compare MostUsedChar Object");

	MostUsedChar charobj = (MostUsedChar) obj;

	if (this.mOftenUse > charobj.mOftenUse)
	    return 1;
	else if (this.mOftenUse < charobj.mOftenUse)
	    return -1;
	else
	    return 0;
    }

    public String toString() {
	sb.setLength(0);
	sb.append("[ MostUsedChar ");
	sb.append(Integer.toString(mKey[0]));
	sb.append(' ');
	sb.append(Integer.toString(mKey[1]));
	sb.append(' ');
	sb.append(Integer.toString(mKey[2]));
	sb.append(' ');
	sb.append(Integer.toString(mKey[3]));
	sb.append(' ');
	sb.append(Integer.toString(mKey[4]));
	sb.append(' ');
	sb.append(Integer.toString(mOftenUse));
	sb.append(' ');
	sb.append(mFinalChar);
	sb.append("] ");

	return sb.toString();
    }

}
