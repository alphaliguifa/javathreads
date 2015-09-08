public class OracleCodec
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
    if (c.charValue() == '\'')
      return "''";
    return "" + c;
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

    if (first.charValue() != '\'') {
      input.reset();
      return null;
    }

    Character second = input.next();
    if (second == null) {
      input.reset();
      return null;
    }

    if (second.charValue() != '\'') {
      input.reset();
      return null;
    }
    return new Character('\'');
  }
}
