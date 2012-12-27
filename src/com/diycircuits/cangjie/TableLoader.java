package com.diycircuits.cangjie;

public class TableLoader {

    public final static int QUICK   = 0;
    public final static int CANGJIE = 1;
    
    public native void setPath(byte[] path);
    public native void initialize();
    public native char getChar();
    public native char passCharArray(char[] array);
    public native int  updateFrequencyQuick(char ch);
    public native void enableHongKongChar(boolean hk);
    public native void setInputMethod(int im);
    public native void searchCangjie(char index0, char index1, char index2, char index3, char index4);
    public native boolean tryMatchCangjie(char index0, char index1, char index2, char index3, char index4);
    public native int totalMatch();
    public native char getMatchChar(int index);
    public native void saveMatch();
    public native void clearAllFrequency();
    public native void reset();

    static {
	System.loadLibrary("chinese_table");
    }

}

