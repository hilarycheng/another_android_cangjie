
import java.io.*;

public class Convert {

    public static void convertQuick() {
	try {
	    FileInputStream fis = new FileInputStream("../res/raw/quick");
	    InputStreamReader input = new InputStreamReader(fis, "UTF-8");
	    BufferedReader reader = new BufferedReader(input);
	    String str = null;
	    int index = 0;
	    int total = 0;
	    System.out.println("const jchar quick[][4] = {");
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
		    System.out.println((int) str.charAt(index + 1) + ", 0 }, ");
		    total++;
		}
	    } while (str != null);
	    System.out.println("};");
	    System.out.println("jint quick_index[" + total + "];");
	    reader.close();
	    input.close();
	    fis.close();
	} catch (Exception ex) {
	    ex.printStackTrace();
	}
    }
    
    public static void main(String args[]) {
	Convert.convertQuick();
    }

}
