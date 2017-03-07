package it.unitn.nlpir.questions;

import jregex.Matcher;
import jregex.Pattern;
import jregex.REFlags;

/**
 * 
 * Evaluates PERL regular expressions on text
 *
 */
public class PerlPatternMatcher {
	
	/**
	 * Search some text for a PERL regular expression
	 * @param text the text to analyze
	 * @param pattern the pattern to search for
	 * @return true if the pattern is contained in the text, false o.w.
	 */
	
	public static Boolean search(String text, String pattern) {
		return new Pattern(pattern, REFlags.IGNORE_CASE).matcher(text).find();	
	}

	public static String getMatch(String text, String pattern) {
		Matcher m = new Pattern(pattern, REFlags.IGNORE_CASE).matcher(text);
		m.find();
		return m.toString();
	}
	
	public static int[] getMatchBoundaries(String text, String pattern) {
		Matcher m = new Pattern(pattern, REFlags.IGNORE_CASE).matcher(text);
		m.find();
		int[] boundaries = new int[2];
		boundaries[0] = m.start();
		boundaries[1] = m.end();
		return boundaries;
	}
}
