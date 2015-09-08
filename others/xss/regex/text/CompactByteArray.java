import B;
import I;
import S;

public final class CompactByteArray
  implements Cloneable
{
  public static final int UNICODECOUNT = 65536;
  private static final int BLOCKSHIFT = 7;
  private static final int BLOCKCOUNT = 128;
  private static final int INDEXSHIFT = 9;
  private static final int INDEXCOUNT = 512;
  private static final int BLOCKMASK = 127;
  private byte[] values;
  private short[] indices;
  private boolean isCompact;
  private int[] hashes;

  public CompactByteArray()
  {
    this(0);
  }

  public CompactByteArray(byte defaultValue)
  {
    this.values = new byte[65536];
    this.indices = new short[512];
    this.hashes = new int[512];
    for (int i = 0; i < 65536; ++i) {
      this.values[i] = defaultValue;
    }
    for (i = 0; i < 512; ++i) {
      this.indices[i] = (short)(i << 7);
      this.hashes[i] = 0;
    }
    this.isCompact = false;
  }

  public CompactByteArray(short[] indexArray, byte[] newValues)
  {
    if (indexArray.length != 512)
      throw new IllegalArgumentException("Index out of bounds!");
    for (int i = 0; i < 512; ++i) {
      short index = indexArray[i];
      if ((index < 0) || (index >= newValues.length + 128))
        throw new IllegalArgumentException("Index out of bounds!");
    }
    this.indices = indexArray;
    this.values = newValues;
    this.isCompact = true;
  }

  public CompactByteArray(String indexArray, String valueArray)
  {
    this(Utility.RLEStringToShortArray(indexArray), Utility.RLEStringToByteArray(valueArray));
  }

  public byte elementAt(char index)
  {
    return this.values[((this.indices[(index >> '\7')] & 0xFFFF) + (index & 0x7F))];
  }

  public void setElementAt(char index, byte value)
  {
    if (this.isCompact)
      expand();
    this.values[index] = value;
    touchBlock(index >> '\7', value);
  }

  public void setElementAt(char start, char end, byte value)
  {
    if (this.isCompact) {
      expand();
    }
    for (int i = start; i <= end; ++i) {
      this.values[i] = value;
      touchBlock(i >> 7, value);
    }
  }

  public void compact()
  {
    if (!(this.isCompact)) {
      int limitCompacted = 0;
      int iBlockStart = 0;
      short iUntouched = -1;

      for (int i = 0; i < this.indices.length; iBlockStart += 128) {
        this.indices[i] = -1;
        boolean touched = blockTouched(i);
        if ((!(touched)) && (iUntouched != -1))
        {
          this.indices[i] = iUntouched;
        } else {
          int jBlockStart = 0;
          int j = 0;
          for (j = 0; j < limitCompacted; )
          {
            if ((this.hashes[i] == this.hashes[j]) && (arrayRegionMatches(this.values, iBlockStart, this.values, jBlockStart, 128)))
            {
              this.indices[i] = (short)jBlockStart;
              break;
            }
            ++j; jBlockStart += 128;
          }

          if (this.indices[i] == -1)
          {
            System.arraycopy(this.values, iBlockStart, this.values, jBlockStart, 128);

            this.indices[i] = (short)jBlockStart;
            this.hashes[j] = this.hashes[i];
            ++limitCompacted;

            if (!(touched))
            {
              iUntouched = (short)jBlockStart;
            }
          }
        }
        ++i;
      }

      int newSize = limitCompacted * 128;
      byte[] result = new byte[newSize];
      System.arraycopy(this.values, 0, result, 0, newSize);
      this.values = result;
      this.isCompact = true;
      this.hashes = null;
    }
  }

  static final boolean arrayRegionMatches(byte[] source, int sourceStart, byte[] target, int targetStart, int len)
  {
    int sourceEnd = sourceStart + len;
    int delta = targetStart - sourceStart;
    for (int i = sourceStart; i < sourceEnd; ++i) {
      if (source[i] != target[(i + delta)])
        return false;
    }
    return true;
  }

  private final void touchBlock(int i, int value)
  {
    this.hashes[i] = (this.hashes[i] + (value << 1) | 0x1);
  }

  private final boolean blockTouched(int i)
  {
    return (this.hashes[i] != 0);
  }

  public short[] getIndexArray()
  {
    return this.indices;
  }

  public byte[] getStringArray()
  {
    return this.values;
  }

  public Object clone()
  {
    try
    {
      CompactByteArray other = (CompactByteArray)super.clone();
      other.values = ((byte[])(byte[])this.values.clone());
      other.indices = ((short[])(short[])this.indices.clone());
      if (this.hashes != null) other.hashes = ((int[])(int[])this.hashes.clone());
      return other;
    } catch (CloneNotSupportedException e) {
      throw new InternalError();
    }
  }

  public boolean equals(Object obj)
  {
    if (obj == null) return false;
    if (this == obj)
      return true;
    if (super.getClass() != obj.getClass())
      return false;
    CompactByteArray other = (CompactByteArray)obj;
    for (int i = 0; i < 65536; ++i)
    {
      if (elementAt((char)i) != other.elementAt((char)i))
        return false;
    }
    return true;
  }

  public int hashCode()
  {
    int result = 0;
    int increment = Math.min(3, this.values.length / 16);
    for (int i = 0; i < this.values.length; i += increment) {
      result = result * 37 + this.values[i];
    }
    return result;
  }

  private void expand()
  {
    if (!(this.isCompact))
      return;
    this.hashes = new int[512];
    byte[] tempArray = new byte[65536];
    for (int i = 0; i < 65536; ++i) {
      byte value = elementAt((char)i);
      tempArray[i] = value;
      touchBlock(i >> 7, value);
    }
    for (int i = 0; i < 512; ++i) {
      this.indices[i] = (short)(i << 7);
    }
    this.values = null;
    this.values = tempArray;
    this.isCompact = false;
  }
}
