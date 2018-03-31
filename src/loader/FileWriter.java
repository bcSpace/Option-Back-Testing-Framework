package loader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

public class FileWriter {
	
	public void writeFile(String path, String content) {
		File file = new File(path);
		file = file.getParentFile();
		if(!file.exists()) file.mkdirs();
		
		try (Writer writer = new BufferedWriter(new OutputStreamWriter(
	              new FileOutputStream(path), "utf-8"))) {
	   writer.write(content);
	} catch(Exception e) {e.printStackTrace();}
	}

}
