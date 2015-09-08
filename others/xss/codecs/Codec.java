public abstract interface Codec
{
  public abstract String encode(String paramString);

  public abstract String encodeCharacter(Character paramCharacter);

  public abstract String decode(String paramString);

  public abstract Character decodeCharacter(PushbackString paramPushbackString);
}
