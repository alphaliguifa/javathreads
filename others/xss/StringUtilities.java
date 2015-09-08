import java.util.Arrays;

public class StringUtilities
{
  public static String stripControls(String input)
  {
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < input.length(); ++i) {
      char c = input.charAt(i);
      if ((c > ' ') && (c < ''))
        sb.append(c);
      else {
        sb.append(' ');
      }
    }
    return sb.toString();
  }

  public static char[] union(char[] c1, char[] c2)
  {
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < c1.length; ++i) {
      if (!(contains(sb, c1[i])))
        sb.append(c1[i]);
    }
    for (int i = 0; i < c2.length; ++i) {
      if (!(contains(sb, c2[i])))
        sb.append(c2[i]);
    }
    char[] c3 = new char[sb.length()];
    sb.getChars(0, sb.length(), c3, 0);
    Arrays.sort(c3);
    return c3;
  }

  public static boolean contains(StringBuffer haystack, char c)
  {
    for (int i = 0; i < haystack.length(); ++i) {
      if (haystack.charAt(i) == c)
        return true;
    }
    return false;
  }
}
