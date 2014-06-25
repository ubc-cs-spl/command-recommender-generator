package ca.ubc.cs.commandrecommender.model.cf;
/**
 * Slightly modifed from SPMF
 * 
 * @author emerson
 *
 */
//TODO: check over
public class Pair {

    private final Integer _left;
    private final Integer _right;

    public Integer getRight() {
        return _right;
    }

    public int compareTo(Pair other) {
		int firstC = this._left.compareTo(other._left);
		return firstC==0 ?
			this._right.compareTo(other._right):
			firstC;
	}

	public Integer getLeft() {
        return _left;
    }

    public Pair(final Integer left, final Integer right) {
        _left = left;
        _right = right;
    }

    public final boolean equals(Object o) {
        if (!(o instanceof Pair))
            return false;

        final Pair other = (Pair) o;
        return equal(getLeft(), other.getLeft()) && equal(getRight(), other.getRight());
    }

    public static final boolean equal(Object o1, Object o2) {
        if (o1 == null) {
            return o2 == null;
        }
        return o1.equals(o2);
    }

    public int hashCode() {
        int hLeft = getLeft() == null ? 0 : getLeft().hashCode();
        int hRight = getRight() == null ? 0 : getRight().hashCode();

        return hLeft + (57 * hRight);
    }
    
    public String toString(){
    	return "("+_left+","+_right+")";
    }
}


