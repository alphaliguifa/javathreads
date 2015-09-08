public final class Utility
{
  static final char ESCAPE = 42405;
  static final byte ESCAPE_BYTE = -91;
  static final char[] HEX_DIGIT = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

  public static final boolean arrayEquals(Object[] source, Object target)
  {
    if (source == null) return (target == null);
    if (!(target instanceof Object[])) return false;
    Object[] targ = (Object[])(Object[])target;
    return ((source.length == targ.length) && (arrayRegionMatches(source, 0, targ, 0, source.length)));
  }

  public static final boolean arrayEquals(int[] source, Object target)
  {
    if (source == null) return (target == null);
    if (!(target instanceof int[])) return false;
    int[] targ = (int[])(int[])target;
    return ((source.length == targ.length) && (arrayRegionMatches(source, 0, targ, 0, source.length)));
  }

  public static final boolean arrayEquals(double[] source, Object target)
  {
    if (source == null) return (target == null);
    if (!(target instanceof double[])) return false;
    double[] targ = (double[])(double[])target;
    return ((source.length == targ.length) && (arrayRegionMatches(source, 0, targ, 0, source.length)));
  }

  public static final boolean arrayEquals(Object source, Object target)
  {
    if (source == null) return (target == null);

    if (source instanceof Object[])
      return arrayEquals((Object[])(Object[])source, target);
    if (source instanceof int[])
      return arrayEquals((int[])(int[])source, target);
    if (source instanceof double[])
      return arrayEquals((int[])(int[])source, target);
    return source.equals(target);
  }

  public static final boolean arrayRegionMatches(Object[] source, int sourceStart, Object[] target, int targetStart, int len)
  {
    int sourceEnd = sourceStart + len;
    int delta = targetStart - sourceStart;
    for (int i = sourceStart; i < sourceEnd; ++i) {
      if (!(arrayEquals(source[i], target[(i + delta)])))
        return false;
    }
    return true;
  }

  public static final boolean arrayRegionMatches(int[] source, int sourceStart, int[] target, int targetStart, int len)
  {
    int sourceEnd = sourceStart + len;
    int delta = targetStart - sourceStart;
    for (int i = sourceStart; i < sourceEnd; ++i) {
      if (source[i] != target[(i + delta)])
        return false;
    }
    return true;
  }

  public static final boolean arrayRegionMatches(double[] source, int sourceStart, double[] target, int targetStart, int len)
  {
    int sourceEnd = sourceStart + len;
    int delta = targetStart - sourceStart;
    for (int i = sourceStart; i < sourceEnd; ++i) {
      if (source[i] != target[(i + delta)])
        return false;
    }
    return true;
  }

  public static final boolean objectEquals(Object source, Object target)
  {
    if (source == null) {
      return (target == null);
    }
    return source.equals(target);
  }

  public static final String arrayToRLEString(short[] a)
  {
    StringBuffer buffer = new StringBuffer();

    buffer.append((char)(a.length >> 16));
    buffer.append((char)a.length);
    short runValue = a[0];
    int runLength = 1;
    for (int i = 1; i < a.length; ++i) {
      short s = a[i];
      if ((s == runValue) && (runLength < 65535)) {
        ++runLength;
      }
      else {
        encodeRun(buffer, runValue, runLength);
        runValue = s;
        runLength = 1;
      }
    }
    encodeRun(buffer, runValue, runLength);
    return buffer.toString();
  }

  public static final String arrayToRLEString(byte[] a)
  {
    StringBuffer buffer = new StringBuffer();
    buffer.append((char)(a.length >> 16));
    buffer.append((char)a.length);
    byte runValue = a[0];
    int runLength = 1;
    byte[] state = new byte[2];
    for (int i = 1; i < a.length; ++i) {
      byte b = a[i];
      if ((b == runValue) && (runLength < 255)) {
        ++runLength;
      }
      else {
        encodeRun(buffer, runValue, runLength, state);
        runValue = b;
        runLength = 1;
      }
    }
    encodeRun(buffer, runValue, runLength, state);

    if (state[0] != 0) {
      appendEncodedByte(buffer, 0, state);
    }

    return buffer.toString();
  }

  public static final String arrayToRLEString(char[] a)
  {
    StringBuffer buffer = new StringBuffer();
    buffer.append((char)(a.length >> 16));
    buffer.append((char)a.length);
    char runValue = a[0];
    int runLength = 1;
    for (int i = 1; i < a.length; ++i) {
      char s = a[i];
      if ((s == runValue) && (runLength < 65535)) { ++runLength;
      } else {
        encodeRun(buffer, (short)runValue, runLength);
        runValue = s;
        runLength = 1;
      }
    }
    encodeRun(buffer, (short)runValue, runLength);
    return buffer.toString();
  }

  public static final String arrayToRLEString(int[] a)
  {
    StringBuffer buffer = new StringBuffer();

    appendInt(buffer, a.length);
    int runValue = a[0];
    int runLength = 1;
    for (int i = 1; i < a.length; ++i) {
      int s = a[i];
      if ((s == runValue) && (runLength < 65535)) {
        ++runLength;
      } else {
        encodeRun(buffer, runValue, runLength);
        runValue = s;
        runLength = 1;
      }
    }
    encodeRun(buffer, runValue, runLength);
    return buffer.toString();
  }

  private static final void encodeRun(StringBuffer buffer, short value, int length)
  {
    if (length < 4) {
      for (int j = 0; j < length; ++j) {
        if (value == 42405) {
          buffer.append(42405);
        }
        buffer.append((char)value);
      }
    }
    else {
      if (length == 42405) {
        if (value == 42405) {
          buffer.append(42405);
        }
        buffer.append((char)value);
        --length;
      }
      buffer.append(42405);
      buffer.append((char)length);
      buffer.append((char)value);
    }
  }

  private static final void encodeRun(StringBuffer buffer, byte value, int length, byte[] state)
  {
    if (length < 4) {
      for (int j = 0; j < length; ++j) {
        if (value == -91) appendEncodedByte(buffer, -91, state);
        appendEncodedByte(buffer, value, state);
      }
    }
    else {
      if (length == -91) {
        if (value == -91) {
          appendEncodedByte(buffer, -91, state);
        }
        appendEncodedByte(buffer, value, state);
        --length;
      }
      appendEncodedByte(buffer, -91, state);
      appendEncodedByte(buffer, (byte)length, state);
      appendEncodedByte(buffer, value, state);
    }
  }

  private static final void encodeRun(StringBuffer buffer, int value, int length)
  {
    if (length < 4) {
      for (int j = 0; j < length; ++j) {
        if (value == 42405) {
          appendInt(buffer, value);
        }
        appendInt(buffer, value);
      }
    }
    else {
      if (length == 42405) {
        if (value == 42405) {
          appendInt(buffer, 42405);
        }
        appendInt(buffer, value);
        --length;
      }
      appendInt(buffer, 42405);
      appendInt(buffer, length);
      appendInt(buffer, value);
    }
  }

  private static final void appendInt(StringBuffer buffer, int value)
  {
    buffer.append((char)(value >>> 16));
    buffer.append((char)(value & 0xFFFF));
  }

  private static final void appendEncodedByte(StringBuffer buffer, byte value, byte[] state)
  {
    if (state[0] != 0) {
      char c = (char)(state[1] << 8 | value & 0xFF);
      buffer.append(c);
      state[0] = 0;
    }
    else {
      state[0] = 1;
      state[1] = value;
    }
  }

  public static final short[] RLEStringToShortArray(String s)
  {
    int length = s.charAt(0) << '\16' | s.charAt(1);
    short[] array = new short[length];
    int ai = 0;
    for (int i = 2; i < s.length(); ++i) {
      char c = s.charAt(i);
      if (c == 42405) {
        c = s.charAt(++i);
        if (c == 42405) {
          array[(ai++)] = (short)c;
        }
        else {
          int runLength = c;
          short runValue = (short)s.charAt(++i);
          for (int j = 0; j < runLength; ++j)
            array[(ai++)] = runValue;
        }
      }
      else
      {
        array[(ai++)] = (short)c;
      }
    }

    if (ai != length) {
      throw new InternalError("Bad run-length encoded short array");
    }

    return array;
  }

  public static final byte[] RLEStringToByteArray(String s)
  {
    int length = s.charAt(0) << '\16' | s.charAt(1);
    byte[] array = new byte[length];
    boolean nextChar = true;
    char c = '\0';
    int node = 0;
    int runLength = 0;
    int i = 2;
    for (int ai = 0; ai < length; )
    {
      byte b;
      if (nextChar) {
        c = s.charAt(i++);
        b = (byte)(c >> '\b');
        nextChar = false;
      }
      else {
        b = (byte)(c & 0xFF);
        nextChar = true;
      }

      switch (node)
      {
      case 0:
        if (b == -91) {
          node = 1;
        }
        else {
          array[(ai++)] = b;
        }
        break;
      case 1:
        if (b == -91) {
          array[(ai++)] = -91;
          node = 0;
        }
        else {
          runLength = b;

          if (runLength < 0) {
            runLength += 256;
          }
          node = 2;
        }
        break;
      case 2:
        for (int j = 0; j < runLength; ++j) {
          array[(ai++)] = b;
        }
        node = 0;
      }

    }

    if (node != 0) {
      throw new InternalError("Bad run-length encoded byte array");
    }

    if (i != s.length()) {
      throw new InternalError("Excess data in RLE byte array string");
    }

    return array;
  }

  public static final char[] RLEStringToCharArray(String s)
  {
    int length = s.charAt(0) << '\16' | s.charAt(1);
    char[] array = new char[length];
    int ai = 0;
    for (int i = 2; i < s.length(); ++i) {
      char c = s.charAt(i);
      if (c == 42405) {
        c = s.charAt(++i);
        if (c == 42405) {
          array[(ai++)] = c;
        } else {
          int runLength = c;
          char runValue = s.charAt(++i);
          for (int j = 0; j < runLength; ++j) array[(ai++)] = runValue;
        }
      }
      else {
        array[(ai++)] = c;
      }
    }

    if (ai != length) {
      throw new InternalError("Bad run-length encoded short array");
    }
    return array;
  }

  public static final int[] RLEStringToIntArray(String s)
  {
    int length = getInt(s, 0);
    int[] array = new int[length];
    int ai = 0; int i = 1;

    int maxI = s.length() / 2;
    while ((ai < length) && (i < maxI)) {
      int c = getInt(s, i++);

      if (c == 42405) {
        c = getInt(s, i++);
        if (c == 42405) {
          array[(ai++)] = c;
        } else {
          int runLength = c;
          int runValue = getInt(s, i++);
          for (int j = 0; j < runLength; ++j)
            array[(ai++)] = runValue;
        }
      }
      else
      {
        array[(ai++)] = c;
      }
    }

    if ((ai != length) || (i != maxI)) {
      throw new InternalError("Bad run-length encoded int array");
    }

    return array;
  }

  public static final String formatForSource(String s)
  {
    StringBuffer buffer = new StringBuffer();
    for (int i = 0; i < s.length(); ) {
      if (i > 0) buffer.append("+\n");
      buffer.append("        \"");
      int count = 11;
      while ((i < s.length()) && (count < 80)) {
        char c = s.charAt(i++);
        if ((c < ' ') || (c == '"'))
        {
          buffer.append('\\');
          buffer.append(HEX_DIGIT[((c & 0x1C0) >> '\6')]);
          buffer.append(HEX_DIGIT[((c & 0x38) >> '\3')]);
          buffer.append(HEX_DIGIT[(c & 0x7)]);
          count += 4;
        }
        else if (c <= '~') {
          buffer.append(c);
          ++count;
        }
        else {
          buffer.append("\\u");
          buffer.append(HEX_DIGIT[((c & 0xF000) >> '\f')]);
          buffer.append(HEX_DIGIT[((c & 0xF00) >> '\b')]);
          buffer.append(HEX_DIGIT[((c & 0xF0) >> '\4')]);
          buffer.append(HEX_DIGIT[(c & 0xF)]);
          count += 6;
        }
      }
      buffer.append('"');
    }
    return buffer.toString();
  }

  public static final String hex(char ch)
  {
    StringBuffer buff = new StringBuffer();
    return hex(ch, buff).toString();
  }

  public static final StringBuffer hex(String src, StringBuffer buff) {
    if ((src != null) && (buff != null)) {
      int strLen = src.length();
      int x = 0;
      hex(src.charAt(x), buff);
      while (x < strLen) {
        buff.append(',');
        hex(src.charAt(x++), buff);
      }
    }

    return buff;
  }

  public static final String hex(String str) {
    StringBuffer buff = new StringBuffer();
    hex(str, buff);
    return buff.toString();
  }

  public static final String hex(StringBuffer buff) {
    return hex(buff.toString());
  }

  public static final StringBuffer hex(char ch, StringBuffer buff) {
    for (int shift = 12; shift >= 0; shift -= 4) {
      buff.append(HEX_DIGIT[(byte)(ch >> shift & 0xF)]);
    }
    return buff;
  }

  static final int getInt(String s, int i)
  {
    return (s.charAt(2 * i) << '\16' | s.charAt(2 * i + 1));
  }
}
