package org.fife.mario;

/**
 * Utility methods for the Mario game.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public final class Utils {

	/**
	 * Private constructor to prevent instantiation.
	 */
	private Utils() {
	}

	/**
	 * Returns a string suitable for a Mario level file, representing text
	 * for an info block.
	 *
	 * @param from The text for the block.
	 * @return The text to put into the level file.
	 */
	public static String createInfoBlockText(String from) {
		return from.replaceAll("\\\"", "\\\\\"").replaceAll("\n", "\\\\n");
	}

	public static String getInfoBlockText(String from) {

		StringBuilder sb = new StringBuilder();

		// Skip enclosing double quotes
		int offs = 1;
		while (offs<from.length()-1) {
			char ch = from.charAt(offs++);
			if (ch!='\\') {
				sb.append(ch);
			}
			else {
				if (offs<from.length()) {
					ch = from.charAt(offs++);
					switch (ch) {
						case '"':
							sb.append('"');
							break;
						case 'n':
							sb.append('\n');
							break;
						default:
							sb.append('\\').append(ch);
							break;
					}
				}
				else { // from ends with '\\'.  Strange, but possible I guess
					sb.append('\\');
				}
			}
		}

		return sb.toString();

	}
}
