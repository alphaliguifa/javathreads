import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class Normalizer
  implements Cloneable
{
  private static final Log log = LogFactory.getLog(Normalizer.class);
  public static final char DONE = 65535;
  private static final int COMPAT_BIT = 1;
  private static final int DECOMP_BIT = 2;
  private static final int COMPOSE_BIT = 4;
  public static final Mode NO_OP = new Mode(0);

  public static final Mode COMPOSE = new Mode(4);

  public static final Mode COMPOSE_COMPAT = new Mode(5);

  public static final Mode DECOMP = new Mode(2);

  public static final Mode DECOMP_COMPAT = new Mode(3);
  public static final int IGNORE_HANGUL = 1;
  static final char HANGUL_BASE = 44032;
  static final char HANGUL_LIMIT = 55204;
  private static final char JAMO_LBASE = 4352;
  private static final char JAMO_VBASE = 4449;
  private static final char JAMO_TBASE = 4519;
  private static final int JAMO_LCOUNT = 19;
  private static final int JAMO_VCOUNT = 21;
  private static final int JAMO_TCOUNT = 28;
  private static final int JAMO_NCOUNT = 588;
  private Mode mode;
  private int options;
  private transient int minDecomp;
  private int currentIndex;
  private int nextIndex;
  private CharacterIterator text;
  private boolean atEnd;
  private StringBuffer buffer;
  private int bufferPos;
  private char currentChar;
  private static final int EMPTY = -1;
  private StringBuffer explodeBuf;
  static final int STR_INDEX_SHIFT = 2;
  static final int STR_LENGTH_MASK = 3;

  public Normalizer(String str, Mode mode)
  {
    this(new StringCharacterIterator(str), mode, 0);
  }

  public Normalizer(String str, Mode mode, int opt)
  {
    this(new StringCharacterIterator(str), mode, opt);
  }

  public Normalizer(CharacterIterator iter, Mode mode)
  {
    this(iter, mode, 0);
  }

  public Normalizer(CharacterIterator iter, Mode mode, int opt)
  {
    this.mode = DECOMP;
    this.options = 0;

    this.currentIndex = 0;
    this.nextIndex = 0;

    this.atEnd = false;

    this.buffer = null;
    this.bufferPos = 0;

    this.explodeBuf = null;

    this.text = iter;
    this.mode = mode;
    this.options = opt;

    this.minDecomp = ((mode.compat()) ? 0 : 11177);
  }

  public Object clone()
  {
    try
    {
      Normalizer copy = (Normalizer)super.clone();
      copy.text = ((CharacterIterator)this.text.clone());

      if (this.buffer != null) {
        copy.buffer = new StringBuffer();
        if (this.buffer.length() > 0) {
          copy.buffer.append(this.buffer);
        }
      }
      return copy;
    }
    catch (CloneNotSupportedException e) {
      throw new InternalError(e.toString());
    }
  }

  public static String normalize(String str, Mode mode, int options)
  {
    return normalize(str, mode, options, false);
  }

  public static String normalize(String str, Mode mode, int options, boolean addSingleQuotation) {
    if (mode.compose())
    {
      return compose(str, mode.compat(), options);
    }
    if (mode.decomp()) {
      return decompose(str, mode.compat(), options, addSingleQuotation);
    }
    return str;
  }

  public static String compose(String source, boolean compat, int options)
  {
    StringBuffer result = new StringBuffer();
    StringBuffer explodeBuf = new StringBuffer();

    int explodePos = -1;
    int basePos = 0;
    int baseIndex = 0;
    int classesSeenL = 0;
    int classesSeenH = 0;

    int minExplode = (compat) ? 0 : 4341;
    int minDecomp = (compat) ? 0 : 11177;

    if (log.isDebugEnabled()) log.debug("minExplode = " + minExplode);

    int i = 0;
    while ((i < source.length()) || (explodePos != -1))
    {
      char ch;
      if (explodePos == -1) {
        ch = source.charAt(i++);
      } else {
        ch = explodeBuf.charAt(explodePos++);
        if (explodePos >= explodeBuf.length()) {
          explodePos = -1;
          explodeBuf.setLength(0);
        }

      }

      int charInfo = composeLookup(ch);
      int type = charInfo & 0x7;
      int index = charInfo >>> 3;

      if (log.isDebugEnabled()) log.debug("Got char " + Utility.hex(ch) + ", type=" + type + ", index=" + index);

      if ((type == 1) || ((type == 2) && (index < minExplode)))
      {
        if (log.isDebugEnabled()) log.debug("New base " + Utility.hex(ch) + ", type=" + type + ", index=" + index);

        classesSeenL = classesSeenH = 0;
        baseIndex = index;
        basePos = result.length();
        result.append(ch);
      }
      else if (type == 3)
      {
        int cclass = ComposeData.typeBit[index];

        boolean seen = 0 != ((cclass < 32) ? classesSeenL & 1 << cclass : classesSeenH & 1 << (cclass & 0x1F));

        if (log.isDebugEnabled()) log.debug("Class of " + Utility.hex(ch) + " = " + cclass + " seen:" + seen + " baseIndex:" + baseIndex + " action:" + composeAction(baseIndex, index));
        int action;
        if ((index < 55) && (!(seen)) && ((action = composeAction(baseIndex, index)) > 0))
        {
          char newBase;
          if (action > 64285)
          {
            if (log.isDebugEnabled()) log.debug("Pairwise exploding");
            newBase = pairExplode(explodeBuf, action);
            explodePos = 0;
            result.setCharAt(basePos, newBase);

            baseIndex = composeLookup(newBase) >>> 3;
            if (log.isDebugEnabled()) log.debug("New base " + Utility.hex(newBase));
          }
          else {
            if (log.isDebugEnabled()) log.debug("Pairwise combining");
            newBase = (char)action;
            result.setCharAt(basePos, newBase);

            baseIndex = composeLookup(newBase) >>> 3;
            if (log.isDebugEnabled()) log.debug("New base " + Utility.hex(newBase));

          }

          int len = result.length();
          if (len - basePos > 1) {
            for (int j = basePos + 1; j < len; ++j) {
              explodeBuf.append(result.charAt(j));
            }
            result.setLength(basePos + 1);
            classesSeenL = classesSeenH = 0;
            if (explodePos == -1) explodePos = 0;
          }
        }
        else {
          if (log.isDebugEnabled()) log.debug("No action");
          bubbleAppend(result, ch, cclass);
          if (cclass < 32)
            classesSeenL |= 1 << cclass;
          else {
            classesSeenH |= 1 << (cclass & 0x1F);
          }
        }
      }
      else if (index > minExplode)
      {
        explode(explodeBuf, index);
        explodePos = 0;
        if (log.isDebugEnabled()) log.debug("explosion: " + Utility.hex(ch) + " --> " + Utility.hex(explodeBuf));

      }
      else if ((type == 7) && (minExplode == 0))
      {
        hangulToJamo(ch, explodeBuf, minDecomp);
        if (log.isDebugEnabled()) log.debug("decomposed hangul " + Utility.hex(ch) + " to jamo " + Utility.hex(explodeBuf));

        explodePos = 0;
      }
      else if (type == 4) {
        classesSeenL = classesSeenH = 0;
        baseIndex = 8983;
        basePos = result.length();
        result.append(ch);
        if (log.isDebugEnabled()) log.debug("got initial jamo " + Utility.hex(ch));
      }
      else if ((type == 5) && (classesSeenL == 0) && (classesSeenH == 0) && (baseIndex == 8983))
      {
        int l = result.charAt(basePos) - 4352;
        int v = ch - 4449;
        char newCh = (char)(44032 + (l * 21 + v) * 28);
        result.setCharAt(basePos, newCh);

        if (log.isDebugEnabled()) log.debug("got medial jamo " + Utility.hex(ch) + ", replacing with Hangul " + Utility.hex(newCh));

        baseIndex = 8984;
      }
      else if ((type == 6) && (classesSeenL == 0) && (classesSeenH == 0) && (baseIndex == 8984))
      {
        char newCh = (char)(result.charAt(basePos) + ch - 4519);
        result.setCharAt(basePos, newCh);

        if (log.isDebugEnabled()) log.debug("got final jamo " + Utility.hex(ch) + ", replacing with Hangul " + Utility.hex(newCh));

        baseIndex = 0;
        basePos = -1;
        classesSeenL = classesSeenH = 0;
      } else {
        if (log.isDebugEnabled()) log.debug("No base as of " + Utility.hex(ch));
        baseIndex = 0;
        basePos = -1;
        classesSeenL = classesSeenH = 0;
        result.append(ch);
      }
    }
    return result.toString();
  }

  private char nextCompose()
  {
    if (log.isDebugEnabled()) log.debug("--------------- top of nextCompose() ---------------");

    int explodePos = -1;
    int basePos = 0;
    int baseIndex = 0;
    int classesSeenL = 0;
    int classesSeenH = 0;

    char lastBase = '\0';
    boolean chFromText = true;

    this.currentIndex = this.nextIndex;
    this.text.setIndex(this.currentIndex);

    int minExplode = (this.mode.compat()) ? 0 : 4341;
    int minDecomp = (this.mode.compat()) ? 0 : 11177;

    initBuffer();
    if (this.explodeBuf == null)
      this.explodeBuf = new StringBuffer();
    else {
      this.explodeBuf.setLength(0);
    }

    char ch = curForward();

    while (ch != 65535)
    {
      int charInfo = composeLookup(ch);
      int type = charInfo & 0x7;
      int index = charInfo >>> 3;

      if ((type == 1) || ((type == 2) && (index < minExplode)))
      {
        if ((getBufferLength() > 0) && (chFromText) && (explodePos == -1))
        {
          if (!(log.isDebugEnabled())) break; log.debug("returning early because we hit a new base"); break;
        }

        classesSeenL = classesSeenH = 0;
        baseIndex = index;
        basePos = getBufferLength();
        this.buffer.append(ch);
        if (log.isDebugEnabled()) log.debug("got BASE char " + Utility.hex(ch) + ", type=" + type + ", index=" + index);

        lastBase = ch;
      }
      else if (type == 3)
      {
        int cclass = ComposeData.typeBit[index];
        boolean seen = 0 != ((cclass < 32) ? classesSeenL & 1 << cclass : classesSeenH & 1 << (cclass & 0x1F));

        if (log.isDebugEnabled()) log.debug("got COMBINING char " + Utility.hex(ch) + ", type=" + type + ", index=" + index + ", class=" + cclass);
        int action;
        if ((index < 55) && (!(seen)) && ((action = composeAction(baseIndex, index)) > 0))
        {
          char newBase;
          if (action > 64285)
          {
            newBase = pairExplode(this.explodeBuf, action);
            explodePos = 0;
            this.buffer.setCharAt(basePos, newBase);

            baseIndex = composeLookup(newBase) >>> 3;

            if (log.isDebugEnabled()) log.debug("Pairwise explosion: " + Utility.hex(lastBase) + "," + Utility.hex(ch) + " --> " + Utility.hex(newBase) + "," + Utility.hex(this.explodeBuf));

            lastBase = newBase;
          }
          else {
            newBase = (char)action;
            this.buffer.setCharAt(basePos, newBase);

            baseIndex = composeLookup(newBase) >>> 3;

            if (log.isDebugEnabled()) log.debug("Pairwise combination: " + Utility.hex(lastBase) + "," + Utility.hex(ch) + " --> " + Utility.hex(newBase));

            lastBase = newBase;
          }

          int len = getBufferLength();
          if (len - basePos > 1) {
            if (log.isDebugEnabled()) log.debug("Reprocessing combining marks");
            for (int j = basePos + 1; j < len; ++j) {
              this.explodeBuf.append(this.buffer.charAt(j));
            }
            this.buffer.setLength(basePos + 1);
            classesSeenL = classesSeenH = 0;
            if (explodePos == -1) explodePos = 0;
          }
        } else {
          if (log.isDebugEnabled()) log.debug("char doesn't combine");

          bubbleAppend(this.buffer, ch, cclass);
          if (cclass < 32)
            classesSeenL |= 1 << cclass;
          else {
            classesSeenH |= 1 << (cclass & 0x1F);
          }
        }
      }
      else if (index > minExplode)
      {
        explode(this.explodeBuf, index);
        explodePos = 0;
        if (log.isDebugEnabled()) log.debug("explosion: " + Utility.hex(ch) + " --> " + Utility.hex(this.explodeBuf));

      }
      else if ((type == 7) && (minExplode == 0))
      {
        hangulToJamo(ch, this.explodeBuf, minDecomp);
        if (log.isDebugEnabled()) log.debug("decomposed hangul " + Utility.hex(ch) + " to jamo " + Utility.hex(this.explodeBuf));

        explodePos = 0;
      }
      else if (type == 4) {
        if ((getBufferLength() > 0) && (chFromText) && (explodePos == -1))
        {
          if (!(log.isDebugEnabled())) break; log.debug("returning early because we hit a new base"); break;
        }

        classesSeenL = classesSeenH = 0;
        baseIndex = 8983;
        basePos = getBufferLength();
        this.buffer.append(ch);
        if (log.isDebugEnabled()) log.debug("got initial jamo " + Utility.hex(ch));
      }
      else if ((type == 5) && (classesSeenL == 0) && (classesSeenH == 0) && (baseIndex == 8983))
      {
        int l = this.buffer.charAt(basePos) - 4352;
        int v = ch - 4449;
        char newCh = (char)(44032 + (l * 21 + v) * 28);
        this.buffer.setCharAt(basePos, newCh);

        if (log.isDebugEnabled()) log.debug("got medial jamo " + Utility.hex(ch) + ", replacing with Hangul " + Utility.hex(newCh));

        baseIndex = 8984;
      }
      else if ((type == 6) && (classesSeenL == 0) && (classesSeenH == 0) && (baseIndex == 8984))
      {
        char newCh = (char)(this.buffer.charAt(basePos) + ch - 4519);
        this.buffer.setCharAt(basePos, newCh);

        if (log.isDebugEnabled()) log.debug("got final jamo " + Utility.hex(ch) + ", replacing with Hangul " + Utility.hex(newCh));

        baseIndex = 0;
        basePos = -1;
        classesSeenL = classesSeenH = 0;
      }
      else {
        baseIndex = 0;
        basePos = -1;
        classesSeenL = classesSeenH = 0;
        this.buffer.append(ch);
        if (log.isDebugEnabled()) log.debug("UNKNOWN char " + Utility.hex(ch));
      }

      if (explodePos == -1) {
        ch = this.text.next();
        chFromText = true;
      } else {
        ch = this.explodeBuf.charAt(explodePos++);
        if (explodePos >= this.explodeBuf.length()) {
          explodePos = -1;
          this.explodeBuf.setLength(0);
        }
        chFromText = false;
      }
    }
    if (getBufferLength() > 0)
      ch = this.buffer.charAt(0);
    else {
      ch = 65535;
    }
    this.nextIndex = this.text.getIndex();
    return ch;
  }

  private char prevCompose()
  {
    if (log.isDebugEnabled()) log.debug("--------------- top of prevCompose() ---------------");

    int minExplode = (this.mode.compat()) ? 0 : 4341;

    this.nextIndex = this.currentIndex;

    initBuffer();
    char ch;
    while ((ch = curBackward()) != 65535) {
      this.buffer.insert(0, ch);

      int charInfo = composeLookup(ch);
      int type = charInfo & 0x7;
      int index = charInfo >>> 3;

      if (log.isDebugEnabled()) log.debug("prevCompose got char " + Utility.hex(ch) + ", type=" + type + ", index=" + index + ", minExplode=" + minExplode);

      if ((type == 1) || ((type == 2) && (index < minExplode)) || (type == 7)) break; if (type == 4)
      {
        break;
      }

    }

    if (getBufferLength() > 0)
    {
      String composed = compose(this.buffer.toString(), this.mode.compat(), this.options);
      if (log.isDebugEnabled()) log.debug("prevCompose called compose(" + Utility.hex(this.buffer) + ")->" + Utility.hex(composed));

      this.buffer.setLength(0);
      this.buffer.append(composed);

      if (getBufferLength() > 1) {
        this.bufferPos = (getBufferLength() - 1);
        ch = this.buffer.charAt(this.bufferPos);
      } else {
        ch = this.buffer.charAt(0);
      }
    }
    else {
      ch = 65535;
    }
    this.currentIndex = this.text.getIndex();
    if (log.isDebugEnabled()) log.debug("prevCompose returning " + Utility.hex(ch));
    return ch;
  }

  private static void bubbleAppend(StringBuffer target, char ch, int cclass) {
    if (log.isDebugEnabled()) log.debug(" bubbleAppend(" + Utility.hex(target) + ", " + Utility.hex(ch) + ", " + cclass + ")");

    if (log.isDebugEnabled()) log.debug(" getComposeClass(" + Utility.hex(ch) + ")=" + getComposeClass(ch));

    if (log.isDebugEnabled()) log.debug(" target before bubbling is : " + Utility.hex(target));

    int i = target.length() - 1;
    while ((cclass != 1) && 
      (i >= 0)) {
      int iClass = getComposeClass(target.charAt(i));
      if (log.isDebugEnabled()) log.debug("  getComposeClass(" + Utility.hex(target.charAt(i)) + ")=" + getComposeClass(target.charAt(i)));

      if (log.isDebugEnabled()) log.debug(" bubbleAppend: target[" + i + "]=" + Utility.hex(target.charAt(i)) + " is iClass=" + iClass);

      if (log.isDebugEnabled()) log.debug(" bubbleAppend: for ch=" + Utility.hex(ch) + " class=" + cclass);

      if (iClass <= cclass)
        break;
      --i;
    }

    if (log.isDebugEnabled()) log.debug(" bubbleAppend inserting " + Utility.hex(ch) + " at index " + (i + 1));

    target.insert(i + 1, ch);

    if (!(log.isDebugEnabled())) return; log.debug(" target is : " + Utility.hex(target));
  }

  private static int getComposeClass(char ch) {
    int cclass = 0;
    int charInfo = composeLookup(ch);
    int type = charInfo & 0x7;
    if (type == 3) {
      cclass = ComposeData.typeBit[(charInfo >>> 3)];
    }
    return cclass;
  }

  static final int composeLookup(char ch) {
    return ComposeData.lookup.elementAt(ch);
  }

  static final int composeAction(int baseIndex, int comIndex) {
    return ComposeData.actions.elementAt((char)(baseIndex + 1024 * comIndex));
  }

  static final void explode(StringBuffer target, int index)
  {
    while ((ch = "".charAt(index++)) != 0)
    {
      char ch;
      target.append(ch); }
  }

  static final char pairExplode(StringBuffer target, int action) {
    int index = ComposeData.actionIndex[(action - 64285)];
    explode(target, index + 1);
    return "".charAt(index);
  }

  public static String decompose(String source, boolean compat, int options)
  {
    return decompose(source, compat, options, false);
  }

  public static String decompose(String source, boolean compat, int options, boolean addSingleQuotation)
  {
    if (log.isDebugEnabled()) log.debug("--------------- top of decompose() ---------------");

    boolean hangul = (options & 0x1) == 0;
    int minDecomp = (compat) ? 0 : 11177;

    StringBuffer result = new StringBuffer();
    StringBuffer buffer = new StringBuffer();
    StringBuffer tmpBuf = new StringBuffer();

    int i = 0; int bufPtr = -1;

    while ((i < source.length()) || (bufPtr >= 0))
    {
      char ch;
      if (bufPtr >= 0) {
        ch = buffer.charAt(bufPtr++);
        if (bufPtr == buffer.length())
          bufPtr = -1;
      }
      else {
        ch = source.charAt(i++);
      }

      int offset = DecompData.offsets.elementAt(ch);
      int index = offset & 0x7FFF;

      if (log.isDebugEnabled()) log.debug("decompose got " + Utility.hex(ch));

      if (index > minDecomp) {
        if ((offset & 0x8000) != 0) {
          if (log.isDebugEnabled()) log.debug(" " + Utility.hex(ch) + " has RECURSIVE decomposition, index=" + index);

          buffer.setLength(0);
          doAppend("", index, buffer);
          bufPtr = 0;
        } else {
          if (log.isDebugEnabled()) log.debug(" " + Utility.hex(ch) + " has decomposition, index=" + index);

          if (!(addSingleQuotation)) {
            doAppend("", index, result);
          } else {
            tmpBuf.setLength(0);
            doAppend("", index, tmpBuf);
            if ((tmpBuf.length() > 1) || (ch == 894) || (ch == 8175))
            {
              for (int j = 0; j < tmpBuf.length(); ++j) {
                char c = tmpBuf.charAt(j);
                if (((c >= '\t') && (c <= '\r')) || ((c >= ' ') && (c <= '/')) || ((c >= ':') && (c <= '@')) || ((c >= '[') && (c <= '`')) || ((c >= '{') && (c <= '~')))
                {
                  result.append('\'');
                  result.append(c);
                  result.append('\'');
                } else {
                  result.append(c);
                }
              }
            }
            else result.append(tmpBuf);
          }
        }
      }
      else if ((ch >= 44032) && (ch < 55204) && (hangul))
        hangulToJamo(ch, result, minDecomp);
      else {
        result.append(ch);
      }
    }
    fixCanonical(result);
    return result.toString();
  }

  private char nextDecomp()
  {
    if (log.isDebugEnabled()) log.debug("--------------- top of nextDecomp() ---------------");

    boolean hangul = (this.options & 0x1) == 0;
    this.currentIndex = this.nextIndex;
    char ch = curForward();

    int offset = DecompData.offsets.elementAt(ch);
    int index = offset & 0x7FFF;

    initBuffer();

    if ((index > this.minDecomp) || (DecompData.canonClass.elementAt(ch) != 0))
    {
      if (index > this.minDecomp) {
        if (log.isDebugEnabled()) log.debug(" " + Utility.hex(ch) + " has decomposition, index=" + index);

        doAppend("", index, this.buffer);

        if ((offset & 0x8000) != 0)
        {
          for (int i = 0; i < getBufferLength(); ++i) {
            ch = this.buffer.charAt(i);
            index = DecompData.offsets.elementAt(ch) & 0x7FFF;

            if (index > this.minDecomp)
              i += doReplace("", index, this.buffer, i);
          }
        }
      }
      else {
        this.buffer.append(ch);
      }
      boolean needToReorder = false;

      while (((ch = this.text.next()) != 65535) && (DecompData.canonClass.elementAt(ch) != 0))
      {
        needToReorder = true;

        index = DecompData.offsets.elementAt(ch) & 0x7FFF;
        if (index > this.minDecomp) {
          doAppend("", index, this.buffer);
        }
        this.buffer.append(ch);
      }

      if ((getBufferLength() > 1) && (needToReorder))
      {
        fixCanonical(this.buffer);
      }
      ch = this.buffer.charAt(0);
    }
    else {
      this.text.next();
      this.buffer.setLength(0);
      this.buffer.append(ch);

      if ((hangul) && (ch >= 44032) && (ch < 55204)) {
        clearBuffer();
        hangulToJamo(ch, this.buffer, this.minDecomp);
        ch = this.buffer.charAt(0);
      }
    }
    this.nextIndex = this.text.getIndex();

    if (log.isDebugEnabled()) log.debug("nextDecomp getBufferLength() " + getBufferLength() + " buffer : " + this.buffer.toString());

    if (log.isDebugEnabled()) log.debug("nextDecomp returning " + Utility.hex(ch) + ", text index=" + this.text.getIndex());

    return ch;
  }

  private char prevDecomp()
  {
    if (log.isDebugEnabled()) log.debug("--------------- top of prevDecomp() ---------------");

    boolean hangul = (this.options & 0x1) == 0;

    this.nextIndex = this.currentIndex;

    char ch = curBackward();

    int offset = DecompData.offsets.elementAt(ch);
    int index = offset & 0x7FFF;

    if (log.isDebugEnabled()) log.debug("prevDecomp got input char " + Utility.hex(ch));

    initBuffer();

    if ((index > this.minDecomp) || (DecompData.canonClass.elementAt(ch) != 0))
    {
      while (ch != 65535) {
        this.buffer.insert(0, ch);
        if (DecompData.canonClass.elementAt(ch) == 0) break;
        ch = this.text.previous();
      }

      if (log.isDebugEnabled()) log.debug("prevDecomp buffer: " + Utility.hex(this.buffer));

      for (int i = 0; i < getBufferLength(); ++i) {
        ch = this.buffer.charAt(i);
        offset = DecompData.offsets.elementAt(ch);
        index = offset & 0x7FFF;

        if (index > this.minDecomp) {
          int j = doReplace("", index, this.buffer, i);
          while (((offset & 0x8000) != 0) && 
            (i < j)) {
            ch = this.buffer.charAt(i);
            index = DecompData.offsets.elementAt(ch) & 0x7FFF;
            if (index > this.minDecomp)
              i += doReplace("", index, this.buffer, i);
            ++i;
          }

          i = j;
        }
      }

      if (log.isDebugEnabled()) log.debug("prevDecomp buffer after decomp: " + Utility.hex(this.buffer));

      if (getBufferLength() > 1)
      {
        fixCanonical(this.buffer);
      }
      this.bufferPos = (getBufferLength() - 1);
      ch = this.buffer.charAt(this.bufferPos);
    }
    else if ((hangul) && (ch >= 44032) && (ch < 55204)) {
      hangulToJamo(ch, this.buffer, this.minDecomp);
      getBufferLength();
      this.bufferPos = (getBufferLength() - 1);
      ch = this.buffer.charAt(this.bufferPos);
    }
    else {
      this.buffer.append(ch);
      getBufferLength();
      this.bufferPos = (getBufferLength() - 1);
    }

    this.currentIndex = this.text.getIndex();

    if (log.isDebugEnabled()) log.debug(" prevDecomp getBufferLength() " + getBufferLength() + " buffer : " + this.buffer.toString());

    if (log.isDebugEnabled()) log.debug(" prevDecomp returning '" + ch + "' " + Utility.hex(ch) + ", text index=" + this.text.getIndex());

    return ch;
  }

  public static final int getClass(char ch) {
    int value = DecompData.canonClass.elementAt(ch);
    return ((value >= 0) ? value : value + 256);
  }

  public char current()
  {
    if ((this.bufferPos >= getBufferLength()) || (getBufferLength() == 0)) {
      this.bufferPos = 0;

      if (this.mode.compose()) {
        this.currentChar = nextCompose();
        this.text.setIndex(this.currentIndex);
      }
      else if (this.mode.decomp()) {
        this.currentChar = nextDecomp();
        this.text.setIndex(this.currentIndex);
      }
      else if (this.currentIndex == 0) {
        this.currentChar = this.text.current();
      }
      else
      {
        this.currentChar = this.text.current();
      }
    }
    else
    {
      this.currentChar = this.buffer.charAt(this.bufferPos);
    }

    return this.currentChar;
  }

  public char first()
  {
    reset();
    return next();
  }

  public char last()
  {
    this.currentIndex = (--this.text.getEndIndex());
    this.text.setIndex(this.currentIndex);
    this.atEnd = true;
    this.currentChar = 65535;
    clearBuffer();
    return previous();
  }

  public char next()
  {
    if (this.buffer != null) { if (++this.bufferPos < this.buffer.length())
        this.currentChar = this.buffer.charAt(this.bufferPos);
    } else {
      this.bufferPos = 0;
      if (this.mode.compose()) {
        this.currentChar = nextCompose();
      }
      else if (this.mode.decomp()) {
        this.currentChar = nextDecomp();
      }
      else
      {
        this.currentChar = this.text.current();
        this.text.next();

        if (this.currentChar != 65535) {
          this.currentIndex = (++this.nextIndex);
        }
      }
    }
    return this.currentChar;
  }

  public char previous()
  {
    if (this.bufferPos > 0)
    {
      this.currentChar = this.buffer.charAt(--this.bufferPos);
    }
    else {
      this.bufferPos = 0;
      if (this.mode.compose()) {
        this.currentChar = prevCompose();
      }
      else if (this.mode.decomp()) {
        this.currentChar = prevDecomp();
      }
      else {
        this.text.setIndex(this.currentIndex);
        this.currentChar = this.text.previous();
        if (this.currentIndex != 0) {
          this.currentIndex = (--this.nextIndex);
        }
      }
    }
    return this.currentChar;
  }

  private int getBufferLength() {
    if (this.buffer == null) {
      return 0;
    }
    return this.buffer.length();
  }

  public char setIndex(int index)
  {
    setIndexOnly(index);
    return current();
  }

  public void setIndexOnly(int index) {
    this.currentIndex = (this.nextIndex = index);
    this.text.setIndex(index);
    this.currentChar = 65535;
    clearBuffer();
  }

  public final int getIndex()
  {
    return this.text.getIndex();
  }

  public final int getBeginIndex()
  {
    return this.text.getBeginIndex();
  }

  public final int getEndIndex()
  {
    return this.text.getEndIndex();
  }

  public void setMode(Mode newMode)
  {
    this.mode = newMode;
    this.minDecomp = ((this.mode.compat()) ? 0 : 11177);
  }

  public Mode getMode()
  {
    return this.mode;
  }

  public void setOption(int option, boolean value)
  {
    if (option != 1) {
      throw new IllegalArgumentException("Illegal option");
    }
    if (value)
      this.options |= option;
    else
      this.options &= (option ^ 0xFFFFFFFF);
  }

  public boolean getOption(int option)
  {
    return ((this.options & option) != 0);
  }

  public void setText(String newText)
  {
    this.text = new StringCharacterIterator(newText);
    reset();
  }

  public void setText(CharacterIterator newText)
  {
    this.text = newText;
    reset();
  }

  private final char curForward()
  {
    char ch = this.text.current();
    if (log.isDebugEnabled()) log.debug(" curForward returning " + Utility.hex(ch) + ", text index=" + this.text.getIndex());

    return ch;
  }

  private final char curBackward() {
    char ch = (this.atEnd) ? this.text.current() : this.text.previous();
    this.atEnd = false;
    if (log.isDebugEnabled()) log.debug(" curBackward returning " + Utility.hex(ch) + ", text index=" + this.text.getIndex());

    return ch;
  }

  static final int doAppend(String source, int offset, StringBuffer dest) {
    int index = offset >>> 2;
    int length = offset & 0x3;

    if (length == 0)
    {
      while ((ch = "".charAt(index++)) != 0)
      {
        char ch;
        dest.append(ch);
        ++length;
      }
    }
    else for (int i = 0; i < length; ++i) {
        dest.append("".charAt(index++));
      }

    return length;
  }

  static final int doInsert(String source, int offset, StringBuffer dest, int pos)
  {
    int index = offset >>> 2;
    int length = offset & 0x3;

    if (length == 0)
    {
      while ((ch = "".charAt(index++)) != 0)
      {
        char ch;
        dest.insert(pos++, ch);
        ++length;
      }
    }
    else for (int i = 0; i < length; ++i) {
        dest.insert(pos++, "".charAt(index++));
      }

    return length;
  }

  static final int doReplace(String source, int offset, StringBuffer dest, int pos)
  {
    int index = offset >>> 2;
    int length = offset & 0x3;

    dest.setCharAt(pos++, "".charAt(index++));
    if (length == 0)
    {
      while ((ch = "".charAt(index++)) != 0)
      {
        char ch;
        dest.insert(pos++, ch);
        ++length;
      }
    }
    else for (int i = 1; i < length; ++i) {
        dest.insert(pos++, "".charAt(index++));
      }

    return length;
  }

  public void reset() {
    this.currentIndex = (this.nextIndex = this.text.getBeginIndex());
    this.text.setIndex(this.currentIndex);
    this.atEnd = false;
    this.bufferPos = 0;
    clearBuffer();
  }

  private final void initBuffer() {
    if (this.buffer == null)
      this.buffer = new StringBuffer(10);
    else {
      this.buffer.setLength(0);
    }
    clearBuffer();
  }

  private final void clearBuffer() {
    this.bufferPos = 0;
    if (this.buffer != null)
      this.buffer.setLength(0);
  }

  private static void fixCanonical(StringBuffer result)
  {
    if (result.length() == 0) return;

    int i = result.length() - 1;
    int currentType = getClass(result.charAt(i));

    for (--i; i >= 0; --i) {
      int lastType = currentType;
      currentType = getClass(result.charAt(i));

      if ((currentType <= lastType) || (lastType == 0))
        continue;
      char temp = result.charAt(i);
      result.setCharAt(i, result.charAt(i + 1));
      result.setCharAt(i + 1, temp);

      if (i < result.length() - 2) {
        i += 2;
      }

      currentType = getClass(result.charAt(i));
    }
  }

  static int hangulToJamo(char ch, StringBuffer result, int decompLimit)
  {
    char sIndex = (char)(ch - 44032);
    char leading = (char)(4352 + sIndex / 588);
    char vowel = (char)(4449 + sIndex % 588 / 28);

    char trailing = (char)(4519 + sIndex % '\28');

    int length = 0;

    length += jamoAppend(leading, decompLimit, result);
    length += jamoAppend(vowel, decompLimit, result);
    if (trailing != 4519) {
      length += jamoAppend(trailing, decompLimit, result);
    }
    return length;
  }

  static final int jamoAppend(char ch, int limit, StringBuffer dest) {
    int offset = DecompData.offsets.elementAt(ch);
    if (offset > limit) {
      return doAppend("", offset, dest);
    }
    dest.append(ch);
    return 1;
  }

  public static final class Mode
  {
    final int mode;

    Mode(int m)
    {
      this.mode = m; }

    final boolean compat() {
      return ((this.mode & 0x1) != 0); }

    final boolean compose() {
      return ((this.mode & 0x4) != 0); }

    final boolean decomp() {
      return ((this.mode & 0x2) != 0);
    }
  }
}
