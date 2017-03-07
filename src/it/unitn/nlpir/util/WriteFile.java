package it.unitn.nlpir.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Paths;

import com.google.common.io.Files;

public class WriteFile {

	private BufferedWriter out;

	public WriteFile() {
		this.out = new BufferedWriter(new OutputStreamWriter(System.out));
	}

	private static BufferedWriter open(String path) {
		try {
			Files.createParentDirs(Paths.get(path).toFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
					new File(path)), "UTF-8"));
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return out;
	}

	public WriteFile(String path) {
		this.out = open(path);
	}

	public WriteFile(String outputDir, String file) {
//		File fOutputDir = new File(outputDir);
//		if (!fOutputDir.exists())
//			fOutputDir.mkdirs();
		String path = Paths.get(outputDir, file).toString();
		this.out = open(path);
	}

	public void write(String content) {
		try {
			this.out.write(content);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public void writeLn(String content) {
		try {
			this.out.write(content);
			this.out.newLine();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public void close() {
		if (this.out != null) {
			try {
				this.out.flush();
				this.out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
