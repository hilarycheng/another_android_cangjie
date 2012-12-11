package com.diycircuits.cangjie;

public class TableLoader {

    public native char getChar();
    public native char passCharArray(char[] array);
    public native int updateFrequencyQuick(char[][] array, char ch);

    static {
	System.loadLibrary("chinese_table");
    }

}

