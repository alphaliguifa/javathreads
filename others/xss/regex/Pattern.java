import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class Pattern
  implements Serializable
{
  public static final int UNIX_LINES = 1;
  public static final int CASE_INSENSITIVE = 2;
  public static final int COMMENTS = 4;
  public static final int MULTILINE = 8;
  public static final int DOTALL = 32;
  public static final int UNICODE_CASE = 64;
  public static final int CANON_EQ = 128;
  private static final long serialVersionUID = 5073258162644648461L;
  private String pattern;
  private int flags;
  private transient String normalizedPattern;
  transient Node root;
  transient Node matchRoot;
  transient char[] buffer;
  transient GroupHead[] groupNodes;
  private transient char[] temp;
  transient int groupCount;
  transient int localCount;
  private transient int cursor;
  private transient int patternLength;
  static final int MAX_REPS = 2147483647;
  static final int GREEDY = 0;
  static final int LAZY = 1;
  static final int POSSESSIVE = 2;
  static final int INDEPENDENT = 3;
  static Node accept = new Node();

  static Node lastAccept = new LastNode();

  static HashMap families = null;

  static HashMap categories = null;

  private static final String[] familyNames = { "BasicLatin", "Latin-1Supplement", "LatinExtended-A", "LatinExtended-Bound", "IPAExtensions", "SpacingModifierLetters", "CombiningDiacriticalMarks", "Greek", "Cyrillic", "Armenian", "Hebrew", "Arabic", "Syriac", "Thaana", "Devanagari", "Bengali", "Gurmukhi", "Gujarati", "Oriya", "Tamil", "Telugu", "Kannada", "Malayalam", "Sinhala", "Thai", "Lao", "Tibetan", "Myanmar", "Georgian", "HangulJamo", "Ethiopic", "Cherokee", "UnifiedCanadianAboriginalSyllabics", "Ogham", "Runic", "Khmer", "Mongolian", "LatinExtendedAdditional", "GreekExtended", "GeneralPunctuation", "SuperscriptsandSubscripts", "CurrencySymbols", "CombiningMarksforSymbols", "LetterlikeSymbols", "NumberForms", "Arrows", "MathematicalOperators", "MiscellaneousTechnical", "ControlPictures", "OpticalCharacterRecognition", "EnclosedAlphanumerics", "BoxDrawing", "BlockElements", "GeometricShapes", "MiscellaneousSymbols", "Dingbats", "BraillePatterns", "CJKRadicalsSupplement", "KangxiRadicals", "IdeographicDescriptionCharacters", "CJKSymbolsandPunctuation", "Hiragana", "Katakana", "Bopomofo", "HangulCompatibilityJamo", "Kanbun", "BopomofoExtended", "EnclosedCJKLettersandMonths", "CJKCompatibility", "CJKUnifiedIdeographsExtensionA", "CJKUnifiedIdeographs", "YiSyllables", "YiRadicals", "HangulSyllables", "HighSurrogates", "HighPrivateUseSurrogates", "LowSurrogates", "PrivateUse", "CJKCompatibilityIdeographs", "AlphabeticPresentationForms", "ArabicPresentationForms-A", "CombiningHalfMarks", "CJKCompatibilityForms", "SmallFormVariants", "ArabicPresentationForms-Bound", "Specials", "HalfwidthandFullwidthForms" };

  private static final String[] categoryNames = { "Cn", "Lu", "Ll", "Lt", "Lm", "Lo", "Mn", "Me", "Mc", "Nd", "Nl", "No", "Zs", "Zl", "Zp", "Cc", "Cf", "Co", "Cs", "Pd", "Ps", "Pe", "Pc", "Po", "Sm", "Sc", "Sk", "So", "L", "M", "N", "Z", "C", "P", "S", "LD", "L1", "all", "ASCII", "Alnum", "Alpha", "Blank", "Cntrl", "Digit", "Graph", "Lower", "Print", "Punct", "Space", "Upper", "XDigit" };

  private static final Node[] familyNodes = { new Range(127), new Range(8388863), new Range(16777599), new Range(25166415), new Range(38797999), new Range(45089535), new Range(50332527), new Range(57672703), new Range(67110143), new Range(87033231), new Range(93324799), new Range(100665087), new Range(117442383), new Range(125831103), new Range(150997375), new Range(159386111), new Range(167774847), new Range(176163583), new Range(184552319), new Range(192941055), new Range(201329791), new Range(209718527), new Range(218107263), new Range(226495999), new Range(234884735), new Range(243273471), new Range(251662335), new Range(268439711), new Range(278925567), new Range(285217279), new Range(301994879), new Range(329257983), new Range(335550079), new Range(377493151), new Range(379590399), new Range(394270719), new Range(402659503), new Range(503324415), new Range(520101887), new Range(536879215), new Range(544219295), new Range(547365071), new Range(550510847), new Range(553656655), new Range(558899599), new Range(563094015), new Range(570434303), new Range(587211775), new Range(603989055), new Range(608183391), new Range(610280703), new Range(620766591), new Range(629155231), new Range(631252479), new Range(637544191), new Range(654321599), new Range(671099135), new Range(780152575), new Range(788541407), new Range(804270079), new Range(805318719), new Range(809513119), new Range(815804671), new Range(822096175), new Range(825241999), new Range(831533471), new Range(832582079), new Range(838873855), new Range(855651327), new Range(872435125), new Range(1308663807), new Range(-1610570609), new Range(-1534024497), new Range(-1409230941), new Range(-671032449), new Range(-612312065), new Range(-603922433), new Range(-536807169), new Range(-117376257), new Range(-83821745), new Range(-78578177), new Range(-31392209), new Range(-30343601), new Range(-28246417), new Range(-26149122), new Specials(), new Range(-16711697) };

  private static final Node[] categoryNodes = { new Category(1), new Category(2), new Category(4), new Category(8), new Category(16), new Category(32), new Category(64), new Category(128), new Category(256), new Category(512), new Category(1024), new Category(2048), new Category(4096), new Category(8192), new Category(16384), new Category(32768), new Category(65536), new Category(262144), new Category(524288), new Category(1048576), new Category(2097152), new Category(4194304), new Category(8388608), new Category(16777216), new Category(33554432), new Category(67108864), new Category(134217728), new Category(268435456), new Category(62), new Category(448), new Category(3584), new Category(28672), new Category(884736), new Category(32505856), new Category(503316480), new Category(574), new Range(255), new All(), new Range(127), new Ctype(1792), new Ctype(768), new Ctype(16384), new Ctype(8192), new Range(3145785), new Ctype(5888), new Range(6357114), new Range(2097278), new Ctype(4096), new Ctype(2048), new Range(4259930), new Ctype(32768) };

  public static Pattern compile(String regex)
  {
    return new Pattern(regex, 0);
  }

  public static Pattern compile(String regex, int flags)
  {
    return new Pattern(regex, flags);
  }

  public String pattern()
  {
    return this.pattern;
  }

  public Matcher matcher(String input)
  {
    Matcher m = new Matcher(this, input);
    return m;
  }

  public int flags()
  {
    return this.flags;
  }

  public static boolean matches(String regex, String input)
  {
    Pattern p = compile(regex);
    Matcher m = p.matcher(input);
    return m.matches();
  }

  public String[] split(String input, int limit)
  {
    int index = 0;
    boolean matchLimited = limit > 0;
    ArrayList matchList = new ArrayList();
    Matcher m = matcher(input);

    while (m.find()) {
      if ((!(matchLimited)) || (matchList.size() < limit - 1)) {
        match = input.substring(index, m.start()).toString();
        matchList.add(match);
        index = m.end(); }
      if (matchList.size() == limit - 1);
      String match = input.substring(index, input.length()).toString();

      matchList.add(match);
      index = m.end();
    }

    if (index == 0) {
      return new String[] { input.toString() };
    }

    if ((!(matchLimited)) || (matchList.size() < limit)) {
      matchList.add(input.substring(index, input.length()).toString());
    }

    int resultSize = matchList.size();
    while ((limit == 0) && 
      (resultSize > 0) && (matchList.get(resultSize - 1).equals("")))
      --resultSize;
    String[] result = new String[resultSize];
    return ((String[])(String[])matchList.subList(0, resultSize).toArray(result));
  }

  public String[] split(String input)
  {
    return split(input, 0);
  }

  private void readObject(ObjectInputStream s)
    throws IOException, ClassNotFoundException
  {
    s.defaultReadObject();

    this.groupCount = 1;
    this.localCount = 0;

    if (this.pattern.length() > 0)
      compile();
    else
      this.root = new Start(lastAccept);
  }

  private Pattern(String p, int f)
  {
    this.pattern = p;
    this.flags = f;

    this.groupCount = 1;
    this.localCount = 0;

    if (this.pattern.length() > 0) {
      compile();
    } else {
      this.root = new Start(lastAccept);
      this.matchRoot = lastAccept;
    }
  }

  private void normalize()
  {
    boolean inCharClass = false;
    char lastChar = 65535;

    this.normalizedPattern = Normalizer.decompose(this.pattern, false, 0);
    this.patternLength = this.normalizedPattern.length();

    StringBuffer newPattern = new StringBuffer(this.patternLength);
    for (int i = 0; i < this.patternLength; ++i) {
      char c = this.normalizedPattern.charAt(i);

      if ((Character.getType(c) == 6) && (lastChar != 65535))
      {
        StringBuffer sequenceBuffer = new StringBuffer();
        sequenceBuffer.append(lastChar);
        sequenceBuffer.append(c);
        while (Character.getType(c) == 6) {
          ++i;
          if (i >= this.patternLength)
            break;
          c = this.normalizedPattern.charAt(i);
          sequenceBuffer.append(c);
        }
        String ea = produceEquivalentAlternation(sequenceBuffer.toString());

        newPattern.setLength(newPattern.length() - 1);
        newPattern.append("(?:").append(ea).append(")");
      } else if ((c == '[') && (lastChar != '\\')) {
        i = normalizeCharClass(newPattern, i);
      } else {
        newPattern.append(c);
      }
      lastChar = c;
    }
    this.normalizedPattern = newPattern.toString();
  }

  private int normalizeCharClass(StringBuffer newPattern, int i)
  {
    StringBuffer charClass = new StringBuffer();
    StringBuffer eq = null;
    char lastChar = 65535;

    ++i;
    charClass.append("[");
    while (true) {
      char c = this.normalizedPattern.charAt(i);

      if ((c == ']') && (lastChar != '\\')) {
        charClass.append(c);
        break; }
      if (Character.getType(c) == 6) {
        StringBuffer sequenceBuffer = new StringBuffer();
        sequenceBuffer.append(lastChar);
        while (Character.getType(c) == 6) {
          sequenceBuffer.append(c);
          ++i;
          if (i >= this.normalizedPattern.length())
            break;
          c = this.normalizedPattern.charAt(i);
        }
        String ea = produceEquivalentAlternation(sequenceBuffer.toString());

        charClass.setLength(charClass.length() - 1);
        if (eq == null)
          eq = new StringBuffer();
        eq.append('|');
        eq.append(ea);
      } else {
        charClass.append(c);
        ++i;
      }
      if (i == this.normalizedPattern.length())
        error("Unclosed character class");
      lastChar = c;
    }
    String result;
    if (eq != null) {
      result = new String("(?:" + charClass.toString() + eq.toString() + ")");
    }
    else {
      result = charClass.toString();
    }

    newPattern.append(result);
    return i;
  }

  private String produceEquivalentAlternation(String source)
  {
    if (source.length() == 1) {
      return new String(source);
    }
    String base = source.substring(0, 1);
    String combiningMarks = source.substring(1);

    String[] perms = producePermutations(combiningMarks);
    StringBuffer result = new StringBuffer(source);

    for (int x = 0; x < perms.length; ++x) {
      String next = base + perms[x];
      if (x > 0)
        result.append("|" + next);
      next = composeOneStep(next);
      if (next != null)
        result.append("|" + produceEquivalentAlternation(next));
    }
    return result.toString();
  }

  private String[] producePermutations(String input)
  {
    if (input.length() == 1) {
      return new String[] { input };
    }
    if (input.length() == 2) {
      if (getClass(input.charAt(1)) == getClass(input.charAt(0)))
      {
        return new String[] { input };
      }
      String[] result = new String[2];
      result[0] = input;
      StringBuffer sb = new StringBuffer(2);
      sb.append(input.charAt(1));
      sb.append(input.charAt(0));
      result[1] = sb.toString();
      return result;
    }

    int length = 1;
    for (int x = 1; x < input.length(); ++x) {
      length *= (x + 1);
    }
    String[] temp = new String[length];

    int[] combClass = new int[input.length()];
    for (int x = 0; x < input.length(); ++x) {
      combClass[x] = getClass(input.charAt(x));
    }

    int index = 0;
    for (int x = 0; x < input.length(); ++x) {
      boolean skip = false;
      for (int y = x - 1; y >= 0; --y) {
        if (combClass[y] == combClass[x]) {
          break label316;
        }
      }
      StringBuffer sb = new StringBuffer(input);
      String otherChars = sb.delete(x, x + 1).toString();
      String[] subResult = producePermutations(otherChars);

      String prefix = input.substring(x, x + 1);
      label316: for (int y = 0; y < subResult.length; ++y)
        temp[(index++)] = prefix + subResult[y];
    }
    String[] result = new String[index];
    for (int x = 0; x < index; ++x)
      result[x] = temp[x];
    return result;
  }

  private int getClass(char c) {
    return Normalizer.getClass(c);
  }

  private String composeOneStep(String input)
  {
    String firstTwoChars = input.substring(0, 2);
    String result = Normalizer.compose(firstTwoChars, false, 0);

    if (result.equals(firstTwoChars)) {
      return null;
    }
    String remainder = input.substring(2);
    return result + remainder;
  }

  private void compile()
  {
    if (has(128))
      normalize();
    else {
      this.normalizedPattern = this.pattern;
    }

    this.patternLength = this.normalizedPattern.length();
    this.temp = new char[this.patternLength + 2];

    this.normalizedPattern.getChars(0, this.patternLength, this.temp, 0);
    this.temp[this.patternLength] = '\0';
    this.temp[(this.patternLength + 1)] = '\0';

    this.buffer = new char[32];
    this.groupNodes = new GroupHead[10];

    this.matchRoot = expr(lastAccept);

    if (this.patternLength != this.cursor) {
      if (peek() == 41)
        error("Unmatched closing ')'");
      else {
        error("Unexpected internal error");
      }

    }

    if (this.matchRoot instanceof Slice) {
      this.root = BnM.optimize(this.matchRoot);
      if (this.root == this.matchRoot)
        this.root = new Start(this.matchRoot);
    }
    else if ((this.matchRoot instanceof Begin) || (this.matchRoot instanceof First))
    {
      this.root = this.matchRoot;
    } else {
      this.root = new Start(this.matchRoot);
    }

    this.temp = null;
    this.buffer = null;
    this.groupNodes = null;
    this.patternLength = 0;
  }

  private static void printObjectTree(Node node)
  {
    while (node != null) {
      do { if (node instanceof Prolog) {
          System.out.println(node);
          printObjectTree(((Prolog)node).loop);
          System.out.println("**** end contents prolog loop");
        } else if (node instanceof Loop) {
          System.out.println(node);
          printObjectTree(((Loop)node).body);
          System.out.println("**** end contents Loop body");
        } else if (node instanceof Curly) {
          System.out.println(node);
          printObjectTree(((Curly)node).atom);
          System.out.println("**** end contents Curly body"); } else {
          if (node instanceof GroupTail) {
            System.out.println(node);
            System.out.println("Tail next is " + node.next);
            return;
          }
          System.out.println(node);
        }
        node = node.next;
        if (node != null)
          System.out.println("->next:"); 
      }
      while (node != accept);
      System.out.println("Accept Node");
      node = null;
    }
  }

  private boolean has(int f)
  {
    return ((this.flags & f) > 0);
  }

  private void accept(int ch, String s)
  {
    int testChar = this.temp[(this.cursor++)];
    if (has(4))
      testChar = parsePastWhitespace(testChar);
    if (ch != testChar)
      error(s);
  }

  private void mark(char c)
  {
    this.temp[this.patternLength] = c;
  }

  private int peek()
  {
    int ch = this.temp[this.cursor];
    if (has(4))
      ch = peekPastWhitespace(ch);
    return ch;
  }

  private int read()
  {
    int ch = this.temp[(this.cursor++)];
    if (has(4))
      ch = parsePastWhitespace(ch);
    return ch;
  }

  private int readEscaped()
  {
    int ch = this.temp[(this.cursor++)];
    return ch;
  }

  private int next()
  {
    int ch = this.temp[(++this.cursor)];
    if (has(4))
      ch = peekPastWhitespace(ch);
    return ch;
  }

  private int nextEscaped()
  {
    int ch = this.temp[(++this.cursor)];
    return ch;
  }

  private int peekPastWhitespace(int ch)
  {
    while ((ASCII.isSpace(ch)) || (ch == 35)) {
      do while (ASCII.isSpace(ch))
          ch = this.temp[(++this.cursor)];
      while (ch != 35);
      ch = peekPastLine();
    }

    return ch;
  }

  private int parsePastWhitespace(int ch)
  {
    while ((ASCII.isSpace(ch)) || (ch == 35)) {
      do while (ASCII.isSpace(ch))
          ch = this.temp[(this.cursor++)];
      while (ch != 35);
      ch = parsePastLine();
    }
    return ch;
  }

  private int parsePastLine()
  {
    int ch = this.temp[(this.cursor++)];
    while ((ch != 0) && (!(isLineSeparator(ch))))
      ch = this.temp[(this.cursor++)];
    return ch;
  }

  private int peekPastLine()
  {
    int ch = this.temp[(++this.cursor)];
    while ((ch != 0) && (!(isLineSeparator(ch))))
      ch = this.temp[(++this.cursor)];
    return ch;
  }

  private boolean isLineSeparator(int ch)
  {
    if (has(1)) {
      return (ch == 10);
    }
    return ((ch == 10) || (ch == 13) || ((ch | 0x1) == 8233) || (ch == 133));
  }

  private int skip()
  {
    int i = this.cursor;
    int ch = this.temp[(i + 1)];
    this.cursor = (i + 2);
    return ch;
  }

  private void unread()
  {
    this.cursor -= 1;
  }

  private Node error(String s)
  {
    throw new PatternSyntaxException(s, this.normalizedPattern, this.cursor - 1);
  }

  private Node expr(Node end)
  {
    Node prev = null;
    while (true) {
      Node node = sequence(end);
      if (prev == null)
        prev = node;
      else {
        prev = new Branch(prev, node);
      }
      if (peek() != 124) {
        return prev;
      }
      next();
    }
  }

  private Node sequence(Node end)
  {
    Node head = null;
    Node tail = null;
    Node node = null;
    while (true)
    {
      int ch = peek();
      switch (ch)
      {
      case 40:
        node = group0();

        if (node == null)
          continue;
        if (head == null)
          head = node;
        else {
          tail.next = node;
        }
        tail = this.root;
        break;
      case 91:
        node = clazz(true);
        break;
      case 92:
        ch = nextEscaped();
        if ((ch == 112) || (ch == 80)) {
          boolean comp = ch == 80;
          boolean oneLetter = true;
          ch = next();
          if (ch != 123)
            unread();
          else {
            oneLetter = false;
          }
          node = family(comp, oneLetter);
        } else {
          unread();
          node = atom();
        }
        break;
      case 94:
        next();
        if (has(8))
          if (has(1))
            node = new UnixCaret();
          else
            node = new Caret();
        else {
          node = new Begin();
        }
        break;
      case 36:
        next();
        if (has(1))
          node = new UnixDollar(has(8));
        else
          node = new Dollar(has(8));
        break;
      case 46:
        next();
        if (has(32)) {
          node = new All();
        }
        else if (has(1))
          node = new UnixDot();
        else {
          node = new Dot();
        }

        break;
      case 41:
      case 124:
        break;
      case 93:
      case 125:
        node = atom();
        break;
      case 42:
      case 43:
      case 63:
        next();
        return error("Dangling meta character '" + (char)ch + "'");
      case 0:
        if (this.cursor >= this.patternLength)
          break;
      default:
        node = atom();
      }

      node = closure(node);

      if (head == null) {
        head = tail = node;
      }
      tail.next = node;
      tail = node;
    }

    if (head == null) {
      return end;
    }
    tail.next = end;
    return head;
  }

  private Node atom()
  {
    int first = 0;
    int prev = -1;
    int ch = peek();
    while (true) {
      switch (ch)
      {
      case 42:
      case 43:
      case 63:
      case 123:
        if (first <= 1) break;
        this.cursor = prev;
        --first; break;
      case 36:
      case 40:
      case 41:
      case 46:
      case 91:
      case 94:
      case 124:
        break;
      case 92:
        ch = nextEscaped();
        if ((ch == 112) || (ch == 80)) {
          if (first > 0) {
            unread();
            break;
          }
          if ((ch != 112) && (ch != 80)) break;
          boolean comp = ch == 80;
          boolean oneLetter = true;
          ch = next();
          if (ch != 123)
            unread();
          else
            oneLetter = false;
          return family(comp, oneLetter);
        }

        unread();
        prev = this.cursor;
        ch = escape(false, first == 0);
        if (ch >= 0) {
          append(ch, first);
          ++first;
          ch = peek();
        }
        if (first == 0) {
          return this.root;
        }

        this.cursor = prev;
        break;
      case 0:
        if (this.cursor >= this.patternLength)
          break;
      default:
        prev = this.cursor;
        append(ch, first);
        ++first;
        ch = next();
      }

    }

    if (first == 1) {
      return newSingle(this.buffer[0]);
    }
    return newSlice(this.buffer, first);
  }

  private void append(int ch, int len)
  {
    if (len >= this.buffer.length) {
      char[] tmp = new char[len + len];
      System.arraycopy(this.buffer, 0, tmp, 0, len);
      this.buffer = tmp;
    }
    this.buffer[len] = (char)ch;
  }

  private Node ref(int refNum)
  {
    boolean done = false;
    while (!(done)) {
      int ch = peek();
      switch (ch)
      {
      case 48:
      case 49:
      case 50:
      case 51:
      case 52:
      case 53:
      case 54:
      case 55:
      case 56:
      case 57:
        int newRefNum = refNum * 10 + ch - 48;

        if (this.groupCount - 1 < newRefNum) {
          done = true;
        }
        else {
          refNum = newRefNum;
          read(); }
        break;
      default:
        done = true;
      }
    }

    if (has(2)) {
      return new CIBackRef(refNum);
    }
    return new BackRef(refNum);
  }

  private int escape(boolean inclass, boolean create)
  {
    int ch = skip();
    switch (ch)
    {
    case 48:
      return o();
    case 49:
    case 50:
    case 51:
    case 52:
    case 53:
    case 54:
    case 55:
    case 56:
    case 57:
      if (!(inclass)) {
        if (this.groupCount < ch - 48)
          error("No such group yet exists at this point in the pattern");
        if (create) {
          this.root = ref(ch - 48);
        }
        return -1;
      }
    case 65:
      if (!(inclass)) {
        if (create) this.root = new Begin();
        return -1;
      }
    case 66:
      if (!(inclass)) {
        if (create) this.root = new Bound(Bound.NONE);
        return -1;
      }
    case 67:
      break;
    case 68:
      if (create) this.root = new NotCtype(1024);
      return -1;
    case 69:
    case 70:
      break;
    case 71:
      if (!(inclass)) {
        if (create) this.root = new LastMatch();
        return -1;
      }
    case 72:
    case 73:
    case 74:
    case 75:
    case 76:
    case 77:
    case 78:
    case 79:
    case 80:
      break;
    case 81:
      if (create) {
        int i = this.cursor;
        int c;
        do {
          do
            if ((c = readEscaped()) == 0) break label520;
          while (c != 92);
          c = readEscaped();
          if (c == 69) break;  }
        while (c != 0);

        int j = this.cursor - 1;
        if (c == 69)
          --j;
        else
          unread();
        for (int x = i; x < j; ++x)
          append(this.temp[x], x - i);
        this.root = newSlice(this.buffer, j - i);
      }
      return -1;
    case 82:
      break;
    case 83:
      if (create) this.root = new NotCtype(2048);
      return -1;
    case 84:
    case 85:
    case 86:
      break;
    case 87:
      if (create) this.root = new NotCtype(67328);
      return -1;
    case 88:
    case 89:
      break;
    case 90:
      if (!(inclass)) {
        if (create) {
          if (has(1))
            this.root = new UnixDollar(false);
          else
            this.root = new Dollar(false);
        }
        return -1;
      }
    case 97:
      return 7;
    case 98:
      if (!(inclass)) {
        if (create) this.root = new Bound(Bound.BOTH);
        return -1;
      }
    case 99:
      return c();
    case 100:
      if (create) this.root = new Ctype(1024);
      return -1;
    case 101:
      return 27;
    case 102:
      return 12;
    case 103:
    case 104:
    case 105:
    case 106:
    case 107:
    case 108:
    case 109:
      break;
    case 110:
      return 10;
    case 111:
    case 112:
    case 113:
      break;
    case 114:
      return 13;
    case 115:
      if (create) this.root = new Ctype(2048);
      return -1;
    case 116:
      return 9;
    case 117:
      return u();
    case 118:
      return 11;
    case 119:
      if (create) this.root = new Ctype(67328);
      return -1;
    case 120:
      return x();
    case 121:
      break;
    case 122:
      if (!(inclass)) {
        if (create) this.root = new End();
        return -1;
      }
    case 58:
    case 59:
    case 60:
    case 61:
    case 62:
    case 63:
    case 64:
    case 91:
    case 92:
    case 93:
    case 94:
    case 95:
    case 96:
    default:
      label520: return ch;
    }
    error("Illegal/unsupported escape squence");
    return -2;
  }

  private Node clazz(boolean consume)
  {
    Node prev = null;
    Node node = null;
    BitClass bits = new BitClass(false);
    boolean include = true;
    boolean firstInClass = true;
    int ch = next();
    while (true) {
      switch (ch)
      {
      case 94:
        if (firstInClass)
          if (this.temp[(this.cursor - 1)] == '[')
          {
            ch = next();
            include = !(include); }
        break;
      case 91:
        firstInClass = false;
        node = clazz(true);
        if (prev == null)
          prev = node;
        else
          prev = new Add(prev, node);
        ch = peek();
        break;
      case 38:
        firstInClass = false;
        ch = next();
        if (ch == 38) {
          ch = next();
          Node rightNode = null;
          while ((ch != 93) && (ch != 38)) {
            if (ch == 91) {
              if (rightNode == null)
                rightNode = clazz(true);
              else
                rightNode = new Add(rightNode, clazz(true));
            } else {
              unread();
              rightNode = clazz(false);
            }
            ch = peek();
          }
          if (rightNode != null)
            node = rightNode;
          if (prev == null) {
            if (rightNode == null) {
              return error("Bad class syntax");
            }
            prev = rightNode;
          } else {
            prev = new Both(prev, node);
          }
        }

        unread();
        break;
      case 0:
        firstInClass = false;
        if (this.cursor >= this.patternLength)
          return error("Unclosed character class");
      case 93:
        firstInClass = false;
        if (prev != null) {
          if (consume)
            next();
          return prev;
        }
      default:
        firstInClass = false;
      }

      node = range(bits);
      if (include) {
        if (prev == null) {
          prev = node;
        }
        else if (prev != node) {
          prev = new Add(prev, node);
        }
      }
      else if (prev == null) {
        prev = node.dup(true);
      }
      else if (prev != node) {
        prev = new Sub(prev, node);
      }

      ch = peek();
    }
  }

  private Node range(BitClass bits)
  {
    int ch = peek();
    if (ch == 92) {
      ch = nextEscaped();
      if ((ch == 112) || (ch == 80)) {
        boolean comp = ch == 80;
        boolean oneLetter = true;

        ch = next();
        if (ch != 123)
          unread();
        else
          oneLetter = false;
        return family(comp, oneLetter);
      }
      unread();
      ch = escape(true, true);
      if (ch == -1)
        return this.root;
    }
    else {
      ch = single();
    }
    if (ch >= 0) {
      if (peek() == 45) {
        char endRange = this.temp[(this.cursor + 1)];
        if (endRange == '[') {
          if (ch < 256)
            return bits.add(ch, flags());
          return newSingle(ch);
        }
        if (endRange != ']') {
          next();
          int m = single();
          if (m < ch)
            return error("Illegal character range");
          if (has(2)) {
            return new CIRange((ch << 16) + m);
          }
          return new Range((ch << 16) + m);
        }
      }
      if (ch < 256)
        return bits.add(ch, flags());
      return newSingle(ch);
    }
    return error("Unexpected character '" + (char)ch + "'");
  }

  private int single() {
    int ch = peek();
    switch (ch)
    {
    case 92:
      return escape(true, false);
    }
    next();
    return ch;
  }

  private Node family(boolean not, boolean singleLetter)
  {
    next();
    String name;
    if (singleLetter) {
      name = new String(this.temp, this.cursor, 1).intern();
      read();
    } else {
      int i = this.cursor;
      mark('}');
      while (read() != 125);
      mark('\0');
      int j = this.cursor;
      if (j > this.patternLength)
        return error("Unclosed character family");
      if (i + 1 >= j)
        return error("Empty character family");
      name = new String(this.temp, i, j - i - 1).intern();
    }

    if (name.startsWith("In")) {
      name = name.substring(2, name.length()).intern();
      return retrieveFamilyNode(name).dup(not);
    }
    if (name.startsWith("Is"))
      name = name.substring(2, name.length()).intern();
    return retrieveCategoryNode(name).dup(not);
  }

  private Node retrieveFamilyNode(String name) {
    if (families == null) {
      int fns = familyNodes.length;
      families = new HashMap((int)(fns / 0.75D) + 1);
      for (int x = 0; x < fns; ++x)
        families.put(familyNames[x], familyNodes[x]);
    }
    Node n = (Node)families.get(name);
    if (n != null) {
      return n;
    }
    return familyError(name, "Unknown character family {");
  }

  private Node retrieveCategoryNode(String name) {
    if (categories == null) {
      int cns = categoryNodes.length;
      categories = new HashMap((int)(cns / 0.75D) + 1);
      for (int x = 0; x < cns; ++x)
        categories.put(categoryNames[x], categoryNodes[x]);
    }
    Node n = (Node)categories.get(name);
    if (n != null) {
      return n;
    }
    return familyError(name, "Unknown character category {");
  }

  private Node familyError(String name, String type) {
    StringBuffer sb = new StringBuffer();
    sb.append(type);
    sb.append(name);
    sb.append("}");
    name = sb.toString();
    return error(name);
  }

  private Node group0()
  {
    Node head = null;
    Node tail = null;
    int save = this.flags;
    this.root = null;
    int ch = next();
    if (ch == 63) {
      ch = skip();
      switch (ch)
      {
      case 58:
        head = createGroup(true);
        tail = this.root;
        head.next = expr(tail);
        break;
      case 33:
      case 61:
        head = createGroup(true);
        tail = this.root;
        head.next = expr(tail);
        if (ch == 61)
          head = tail = new Pos(head);
        else {
          head = tail = new Neg(head);
        }
        break;
      case 62:
        head = createGroup(true);
        tail = this.root;
        head.next = expr(tail);
        head = tail = new Ques(head, 3);
        break;
      case 60:
        ch = read();
        head = createGroup(true);
        tail = this.root;
        head.next = expr(tail);
        TreeInfo info = new TreeInfo();
        head.study(info);
        if (!(info.maxValid)) {
          return error("Look-behind group does not have an obvious maximum length");
        }

        if (ch == 61) {
          head = tail = new Behind(head, info.maxLength, info.minLength);
        }
        else if (ch == 33) {
          head = tail = new NotBehind(head, info.maxLength, info.minLength);
        }
        else {
          error("Unknown look-behind group");
        }
        break;
      case 49:
      case 50:
      case 51:
      case 52:
      case 53:
      case 54:
      case 55:
      case 56:
      case 57:
        if (this.groupNodes[(ch - 48)] != null) {
          head = tail = new GroupRef(this.groupNodes[(ch - 48)]);
        }
        else
          return error("Unknown group reference");
      case 36:
      case 64:
        return error("Unknown group type");
      case 34:
      case 35:
      case 37:
      case 38:
      case 39:
      case 40:
      case 41:
      case 42:
      case 43:
      case 44:
      case 45:
      case 46:
      case 47:
      case 48:
      case 59:
      case 63:
      default:
        unread();
        addFlag();
        ch = read();
        if (ch == 41) {
          return null;
        }
        if (ch != 58) {
          return error("Unknown inline modifier");
        }
        head = createGroup(true);
        tail = this.root;
        head.next = expr(tail);
      }
    }
    else {
      head = createGroup(false);
      tail = this.root;
      head.next = expr(tail);
    }

    accept(41, "Unclosed group");
    this.flags = save;

    Node node = closure(head);
    if (node == head) {
      this.root = tail;
      return node;
    }
    if (head == tail) {
      this.root = node;
      return node;
    }

    if (node instanceof Ques) {
      Ques ques = (Ques)node;
      if (ques.type == 2) {
        this.root = node;
        return node;
      }

      tail.next = new Dummy();
      tail = tail.next;
      if (ques.type == 0)
        head = new Branch(head, tail);
      else {
        head = new Branch(tail, head);
      }
      this.root = tail;
      return head; }
    if (node instanceof Curly) {
      Curly curly = (Curly)node;
      if (curly.type == 2) {
        this.root = node;
        return node;
      }

      TreeInfo info = new TreeInfo();
      if (head.study(info)) {
        GroupTail temp = (GroupTail)tail;
        head = this.root = new GroupCurly(head.next, curly.cmin, curly.cmax, curly.type, ((GroupTail)tail).localIndex, ((GroupTail)tail).groupIndex);

        return head;
      }
      int temp = ((GroupHead)head).localIndex;
      Loop loop;
      if (curly.type == 0)
        loop = new Loop(this.localCount, temp);
      else
        loop = new LazyLoop(this.localCount, temp);
      Prolog prolog = new Prolog(loop);
      this.localCount += 1;
      loop.cmin = curly.cmin;
      loop.cmax = curly.cmax;
      loop.body = head;
      tail.next = loop;
      this.root = loop;
      return prolog;
    }
    if (node instanceof First) {
      this.root = node;
      return node;
    }
    return error("Internal logic error");
  }

  private Node createGroup(boolean anonymous)
  {
    int localIndex = this.localCount++;
    int groupIndex = 0;
    if (!(anonymous))
      groupIndex = this.groupCount++;
    GroupHead head = new GroupHead(localIndex);
    this.root = new GroupTail(localIndex, groupIndex);
    if ((!(anonymous)) && (groupIndex < 10))
      this.groupNodes[groupIndex] = head;
    return head;
  }

  private void addFlag()
  {
    int ch = peek();
    while (true) {
      switch (ch)
      {
      case 105:
        this.flags |= 2;
        break;
      case 109:
        this.flags |= 8;
        break;
      case 115:
        this.flags |= 32;
        break;
      case 100:
        this.flags |= 1;
        break;
      case 117:
        this.flags |= 64;
        break;
      case 99:
        this.flags |= 128;
        break;
      case 120:
        this.flags |= 4;
        break;
      case 45:
        ch = next();
        subFlag();
      default:
        return;
      }
      ch = next();
    }
  }

  private void subFlag()
  {
    int ch = peek();
    while (true) {
      switch (ch)
      {
      case 105:
        this.flags &= -3;
        break;
      case 109:
        this.flags &= -9;
        break;
      case 115:
        this.flags &= -33;
        break;
      case 100:
        this.flags &= -2;
        break;
      case 117:
        this.flags &= -65;
        break;
      case 99:
        this.flags &= -129;
        break;
      case 120:
        this.flags &= -5;
        break;
      case 101:
      case 102:
      case 103:
      case 104:
      case 106:
      case 107:
      case 108:
      case 110:
      case 111:
      case 112:
      case 113:
      case 114:
      case 116:
      case 118:
      case 119:
      default:
        return;
      }
      ch = next();
    }
  }

  private Node closure(Node prev)
  {
    int ch = peek();
    switch (ch)
    {
    case 63:
      ch = next();
      if (ch == 63) {
        next();
        return new Ques(prev, 1); }
      if (ch == 43) {
        next();
        return new Ques(prev, 2);
      }
      return new Ques(prev, 0);
    case 42:
      ch = next();
      if (ch == 63) {
        next();
        return new Curly(prev, 0, 2147483647, 1); }
      if (ch == 43) {
        next();
        return new Curly(prev, 0, 2147483647, 2);
      }
      return new Curly(prev, 0, 2147483647, 0);
    case 43:
      ch = next();
      if (ch == 63) {
        next();
        return new Curly(prev, 1, 2147483647, 1); }
      if (ch == 43) {
        next();
        return new Curly(prev, 1, 2147483647, 2);
      }
      return new Curly(prev, 1, 2147483647, 0);
    case 123:
      ch = this.temp[(this.cursor + 1)];
      if (ASCII.isDigit(ch)) {
        skip();
        int cmin = 0;
        do
          cmin = cmin * 10 + ch - 48;
        while (ASCII.isDigit(ch = read()));
        int cmax = cmin;
        if (ch == 44) {
          ch = read();
          cmax = 2147483647;
          if (ch != 125) {
            cmax = 0;
            while (ASCII.isDigit(ch)) {
              cmax = cmax * 10 + ch - 48;
              ch = read();
            }
          }
        }
        if (ch != 125)
          return error("Unclosed counted closure");
        if ((cmin | cmax | cmax - cmin) < 0) {
          return error("Illegal repetition range");
        }
        ch = peek();
        Curly curly;
        if (ch == 63) {
          next();
          curly = new Curly(prev, cmin, cmax, 1);
        } else if (ch == 43) {
          next();
          curly = new Curly(prev, cmin, cmax, 2);
        } else {
          curly = new Curly(prev, cmin, cmax, 0);
        }
        return curly;
      }
      error("Illegal repetition");

      return prev;
    }
    return prev;
  }

  private int c()
  {
    if (this.cursor < this.patternLength) {
      return (read() ^ 0x40);
    }
    error("Illegal control escape sequence");
    return -1;
  }

  private int o()
  {
    int n = read();
    if ((n - 48 | 55 - n) >= 0) {
      int m = read();
      if ((m - 48 | 55 - m) >= 0) {
        int o = read();
        if (((o - 48 | 55 - o) >= 0) && ((n - 48 | 51 - n) >= 0)) {
          return ((n - 48) * 64 + (m - 48) * 8 + o - 48);
        }
        unread();
        return ((n - 48) * 8 + m - 48);
      }
      unread();
      return (n - 48);
    }
    error("Illegal octal escape sequence");
    return -1;
  }

  private int x()
  {
    int n = read();
    if (ASCII.isHexDigit(n)) {
      int m = read();
      if (ASCII.isHexDigit(m)) {
        return (ASCII.toDigit(n) * 16 + ASCII.toDigit(m));
      }
    }
    error("Illegal hexadecimal escape sequence");
    return -1;
  }

  private int u()
  {
    int n = 0;
    for (int i = 0; i < 4; ++i) {
      int ch = read();
      if (!(ASCII.isHexDigit(ch))) {
        error("Illegal Unicode escape sequence");
      }
      n = n * 16 + ASCII.toDigit(ch);
    }
    return n;
  }

  private Node newSingle(int ch)
  {
    int f = this.flags;
    if ((f & 0x2) == 0) {
      return new Single(ch);
    }
    if ((f & 0x40) == 0) {
      return new SingleA(ch);
    }
    return new SingleU(ch);
  }

  private Node newSlice(char[] buf, int count)
  {
    char[] tmp = new char[count];
    int i = this.flags;
    if ((i & 0x2) == 0) {
      for (i = 0; i < count; ++i) {
        tmp[i] = buf[i];
      }
      return new Slice(tmp); }
    if ((i & 0x40) == 0) {
      for (i = 0; i < count; ++i) {
        tmp[i] = (char)ASCII.toLower(buf[i]);
      }
      return new SliceA(tmp);
    }
    for (i = 0; i < count; ++i) {
      char c = buf[i];
      c = Character.toUpperCase(c);
      c = Character.toLowerCase(c);
      tmp[i] = c;
    }
    return new SliceU(tmp);
  }

  static final class BnM extends Pattern.Node
  {
    char[] buffer;
    int[] lastOcc;
    int[] optoSft;

    static Pattern.Node optimize(Pattern.Node node)
    {
      if (!(node instanceof Pattern.Slice)) {
        return node;
      }
      char[] src = ((Pattern.Slice)node).buffer;
      int patternLength = src.length;

      if (patternLength < 4) {
        return node;
      }

      int[] lastOcc = new int[128];
      int[] optoSft = new int[patternLength];

      for (int i = 0; i < patternLength; ++i) {
        lastOcc[(src[i] & 0x7F)] = (i + 1);
      }

      for (i = patternLength; i > 0; --i)
      {
        for (int j = patternLength - 1; j >= i; --j)
        {
          if (src[j] != src[(j - i)])
            break label125;
          optoSft[(j - 1)] = i;
        }

        label125: while (j > 0) {
          optoSft[(--j)] = i;
        }
      }

      optoSft[(patternLength - 1)] = 1;
      return new BnM(src, lastOcc, optoSft, node.next); }

    BnM(char[] src, int[] lastOcc, int[] optoSft, Pattern.Node next) {
      this.buffer = src;
      this.lastOcc = lastOcc;
      this.optoSft = optoSft;
      this.next = next; }

    boolean match(Matcher matcher, int i, String seq) {
      char[] src = this.buffer;
      int patternLength = src.length;
      int last = matcher.to - patternLength;

      while (i <= last)
      {
        for (int j = patternLength - 1; j >= 0; --j) {
          label20: char ch = seq.charAt(i + j);
          if (ch == src[j]) {
            continue;
          }
          i += Math.max(j + 1 - this.lastOcc[(ch & 0x7F)], this.optoSft[j]);
          break label20:
        }

        matcher.first = i;
        boolean ret = this.next.match(matcher, i + patternLength, seq);
        if (ret) {
          matcher.first = i;
          matcher.groups[0] = matcher.first;
          matcher.groups[1] = matcher.last;
          return true;
        }
        ++i;
      }
      return false; }

    boolean study(Pattern.TreeInfo info) {
      info.minLength += this.buffer.length;
      info.maxValid = false;
      return this.next.study(info);
    }
  }

  static final class Bound extends Pattern.Node
  {
    static int LEFT = 1;
    static int RIGHT = 2;
    static int BOTH = 3;
    static int NONE = 4;
    int type;

    Bound(int n)
    {
      this.type = n;
    }

    int check(Matcher matcher, int i, String seq) {
      boolean left = false;
      char ch;
      if (i > matcher.from) {
        ch = seq.charAt(i - 1);
        left = (ch == '_') || (Character.isLetterOrDigit(ch));
      }
      boolean right = false;
      if (i < matcher.to) {
        ch = seq.charAt(i);
        right = (ch == '_') || (Character.isLetterOrDigit(ch));
      }
      return (((left ^ right)) ? RIGHT : (right) ? LEFT : NONE); }

    boolean match(Matcher matcher, int i, String seq) {
      return (((check(matcher, i, seq) & this.type) > 0) && (this.next.match(matcher, i, seq)));
    }
  }

  static final class Sub extends Pattern.Add
  {
    Sub(Pattern.Node lhs, Pattern.Node rhs)
    {
      super(lhs, rhs); }

    boolean match(Matcher matcher, int i, String seq) {
      if (i < matcher.to) {
        return ((!(this.rhs.match(matcher, i, seq))) && (this.lhs.match(matcher, i, seq)) && (this.next.match(matcher, matcher.last, seq)));
      }

      return false; }

    boolean study(Pattern.TreeInfo info) {
      this.lhs.study(info);
      return this.next.study(info);
    }
  }

  static class Both extends Pattern.Node
  {
    Pattern.Node lhs;
    Pattern.Node rhs;

    Both(Pattern.Node lhs, Pattern.Node rhs)
    {
      this.lhs = lhs;
      this.rhs = rhs; }

    boolean match(Matcher matcher, int i, String seq) {
      if (i < matcher.to) {
        return ((this.lhs.match(matcher, i, seq)) && (this.rhs.match(matcher, i, seq)) && (this.next.match(matcher, matcher.last, seq)));
      }
      return false; }

    boolean study(Pattern.TreeInfo info) {
      boolean maxV = info.maxValid;
      boolean detm = info.deterministic;

      int minL = info.minLength;
      int maxL = info.maxLength;
      this.lhs.study(info);

      int minL2 = info.minLength;
      int maxL2 = info.maxLength;

      info.minLength = minL;
      info.maxLength = maxL;
      this.rhs.study(info);

      info.minLength = Math.min(minL2, info.minLength);
      info.maxLength = Math.max(maxL2, info.maxLength);
      info.maxValid = maxV;
      info.deterministic = detm;

      return this.next.study(info);
    }
  }

  static class Add extends Pattern.Node
  {
    Pattern.Node lhs;
    Pattern.Node rhs;

    Add(Pattern.Node lhs, Pattern.Node rhs)
    {
      this.lhs = lhs;
      this.rhs = rhs; }

    boolean match(Matcher matcher, int i, String seq) {
      if (i < matcher.to) {
        return ((((this.lhs.match(matcher, i, seq)) || (this.rhs.match(matcher, i, seq)))) && (this.next.match(matcher, matcher.last, seq)));
      }
      return false; }

    boolean study(Pattern.TreeInfo info) {
      boolean maxV = info.maxValid;
      boolean detm = info.deterministic;

      int minL = info.minLength;
      int maxL = info.maxLength;
      this.lhs.study(info);

      int minL2 = info.minLength;
      int maxL2 = info.maxLength;

      info.minLength = minL;
      info.maxLength = maxL;
      this.rhs.study(info);

      info.minLength = Math.min(minL2, info.minLength);
      info.maxLength = Math.max(maxL2, info.maxLength);
      info.maxValid = maxV;
      info.deterministic = detm;

      return this.next.study(info);
    }
  }

  static final class NotBehind extends Pattern.Node
  {
    Pattern.Node cond;
    int rmax;
    int rmin;

    NotBehind(Pattern.Node cond, int rmax, int rmin)
    {
      this.cond = cond;
      this.rmax = rmax;
      this.rmin = rmin; }

    boolean match(Matcher matcher, int i, String seq) {
      int from = Math.max(i - this.rmax, matcher.from);
      for (int j = i - this.rmin; j >= from; --j) {
        if ((this.cond.match(matcher, j, seq)) && (matcher.last == i)) {
          return false;
        }
      }
      return this.next.match(matcher, i, seq);
    }
  }

  static final class Behind extends Pattern.Node
  {
    Pattern.Node cond;
    int rmax;
    int rmin;

    Behind(Pattern.Node cond, int rmax, int rmin)
    {
      this.cond = cond;
      this.rmax = rmax;
      this.rmin = rmin; }

    boolean match(Matcher matcher, int i, String seq) {
      int from = Math.max(i - this.rmax, matcher.from);
      for (int j = i - this.rmin; j >= from; --j) {
        if ((this.cond.match(matcher, j, seq)) && (matcher.last == i)) {
          return this.next.match(matcher, i, seq);
        }
      }
      return false;
    }
  }

  static final class Neg extends Pattern.Node
  {
    Pattern.Node cond;

    Neg(Pattern.Node cond)
    {
      this.cond = cond; }

    boolean match(Matcher matcher, int i, String seq) {
      return ((!(this.cond.match(matcher, i, seq))) && (this.next.match(matcher, i, seq)));
    }
  }

  static final class Pos extends Pattern.Node
  {
    Pattern.Node cond;

    Pos(Pattern.Node cond)
    {
      this.cond = cond; }

    boolean match(Matcher matcher, int i, String seq) {
      return ((this.cond.match(matcher, i, seq)) && (this.next.match(matcher, i, seq)));
    }
  }

  static final class Conditional extends Pattern.Node
  {
    Pattern.Node cond;
    Pattern.Node yes;
    Pattern.Node not;

    Conditional(Pattern.Node cond, Pattern.Node yes, Pattern.Node not)
    {
      this.cond = cond;
      this.yes = yes;
      this.not = not; }

    boolean match(Matcher matcher, int i, String seq) {
      if (this.cond.match(matcher, i, seq)) {
        return this.yes.match(matcher, i, seq);
      }
      return this.not.match(matcher, i, seq);
    }

    boolean study(Pattern.TreeInfo info) {
      int minL = info.minLength;
      int maxL = info.maxLength;
      boolean maxV = info.maxValid;
      info.reset();
      this.yes.study(info);

      int minL2 = info.minLength;
      int maxL2 = info.maxLength;
      boolean maxV2 = info.maxValid;
      info.reset();
      this.not.study(info);

      info.minLength = (minL + Math.min(minL2, info.minLength));
      info.maxLength = (maxL + Math.max(maxL2, info.maxLength));
      info.maxValid = (maxV & maxV2 & info.maxValid);
      info.deterministic = false;
      return this.next.study(info);
    }
  }

  static final class First extends Pattern.Node
  {
    Pattern.Node atom;

    First(Pattern.Node node)
    {
      this.atom = Pattern.BnM.optimize(node); }

    boolean match(Matcher matcher, int i, String seq) {
      if (this.atom instanceof Pattern.BnM) {
        return ((this.atom.match(matcher, i, seq)) && (this.next.match(matcher, matcher.last, seq)));
      }
      while (true)
      {
        if (i > matcher.to) {
          return false;
        }
        if (this.atom.match(matcher, i, seq)) {
          return this.next.match(matcher, matcher.last, seq);
        }
        ++i;
        matcher.first += 1; }
    }

    boolean study(Pattern.TreeInfo info) {
      this.atom.study(info);
      info.maxValid = false;
      info.deterministic = false;
      return this.next.study(info);
    }
  }

  static class CIBackRef extends Pattern.Node
  {
    int groupIndex;

    CIBackRef(int groupCount)
    {
      this.groupIndex = (groupCount + groupCount); }

    boolean match(Matcher matcher, int i, String seq) {
      int j = matcher.groups[this.groupIndex];
      int k = matcher.groups[(this.groupIndex + 1)];

      int groupSize = k - j;

      if (j < 0) {
        return false;
      }

      if (i + groupSize > matcher.to) {
        return false;
      }

      for (int index = 0; index < groupSize; ++index) {
        char c1 = seq.charAt(i + index);
        char c2 = seq.charAt(j + index);
        if (c1 != c2) {
          c1 = Character.toUpperCase(c1);
          c2 = Character.toUpperCase(c2);
          if (c1 != c2) {
            c1 = Character.toLowerCase(c1);
            c2 = Character.toLowerCase(c2);
            if (c1 != c2) {
              return false;
            }
          }
        }
      }
      return this.next.match(matcher, i + groupSize, seq); }

    boolean study(Pattern.TreeInfo info) {
      info.maxValid = false;
      return this.next.study(info);
    }
  }

  static class BackRef extends Pattern.Node
  {
    int groupIndex;

    BackRef(int groupCount)
    {
      this.groupIndex = (groupCount + groupCount); }

    boolean match(Matcher matcher, int i, String seq) {
      int j = matcher.groups[this.groupIndex];
      int k = matcher.groups[(this.groupIndex + 1)];

      int groupSize = k - j;

      if (j < 0) {
        return false;
      }

      if (i + groupSize > matcher.to) {
        return false;
      }

      for (int index = 0; index < groupSize; ++index) {
        if (seq.charAt(i + index) != seq.charAt(j + index))
          return false;
      }
      return this.next.match(matcher, i + groupSize, seq); }

    boolean study(Pattern.TreeInfo info) {
      info.maxValid = false;
      return this.next.study(info);
    }
  }

  static final class LazyLoop extends Pattern.Loop
  {
    LazyLoop(int countIndex, int beginIndex)
    {
      super(countIndex, beginIndex);
    }

    boolean match(Matcher matcher, int i, String seq) {
      if (i > matcher.locals[this.beginIndex]) {
        int count = matcher.locals[this.countIndex];
        boolean result;
        if (count < this.cmin) {
          matcher.locals[this.countIndex] = (count + 1);
          result = this.body.match(matcher, i, seq);

          if (!(result))
            matcher.locals[this.countIndex] = count;
          return result;
        }
        if (this.next.match(matcher, i, seq))
          return true;
        if (count < this.cmax) {
          matcher.locals[this.countIndex] = (count + 1);
          result = this.body.match(matcher, i, seq);

          if (!(result))
            matcher.locals[this.countIndex] = count;
          return result;
        }
        return false;
      }
      return this.next.match(matcher, i, seq); }

    boolean matchInit(Matcher matcher, int i, String seq) {
      int save = matcher.locals[this.countIndex];
      boolean ret = false;
      if (0 < this.cmin) {
        matcher.locals[this.countIndex] = 1;
        ret = this.body.match(matcher, i, seq);
      } else if (this.next.match(matcher, i, seq)) {
        ret = true;
      } else if (0 < this.cmax) {
        matcher.locals[this.countIndex] = 1;
        ret = this.body.match(matcher, i, seq);
      }
      matcher.locals[this.countIndex] = save;
      return ret; }

    boolean study(Pattern.TreeInfo info) {
      info.maxValid = false;
      info.deterministic = false;
      return false;
    }
  }

  static class Loop extends Pattern.Node
  {
    Pattern.Node body;
    int countIndex;
    int beginIndex;
    int cmin;
    int cmax;

    Loop(int countIndex, int beginIndex)
    {
      this.countIndex = countIndex;
      this.beginIndex = beginIndex;
    }

    boolean match(Matcher matcher, int i, String seq) {
      if (i > matcher.locals[this.beginIndex]) {
        int count = matcher.locals[this.countIndex];
        boolean b;
        if (count < this.cmin) {
          matcher.locals[this.countIndex] = (count + 1);
          b = this.body.match(matcher, i, seq);

          if (!(b)) {
            matcher.locals[this.countIndex] = count;
          }

          return b;
        }

        if (count < this.cmax) {
          matcher.locals[this.countIndex] = (count + 1);
          b = this.body.match(matcher, i, seq);

          if (!(b))
            matcher.locals[this.countIndex] = count;
          else
            return true;
        }
      }
      return this.next.match(matcher, i, seq); }

    boolean matchInit(Matcher matcher, int i, String seq) {
      int save = matcher.locals[this.countIndex];
      boolean ret = false;
      if (0 < this.cmin) {
        matcher.locals[this.countIndex] = 1;
        ret = this.body.match(matcher, i, seq);
      } else if (0 < this.cmax) {
        matcher.locals[this.countIndex] = 1;
        ret = this.body.match(matcher, i, seq);
        if (!(ret))
          ret = this.next.match(matcher, i, seq);
      } else {
        ret = this.next.match(matcher, i, seq);
      }
      matcher.locals[this.countIndex] = save;
      return ret; }

    boolean study(Pattern.TreeInfo info) {
      info.maxValid = false;
      info.deterministic = false;
      return false;
    }
  }

  static final class Prolog extends Pattern.Node
  {
    Pattern.Loop loop;

    Prolog(Pattern.Loop loop)
    {
      this.loop = loop; }

    boolean match(Matcher matcher, int i, String seq) {
      return this.loop.matchInit(matcher, i, seq); }

    boolean study(Pattern.TreeInfo info) {
      return this.loop.study(info);
    }
  }

  static final class GroupTail extends Pattern.Node
  {
    int localIndex;
    int groupIndex;

    GroupTail(int localCount, int groupCount)
    {
      this.localIndex = localCount;
      this.groupIndex = (groupCount + groupCount); }

    boolean match(Matcher matcher, int i, String seq) {
      int tmp = matcher.locals[this.localIndex];
      if (tmp >= 0)
      {
        int groupStart = matcher.groups[this.groupIndex];
        int groupEnd = matcher.groups[(this.groupIndex + 1)];

        matcher.groups[this.groupIndex] = tmp;
        matcher.groups[(this.groupIndex + 1)] = i;
        if (this.next.match(matcher, i, seq)) {
          return true;
        }
        matcher.groups[this.groupIndex] = groupStart;
        matcher.groups[(this.groupIndex + 1)] = groupEnd;
        return false;
      }

      matcher.last = i;
      return true;
    }
  }

  static final class GroupRef extends Pattern.Node
  {
    Pattern.GroupHead head;

    GroupRef(Pattern.GroupHead head)
    {
      this.head = head; }

    boolean match(Matcher matcher, int i, String seq) {
      return ((this.head.matchRef(matcher, i, seq)) && (this.next.match(matcher, matcher.last, seq)));
    }

    boolean study(Pattern.TreeInfo info) {
      info.maxValid = false;
      info.deterministic = false;
      return this.next.study(info);
    }
  }

  static final class GroupHead extends Pattern.Node
  {
    int localIndex;

    GroupHead(int localCount)
    {
      this.localIndex = localCount; }

    boolean match(Matcher matcher, int i, String seq) {
      int save = matcher.locals[this.localIndex];
      matcher.locals[this.localIndex] = i;
      boolean ret = this.next.match(matcher, i, seq);
      matcher.locals[this.localIndex] = save;
      return ret; }

    boolean matchRef(Matcher matcher, int i, String seq) {
      int save = matcher.locals[this.localIndex];
      matcher.locals[this.localIndex] = (i ^ 0xFFFFFFFF);
      boolean ret = this.next.match(matcher, i, seq);
      matcher.locals[this.localIndex] = save;
      return ret;
    }
  }

  static final class Branch extends Pattern.Node
  {
    Pattern.Node prev;

    Branch(Pattern.Node lhs, Pattern.Node rhs)
    {
      this.prev = lhs;
      this.next = rhs; }

    boolean match(Matcher matcher, int i, String seq) {
      return ((this.prev.match(matcher, i, seq)) || (this.next.match(matcher, i, seq))); }

    boolean study(Pattern.TreeInfo info) {
      int minL = info.minLength;
      int maxL = info.maxLength;
      boolean maxV = info.maxValid;
      info.reset();
      this.prev.study(info);

      int minL2 = info.minLength;
      int maxL2 = info.maxLength;
      boolean maxV2 = info.maxValid;
      info.reset();
      this.next.study(info);

      info.minLength = (minL + Math.min(minL2, info.minLength));
      info.maxLength = (maxL + Math.max(maxL2, info.maxLength));
      info.maxValid = (maxV & maxV2 & info.maxValid);
      info.deterministic = false;
      return false;
    }
  }

  static final class GroupCurly extends Pattern.Node
  {
    Pattern.Node atom;
    int type;
    int cmin;
    int cmax;
    int localIndex;
    int groupIndex;

    GroupCurly(Pattern.Node node, int cmin, int cmax, int type, int local, int group)
    {
      this.atom = node;
      this.type = type;
      this.cmin = cmin;
      this.cmax = cmax;
      this.localIndex = local;
      this.groupIndex = group; }

    boolean match(Matcher matcher, int i, String seq) {
      int[] groups = matcher.groups;
      int[] locals = matcher.locals;
      int save0 = locals[this.localIndex];
      int save1 = groups[this.groupIndex];
      int save2 = groups[(this.groupIndex + 1)];

      locals[this.localIndex] = -1;

      boolean ret = true;
      for (int j = 0; j < this.cmin; ++j) {
        if (this.atom.match(matcher, i, seq)) {
          groups[this.groupIndex] = i;
          groups[(this.groupIndex + 1)] = (i = matcher.last);
        } else {
          ret = false;
          break;
        }
      }
      if (ret)
      {
        if (this.type == 0)
          ret = match0(matcher, i, this.cmin, seq);
        else if (this.type == 1)
          ret = match1(matcher, i, this.cmin, seq);
        else
          ret = match2(matcher, i, this.cmin, seq);
      }
      if (!(ret)) {
        locals[this.localIndex] = save0;
        groups[this.groupIndex] = save1;
        groups[(this.groupIndex + 1)] = save2;
      }
      return ret;
    }

    boolean match0(Matcher matcher, int i, int j, String seq) {
      int[] groups = matcher.groups;
      int save0 = groups[this.groupIndex];
      int save1 = groups[(this.groupIndex + 1)];

      if (j < this.cmax)
      {
        if (this.atom.match(matcher, i, seq))
        {
          int k = matcher.last - i;
          if (k <= 0) {
            groups[this.groupIndex] = i;
            label172: groups[(this.groupIndex + 1)] = (i += k);
          }
          else {
            do {
              groups[this.groupIndex] = i;
              groups[(this.groupIndex + 1)] = (i += k);
              if (++j >= this.cmax)
                break label172;
              if (!(this.atom.match(matcher, i, seq))) break label172;
            }
            while (i + k == matcher.last);
            if (match0(matcher, i, j, seq)) {
              return true;
            }

            while (j > this.cmin) {
              if (this.next.match(matcher, i, seq)) {
                groups[(this.groupIndex + 1)] = i;
                groups[this.groupIndex] = (i -= k);
                return true;
              }

              groups[(this.groupIndex + 1)] = i;
              groups[this.groupIndex] = (i -= k);
              --j; }
          }
        }
      }
      groups[this.groupIndex] = save0;
      groups[(this.groupIndex + 1)] = save1;
      return this.next.match(matcher, i, seq);
    }

    boolean match1(Matcher matcher, int i, int j, String seq) {
      while (true) {
        if (this.next.match(matcher, i, seq))
          return true;
        if (j >= this.cmax)
          return false;
        if (!(this.atom.match(matcher, i, seq)))
          return false;
        if (i == matcher.last) {
          return false;
        }
        matcher.groups[this.groupIndex] = i;
        matcher.groups[(this.groupIndex + 1)] = (i = matcher.last);
        ++j;
      }
    }

    boolean match2(Matcher matcher, int i, int j, String seq) {
      for (; j < this.cmax; ++j) {
        if (!(this.atom.match(matcher, i, seq))) {
          break;
        }
        matcher.groups[this.groupIndex] = i;
        matcher.groups[(this.groupIndex + 1)] = matcher.last;
        if (i == matcher.last) {
          break;
        }
        i = matcher.last;
      }
      return this.next.match(matcher, i, seq);
    }

    boolean study(Pattern.TreeInfo info) {
      int minL = info.minLength;
      int maxL = info.maxLength;
      boolean maxV = info.maxValid;
      boolean detm = info.deterministic;
      info.reset();

      this.atom.study(info);

      int temp = info.minLength * this.cmin + minL;
      if (temp < minL) {
        temp = 268435455;
      }
      info.minLength = temp;

      if ((maxV & info.maxValid)) {
        temp = info.maxLength * this.cmax + maxL;
        info.maxLength = temp;
        if (temp < maxL)
          info.maxValid = false;
      }
      else {
        info.maxValid = false;
      }

      if ((info.deterministic) && (this.cmin == this.cmax))
        info.deterministic = detm;
      else {
        info.deterministic = false;
      }

      return this.next.study(info);
    }
  }

  static final class Curly extends Pattern.Node
  {
    Pattern.Node atom;
    int type;
    int cmin;
    int cmax;

    Curly(Pattern.Node node, int cmin, int cmax, int type)
    {
      this.atom = node;
      this.type = type;
      this.cmin = cmin;
      this.cmax = cmax;
    }

    boolean match(Matcher matcher, int i, String seq) {
      for (int j = 0; j < this.cmin; ++j) {
        if (this.atom.match(matcher, i, seq)) {
          i = matcher.last;
        }
        else
          return false;
      }
      if (this.type == 0)
        return match0(matcher, i, j, seq);
      if (this.type == 1) {
        return match1(matcher, i, j, seq);
      }
      return match2(matcher, i, j, seq);
    }

    boolean match0(Matcher matcher, int i, int j, String seq)
    {
      if (j >= this.cmax)
      {
        return this.next.match(matcher, i, seq);
      }
      int backLimit = j;
      if (this.atom.match(matcher, i, seq))
      {
        int k = matcher.last - i;
        if (k != 0)
        {
          i = matcher.last;
          ++j;

          while (j < this.cmax) {
            if (!(this.atom.match(matcher, i, seq)))
              break;
            if (i + k != matcher.last) {
              if (!(match0(matcher, matcher.last, j + 1, seq))) break;
              return true;
            }

            i += k;
            ++j;
          }

          while (j >= backLimit) {
            if (this.next.match(matcher, i, seq))
              return true;
            i -= k;
            --j;
          }
          return false; }
      }
      return this.next.match(matcher, i, seq);
    }

    boolean match1(Matcher matcher, int i, int j, String seq)
    {
      while (true)
      {
        if (this.next.match(matcher, i, seq)) {
          return true;
        }
        if (j >= this.cmax) {
          return false;
        }
        if (!(this.atom.match(matcher, i, seq))) {
          return false;
        }
        if (i == matcher.last) {
          return false;
        }
        i = matcher.last;
        ++j; }
    }

    boolean match2(Matcher matcher, int i, int j, String seq) {
      for (; j < this.cmax; ++j) {
        if (!(this.atom.match(matcher, i, seq)))
          break;
        if (i == matcher.last)
          break;
        i = matcher.last;
      }
      return this.next.match(matcher, i, seq);
    }

    boolean study(Pattern.TreeInfo info) {
      int minL = info.minLength;
      int maxL = info.maxLength;
      boolean maxV = info.maxValid;
      boolean detm = info.deterministic;
      info.reset();

      this.atom.study(info);

      int temp = info.minLength * this.cmin + minL;
      if (temp < minL) {
        temp = 268435455;
      }
      info.minLength = temp;

      if ((maxV & info.maxValid)) {
        temp = info.maxLength * this.cmax + maxL;
        info.maxLength = temp;
        if (temp < maxL)
          info.maxValid = false;
      }
      else {
        info.maxValid = false;
      }

      if ((info.deterministic) && (this.cmin == this.cmax))
        info.deterministic = detm;
      else {
        info.deterministic = false;
      }
      return this.next.study(info);
    }
  }

  static final class Ques extends Pattern.Node
  {
    Pattern.Node atom;
    int type;

    Ques(Pattern.Node node, int type)
    {
      this.atom = node;
      this.type = type; }

    boolean match(Matcher matcher, int i, String seq) {
      switch (this.type)
      {
      case 0:
        return (((this.atom.match(matcher, i, seq)) && (this.next.match(matcher, matcher.last, seq))) || (this.next.match(matcher, i, seq)));
      case 1:
        return ((this.next.match(matcher, i, seq)) || ((this.atom.match(matcher, i, seq)) && (this.next.match(matcher, matcher.last, seq))));
      case 2:
        if (this.atom.match(matcher, i, seq)) i = matcher.last;
        return this.next.match(matcher, i, seq);
      }
      return ((this.atom.match(matcher, i, seq)) && (this.next.match(matcher, matcher.last, seq)));
    }

    boolean study(Pattern.TreeInfo info) {
      if (this.type != 3) {
        int minL = info.minLength;
        this.atom.study(info);
        info.minLength = minL;
        info.deterministic = false;
        return this.next.study(info);
      }
      this.atom.study(info);
      return this.next.study(info);
    }
  }

  static final class UnixDot extends Pattern.Node
  {
    boolean match(Matcher matcher, int i, String seq)
    {
      if (i < matcher.to) {
        char ch = seq.charAt(i);
        return ((ch != '\n') && (this.next.match(matcher, i + 1, seq)));
      }
      return false; }

    boolean study(Pattern.TreeInfo info) {
      info.minLength += 1;
      info.maxLength += 1;
      return this.next.study(info);
    }
  }

  static final class Dot extends Pattern.Node
  {
    boolean match(Matcher matcher, int i, String seq)
    {
      if (i < matcher.to) {
        char ch = seq.charAt(i);
        return ((ch != '\n') && (ch != '\r') && ((ch | 0x1) != 8233) && (ch != 133) && (this.next.match(matcher, i + 1, seq)));
      }

      return false; }

    boolean study(Pattern.TreeInfo info) {
      info.minLength += 1;
      info.maxLength += 1;
      return this.next.study(info);
    }
  }

  static final class All extends Pattern.Node
  {
    Pattern.Node dup(boolean not)
    {
      if (not) {
        return new Pattern.Single(-1);
      }
      return new All();
    }

    boolean match(Matcher matcher, int i, String seq) {
      return ((i < matcher.to) && (this.next.match(matcher, i + 1, seq))); }

    boolean study(Pattern.TreeInfo info) {
      info.minLength += 1;
      info.maxLength += 1;
      return this.next.study(info);
    }
  }

  static class CINotRange extends Pattern.NotRange
  {
    int lower;
    int upper;

    CINotRange(int n)
    {
      this.lower = (n >>> 16);
      this.upper = (n & 0xFFFF); }

    Pattern.Node dup(boolean not) {
      if (not) {
        return new Pattern.CIRange((this.lower << 16) + this.upper);
      }
      return new CINotRange((this.lower << 16) + this.upper);
    }

    boolean match(Matcher matcher, int i, String seq) {
      if (i < matcher.to) {
        char ch = seq.charAt(i);
        boolean m = (ch - this.lower | this.upper - ch) < 0;
        if (m) {
          ch = Character.toUpperCase(ch);
          m = (ch - this.lower | this.upper - ch) < 0;
          if (m) {
            ch = Character.toLowerCase(ch);
            m = (ch - this.lower | this.upper - ch) < 0;
          }
        }

        return ((m) && (this.next.match(matcher, i + 1, seq)));
      }
      return false;
    }
  }

  static class NotRange extends Pattern.Node
  {
    int lower;
    int upper;

    NotRange()
    {
    }

    NotRange(int n)
    {
      this.lower = (n >>> 16);
      this.upper = (n & 0xFFFF); }

    Pattern.Node dup(boolean not) {
      if (not) {
        return new Pattern.Range((this.lower << 16) + this.upper);
      }
      return new NotRange((this.lower << 16) + this.upper);
    }

    boolean match(Matcher matcher, int i, String seq) {
      if (i < matcher.to) {
        char ch = seq.charAt(i);
        return (((ch - this.lower | this.upper - ch) < 0) && (this.next.match(matcher, i + 1, seq)));
      }

      return false; }

    boolean study(Pattern.TreeInfo info) {
      info.minLength += 1;
      info.maxLength += 1;
      return this.next.study(info);
    }
  }

  static final class CIRange extends Pattern.Range
  {
    CIRange(int n)
    {
      this.lower = (n >>> 16);
      this.upper = (n & 0xFFFF); }

    Pattern.Node dup(boolean not) {
      if (not) {
        return new Pattern.CINotRange((this.lower << 16) + this.upper);
      }
      return new CIRange((this.lower << 16) + this.upper); }

    boolean match(Matcher matcher, int i, String seq) {
      if (i < matcher.to) {
        char ch = seq.charAt(i);
        boolean m = (ch - this.lower | this.upper - ch) >= 0;
        if (!(m)) {
          ch = Character.toUpperCase(ch);
          m = (ch - this.lower | this.upper - ch) >= 0;
          if (!(m)) {
            ch = Character.toLowerCase(ch);
            m = (ch - this.lower | this.upper - ch) >= 0;
          }
        }
        return ((m) && (this.next.match(matcher, i + 1, seq)));
      }
      return false;
    }
  }

  static class Range extends Pattern.Node
  {
    int lower;
    int upper;

    Range()
    {
    }

    Range(int n)
    {
      this.lower = (n >>> 16);
      this.upper = (n & 0xFFFF); }

    Pattern.Node dup(boolean not) {
      if (not) {
        return new Pattern.NotRange((this.lower << 16) + this.upper);
      }
      return new Range((this.lower << 16) + this.upper); }

    boolean match(Matcher matcher, int i, String seq) {
      if (i < matcher.to) {
        char ch = seq.charAt(i);
        return (((ch - this.lower | this.upper - ch) >= 0) && (this.next.match(matcher, i + 1, seq)));
      }

      return false; }

    boolean study(Pattern.TreeInfo info) {
      info.minLength += 1;
      info.maxLength += 1;
      return this.next.study(info);
    }
  }

  static final class SliceU extends Pattern.Node
  {
    char[] buffer;

    SliceU(char[] buf)
    {
      this.buffer = buf; }

    boolean match(Matcher matcher, int i, String seq) {
      char[] buf = this.buffer;
      int len = buf.length;
      if (i + len > matcher.to) {
        return false;
      }
      for (int j = 0; j < len; ++j) {
        char c = seq.charAt(i + j);
        c = Character.toUpperCase(c);
        c = Character.toLowerCase(c);
        if (buf[j] != c) {
          return false;
        }
      }
      return this.next.match(matcher, i + len, seq); }

    boolean study(Pattern.TreeInfo info) {
      info.minLength += this.buffer.length;
      info.maxLength += this.buffer.length;
      return this.next.study(info);
    }
  }

  static final class SliceA extends Pattern.Node
  {
    char[] buffer;

    SliceA(char[] buf)
    {
      this.buffer = buf; }

    boolean match(Matcher matcher, int i, String seq) {
      char[] buf = this.buffer;
      int len = buf.length;
      if (i + len > matcher.to) {
        return false;
      }
      for (int j = 0; j < len; ++j) {
        int c = ASCII.toLower(seq.charAt(i + j));
        if (buf[j] != c) {
          return false;
        }
      }
      return this.next.match(matcher, i + len, seq); }

    boolean study(Pattern.TreeInfo info) {
      info.minLength += this.buffer.length;
      info.maxLength += this.buffer.length;
      return this.next.study(info);
    }
  }

  static final class Slice extends Pattern.Node
  {
    char[] buffer;

    Slice(char[] buf)
    {
      this.buffer = buf; }

    boolean match(Matcher matcher, int i, String seq) {
      char[] buf = this.buffer;
      int len = buf.length;
      if (i + len > matcher.to) {
        return false;
      }
      for (int j = 0; j < len; ++j) {
        if (buf[j] != seq.charAt(i + j))
          return false;
      }
      return this.next.match(matcher, i + len, seq); }

    boolean study(Pattern.TreeInfo info) {
      info.minLength += this.buffer.length;
      info.maxLength += this.buffer.length;
      return this.next.study(info);
    }
  }

  static final class Not extends Pattern.Node
  {
    Pattern.Node atom;

    Not(Pattern.Node atom)
    {
      this.atom = atom; }

    boolean match(Matcher matcher, int i, String seq) {
      return ((!(this.atom.match(matcher, i, seq))) && (this.next.match(matcher, i + 1, seq))); }

    boolean study(Pattern.TreeInfo info) {
      info.minLength += 1;
      info.maxLength += 1;
      return this.next.study(info);
    }
  }

  static final class Specials extends Pattern.Node
  {
    Pattern.Node dup(boolean not)
    {
      if (not) {
        return new Pattern.Not(this);
      }
      return new Specials(); }

    boolean match(Matcher matcher, int i, String seq) {
      if (i < matcher.to) {
        int ch = seq.charAt(i);
        return (((((ch - 65520 | 65533 - ch) >= 0) || (ch == 65279))) && (this.next.match(matcher, i + 1, seq)));
      }

      return false; }

    boolean study(Pattern.TreeInfo info) {
      info.minLength += 1;
      info.maxLength += 1;
      return this.next.study(info);
    }
  }

  static final class NotCtype extends Pattern.Node
  {
    int ctype;

    NotCtype(int type)
    {
      this.ctype = type; }

    Pattern.Node dup(boolean not) {
      if (not) {
        return new Pattern.Ctype(this.ctype);
      }
      return new NotCtype(this.ctype);
    }

    boolean match(Matcher matcher, int i, String seq) {
      return ((i < matcher.to) && (!(ASCII.isType(seq.charAt(i), this.ctype))) && (this.next.match(matcher, i + 1, seq)));
    }

    boolean study(Pattern.TreeInfo info)
    {
      info.minLength += 1;
      info.maxLength += 1;
      return this.next.study(info);
    }
  }

  static final class Ctype extends Pattern.Node
  {
    int ctype;

    Ctype(int type)
    {
      this.ctype = type; }

    Pattern.Node dup(boolean not) {
      if (not) {
        return new Pattern.NotCtype(this.ctype);
      }
      return new Ctype(this.ctype);
    }

    boolean match(Matcher matcher, int i, String seq) {
      return ((i < matcher.to) && (ASCII.isType(seq.charAt(i), this.ctype)) && (this.next.match(matcher, i + 1, seq)));
    }

    boolean study(Pattern.TreeInfo info)
    {
      info.minLength += 1;
      info.maxLength += 1;
      return this.next.study(info);
    }
  }

  static final class Category extends Pattern.Node
  {
    int atype;

    Category(int type)
    {
      this.atype = type; }

    Pattern.Node dup(boolean not) {
      return new Category((not) ? this.atype ^ 0xFFFFFFFF : this.atype); }

    boolean match(Matcher matcher, int i, String seq) {
      return ((i < matcher.to) && ((this.atype & 1 << Character.getType(seq.charAt(i))) != 0) && (this.next.match(matcher, i + 1, seq)));
    }

    boolean study(Pattern.TreeInfo info)
    {
      info.minLength += 1;
      info.maxLength += 1;
      return this.next.study(info);
    }
  }

  static final class NotSingleU extends Pattern.Node
  {
    int ch;

    NotSingleU(int c)
    {
      this.ch = Character.toLowerCase(Character.toUpperCase((char)c)); }

    Pattern.Node dup(boolean not) {
      if (not) {
        return new Pattern.SingleU(this.ch);
      }
      return new NotSingleU(this.ch); }

    boolean match(Matcher matcher, int i, String seq) {
      if (i < matcher.to) {
        char c = seq.charAt(i);
        if (c == this.ch)
          return false;
        c = Character.toUpperCase(c);
        c = Character.toLowerCase(c);
        if (c != this.ch)
          return this.next.match(matcher, i + 1, seq);
      }
      return false; }

    boolean study(Pattern.TreeInfo info) {
      info.minLength += 1;
      info.maxLength += 1;
      return this.next.study(info);
    }
  }

  static final class SingleU extends Pattern.Node
  {
    int ch;

    SingleU(int c)
    {
      this.ch = Character.toLowerCase(Character.toUpperCase((char)c)); }

    Pattern.Node dup(boolean not) {
      if (not) {
        return new Pattern.NotSingleU(this.ch);
      }
      return new SingleU(this.ch); }

    boolean match(Matcher matcher, int i, String seq) {
      if (i < matcher.to) {
        char c = seq.charAt(i);
        if (c == this.ch)
          return this.next.match(matcher, i + 1, seq);
        c = Character.toUpperCase(c);
        c = Character.toLowerCase(c);
        if (c == this.ch)
          return this.next.match(matcher, i + 1, seq);
      }
      return false; }

    boolean study(Pattern.TreeInfo info) {
      info.minLength += 1;
      info.maxLength += 1;
      return this.next.study(info);
    }
  }

  static final class NotSingleA extends Pattern.Node
  {
    int ch;

    NotSingleA(int n)
    {
      this.ch = ASCII.toLower(n); }

    Pattern.Node dup(boolean not) {
      if (not) {
        return new Pattern.SingleA(this.ch);
      }
      return new NotSingleA(this.ch); }

    boolean match(Matcher matcher, int i, String seq) {
      if (i < matcher.to) {
        int c = seq.charAt(i);
        if ((c != this.ch) && (ASCII.toLower(c) != this.ch)) {
          return this.next.match(matcher, i + 1, seq);
        }
      }
      return false;
    }

    boolean study(Pattern.TreeInfo info) {
      info.minLength += 1;
      info.maxLength += 1;
      return this.next.study(info);
    }
  }

  static final class SingleA extends Pattern.Node
  {
    int ch;

    SingleA(int n)
    {
      this.ch = ASCII.toLower(n); }

    Pattern.Node dup(boolean not) {
      if (not) {
        return new Pattern.NotSingleA(this.ch);
      }
      return new SingleA(this.ch); }

    boolean match(Matcher matcher, int i, String seq) {
      if (i < matcher.to) {
        int c = seq.charAt(i);
        if ((c == this.ch) || (ASCII.toLower(c) == this.ch)) {
          return this.next.match(matcher, i + 1, seq);
        }
      }
      return false;
    }

    boolean study(Pattern.TreeInfo info) {
      info.minLength += 1;
      info.maxLength += 1;
      return this.next.study(info);
    }
  }

  static final class NotSingle extends Pattern.Node
  {
    int ch;

    NotSingle(int n)
    {
      this.ch = n; }

    Pattern.Node dup(boolean not) {
      if (not) {
        return new Pattern.Single(this.ch);
      }
      return new NotSingle(this.ch); }

    boolean match(Matcher matcher, int i, String seq) {
      return ((i < matcher.to) && (seq.charAt(i) != this.ch) && (this.next.match(matcher, i + 1, seq)));
    }

    boolean study(Pattern.TreeInfo info)
    {
      info.minLength += 1;
      info.maxLength += 1;
      return this.next.study(info);
    }
  }

  static final class Single extends Pattern.Node
  {
    int ch;

    Single(int n)
    {
      this.ch = n; }

    Pattern.Node dup(boolean not) {
      if (not) {
        return new Pattern.NotSingle(this.ch);
      }
      return new Single(this.ch); }

    boolean match(Matcher matcher, int i, String seq) {
      return ((i < matcher.to) && (seq.charAt(i) == this.ch) && (this.next.match(matcher, i + 1, seq)));
    }

    boolean study(Pattern.TreeInfo info)
    {
      info.minLength += 1;
      info.maxLength += 1;
      return this.next.study(info);
    }
  }

  static final class UnixDollar extends Pattern.Node
  {
    boolean multiline;

    UnixDollar(boolean mul)
    {
      this.multiline = mul; }

    boolean match(Matcher matcher, int i, String seq) {
      if (i < matcher.to) {
        char ch = seq.charAt(i);
        if (ch == '\n')
        {
          if ((!(this.multiline)) && (i != matcher.to - 1))
            return false;
        }
        else return false;
      }

      return this.next.match(matcher, i, seq); }

    boolean study(Pattern.TreeInfo info) {
      this.next.study(info);
      return info.deterministic;
    }
  }

  static final class Dollar extends Pattern.Node
  {
    boolean multiline;

    Dollar(boolean mul)
    {
      this.multiline = mul;
    }

    boolean match(Matcher matcher, int i, String seq)
    {
      char ch;
      if (!(this.multiline)) {
        if (i < matcher.to - 2)
          return false;
        if (i == matcher.to - 2) {
          ch = seq.charAt(i);
          if (ch != '\r')
            return false;
          ch = seq.charAt(i + 1);
          if (ch != '\n') {
            return false;
          }
        }
      }

      if (i < matcher.to) {
        ch = seq.charAt(i);
        if (ch == '\n')
        {
          if ((i > 0) && (seq.charAt(i - 1) == '\r'))
            return false; 
        }
        else if ((ch != '\r') && (ch != 133)) if ((ch | 0x1) != 8233)
          {
            return false;
          }
      }
      return this.next.match(matcher, i, seq); }

    boolean study(Pattern.TreeInfo info) {
      this.next.study(info);
      return info.deterministic;
    }
  }

  static final class LastMatch extends Pattern.Node
  {
    boolean match(Matcher matcher, int i, String seq)
    {
      if (i != matcher.oldLast)
        return false;
      return this.next.match(matcher, i, seq);
    }
  }

  static final class UnixCaret extends Pattern.Node
  {
    boolean match(Matcher matcher, int i, String seq)
    {
      if (i > matcher.from) {
        char ch = seq.charAt(i - 1);
        if (ch != '\n') {
          return false;
        }
      }

      if (i == matcher.to)
        return false;
      return this.next.match(matcher, i, seq);
    }
  }

  static final class Caret extends Pattern.Node
  {
    boolean match(Matcher matcher, int i, String seq)
    {
      if (i > matcher.from) {
        char ch = seq.charAt(i - 1);
        if ((ch != '\n') && (ch != '\r') && ((ch | 0x1) != 8233) && (ch != 133))
        {
          return false;
        }

        if ((ch == '\r') && (seq.charAt(i) == '\n')) {
          return false;
        }
      }
      if (i == matcher.to)
        return false;
      return this.next.match(matcher, i, seq);
    }
  }

  static final class End extends Pattern.Node
  {
    boolean match(Matcher matcher, int i, String seq)
    {
      return ((i == matcher.to) && (this.next.match(matcher, i, seq)));
    }
  }

  static final class Begin extends Pattern.Node
  {
    boolean match(Matcher matcher, int i, String seq)
    {
      if ((i == matcher.from) && (this.next.match(matcher, i, seq))) {
        matcher.first = i;
        matcher.groups[0] = i;
        matcher.groups[1] = matcher.last;
        return true;
      }
      return false;
    }
  }

  static final class Start extends Pattern.Node
  {
    int minLength;

    Start(Pattern.Node node)
    {
      this.next = node;
      Pattern.TreeInfo info = new Pattern.TreeInfo();
      this.next.study(info);
      this.minLength = info.minLength; }

    boolean match(Matcher matcher, int i, String seq) {
      if (i > matcher.to - this.minLength)
        return false;
      boolean ret = false;
      int guard = matcher.to - this.minLength;
      for (; i <= guard; ++i) {
        if ((ret = this.next.match(matcher, i, seq)))
          break;
      }
      if (ret) {
        matcher.first = i;
        matcher.groups[0] = matcher.first;
        matcher.groups[1] = matcher.last;
      }
      return ret; }

    boolean study(Pattern.TreeInfo info) {
      this.next.study(info);
      info.maxValid = false;
      info.deterministic = false;
      return false;
    }
  }

  static class Dummy extends Pattern.Node
  {
    boolean match(Matcher matcher, int i, String seq)
    {
      return this.next.match(matcher, i, seq);
    }
  }

  static class LastNode extends Pattern.Node
  {
    boolean match(Matcher matcher, int i, String seq)
    {
      if ((matcher.acceptMode == 1) && (i != matcher.to))
        return false;
      matcher.last = i;
      matcher.groups[0] = matcher.first;
      matcher.groups[1] = matcher.last;
      return true;
    }
  }

  static class Node
  {
    Node next;

    Node()
    {
      this.next = Pattern.accept; }

    Node dup(boolean not) {
      if (not) {
        return new Pattern.Not(this);
      }
      throw new RuntimeException("internal error in Node dup()");
    }

    boolean match(Matcher matcher, int i, String seq)
    {
      matcher.last = i;
      matcher.groups[0] = matcher.first;
      matcher.groups[1] = matcher.last;
      return true;
    }

    boolean study(Pattern.TreeInfo info)
    {
      if (this.next != null) {
        return this.next.study(info);
      }
      return info.deterministic;
    }
  }

  static final class BitClass extends Pattern.Node
  {
    boolean[] bits = new boolean[256];
    boolean complementMe = false;

    BitClass(boolean not) { this.complementMe = not; }

    BitClass(boolean[] newBits, boolean not) {
      this.complementMe = not;
      this.bits = newBits; }

    Pattern.Node add(int c, int f) {
      if ((f & 0x2) == 0) {
        this.bits[c] = true;
        return this;
      }
      if (c < 128) {
        this.bits[c] = true;
        if (ASCII.isUpper(c)) {
          c += 32;
          this.bits[c] = true;
        } else if (ASCII.isLower(c)) {
          c -= 32;
          this.bits[c] = true;
        }
        return this;
      }
      c = Character.toLowerCase((char)c);
      this.bits[c] = true;
      c = Character.toUpperCase((char)c);
      this.bits[c] = true;
      return this; }

    Pattern.Node dup(boolean not) {
      return new BitClass(this.bits, not); }

    boolean match(Matcher matcher, int i, String seq) {
      if (i < matcher.to) {
        int c = seq.charAt(i);
        if (c > 255)
          return false;
        if (this.complementMe) {
          return ((this.bits[c] == 0) && (this.next.match(matcher, i + 1, seq)));
        }
        if (this.bits[c] != 0)
          return this.next.match(matcher, i + 1, seq);
      }
      return false; }

    boolean study(Pattern.TreeInfo info) {
      info.minLength += 1;
      info.maxLength += 1;
      return this.next.study(info);
    }
  }

  static final class TreeInfo
  {
    int minLength;
    int maxLength;
    boolean maxValid;
    boolean deterministic;

    TreeInfo()
    {
      reset(); }

    void reset() {
      this.minLength = 0;
      this.maxLength = 0;
      this.maxValid = true;
      this.deterministic = true;
    }
  }
}
