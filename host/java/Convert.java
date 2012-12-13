
import java.io.*;

public class Convert {

    public static void convertQuick() {
	try {
	    int totalQuickColumn = 3;
	    FileInputStream fis = new FileInputStream("../../res/raw/quick");
	    InputStreamReader input = new InputStreamReader(fis, "UTF-8");
	    BufferedReader reader = new BufferedReader(input);
	    String str = null;
	    int index = 0;
	    int total = 0;
	    System.out.println("#define QUICK_COLUMN " + totalQuickColumn);
	    System.out.println("const jchar quick[][QUICK_COLUMN] = {");
	    do {
		str = reader.readLine();
		if (str == null)
		    break;
		index = str.indexOf('\t');
		if (index < 0) index = str.indexOf(' ');
		if (index > 0) {
		    System.out.print("\t { ");
		    if ((int) str.charAt(1) == 9 || str.charAt(1) == ' ') 
			System.out.print((int) str.charAt(0) + ", 0, ");
		    else
			System.out.print((int) str.charAt(0) + ", " + (int) str.charAt(1) + ", ");
		    System.out.println((int) str.charAt(index + 1) + " }, ");
		    total++;
		}
	    } while (str != null);
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
    
    public static void convertCangjieHK() {
	try {
	    int totalCangjieColumn = 6;
	    FileInputStream fis = new FileInputStream("../../res/raw/cj_hk");
	    InputStreamReader input = new InputStreamReader(fis, "UTF-8");
	    BufferedReader reader = new BufferedReader(input);
	    String str = null;
	    int index = 0;
	    int total = 0;
	    char column[] = new char[5];
	    System.out.println("#define CANGJIE_HK_COLUMN " + totalCangjieColumn);
	    System.out.println("const jchar cangjie_hk[][CANGJIE_HK_COLUMN] = {");
	    do {
		str = reader.readLine();
		if (str == null)
		    break;
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
		    } else {
			System.err.println("Character Not Found : " + str.charAt(index + 1) + " " + Character.getType(str.charAt(index + 1)));
		    }
		}
	    } while (str != null);
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
	Convert.convertQuick();
	Convert.convertCangjie();
	Convert.convertCangjieHK();
    }

}
