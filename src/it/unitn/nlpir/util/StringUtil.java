package it.unitn.nlpir.util;
/**
 * (c) UNITN
* @author IKernels group
 *
 */
public class StringUtil {
	
	public static String filterHTMLEntities(String text) {
		return text.replace("&ldquo;", "\"")
				.replace("&rdquo;", "\"")
				.replace("&rsquo;", "'")
				.replace("&nbsp;", " ")
				.replace("&quot;", "\"")
				.replace("&lt;", "<")
				.replace("&gt;", ">")
				.replace("&amp;", "&")
				.replace("&ndash;", "-");
	}
	
}
