public abstract interface Encoder
{
  public static final char[] CHAR_LOWERS = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z' };
  public static final char[] CHAR_UPPERS = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };
  public static final char[] CHAR_DIGITS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
  public static final char[] CHAR_SPECIALS = { '.', '-', '_', '!', '@', '$', '^', '*', '=', '~', '|', '+', '?' };
  public static final char[] CHAR_LETTERS = StringUtilities.union(CHAR_LOWERS, CHAR_UPPERS);
  public static final char[] CHAR_ALPHANUMERICS = StringUtilities.union(CHAR_LETTERS, CHAR_DIGITS);

  public static final char[] CHAR_PASSWORD_LOWERS = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'j', 'k', 'm', 'n', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z' };
  public static final char[] CHAR_PASSWORD_UPPERS = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'L', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };
  public static final char[] CHAR_PASSWORD_DIGITS = { '2', '3', '4', '5', '6', '7', '8', '9' };
  public static final char[] CHAR_PASSWORD_SPECIALS = { '_', '.', '!', '@', '$', '*', '=', '-', '?' };
  public static final char[] CHAR_PASSWORD_LETTERS = StringUtilities.union(CHAR_PASSWORD_LOWERS, CHAR_PASSWORD_UPPERS);

  public abstract String normalize(String paramString);

  public abstract String encodeForCSS(String paramString);

  public abstract String encodeForHTML(String paramString);

  public abstract String encodeForHTMLAttribute(String paramString);

  public abstract String encodeForJavaScript(String paramString);

  public abstract String encodeForSQL(Codec paramCodec, String paramString);

  public abstract String encodeForOS(Codec paramCodec, String paramString);

  public abstract String encodeForLDAP(String paramString);

  public abstract String encodeForDN(String paramString);

  public abstract String encodeForXPath(String paramString);

  public abstract String encodeForXML(String paramString);

  public abstract String encodeForXMLAttribute(String paramString);

  public abstract String encodeForURL(String paramString)
    throws EncodingException;

  public abstract String decodeFromURL(String paramString)
    throws EncodingException;
}
