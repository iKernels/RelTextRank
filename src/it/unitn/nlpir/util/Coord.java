package it.unitn.nlpir.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Coord{
	public static Pattern p = Pattern.compile("\\{([0-9]+)\\;([0-9]+)\\}");
	
	private int begin;
	private int end;
	
	
	
	public Coord(int begin, int end) {
		super();
		this.begin = begin;
		this.end = end;
	}
	

	/**
	 * format: LABEL{start;end}
	 * @param serializedCoorrd
	 */
	public Coord(String serializedCoord) {
		super();
		Matcher m = p.matcher(serializedCoord);
		m.find();
		this.begin = Integer.valueOf(m.group(1));
		this.end = Integer.valueOf(m.group(2));
	}

	
	
	public int getBegin() {
		return begin;
	}


	public void setBegin(int begin) {
		this.begin = begin;
	}


	public int getEnd() {
		return end;
	}


	public void setEnd(int end) {
		this.end = end;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		
		result = prime * result + begin;
		result = prime * result + end;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Coord other = (Coord) obj;
		
		if (begin != other.begin)
			return false;
		if (end != other.end)
			return false;
		return true;
	}
	
	
}