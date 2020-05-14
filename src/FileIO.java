
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class FileIO {

	
	public static final String fileSep = System.getProperty("file.separator");
	public static final String lineSep = System.getProperty("line.separator");
	
	public static ArrayList<String> readFile(String filename) throws IOException {
		
		ArrayList<String> output = new ArrayList<String>();
		Scanner scan = null;
		
		try {
			
			FileReader reader = new FileReader(filename);
			scan = new Scanner(reader);
			
			while (scan.hasNext()) {
				String line = scan.nextLine();
				output.add(line);
			}
		
		} finally {
			if (scan != null)
				scan.close();
		}
		
		return output;
	}
	
	public static void writeFile(String filename, ArrayList<String> fileData) throws IOException{
		
		FileWriter f = null;

		StringBuffer s = new StringBuffer();
		for (String e : fileData) {
			s.append(e + "\n");
		}
		

		try {
			f = new FileWriter(filename);
			for (String line : fileData) {
				f.write(line);
				f.write(lineSep);
			}
		} finally {
			if (f != null)
				f.close();
		}
	}
}
