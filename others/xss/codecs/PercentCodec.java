import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PercentCodec
  implements Codec
{
  private static final Log log = LogFactory.getLog(PercentCodec.class);

  public String encode(String input)
  {
    return null;
  }

  public String encodeCharacter(Character c)
  {
    return null;
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

    if (first.charValue() != '%') {
      input.reset();
      return null;
    }

    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < 2; ++i) {
      Character c = input.nextHex();
      if (c == null) continue; sb.append(c);
    }
    if (sb.length() == 2);
    try
    {
      int ret = Integer.parseInt(sb.toString(), 16);

      return new Character((char)ret);
    }
    catch (NumberFormatException e)
    {
      log.warn("NumberFormatException", e);

      input.reset(); }
    return null;
  }
}
