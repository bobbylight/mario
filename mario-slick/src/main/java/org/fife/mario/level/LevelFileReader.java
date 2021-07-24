package org.fife.mario.level;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


/**
 * Reads a level file, skipping comment lines and empty lines.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class LevelFileReader {

	private BufferedReader r;

	/**
	 * Denotes a comment in a level file.
	 */
	private static final String COMMENT_START			= "#";


	/**
	 * Constructor.
	 *
	 * @param file The file to open.
	 * @throws IOException If an IO error occurs.
	 */
	public LevelFileReader(File file) throws IOException {
		r = new BufferedReader(new FileReader(file));
	}


	/**
	 * Constructor.
	 *
	 * @param resource The name of the resource to open.
	 * @throws IOException If an IO error occurs.
	 */
	public LevelFileReader(String resource) throws IOException {
		InputStream in = getClass().getClassLoader().getResourceAsStream(resource);
		r = new BufferedReader(new InputStreamReader(in));
	}


	/**
	 * Closes this reader.
	 *
	 * @throws IOException If an IO error occurs.
	 */
	public void close() throws IOException {
		r.close();
	}


	/**
	 * Reads the next line from the level file, expecting it to be in the
	 * form "<code>key=value</code>".  If it isn't, or if the key is an
	 * unexpected value, an exception is thrown.
	 *
	 * @param key The value that the key should be.
	 * @return The value on the line.
	 * @throws IOException If the file is in the wrong format, or an IO error
	 *         occurs.
	 */
	public String readKeyValueLine(String key) throws IOException {

		String line = readLine();
		if (line==null) {
			throw new IOException("Unexpected end of file; expected " +
							key + "=XXX");
		}

		int equals = line.indexOf('=');
		if (equals==-1) {
			throw new IOException("Invalid level file; expected " +
					key + "=XXX, found '" + line + "'");
		}

		return line.substring(equals+1);

	}


	/**
	 * Returns the next line in the input file, stripping out empty lines and
	 * comments.
	 *
	 * @return The next line, or <code>null</code> if the EOF is reached.
	 * @throws IOException If an IO error occurs.
	 */
	public String readLine() throws IOException {

		String line;

		do {
			line = r.readLine();
			if (line!=null) {
				int commentStart = line.indexOf(COMMENT_START);
				if (commentStart>-1) {
					line = line.substring(0, commentStart);
				}
				line = line.trim();
			}
		} while (line!=null && line.length()==0);

		return line;

	}


}
