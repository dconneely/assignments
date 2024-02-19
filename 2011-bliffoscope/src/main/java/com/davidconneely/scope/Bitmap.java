package com.davidconneely.scope;

/**
 * Represents a 2D rectangular monochrome bitmap. The data used to initialize
 * the <code>Bitmap</code> instance should always be an array of arrays of the
 * same width, or otherwise unexpected behaviour can result (usually an
 * <code>UnexpectedShapeException</code> but could be others, such as
 * <code>ArrayIndexOutOfBoundsException</code>).
 * <p>
 * Also handles removing "useless" clear margins and maintains the midpoint of
 * the shape that will be reported as its location.
 * <p>
 * It is "mostly immutable", but it does not try to protect the contained bitmap
 * data by making a defensive copy of the array of arrays during construction.
 * So you should avoid modifying the original array or arrays after creating a
 * <code>Bitmap</code> instance from it unless you intend for this to change the
 * contents of the <code>Bitmap</code> instance.
 * <p>
 * Additionally, <code>Bitmap</code> does not override the <code>equals</code>
 * and <code>hashCode</code> methods (they are not currently needed).
 */
public final class Bitmap {
	private final int midX;
	private final int midY;
	private final boolean[][] data;

	/**
	 * We don't expect to have many empty <code>Bitmap</code> instances, but
	 * they can share this value because the empty instance really is immutable.
	 */
	private static final Bitmap EMPTY = new Bitmap(new boolean[0][0], 0, 0);

	/**
	 * Construct a <code>Bitmap</code> instance from a 2D array of
	 * <code>boolean</code> values, calculating the midpoint automatically.
	 * Used internally by the public <code>fromArray</code> method.
	 */
	private Bitmap(boolean[][] data) {
		this.data = data;
		int height = data.length;
		int width = (height != 0) ? data[0].length : 0;
		this.midY = (height != 0) ? (height - 1) / 2 : 0;
		this.midX = (width != 0) ? (width - 1) / 2 : 0;
	}

	/**
	 * Construct a <code>Bitmap</code> instance from an array of arrays of
	 * <code>boolean</code> values, and set the midpoint to specific values.
	 * Used internally by the public <code>trim</code> method.
	 */
	private Bitmap(boolean[][] data, int midX, int midY) {
		this.data = data;
		this.midX = midX;
		this.midY = midY;
	}

	/**
	 * Return the <code>boolean</code> value at the specified coordinates in the
	 * array of arrays (which is treated like a 2D array).
	 */
	boolean at(int x, int y) {
		return data[y][x];
	}
	
	/**
	 * Return the number of elements in the array of arrays with the value
	 * <code>true</code>.
	 */
	int cardinality() {
		// as we didn't make a defensive copy of the data array,
		// we can't cache this count. 
		int cardinality = 0;
		int height = height();
		int width = width();
		for (int i = 0; i < height; ++i) {
			checkLineWidth(i, width);
			for (int j = 0; j < width; ++j) {
				if (data[i][j]) {
					++cardinality;
				}
			}
		}
		return cardinality;
	}
	
	/**
	 * Factory method for obtaining <code>Bitmap</code> instances. The returned
	 * <code>Bitmap</code> instance currently uses the provided
	 * <code>data</code> argument as its backing array of arrays, so be aware
	 * that changes made in the original <code>data</code> array of arrays can
	 * affect the contents of the returned <code>Bitmap</code> instance.
	 */
	public static Bitmap fromArray(boolean[][] data) {
		if (data == null) {
			throw new NullPointerException();
		} else if (data.length == 0) {
			return EMPTY;
		} else {
			return new Bitmap(data);
		}
	}

	/**
	 * Rows in the bitmap.
	 */
	int height() {
		return data.length;
	}

	/**
	 * Columns in the bitmap.
	 */
	int width() {
		return (data.length != 0) ? data[0].length : 0;
	}

	/**
	 * Zero-based index of the mid-column in the data. For example, if the width
	 * was 3, the mid-column would be 1; and if the width was 6, the mid-column
	 * would be 2 (if the width is even, it is the leftmost of the two
	 * midpoints). This value is maintained when the left and right margins are
	 * removed by the <code>trim</code> method.
	 */
	int midX() {
		return midX;
	}

	/**
	 * Zero-based index of the mid-row in the data. For example, if the height
	 * was 3, the mid-row would be 1; and if the height was 6, the mid-row would
	 * be 2 (if the height is even, it is the topmost of the two midpoints).
	 * This value is maintained when the top and bottom margins are removed by
	 * the <code>trim</code> method.
	 */
	int midY() {
		return midY;
	}

	/**
	 * If the shape being looked for has margins of all 'false' on any of the 4
	 * edges, then these do not help to find the shape (and may hinder if the
	 * shape is close to an edge, for example), so we trim margins, and ensure
	 * that the midpoints are maintained correctly.
	 */
	Bitmap trim() {
		int height = height();
		int width = width();
		int top = Integer.MAX_VALUE;
		int bottom = Integer.MAX_VALUE;
		int left = Integer.MAX_VALUE;
		int right = Integer.MAX_VALUE;
		for (int i = 0; i < height; ++i) {
			checkLineWidth(i, width);
			for (int j = 0; j < width; ++j) {
				if (data[i][j]) {
					if (i < top) {
						top = i;
					}
					if (height - 1 - i < bottom) {
						bottom = height - 1 - i;
					}
					if (j < left) {
						left = j;
					}
					if (width - 1 - j < right) {
						right = width - 1 - j;
					}
				}
			}
		}
		// nothing to change if the margins are all zero, or if the data was
		// zero-sized to start with; the midpoint doesn't need updating either.
		if ((left == 0 && right == 0 && top == 0 && bottom == 0) || height == 0) {
			return this;
		}
		// note that if any of the margins is huge, then they all will be;
		// in these cases we just trim the array back to empty.
		if (left == Integer.MAX_VALUE || right == Integer.MAX_VALUE
				|| top == Integer.MAX_VALUE || bottom == Integer.MAX_VALUE) {
			return Bitmap.EMPTY;
		}
		// copy into a new smaller array without the all-zero margins.
		boolean[][] newData = new boolean[height - top - bottom][];
		for (int i = top; i < height - bottom; ++i) {
			newData[i - top] = new boolean[width - left - right];
			// make a copy of the row without the left and right margins.
			System.arraycopy(data[i], left, newData[i - top], 0, newData[i
					- top].length);
		}
		return new Bitmap(newData, midX - left, midY - top);
	}

	/**
	 * Returns the bitmap's data in a similar format to the input data.
	 */
	@Override
	public String toString() {
		// this could use StringWriter and BitmapLoader.toWriter now.
		// however, I wrote this first, and it should probably include extra
		// information like the midpoint location.
		StringBuilder sb = new StringBuilder();
		int height = height();
		int width = width();
		for (int i = 0; i < height; ++i) {
			checkLineWidth(i, width);
			for (int j = 0; j < width; ++j) {
				sb.append(at(j, i) ? '+' : ' ');
			}
			sb.append('\n');
		}
		return sb.toString();
	}

	/**
	 * Tiny internal helper method used to sanity-check the data is rectangular.
	 * 
	 * @throws UnexpectedShapeException if row <code>i</code> is not of width
	 *                                  <code>width</code>
	 */
	private void checkLineWidth(int i, int width) {
		if (data[i].length != width) {
			throw new UnexpectedShapeException(i + 1, data[i].length, width);
		}
	}
}
