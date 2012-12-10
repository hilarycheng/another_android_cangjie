package com.diycircuits.cangjie;

import java.util.*;
import android.util.*;

public class MostUsed {

    private StringBuffer sb = new StringBuffer();
    private TreeMap<MostUsedKey, ArrayList<MostUsedChar>> map = new TreeMap<MostUsedKey, ArrayList<MostUsedChar>>();

    public MostUsed() {
    }

    public synchronized void addChar(char key[], int key_len, char finalChar) {
	sb.setLength(0);
	for (int count = 0; count < key_len; count++) sb.append(key[count]);
	String k = sb.toString();
	MostUsedKey mKey = new MostUsedKey(key, key_len);
	if (map.containsKey(mKey)) {
	    boolean added = false;
	    ArrayList<MostUsedChar> c = map.get(mKey);
	    for (int count = 0; count < c.size(); count++) {
		if (c.get(count).getFinalChar() == finalChar) {
		    c.get(count).charUsed();
		    added = true;
		    break;
		}
	    }
	    if (!added) {
		MostUsedChar cc = new MostUsedChar(key, key_len, finalChar);
		c.add(cc);
	    }
	} else {
	    MostUsedChar c = new MostUsedChar(key, key_len, finalChar);
	    ArrayList<MostUsedChar> cc = new ArrayList<MostUsedChar>();
	    cc.add(c);
	    map.put(mKey, cc);
	}

	Log.i("Cangjie", "Most Used " + map);
    }

}
