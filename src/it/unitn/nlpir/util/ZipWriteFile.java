package it.unitn.nlpir.util;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Paths;
import java.util.zip.GZIPOutputStream;

import com.google.common.io.Files;

public class ZipWriteFile extends WriteFile {

	//private OutputStream out;
	private Writer writer;
	private FileOutputStream output;
	private Writer open(String path) {
		try {
			
			Files.createParentDirs(Paths.get(path).toFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			output = new FileOutputStream(path);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		};
		
		try {
			writer = new OutputStreamWriter(new GZIPOutputStream(output), "UTF-8");
			
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return writer;
	}

	public ZipWriteFile(String path) {
		this.writer = open(path);
	}

	public ZipWriteFile(String outputDir, String file) {
		String path = Paths.get(outputDir, file+".gz").toString();
		this.writer = open(path);
	}

	public void write(String content) {
		try {
			this.writer.write(content);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public void writeLn(String content) {
		try {
			this.writer.write(content);
			this.writer.write(System.lineSeparator());
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public void close() {
		if (this.output != null) {
			try {
				this.writer.flush();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				this.writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			try {
				this.output.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
