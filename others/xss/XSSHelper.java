public class XSSHelper
{
  private static Encoder encoder = null;

  public static Encoder encoder()
  {
    if (encoder == null)
      encoder = new DefaultEncoder();
    return encoder;
  }

  public static void setEncoder(Encoder encoder)
  {
    encoder = encoder;
  }
}
