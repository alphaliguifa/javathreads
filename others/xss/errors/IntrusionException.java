//import com.ubs.james.commons.misc.impl.Logger;
//import com.ubs.james.commons.misc.impl.Logger.Module;

public class IntrusionException extends RuntimeException
{
  private static final long serialVersionUID = 1L;
//  private static final Logger.Module MOD = new Logger.Module("JMZ.WB");
//  private static final Logger LOG = Logger.getLogger(EnterpriseSecurityException.class, MOD);

  protected String logMessage = null;

  public IntrusionException(String userMessage, String logMessage)
  {
    super(userMessage);
    this.logMessage = logMessage;
//    LOG.error("IntrusionException", "INTRUSION - " + logMessage);
  }

  public IntrusionException(String userMessage, String logMessage, Throwable cause)
  {
    super(userMessage);
    this.logMessage = logMessage;
//    LOG.error("IntrusionException", "INTRUSION - " + logMessage, cause);
  }

  public String getUserMessage()
  {
    return getMessage();
  }

  public String getLogMessage()
  {
    return this.logMessage;
  }
}
