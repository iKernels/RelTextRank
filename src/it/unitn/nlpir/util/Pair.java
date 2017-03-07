package it.unitn.nlpir.util;

public class Pair<K, V> {
	
	private K a;
	private V b;
	
	@Override
	public String toString() {
		return "Pair [a=" + a + ", b=" + b + "]";
	}

	public Pair(K a, V b) {
		this.a = a;
		this.b = b;
	}
	
	public K getA() {
		return this.a;
	}
	
	public V getB() {
		return this.b;
	}
	
	public void setA(K a) {
        this.a = a;
    }
    
    public void setB(V b) {
        this.b = b;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((a == null) ? 0 : a.hashCode());
		result = prime * result + ((b == null) ? 0 : b.hashCode());
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
		Pair other = (Pair) obj;
		if (a == null) {
			if (other.a != null)
				return false;
		} else if (!a.equals(other.a))
			return false;
		if (b == null) {
			if (other.b != null)
				return false;
		} else if (!b.equals(other.b))
			return false;
		return true;
	}
    
    
}
