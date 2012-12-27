import java.awt.*;
import java.io.*;
import java.util.*;

public class Convert {

    public static void convertQuick() {
	try {

	    HashMap<String, ArrayList<Character>> charList = 
		new HashMap<String, ArrayList<Character>>();
	    
	    Font font = new Font("Droid Sans Fallback", 16, Font.PLAIN);
	    int totalQuickColumn = 3;
	    FileInputStream fis = new FileInputStream("quick-classic.txt");
	    InputStreamReader input = new InputStreamReader(fis, "UTF-8");
	    BufferedReader reader = new BufferedReader(input);
	    String str = null;
	    int index = 0;
	    int total = 0;
	    ArrayList<String> keyList = new ArrayList<String>();
	    System.out.println("#define QUICK_COLUMN " + totalQuickColumn);
	    System.out.println("const jchar quick[][QUICK_COLUMN] = {");
	    do {
		str = reader.readLine();
		if (str == null)
		    break;
		index = str.indexOf('\t');
		if (index < 0) index = str.indexOf(' ');
		if (index > 0) {
		    if (font.canDisplay(str.charAt(index + 1))) {
			StringBuffer sb = new StringBuffer();
			// System.out.print("\t { ");
			if ((int) str.charAt(1) == 9 || str.charAt(1) == ' ')  {
			    // System.out.print("'" + str.charAt(0) + "',   0, ");
			    sb.append(str.charAt(0));
			} else {
			    sb.append(str.charAt(0));
			    sb.append(str.charAt(1));
			    // System.out.print("'" + str.charAt(0) + "', '" + str.charAt(1) + "', ");
			}
			String key = sb.toString();
			// System.out.println((int) str.charAt(index + 1) + " }, ");
			Character ch = new Character(str.charAt(index + 1));

			if (!keyList.contains(key)) keyList.add(key);
			
			if (charList.containsKey(key)) {
			    charList.get(key).add(ch);
			} else {
			    ArrayList<Character> c = new ArrayList<Character>();
			    c.add(ch);
			    charList.put(key, c);
			}
			total++;
		    }
		}
	    } while (str != null);
	    for (int count = 0; count < keyList.size(); count++) {
		String k = keyList.get(count);
		ArrayList<Character> l = charList.get(k);
		for (int loop = 0; loop < l.size(); loop++) {
		    if (k.length() == 1) {
			System.out.println("\t { '" + k.charAt(0) + "',   0, " + l.get(loop) + " }, ");
		    } else {
			System.out.println("\t { '" + k.charAt(0) + "', '" + k.charAt(1) + "', " + l.get(loop) + " }, ");
		    }
		}
	    }
	    System.out.println("};");
	    System.out.println("jint quick_index[" + total + "];");
	    System.out.println("jint quick_frequency[" + total + "];");
	    reader.close();
	    input.close();
	    fis.close();
	} catch (Exception ex) {
	    ex.printStackTrace();
	}
    }
    
    public static void convertCangjie() {
	try {
	    int totalCangjieColumn = 6;
	    FileInputStream fis = new FileInputStream("../../res/raw/cj");
	    InputStreamReader input = new InputStreamReader(fis, "UTF-8");
	    BufferedReader reader = new BufferedReader(input);
	    String str = null;
	    int index = 0;
	    int total = 0;
	    char column[] = new char[5];
	    System.out.println("#define CANGJIE_COLUMN " + totalCangjieColumn);
	    System.out.println("const jchar cangjie[][CANGJIE_COLUMN] = {");
	    do {
		str = reader.readLine();
		if (str == null)
		    break;
		index = str.indexOf('\t');
		if (index < 0) index = str.indexOf(' ');
		if (index > 0) {
		    System.out.print("\t { ");
		    for (int count = 0; count < 5; count++) {
			if (count < index) {
			    column[count] = str.charAt(count);
			    if (column[count] < 'a' || column[count] > 'z') column[count] = 0;
			    if (((int) column[count]) >= 10 || ((int) column[count]) <= 99) System.out.print(' ');
			    if (((int) column[count]) <= 9) System.out.print(' ');
			    System.out.print(((int)	column[count]));
			} else {
			    System.out.print("  0");
			}
			System.out.print(", ");
		    }
		    System.out.println((int) str.charAt(index + 1) + " }, ");
		    total++;
		}
	    } while (str != null);
	    System.out.println("};");
	    System.out.println("jint cangjie_index[" + total + "];");
	    System.out.println("jint cangjie_frequency[" + total + "];");
	    reader.close();
	    input.close();
	    fis.close();
	} catch (Exception ex) {
	    ex.printStackTrace();
	}
    }

    public static class CangjieChar {
	public char c;
	public boolean hk;

