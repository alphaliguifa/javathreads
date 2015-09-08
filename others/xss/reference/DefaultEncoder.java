import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class DefaultEncoder
  implements Encoder
{
  List codecs = new ArrayList();
  private HTMLEntityCodec htmlCodec = new HTMLEntityCodec();
  private PercentCodec percentCodec = new PercentCodec();
  private JavaScriptCodec javaScriptCodec = new JavaScriptCodec();
  private CSSCodec cssCodec = new CSSCodec();

//  private static final Logger.Module MOD = new Logger.Module("JMZ.WB");
//  private static final Logger LOG = Logger.getLogger(DefaultEncoder.class, MOD);

  private static final char[] IMMUNE_HTML = { ',', '.', '-', '_', ' ' };
  private static final char[] IMMUNE_HTMLATTR = { ',', '.', '-', '_' };
  private static final char[] IMMUNE_CSS = { ' ' };
  private static final char[] IMMUNE_JAVASCRIPT = { ',', '.', '-', '_', ' ' };
  private static final char[] IMMUNE_VBSCRIPT = { ' ' };
  private static final char[] IMMUNE_XML = { ',', '.', '-', '_', ' ' };
  private static final char[] IMMUNE_SQL = { ' ' };
  private static final char[] IMMUNE_OS = { '-' };
  private static final char[] IMMUNE_XMLATTR = { ',', '.', '-', '_' };
  private static final char[] IMMUNE_XPATH = { ',', '.', '-', '_', ' ' };

  public DefaultEncoder()
  {
    this.codecs.add(this.htmlCodec);
    this.codecs.add(this.percentCodec);
    this.codecs.add(this.javaScriptCodec);
  }

  public DefaultEncoder(List codecs)
  {
    Iterator i = codecs.iterator();
    while (i.hasNext()) {
      Object o = i.next();
      if (!(o instanceof Codec)) {
        throw new IllegalArgumentException("Codec list must contain only Codec instances");
      }
    }
    this.codecs = codecs;
  }

  public String canonicalize(String input) {
    if (input == null) return null;
    return canonicalize(input, true);
  }

  public String canonicalize(String input, boolean strict) {
    if (input == null) return null;
    String candidate = canonicalizeOnce(input);
    String canary = canonicalizeOnce(candidate);
    if (!(candidate.equals(canary))) {
      if (strict) {
        throw new IntrusionException("Input validation failure", "Double encoding detected in " + input);
      }
//      LOG.warn("canonicalize", "Double encoding detected in " + input);
    }

    return candidate;
  }

  private String canonicalizeOnce(String input)
  {
    if (input == null) return null;
    StringBuffer sb = new StringBuffer();
    PushbackString pbs = new PushbackString(input);
    while (pbs.hasNext())
    {
      boolean encoded = decodeNext(pbs);

      Character ch = pbs.next();

      if (encoded)
        pbs.pushback(ch);
      else {
        sb.append(ch);
      }
    }
    return sb.toString();
  }

  private boolean decodeNext(PushbackString pbs)
  {
    Iterator i = this.codecs.iterator();
    pbs.mark();
    while (i.hasNext()) {
      pbs.reset();
      Codec codec = (Codec)i.next();
      Character decoded = codec.decodeCharacter(pbs);
      if (decoded != null) {
        pbs.pushback(decoded);
        return true;
      }
    }
    pbs.reset();
    return false;
  }

  public String normalize(String input)
  {
    String separated = Normalizer.normalize(input, Normalizer.DECOMP, 0);

    return Pattern.compile("[^\\p{ASCII}]").matcher(separated).replaceAll("");
  }

  private String encode(char c, Codec codec, char[] baseImmune, char[] specialImmune)
  {
    if ((isContained(baseImmune, c)) || (isContained(specialImmune, c))) {
      return "" + c;
    }
    return codec.encodeCharacter(new Character(c));
  }

  public String encodeForHTML(String input)
  {
    if (input == null) return null;
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < input.length(); ++i) {
      char c = input.charAt(i);
      if ((c == '\t') || (c == '\n') || (c == '\r')) {
        sb.append(c);
      } else if ((c <= '\31') || ((c >= '') && (c <= 159))) {
//        LOG.warn("encodeForHTML", "Attempt to HTML entity encode illegal character: " + c + " (skipping)");
        sb.append(' ');
      } else {
        sb.append(encode(c, this.htmlCodec, CHAR_ALPHANUMERICS, IMMUNE_HTML));
      }
    }
    return sb.toString();
  }

  public String encodeForHTMLAttribute(String input)
  {
    if (input == null) return null;
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < input.length(); ++i) {
      char c = input.charAt(i);
      sb.append(encode(c, this.htmlCodec, CHAR_ALPHANUMERICS, IMMUNE_HTMLATTR));
    }
    return sb.toString();
  }

  public String encodeForCSS(String input)
  {
    if (input == null) return null;
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < input.length(); ++i) {
      char c = input.charAt(i);
      if (c != 0) {
        sb.append(encode(c, this.cssCodec, CHAR_ALPHANUMERICS, IMMUNE_CSS));
      }
    }
    return sb.toString();
  }

  public String encodeForJavaScript(String input)
  {
    if (input == null) return null;
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < input.length(); ++i) {
      char c = input.charAt(i);
      sb.append(encode(c, this.javaScriptCodec, CHAR_ALPHANUMERICS, IMMUNE_JAVASCRIPT));
    }
    return sb.toString();
  }

  public String encodeForSQL(Codec codec, String input)
  {
    if (input == null) return null;
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < input.length(); ++i) {
      char c = input.charAt(i);
      sb.append(encode(c, codec, CHAR_ALPHANUMERICS, IMMUNE_SQL));
    }
    return sb.toString();
  }

  public String encodeForOS(Codec codec, String input)
  {
    if (input == null) return null;
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < input.length(); ++i) {
      char c = input.charAt(i);
      sb.append(encode(c, codec, CHAR_ALPHANUMERICS, IMMUNE_OS));
    }
    return sb.toString();
  }

  public String encodeForLDAP(String input)
  {
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < input.length(); ++i) {
      char c = input.charAt(i);
      switch (c)
      {
      case '\\':
        sb.append("\\5c");
        break;
      case '*':
        sb.append("\\2a");
        break;
      case '(':
        sb.append("\\28");
        break;
      case ')':
        sb.append("\\29");
        break;
      case '\0':
        sb.append("\\00");
        break;
      default:
        sb.append(c);
      }
    }
    return sb.toString();
  }

  public String encodeForDN(String input)
  {
    StringBuffer sb = new StringBuffer();
    if ((input.length() > 0) && (((input.charAt(0) == ' ') || (input.charAt(0) == '#')))) {
      sb.append('\\');
    }
    for (int i = 0; i < input.length(); ++i) {
      char c = input.charAt(i);
      switch (c)
      {
      case '\\':
        sb.append("\\\\");
        break;
      case ',':
        sb.append("\\,");
        break;
      case '+':
        sb.append("\\+");
        break;
      case '"':
        sb.append("\\\"");
        break;
      case '<':
        sb.append("\\<");
        break;
      case '>':
        sb.append("\\>");
        break;
      case ';':
        sb.append("\\;");
        break;
      default:
        sb.append(c);
      }
    }

    if ((input.length() > 1) && (input.charAt(input.length() - 1) == ' ')) {
      sb.insert(sb.length() - 1, '\\');
    }
    return sb.toString();
  }

  public String encodeForXPath(String input)
  {
    if (input == null) return null;
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < input.length(); ++i) {
      char c = input.charAt(i);
      sb.append(encode(c, this.htmlCodec, CHAR_ALPHANUMERICS, IMMUNE_XPATH));
    }
    return sb.toString();
  }

  public String encodeForXML(String input)
  {
    if (input == null) return null;
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < input.length(); ++i) {
      char c = input.charAt(i);
      sb.append(encode(c, this.htmlCodec, CHAR_ALPHANUMERICS, IMMUNE_XML));
    }
    return sb.toString();
  }

  public String encodeForXMLAttribute(String input)
  {
    if (input == null) return null;
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < input.length(); ++i) {
      char c = input.charAt(i);
      sb.append(encode(c, this.htmlCodec, CHAR_ALPHANUMERICS, IMMUNE_XMLATTR));
    }
    return sb.toString();
  }

  public String encodeForURL(String input)
    throws EncodingException
  {
    try
    {
      return URLEncoder.encode(input);
    } catch (Exception e) {
      throw new EncodingException("Encoding failure", "Problem URL decoding input", e);
    }
  }

  public String decodeFromURL(String input)
    throws EncodingException
  {
    String canonical = canonicalize(input);
    try {
      return URLDecoder.decode(canonical);
    } catch (Exception e) {
      throw new EncodingException("Decoding failed", "Problem URL decoding input", e);
    }
  }

  protected boolean isContained(char[] haystack, char c)
  {
    for (int i = 0; i < haystack.length; ++i) {
      if (c == haystack[i])
        return true;
    }
    return false;
  }

  static
  {
    Arrays.sort(IMMUNE_HTML);
    Arrays.sort(IMMUNE_HTMLATTR);
    Arrays.sort(IMMUNE_JAVASCRIPT);
    Arrays.sort(IMMUNE_VBSCRIPT);
    Arrays.sort(IMMUNE_XML);
    Arrays.sort(IMMUNE_XMLATTR);
    Arrays.sort(IMMUNE_XPATH);
    Arrays.sort(CHAR_LOWERS);
    Arrays.sort(CHAR_UPPERS);
    Arrays.sort(CHAR_DIGITS);
    Arrays.sort(CHAR_SPECIALS);
    Arrays.sort(CHAR_LETTERS);
    Arrays.sort(CHAR_ALPHANUMERICS);
    Arrays.sort(CHAR_PASSWORD_LOWERS);
    Arrays.sort(CHAR_PASSWORD_UPPERS);
    Arrays.sort(CHAR_PASSWORD_DIGITS);
    Arrays.sort(CHAR_PASSWORD_SPECIALS);
    Arrays.sort(CHAR_PASSWORD_LETTERS);
  }
}
