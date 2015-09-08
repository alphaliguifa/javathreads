public class EncodingException extends EnterpriseSecurityException
{
  private static final long serialVersionUID = 1L;

  protected EncodingException()
  {
  }

  public EncodingException(String userMessage, String logMessage)
  {
    super(userMessage, logMessage);
  }

  public EncodingException(String userMessage, String logMessage, Throwable cause)
  {
    super(userMessage, logMessage, cause);
  }
}
