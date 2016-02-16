package org.devnexus.dn_tv_generator;

import java.awt.FontMetrics;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Globally available utility classes, mostly for string manipulation.
 * 
 * @author Jim Menard, <a href="mailto:jimm@io.com">jimm@io.com</a>
 */
public class StringUtils {
  /**
   * Returns an array of strings, one for each line in the string after it has
   * been wrapped to fit lines of <var>maxWidth</var>. Lines end with any of
   * cr, lf, or cr lf. A line ending at the end of the string will not output a
   * further, empty string.
   * <p>
   * This code assumes <var>str</var> is not <code>null</code>.
   * 
   * @param str
   *          the string to split
   * @param fm
   *          needed for string width calculations
   * @param maxWidth
   *          the max line width, in points
   * @return a non-empty list of strings
   */
  public static List wrap(String str, FontMetrics fm, int maxWidth) {
    ArrayList toReturn = new ArrayList(3);
      wrapLineInto(str, toReturn, fm, maxWidth);
      return toReturn;
  }

  /**
   * Given a line of text and font metrics information, wrap the line and add
   * the new line(s) to <var>list</var>.
   * 
   * @param line
   *          a line of text
   * @param list
   *          an output list of strings
   * @param fm
   *          font metrics
   * @param maxWidth
   *          maximum width of the line(s)
   */
  public static void wrapLineInto(String line, List list, FontMetrics fm, int maxWidth) {
    StringBuilder builder = new StringBuilder();
    int length = 0;
    for (char character : line.toCharArray()) {
        builder.append(character);
        length += fm.charWidth(character);
        if (length > maxWidth) {
            String toSplit = builder.toString();
            if (toSplit.lastIndexOf(" ") == -1) {//No whitespace to split on, just toss it on the screen.
                length = 0;
                builder = new StringBuilder();
                if (character != ' ') {
                    builder.append(character);
                }
                list.add(toSplit.substring(0, toSplit.length() - 1));
            } else {
                int lastSpace = toSplit.lastIndexOf(" ");
                list.add(toSplit.subSequence(0, lastSpace));
                length = 0;
                builder = new StringBuilder().append(toSplit.subSequence(lastSpace, toSplit.length()).toString().trim());
            }
        }
    }
    list.add( builder.toString());
     
  }

  /**
   * Returns the index of the first whitespace character or '-' in <var>line</var>
   * that is at or before <var>start</var>. Returns -1 if no such character is
   * found.
   * 
   * @param line
   *          a string
   * @param start
   *          where to star looking
   */
  public static int findBreakBefore(String line, int start) {
    for (int i = start; i >= 0; --i) {
      char c = line.charAt(i);
      if (Character.isWhitespace(c) || c == '-')
        return i;
    }
    return -1;
  }

  /**
   * Returns the index of the first whitespace character or '-' in <var>line</var>
   * that is at or after <var>start</var>. Returns -1 if no such character is
   * found.
   * 
   * @param line
   *          a string
   * @param start
   *          where to star looking
   */
  public static int findBreakAfter(String line, int start) {
    int len = line.length();
    for (int i = start; i < len; ++i) {
      char c = line.charAt(i);
      if (Character.isWhitespace(c) || c == '-')
        return i;
    }
    return -1;
  }
  /**
   * Returns an array of strings, one for each line in the string. Lines end
   * with any of cr, lf, or cr lf. A line ending at the end of the string will
   * not output a further, empty string.
   * <p>
   * This code assumes <var>str</var> is not <code>null</code>.
   * 
   * @param str
   *          the string to split
   * @return a non-empty list of strings
   */
  public static List splitIntoLines(String str) {
    ArrayList strings = new ArrayList();

    int len = str.length();
    if (len == 0) {
      strings.add("");
      return strings;
    }

    int lineStart = 0;

    for (int i = 0; i < len; ++i) {
      char c = str.charAt(i);
      if (c == '\r') {
        int newlineLength = 1;
        if ((i + 1) < len && str.charAt(i + 1) == '\n')
          newlineLength = 2;
        strings.add(str.substring(lineStart, i));
        lineStart = i + newlineLength;
        if (newlineLength == 2) // skip \n next time through loop
          ++i;
      } else if (c == '\n') {
        strings.add(str.substring(lineStart, i));
        lineStart = i + 1;
      }
    }
    if (lineStart < len)
      strings.add(str.substring(lineStart));

    return strings;
  }

}