	public CangjieChar(char _c, boolean _h) { c = _c; hk = _h; }
    }
    
    public static void convertCangjieHK() {
	try {
	    ArrayList<String> codeList = new ArrayList<String>();
	    HashMap<String, ArrayList<CangjieChar>> codeMap = new HashMap<String, ArrayList<CangjieChar>>();
	    int totalCangjieColumn = 7;
	    FileInputStream fis = new FileInputStream("cangjie3.txt");
	    InputStreamReader input = new InputStreamReader(fis, "UTF-8");
	    BufferedReader reader = new BufferedReader(input);
	    String str = null;
	    int index = 0;
	    int total = 0;
	    char column[] = new char[5];
	    boolean hkchar = false;

	    System.out.println("#define CANGJIE_COLUMN " + totalCangjieColumn);
	    System.out.println("const jchar cangjie[][CANGJIE_COLUMN] = {");
	    do {
		str = reader.readLine();
		if (str == null)
		    break;
		if (str.compareTo("#####") == 0) {
		    hkchar = true;
		    continue;
		}
		index = str.indexOf('\t');
		if (index < 0) index = str.indexOf(' ');
		if (index > 0) {
		    int type = Character.getType(str.charAt(index + 1));
		    if (Character.isLetter(str.charAt(index + 1)) ||
			type == Character.START_PUNCTUATION || type == Character.END_PUNCTUATION ||
			type == Character.OTHER_PUNCTUATION || type == Character.MATH_SYMBOL ||
			type == Character.DASH_PUNCTUATION  || type == Character.CONNECTOR_PUNCTUATION ||
			type == Character.OTHER_SYMBOL      || type == Character.INITIAL_QUOTE_PUNCTUATION ||
			type == Character.FINAL_QUOTE_PUNCTUATION || type == Character.SPACE_SEPARATOR) {
			// System.out.print("\t { ");
			// for (int count = 0; count < 5; count++) {
			//     if (count < index) {
			// 	column[count] = str.charAt(count);
			// 	if (column[count] < 'a' || column[count] > 'z') column[count] = 0;
			// 	if (((int) column[count]) >= 10 || ((int) column[count]) <= 99) System.out.print(' ');
			// 	if (((int) column[count]) <= 9) System.out.print(' ');
			// 	System.out.print(((int)	column[count]));
			//     } else {
			// 	System.out.print("  0");
			//     }
			//     System.out.print(", ");
			// }
			// System.out.println((int) str.charAt(index + 1) + " }, ");

			String cangjie = str.substring(0, index).trim();
			char   ch      = str.charAt(index + 1);
			if (!codeList.contains(cangjie)) codeList.add(cangjie);
			ArrayList<CangjieChar> list = null;
			if (codeMap.containsKey(cangjie)) {
			    list = codeMap.get(cangjie);
			} else {
			    list = new ArrayList<CangjieChar>();
			}
			CangjieChar cc = new CangjieChar(ch, hkchar);
			list.add(cc);
			codeMap.put(cangjie, list);

			total++;
		    } else {
			System.err.println("Character Not Found : " + str.charAt(index + 1) + " " + Character.getType(str.charAt(index + 1)));
		    }
		}
	    } while (str != null);

	    Collections.sort(codeList);

	    for (int count0 = 0; count0 < codeList.size(); count0++) {
		String _str = codeList.get(count0);
		ArrayList<CangjieChar> ca = codeMap.get(_str);
		for (int count1 = 0; count1 < ca.size(); count1++) {
		    for (int count2 = 0; count2 < 5; count2++) {
			if (count2 < _str.length()) 
			    System.out.print("'" + _str.charAt(count2) + "', ");
			else
			    System.out.print("  0, ");
		    }
		    System.out.println(((int) ca.get(count1).c) + ", " + (ca.get(count1).hk ? 1 : 0) + ", ");
		}
	    }

	    System.out.println("};");
	    System.out.println("jint cangjie_hk_index[" + total + "];");
	    System.out.println("jint cangjie_hk_frequency[" + total + "];");
	    reader.close();
	    input.close();
	    fis.close();
	} catch (Exception ex) {
	    ex.printStackTrace();
	}
    }
    
    public static void main(String args[]) {

	System.err.println("Compare " + "a".compareTo("abc"));
	System.err.println("Compare " + "abc".compareTo("ab"));
	System.err.println("Compare " + "az".compareTo("abcd"));
	System.err.println("Compare " + "z".compareTo("abcd"));

	if (args.length != 1) return;
	if (args[0].compareTo("0") == 0)
	    Convert.convertQuick();
	if (args[0].compareTo("1") == 0)
	    Convert.convertCangjieHK();

    }

}
