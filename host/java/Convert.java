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
	    Font font = new Font("Droid Sans Fallback", 16, Font.PLAIN);
	    ArrayList<String> codeList = new ArrayList<String>();
	    HashMap<String, ArrayList<CangjieChar>> codeMap = new HashMap<String, ArrayList<CangjieChar>>();
	    int totalCangjieColumn = 8;
	    FileInputStream fis = new FileInputStream("cangjie3.txt");
	    InputStreamReader input = new InputStreamReader(fis, "UTF-8");
	    BufferedReader reader = new BufferedReader(input);
	    String str = null;
	    int index = 0;
	    int total = 0;
	    char column[] = new char[5];
	    boolean hkchar = false;
	    int counter[] = new int[26];

	    for (int count = 0; count < counter.length; count++) counter[count] = 0;
	    
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
		if (index > 0 && font.canDisplay(str.charAt(index + 1))) {
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

			// total++;
		    } else {
			System.err.println("Character Not Found : " + str.charAt(index + 1) + " " + Character.getType(str.charAt(index + 1)));
		    }
		}
	    } while (str != null);

	    Collections.sort(codeList);

	    total = 0;
	    for (int count0 = 0; count0 < codeList.size(); count0++) {
		String _str = codeList.get(count0);
		ArrayList<CangjieChar> ca = codeMap.get(_str);
		for (int count1 = 0; count1 < ca.size(); count1++) {

		    int i = _str.charAt(0) - 'a';
		    counter[i]++;
		    total++;
			
		    int l = 0;
		    for (int count2 = 0; count2 < 5; count2++) {
			if (count2 < _str.length()) {
			    l = count2;
			    System.out.print("'" + _str.charAt(count2) + "', ");
			} else {
			    System.out.print("  0, ");
			}
		    }
		    System.out.println(((int) ca.get(count1).c) + ", " + (ca.get(count1).hk ? 1 : 0) + ", " + (l + 1) + ", ");
		}
	    }

	    System.out.println("};");

	    int offset = 0;
	    System.out.println("jint cangjie_code_index[26][2] = {");
	    for (int count0 = 0; count0 < counter.length; count0++) {
		System.out.println("{ " + offset + "," + counter[count0] + " },");
		offset += counter[count0];
	    }
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

    public static void convertStroke() {
	try {
	    FileInputStream fis = new FileInputStream("StrokeOrder.sql");
	    InputStreamReader input = new InputStreamReader(fis, "UTF-8");
	    BufferedReader reader = new BufferedReader(input);

	    HashMap<String, String> mapping = new HashMap<String, String>();
	    ArrayList<String> order = new ArrayList<String>();
	    String line = null;
	    int total = 0, max = 0;
	    do {
		line = reader.readLine();
		if (line == null)
		    continue;
		if (!line.startsWith("INSERT INTO StrokeOrder"))
		    continue;
		line = line.substring(62);
		if (line.length() < 3)
		    continue;
		line = line.substring(0, line.length() - 2);
		StringTokenizer token = new StringTokenizer(line, ",");
		if (token.countTokens() != 3)
		    continue;
		String stroke = token.nextToken();
		String ch     = token.nextToken();

		if (!ch.startsWith("'") || !ch.endsWith("'"))
		    continue;

		if (!stroke.startsWith("'") || !stroke.endsWith("'"))
		    continue;

		byte[] b = ch.substring(1, 2).getBytes("BIG5");
		if (b.length != 2)
		    continue;

		if (stroke.compareTo("'6'") == 0 ||
		    stroke.compareTo("'7'") == 0)
		    continue;

		stroke = stroke.substring(1, stroke.length() - 1);
		ch     = ch.substring(1, 2);

		if (stroke.length() > max) max = stroke.length();

		if (mapping.containsKey(stroke)) {
		    mapping.put(stroke, mapping.get(stroke) + ch);
		} else {
		    mapping.put(stroke, ch);
		}

		if (!order.contains(stroke)) order.add(stroke);
		// System.out.println(total + " " + stroke + " " + ch + " " + b.length + " " + ch + " " + b[0]);

		total++;
	    } while (line != null);

	    int allkey = 0;
	    System.out.println("struct STROKE_ORDER {");
	    System.out.println("char stroke[" + (max + 1) + "];");
	    System.out.println("int ch;");
	    System.out.println("int num;");
	    System.out.println("} stroke[] = {");
	    for (int count = 0; count < order.size(); count++) {
		String ch = mapping.get(order.get(count));
		for (int count0 = 0; count0 < ch.length(); count0++) {
		    System.out.print(" { \"" + order.get(count) + "\", ");
		    System.out.print((int) ch.charAt(count0));
		    System.out.print(", ");
		    System.out.print(order.get(count).length());
		    System.out.println(" },");
		    allkey++;
		}
	    }
	    System.out.println("};");

	    System.out.println("#define STROKE_MAXKEY " + max);
	    System.out.println("#define STROKE_TOTAL " + allkey);
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
	if (args[0].compareTo("2") == 0)
	    Convert.convertStroke();

    }

}
