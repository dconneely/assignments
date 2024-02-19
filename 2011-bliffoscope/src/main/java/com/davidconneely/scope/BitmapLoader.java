package com.davidconneely.scope;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.util.LinkedList;
import java.util.List;

/**
 * Reads scope data from provided input. The specification isn't very
 * detailed about the text format, so some assumptions about what is allowed and
 * what is not have been made.
 * <p>
 * <b>Assumptions:</b>
 * <ul>
 * <li>The text consists of lines, which must all be the same width.</li>
 * <li>Blank lines are not allowed (although the file can end with a line
 * separator) unless all the lines are of zero width.</li>
 * <li>The only characters that will appear in input data are ' ' or '+' (and
 * line separators).</li>
 * <li>Users will want a diagnostic if the input data doesn't match these
 * specifications that tells them where the problem is, and they will prefer
 * one-based line and column numbers.</li>
 * </ul>
 */
public final class BitmapLoader {

	private static final boolean[][] BOOL_2D = new boolean[0][0];

	static Bitmap fromString(String data) throws IOException {
		Reader sr = new StringReader(data);
		return BitmapLoader.fromReader(sr);
	}

	/**
	 * Initialize a <code>Bitmap</code> instance from the resource name
	 * provided.
	 * <p>
	 * Before loading, an absolute resource name is constructed from the given
	 * resource name using this algorithm:
	 * <ul>
	 * <li>If the resource name begins with a <code>'/'</code> character, then
	 * the absolute name of the resource is the portion of the resource name
	 * following the <code>'/'</code>.
	 * <li>Otherwise, the absolute name is formed by prefixing the resource name
	 * with a path prefix (currently empty string).
	 * </ul>
	 * 
	 * @throws IOException
	 *             If an I/O error occurs.
	 * @throws UnexpectedShapeException
	 *             If a line of input data is too short or too long.
	 * @throws UnexpectedValueException
	 *             If a line of input data has chars other than ' ' or '+'.
	 */
	static Bitmap fromResource(String resourceName) throws IOException {
		resourceName = resolveResourceName(resourceName);
		ClassLoader cl = BitmapLoader.class.getClassLoader();
		InputStream is = (cl != null) ? cl.getResourceAsStream(resourceName)
				: ClassLoader.getSystemResourceAsStream(resourceName);
		if (is == null) {
			throw new FileNotFoundException("Resource /" + resourceName
					+ " could not be loaded.");
		}
		// our known resource files contain just ' ', '+' and '\n' characters,
		// so we could use US-ASCII or ISO_8859-1 equally.
		Bitmap ret = BitmapLoader.fromInputStream(is, "UTF-8");
		is.close();
		return ret;
	}

	/**
	 * Initialize a <code>Bitmap</code> instance from an
	 * <code>InputStream</code> instance.
	 * <p>
	 * Assumes the <code>InputStream</code> contains character data encoded as
	 * bytes using the given <code>charset</code>.
	 * 
	 * @throws IOException
	 *             If an I/O error occurs.
	 * @throws UnexpectedShapeException
	 *             If a line of input data is too short or too long.
	 * @throws UnexpectedValueException
	 *             If a line of input data has chars other than ' ' or '+'.
	 */
	public static Bitmap fromInputStream(InputStream is, String charset)
			throws IOException {
		return fromReader(new InputStreamReader(is, charset));
	}

	/**
	 * Initialize a <code>Bitmap</code> instance from a <code>Reader</code>
	 * instance.
	 * 
	 * @throws IOException
	 *             If an I/O error occurs.
	 * @throws UnexpectedShapeException
	 *             If a line of input data is too short or too long.
	 * @throws UnexpectedValueException
	 *             If a line of input data has chars other than ' ' or '+'.
	 */
	static Bitmap fromReader(Reader rdr) throws IOException {
		LineNumberReader lnr = new LineNumberReader(rdr);
		return BitmapLoader.readBitmap(lnr);
	}

	/**
	 * Read the rectangular bitmap data from the input provided. Tries to
	 * exhaust the input.
	 */
	private static Bitmap readBitmap(LineNumberReader lnr) throws IOException {
		List<boolean[]> rows = new LinkedList<boolean[]>();
		boolean[] row = readLine(lnr);
		int expectedCols = (row != null) ? row.length : 0;
		while (row != null) {
			if (expectedCols != row.length) {
				throw new UnexpectedShapeException(lnr.getLineNumber(),
						row.length, expectedCols);
			}
			rows.add(row);
			row = readLine(lnr);
		}
		return Bitmap.fromArray(rows.toArray(BOOL_2D));
	}

	/**
	 * Read a line of input data.
	 */
	private static boolean[] readLine(LineNumberReader lnr) throws IOException {
		String line = lnr.readLine();
		if (line == null) {
			return null;
		}
		boolean[] cols = new boolean[line.length()];
		for (int i = 0; i < line.length(); ++i) {
			switch (line.charAt(i)) {
			case ' ':
				// cols[i] is already false
				break;
			case '+':
				cols[i] = true;
				break;
			default:
				throw new UnexpectedValueException(lnr.getLineNumber(), i + 1,
						Character.toString(line.charAt(i)));
			}
		}
		return cols;
	}

	/**
	 * This will print out a <code>Bitmap</code> instance to the given
	 * <code>Writer</code> instance in the same format as we load them.
	 */
	static void toWriter(Bitmap bitmap, Writer wr) throws IOException {
		int height = bitmap.height();
		int width = bitmap.width();
		for (int i = 0; i < height; ++i) {
			for (int j = 0; j < width; ++j) {
				wr.write(bitmap.at(j, i) ? '+' : ' ');
			}
			wr.write('\n');
		}
	}

	/**
	 * Resolve "relative" resources relative to the default
	 * package. Similar to <code>Class.getResource</code> method.
	 */
	private static String resolveResourceName(String resource) {
		if (resource == null) {
			return null;
		}
		if (resource.length() > 1 && resource.charAt(0) == '/') {
			return resource.substring(1);
		} else {
			return resource;
		}
	}
}