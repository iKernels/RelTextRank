package it.unitn.nlpir.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReadFile {
    
    private BufferedReader in;
    private String line = null;

    public ReadFile(String path) {
        in = null;
        try {
            in = new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF-8"));
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Logger.getLogger(ReadFile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public boolean hasNextLine() {
    	if(this.line == null) {
	        try {
	            this.line = in.readLine();
	        } catch (IOException ex) {
	            Logger.getLogger(ReadFile.class.getName()).log(Level.SEVERE, null, ex);
	        }
    	}
        
        return line != null;
    }
    
    public String nextLine() {
    	if(this.line == null) {
    		try {
				return in.readLine();
			} catch (IOException ex) {
				Logger.getLogger(ReadFile.class.getName()).log(Level.SEVERE, null, ex);
			}
    		return null;
    	} else {
    		String retValue = this.line;
    		this.line = null;
    		return retValue;
    	}
    }
    
    public void close() {
        try {
            in.close();
        } catch (IOException ex) {
            Logger.getLogger(ReadFile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
