package blockygame.util;

/**
 * An immutable pair of ints.
 */
public final class Tuple {
	public final int x, y;
	
	public Tuple(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public Tuple add(int ox, int oy) {
		if (ox == 0 && oy == 0)
			return this;
		return new Tuple(x + ox, y + oy);
	}
	
	public Tuple add(Tuple other) {
		return add(other.x, other.y);
	}
	
	public Tuple subtract(int ox, int oy) {
		if (ox == 0 && oy == 0)
			return this;
		return new Tuple(x - ox, y - oy);
	}
	
	public Tuple subtract(Tuple other) {
		return subtract(other.x, other.y);
	}
	
	@Override
	public String toString() {
		return "Tuple(" + x + ", " + y + ")";
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Tuple))
			return false;
		Tuple other = (Tuple) obj;
		
		return this.x == other.x && this.y == other.y;
	}	
	
}