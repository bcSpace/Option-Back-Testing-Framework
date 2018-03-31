package loader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class FileLoader {

	
	public String[] loadData(String path) {
		int lineCounter = 0;
		String s[] = new String[5000];
		try {
			File file = new File(path);
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				s[lineCounter] = line;
				lineCounter++;
			}
			fileReader.close();
		} catch (IOException e) {
//			e.printStackTrace();
			return new String[9999];
		}
		String a[] = new String[lineCounter];
		for(int i = 0; i < lineCounter; i++) a[i] = s[i];
		return a;
	}
	
	
}
