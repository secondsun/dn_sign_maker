package org.devnexus.dn_tv_generator;

import java.awt.FontMetrics;
import java.util.ArrayList;
import java.util.List;


public class StringUtils {

    public static List wrap(String str, FontMetrics fm, int maxWidth) {
    ArrayList toReturn = new ArrayList(3);
      wrapLineInto(str, toReturn, fm, maxWidth);
      return toReturn;
  }

  public static void wrapLineInto(String line, List list, FontMetrics fm, int maxWidth) {
    StringBuilder builder = new StringBuilder();
    int length = 0;
    for (char character : line.toCharArray()) {
        builder.append(character);
        length = fm.stringWidth(builder.toString());
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
    list.add( builder.toString().trim());
     
  }

}