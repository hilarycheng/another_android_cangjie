package com.diycircuits.cangjie;

public class TableLoader {

    public native void setPath(byte[] path);
    public native void initialize();
    public native char getChar();
    public native char passCharArray(char[] array);
    public native int  updateFrequencyQuick(char ch);
    public native void searchQuick(char index0, char index1);
    public native int totalMatch();
    public native char getMatchChar(int index);
    public native void saveMatch();
    public native void clearAllFrequency();
    public native void reset();

    static {
	System.loadLibrary("chinese_table");
    }

}

