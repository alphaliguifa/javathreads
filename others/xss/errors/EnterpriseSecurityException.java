//import com.ubs.james.commons.misc.impl.Logger;
//import com.ubs.james.commons.misc.impl.Logger.Module;

public class EnterpriseSecurityException extends Exception
{
  private static final long serialVersionUID = 1L;
//  private static final Logger.Module MOD = new Logger.Module("JMZ.WB");
//  private static final Logger LOG = Logger.getLogger(EnterpriseSecurityException.class, MOD);

  protected String logMessage = null;

  private Throwable cause = null;

  protected EnterpriseSecurityException()
  {
  }

  public EnterpriseSecurityException(String userMessage, String logMessage)
  {
    super(userMessage);
    this.logMessage = logMessage;
//    LOG.error("EnterpriseSecurityException", logMessage);
  }

  public EnterpriseSecurityException(String userMessage, String logMessage, Throwable cause)
  {
    super(userMessage);
    this.logMessage = logMessage;
    this.cause = cause;
//    LOG.error("EnterpriseSecurityException", logMessage);
  }

  public String getUserMessage()
  {
    return getMessage();
  }

  public String getLogMessage()
  {
    return this.logMessage;
  }

  public Throwable getCause() {
    return this.cause;
  }
}
