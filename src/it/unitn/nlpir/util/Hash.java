package it.unitn.nlpir.util;

import java.io.UnsupportedEncodingException;
import java.security.*;

public class Hash {
   
    public Hash() {
    }
    
    private static byte[] createHash(String text, String method) {
        try {
            byte[] b = null;
			try {
				b = text.getBytes("UTF-8");
				/*for (int i = 0 ; i < Math.min(b.length, 100); i++){
					System.out.print(b[i]+" ");
				}
				System.out.println();*/
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            MessageDigest algorithm = MessageDigest.getInstance(method);
            algorithm.reset();
            algorithm.update(b);
            //sha.update(saltPlusPlainTextPassword.getBytes("UTF-8"));
            byte messageDigest[] = algorithm.digest();
            return messageDigest;
        }
        catch(NoSuchAlgorithmException nsae) {
            return null;
        }
    }
   
    
    public static String getHash(String text) {
        try {
            byte[] b = createHash(text, "SHA-1");
            return asHex(b);
        }
        catch(Exception e) {
        	return null;
        }
    }

    private static String asHex(byte[] b) {
        String result = "";
        for (int i=0; i < b.length; i++) {
            result +=
            Integer.toString(( b[i] & 0xff ) + 0x100, 16).substring(1 );
        }
        return result;
    }
    
    public static void main(String[] args) {
    	System.out.println(getHash(args[0]));
    	System.out.println(getHash("Test 2"));
    }
    
}