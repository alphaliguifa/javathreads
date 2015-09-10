

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;



public class MultipartRequest {

    private final static Logger.Module MOD = new Logger.Module(ModuleId.MOD_UTIL);
    private final static Logger LOG = Logger.getLogger(MultipartRequest.class, MOD);

    private HttpServletRequest req = null;
    private int maxFileSize = 0;
    private Map files = new HashMap();
    private Map parameters = new HashMap();
    
    public MultipartRequest(PortletRequest request, int maxFileSize) 
        throws IOException, FrameworkException
    {
        if (request == null)
            throw new IllegalArgumentException("Request cannot be null");
            
        req = (HttpServletRequest)((PortletRequestImpl) request).getInternalServletRequest();        
        this.maxFileSize = maxFileSize;
        
        parse();
    }

    public MultipartRequest(HttpServletRequest request, int maxFileSize) 
        throws IOException, FrameworkException
    {
        if (request == null)
            throw new IllegalArgumentException("Request cannot be null");
            
        req = request;
        this.maxFileSize = maxFileSize;        
        
        parse();
    }
    
    private void parse() throws Framework.FrameworkException, IOException
    {
        final String METHOD_NAME = "parse";
            
        File file = null;
        String filename = null;

        try
        {
        	Framework.createServletInstance(null);
            WebRequestContext ctx = FakeJAMFactory.getInstance(req).getWebRequestContext();
            Translator translator = new TranslatorImpl(ctx.getSessionContext().getLocaleContext().getLocale(), "UTF-8",null);
            ctx.getSessionContext().setTranslator(translator);
            Parameters ctxParams = Framework.getInstance().getParameterManager().cloneParameters(ctx.getSessionContext(), req);
            WebRequestContext webCtx = new WebRequestContextImpl(ctx.getSessionContext(), req, null, ctxParams, null, null);
            Parameters params = webCtx.getParameters();
            Iterator iter = params.iterator();
            while (iter.hasNext())
            {
                Parameter param = (Parameter)iter.next();
                if (param instanceof FileParameter)
                {
                    FileItem fileItem = (FileItem)((FileParameter) param).getFileItem();
                    //files.put( fileItem.getFileName(), fileItem );                    
                    files.put(param.getName(), fileItem );
                    long length = fileItem.getFile().length();
                    
                    if ( LOG.isDebugEnabled() )
                        LOG.debug(METHOD_NAME, "File attachment '" + fileItem.getFileName() + "' of size " + length);

                    if ( length > maxFileSize )
                        throw new IOException();
                }
                else
                {
                    parameters.put( param.getName(), param.getValue() );
                    if ( LOG.isDebugEnabled() )
                        LOG.debug(METHOD_NAME, "Parameter: " + param.getName() + ", value: " + param.getValue());
                }
            }
        } 
        catch (IOException e)
        {
            LOG.error(METHOD_NAME, "Parsing error", e);
            throw e;
        }
    }
    
    public Iterator getParameterNames()
    {
        return parameters.keySet().iterator();
    }
    
    public String getParameter(String name)
    {
        return (String)parameters.get(name);
    }
    
    public Iterator getFileNames()
    {
        return files.keySet().iterator();
    }
    
    public String getFileName(String filename)
    {
        FileItem item = (FileItem)files.get(filename);
        if (item == null)
            return null;
        else return item.getFileName();
    }
    
    public File getFile(String filename)
    {
        FileItem item = (FileItem)files.get(filename);
        if (item == null)
            return null;
        else return item.getFile();
    }
    
    public String getContentType(String filename)
    {
        FileItem item = (FileItem)files.get(filename);
        if (item == null)
            return null;
        else return item.getContentType();
    }
}    

