import java.util.HashMap;

public class HTMLEntityCodec
  implements Codec
{
  private static HashMap characterToEntityMap;
  private static HashMap entityToCharacterMap;

  public HTMLEntityCodec()
  {
    initializeMaps();
  }

  public String encode(String input)
  {
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < input.length(); ++i) {
      sb.append(encodeCharacter(new Character(input.charAt(i))));
    }
    return sb.toString();
  }

  public String encodeCharacter(Character c)
  {
    String entityName = (String)characterToEntityMap.get(c);
    if (entityName != null) {
      return "&" + entityName + ";";
    }
    return "&#" + c.charValue() + ";";
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

    if (first.charValue() != '&') {
      input.reset();
      return null;
    }

    Character second = input.next();
    if (second == null) {
      input.reset();
      return null;
    }
    Character c;
    if (second.charValue() == '#')
    {
      c = getNumericEntity(input);
      if (c != null) return c;
    } else if (Character.isLetter(second.charValue()))
    {
      input.pushback(second);
      c = getNamedEntity(input);
      if (c != null) return c;
    }
    input.reset();
    return null;
  }

  private Character getNumericEntity(PushbackString input)
  {
    Character first = input.peek();
    if (first == null) return null;

    if ((first.charValue() == 'x') || (first.charValue() == 'X')) {
      input.next();
      return parseHex(input);
    }
    return parseNumber(input);
  }

  private Character parseNumber(PushbackString input)
  {
    StringBuffer sb = new StringBuffer();
    while (input.hasNext()) {
      Character c = input.peek();

      if (Character.isDigit(c.charValue())) {
        sb.append(c);
        input.next();
      }
      else {
        if (c.charValue() != ';') break;
        input.next();
        break;
      }

    }

    try
    {
      int i = Integer.parseInt(sb.toString());

      return new Character((char)i);
    } catch (NumberFormatException e) {
    }
    return null;
  }

  private Character parseHex(PushbackString input)
  {
    StringBuffer sb = new StringBuffer();
    while (input.hasNext()) {
      Character c = input.peek();

      if ("0123456789ABCDEFabcdef".indexOf(c.charValue()) != -1) {
        sb.append(c);
        input.next();
      }
      else {
        if (c.charValue() != ';') break;
        input.next();
        break;
      }

    }

    try
    {
      int i = Integer.parseInt(sb.toString(), 16);

      return new Character((char)i);
    } catch (NumberFormatException e) {
    }
    return null;
  }

  private Character getNamedEntity(PushbackString input)
  {
    StringBuffer possible = new StringBuffer();
    int len = Math.min(input.remainder().length(), 7);
    for (int i = 0; i < len; ++i) {
      possible.append(Character.toLowerCase(input.next().charValue()));

      Character entity = (Character)entityToCharacterMap.get(possible.toString());
      if (entity == null)
        continue;
      if (input.peek(';')) {
        input.next();
      }
      return entity;
    }

    return null;
  }

  private synchronized void initializeMaps()
  {
    if ((characterToEntityMap != null) && (entityToCharacterMap != null)) {
      return;
    }
    String[] entityNames = { "quot", "amp", "lt", "gt", "nbsp", "iexcl", "cent", "pound", "curren", "yen", "brvbar", "sect", "uml", "copy", "ordf", "laquo", "not", "shy", "reg", "macr", "deg", "plusmn", "sup2", "sup3", "acute", "micro", "para", "middot", "cedil", "sup1", "ordm", "raquo", "frac14", "frac12", "frac34", "iquest", "Agrave", "Aacute", "Acirc", "Atilde", "Auml", "Aring", "AElig", "Ccedil", "Egrave", "Eacute", "Ecirc", "Euml", "Igrave", "Iacute", "Icirc", "Iuml", "ETH", "Ntilde", "Ograve", "Oacute", "Ocirc", "Otilde", "Ouml", "times", "Oslash", "Ugrave", "Uacute", "Ucirc", "Uuml", "Yacute", "THORN", "szlig", "agrave", "aacute", "acirc", "atilde", "auml", "aring", "aelig", "ccedil", "egrave", "eacute", "ecirc", "euml", "igrave", "iacute", "icirc", "iuml", "eth", "ntilde", "ograve", "oacute", "ocirc", "otilde", "ouml", "divide", "oslash", "ugrave", "uacute", "ucirc", "uuml", "yacute", "thorn", "yuml", "OElig", "oelig", "Scaron", "scaron", "Yuml", "fnof", "circ", "tilde", "Alpha", "Beta", "Gamma", "Delta", "Epsilon", "Zeta", "Eta", "Theta", "Iota", "Kappa", "Lambda", "Mu", "Nu", "Xi", "Omicron", "Pi", "Rho", "Sigma", "Tau", "Upsilon", "Phi", "Chi", "Psi", "Omega", "alpha", "beta", "gamma", "delta", "epsilon", "zeta", "eta", "theta", "iota", "kappa", "lambda", "mu", "nu", "xi", "omicron", "pi", "rho", "sigmaf", "sigma", "tau", "upsilon", "phi", "chi", "psi", "omega", "thetasym", "upsih", "piv", "ensp", "emsp", "thinsp", "zwnj", "zwj", "lrm", "rlm", "ndash", "mdash", "lsquo", "rsquo", "sbquo", "ldquo", "rdquo", "bdquo", "dagger", "Dagger", "bull", "hellip", "permil", "prime", "Prime", "lsaquo", "rsaquo", "oline", "frasl", "euro", "image", "weierp", "real", "trade", "alefsym", "larr", "uarr", "rarr", "darr", "harr", "crarr", "lArr", "uArr", "rArr", "dArr", "hArr", "forall", "part", "exist", "empty", "nabla", "isin", "notin", "ni", "prod", "sum", "minus", "lowast", "radic", "prop", "infin", "ang", "and", "or", "cap", "cup", "int", "there4", "sim", "cong", "asymp", "ne", "equiv", "le", "ge", "sub", "sup", "nsub", "sube", "supe", "oplus", "otimes", "perp", "sdot", "lceil", "rceil", "lfloor", "rfloor", "lang", "rang", "loz", "spades", "clubs", "hearts", "diams" };

    char[] entityValues = { '"', '&', '<', '>', 160, 161, 162, 163, 164, 165, 166, 167, 168, 169, 170, 171, 172, 173, 174, 175, 176, 177, 178, 179, 180, 181, 182, 183, 184, 185, 186, 187, 188, 189, 190, 191, 192, 193, 194, 195, 196, 197, 198, 199, 200, 201, 202, 203, 204, 205, 206, 207, 208, 209, 210, 211, 212, 213, 214, 215, 216, 217, 218, 219, 220, 221, 222, 223, 224, 225, 226, 227, 228, 229, 230, 231, 232, 233, 234, 235, 236, 237, 238, 239, 240, 241, 242, 243, 244, 245, 246, 247, 248, 249, 250, 251, 252, 253, 254, 255, 338, 339, 352, 353, 376, 402, 710, 732, 913, 914, 915, 916, 917, 918, 919, 920, 921, 922, 923, 924, 925, 926, 927, 928, 929, 931, 932, 933, 934, 935, 936, 937, 945, 946, 947, 948, 949, 950, 951, 952, 953, 954, 955, 956, 957, 958, 959, 960, 961, 962, 963, 964, 965, 966, 967, 968, 969, 977, 978, 982, 8194, 8195, 8201, 8204, 8205, 8206, 8207, 8211, 8212, 8216, 8217, 8218, 8220, 8221, 8222, 8224, 8225, 8226, 8230, 8240, 8242, 8243, 8249, 8250, 8254, 8260, 8364, 8465, 8472, 8476, 8482, 8501, 8592, 8593, 8594, 8595, 8596, 8629, 8656, 8657, 8658, 8659, 8660, 8704, 8706, 8707, 8709, 8711, 8712, 8713, 8715, 8719, 8721, 8722, 8727, 8730, 8733, 8734, 8736, 8743, 8744, 8745, 8746, 8747, 8756, 8764, 8773, 8776, 8800, 8801, 8804, 8805, 8834, 8835, 8836, 8838, 8839, 8853, 8855, 8869, 8901, 8968, 8969, 8970, 8971, 9001, 9002, 9674, 9824, 9827, 9829, 9830 };

    characterToEntityMap = new HashMap(entityNames.length);
    entityToCharacterMap = new HashMap(entityValues.length);
    for (int i = 0; i < entityNames.length; ++i) {
      String e = entityNames[i];
      Character c = new Character(entityValues[i]);
      entityToCharacterMap.put(e, c);
      characterToEntityMap.put(c, e);
    }
  }
}