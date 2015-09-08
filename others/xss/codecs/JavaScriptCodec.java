public class JavaScriptCodec
  implements Codec
{
  public String encode(String input)
  {
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < input.length(); ++i) {
      char c = input.charAt(i);
      sb.append(encodeCharacter(new Character(c)));
    }
    return sb.toString();
  }

  public String encodeCharacter(Character c)
  {
    char ch = c.charValue();
    if (ch == 0) return "\\0";
    if (ch == '\b') return "\\b";
    if (ch == '\t') return "\\t";
    if (ch == '\n') return "\\n";
    if (ch == '\11') return "\\v";
    if (ch == '\f') return "\\f";
    if (ch == '\r') return "\\r";
    if (ch == '"') return "\\\"";
    if (ch == '\'') return "\\'";
    if (ch == '\\') return "\\\\";

    String temp = Integer.toHexString(ch);
    if (ch <= 256) {
      String pad = "00".substring(temp.length());
      return "\\x" + pad + temp.toUpperCase();
    }

    String pad = "0000".substring(temp.length());
    return "\\u" + pad + temp.toUpperCase();
  }

  public String decode(String input)
  {
    StringBuffer sb = new StringBuffer();
    PushbackString pbs = new PushbackString(input);
    while (pbs.hasNext()) {
      Character c = decodeCharacter(pbs);
      if (c != null)
        sb.append(c);
      else {
        sb.append(pbs.next());
      }
    }
    return sb.toString();
  }

  public Character decodeCharacter(PushbackString input)
  {
    input.mark();
    Character first = input.next();
    if (first == null) {
      input.reset();
      return null;
    }

    if (first.charValue() != '\\') {
      input.reset();
      return null;
    }

    Character second = input.next();
    if (second == null) {
      input.reset();
      return null;
    }

    if (second.charValue() == '0')
      return new Character('\0');
    if (second.charValue() == 'b')
      return new Character('\b');
    if (second.charValue() == 't')
      return new Character('\t');
    if (second.charValue() == 'n')
      return new Character('\n');
    if (second.charValue() == 'v')
      return new Character('\11');
    if (second.charValue() == 'f')
      return new Character('\f');
    if (second.charValue() == 'r')
      return new Character('\r');
    if (second.charValue() == '"')
      return new Character('"');
    if (second.charValue() == '\'')
      return new Character('\'');
    if (second.charValue() == '\\') {
      return new Character('\\');
    }

    StringBuffer sb;
    int i;
    Character c;
    if (Character.toLowerCase(second.charValue()) == 'x')
    {
      sb = new StringBuffer();
      for (i = 0; i < 2; ++i) {
        c = input.nextHex();
        if (c == null) continue; sb.append(c);
      }
      if (sb.length() == 2) {
        try
        {
          i = Integer.parseInt(sb.toString(), 16);

          return new Character((char)i);
        }
        catch (NumberFormatException e) {
          input.reset();
          return null;
        }
      }

    }
    else if (Character.toLowerCase(second.charValue()) == 'u')
    {
      sb = new StringBuffer();
      for (int e = 0; e < 4; ++e) {
        c = input.nextHex();
        if (c == null) continue; sb.append(c);
      }
      if (sb.length() == 4) {
        try
        {
          int ret = Integer.parseInt(sb.toString(), 16);

          return new Character((char)ret);
        }
        catch (NumberFormatException e) {
          input.reset();
          return null;
        }
      }

    }

    return second;
  }
}
