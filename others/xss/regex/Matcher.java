public final class Matcher
{
  Pattern parentPattern;
  int[] groups;
  int from;
  int to;
  String text;
  static final int ENDANCHOR = 1;
  static final int NOANCHOR = 0;
  int acceptMode = 0;

  int first = -1; int last = -1;

  int oldLast = -1;

  int lastAppendPosition = 0;
  int[] locals;

  Matcher()
  {
  }

  Matcher(Pattern parent, String text)
  {
    this.parentPattern = parent;
    this.text = text;

    int parentGroupCount = Math.max(parent.groupCount, 10);
    this.groups = new int[parentGroupCount * 2];
    this.locals = new int[parent.localCount];

    reset();
  }

  public Pattern pattern()
  {
    return this.parentPattern;
  }

  public Matcher reset()
  {
    this.first = -1;
    this.last = -1;
    this.oldLast = -1;
    for (int i = 0; i < this.groups.length; ++i)
      this.groups[i] = -1;
    for (int i = 0; i < this.locals.length; ++i)
      this.locals[i] = -1;
    this.lastAppendPosition = 0;
    return this;
  }

  public Matcher reset(String input)
  {
    this.text = input;
    return reset();
  }

  public int start()
  {
    if (this.first < 0)
      throw new IllegalStateException("No match available");
    return this.first;
  }

  public int start(int group)
  {
    if (this.first < 0)
      throw new IllegalStateException("No match available");
    if (group > groupCount())
      throw new IndexOutOfBoundsException("No group " + group);
    return this.groups[(group * 2)];
  }

  public int end()
  {
    if (this.first < 0)
      throw new IllegalStateException("No match available");
    return this.last;
  }

  public int end(int group)
  {
    if (this.first < 0)
      throw new IllegalStateException("No match available");
    if (group > groupCount())
      throw new IndexOutOfBoundsException("No group " + group);
    return this.groups[(group * 2 + 1)];
  }

  public String group()
  {
    return group(0);
  }

  public String group(int group)
  {
    if (this.first < 0)
      throw new IllegalStateException("No match found");
    if ((group < 0) || (group > groupCount()))
      throw new IndexOutOfBoundsException("No group " + group);
    if ((this.groups[(group * 2)] == -1) || (this.groups[(group * 2 + 1)] == -1))
      return null;
    return getSubSequence(this.groups[(group * 2)], this.groups[(group * 2 + 1)]).toString();
  }

  public int groupCount()
  {
    return (this.parentPattern.groupCount - 1);
  }

  public boolean matches()
  {
    reset();
    return match(0, getTextLength(), 1);
  }

  public boolean find()
  {
    if (this.last == this.first)
      this.last += 1;
    if (this.last > this.to) {
      for (int i = 0; i < this.groups.length; ++i)
        this.groups[i] = -1;
      return false;
    }
    return find(this.last, getTextLength());
  }

  public boolean find(int start)
  {
    int limit = getTextLength();
    if ((start < 0) || (start > limit))
      throw new IndexOutOfBoundsException("Illegal start index");
    reset();
    return find(start, limit);
  }

  public boolean lookingAt()
  {
    reset();
    return match(0, getTextLength(), 0);
  }

  public Matcher appendReplacement(StringBuffer sb, String replacement)
  {
    if (this.first < 0) {
      throw new IllegalStateException("No match available");
    }

    int cursor = 0;
    String s = replacement;
    StringBuffer result = new StringBuffer();

    while (cursor < replacement.length()) {
      char nextChar = replacement.charAt(cursor);
      if (nextChar == '\\') {
        ++cursor;
        nextChar = replacement.charAt(cursor);
        result.append(nextChar);
        ++cursor;
      } else if (nextChar == '$')
      {
        ++cursor;

        int refNum = replacement.charAt(cursor) - '0';
        if ((refNum < 0) || (refNum > 9)) {
          throw new IllegalArgumentException("Illegal group reference");
        }
        ++cursor;

        boolean done = false;
        while (!(done)) {
          if (cursor >= replacement.length()) {
            break;
          }
          int nextDigit = replacement.charAt(cursor) - '0';
          if (nextDigit < 0) break; if (nextDigit > 9) {
            break;
          }
          int newRefNum = refNum * 10 + nextDigit;
          if (groupCount() < newRefNum) {
            done = true;
          } else {
            refNum = newRefNum;
            ++cursor;
          }

        }

        if (group(refNum) != null)
          result.append(group(refNum));
      } else {
        result.append(nextChar);
        ++cursor;
      }

    }

    sb.append(getSubSequence(this.lastAppendPosition, this.first));

    sb.append(result.toString());

    this.lastAppendPosition = this.last;
    return this;
  }

  public StringBuffer appendTail(StringBuffer sb)
  {
    sb.append(getSubSequence(this.lastAppendPosition, getTextLength()).toString());
    return sb;
  }

  public String replaceAll(String replacement)
  {
    reset();
    boolean result = find();
    if (result) {
      StringBuffer sb = new StringBuffer();
      do {
        appendReplacement(sb, replacement);
        result = find(); }
      while (result);
      appendTail(sb);
      return sb.toString();
    }
    return this.text.toString();
  }

  public String replaceFirst(String replacement)
  {
    StringBuffer sb = new StringBuffer();
    reset();
    if (find())
      appendReplacement(sb, replacement);
    appendTail(sb);
    return sb.toString();
  }

  private boolean find(int from, int to)
  {
    from = (from < 0) ? 0 : from;
    this.to = to;
    this.first = from;
    this.last = -1;
    this.oldLast = ((this.oldLast < 0) ? from : this.oldLast);
    for (int i = 0; i < this.groups.length; ++i)
      this.groups[i] = -1;
    this.acceptMode = 0;

    boolean result = this.parentPattern.root.match(this, from, this.text);
    if (!(result))
      this.first = -1;
    this.oldLast = this.last;
    return result;
  }

  private boolean match(int from, int to, int anchor)
  {
    from = (from < 0) ? 0 : from;
    this.to = to;
    this.first = from;
    this.last = -1;
    this.oldLast = ((this.oldLast < 0) ? from : this.oldLast);
    for (int i = 0; i < this.groups.length; ++i)
      this.groups[i] = -1;
    this.acceptMode = anchor;

    boolean result = this.parentPattern.matchRoot.match(this, from, this.text);
    if (!(result))
      this.first = -1;
    this.oldLast = this.last;
    return result;
  }

  int getTextLength()
  {
    return this.text.length();
  }

  String getSubSequence(int beginIndex, int endIndex)
  {
    return this.text.substring(beginIndex, endIndex);
  }

  char charAt(int i)
  {
    return this.text.charAt(i);
  }
}
