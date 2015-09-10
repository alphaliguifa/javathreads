

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

//import com.oreilly.servlet.MultipartRequest;
//import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;


public class FileUploadHelper {

	private final static Logger.Module MOD = new Logger.Module(ModuleId.MOD_UTIL);
    private final static Logger LOG = Logger.getLogger(FileUploadHelper.class, MOD);
    private final static PerformanceLogger PLOG = PerformanceLogger.getLogger(FileUploadHelper.class);

    private static final String PARAM_NAME_PREFIX = "\"";

    private static final String PARAM_NAME_SUFFIX = "\"";

   // private static String UPLOAD_FOLDER = ICRMConstant.FILE_UPLOAD_PATH;

    private static int MAX_FILE_SIZE = 50 * 1024 * 1024;

    private static String DEFAULT_ENCODING = "UTF-8";

    public static AttachmentSO getAttachment(HttpServletRequest request, AttachmentSO attachment) throws IOException {
        final String METHOD_NAME = "getAttachment";
        PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_ENTRY);
        //MultipartRequest multi = new MultipartRequest(request, UPLOAD_FOLDER, MAX_FILE_SIZE, DEFAULT_ENCODING, new DefaultFileRenamePolicy());
    	MultipartRequest multi=null;
		try {
			multi = new MultipartRequest(request, MAX_FILE_SIZE);
		} catch (FrameworkException e) {
			LOG.error(METHOD_NAME, "Error occurs in getAttachment", e);
		}
		String name = null;
		if(multi != null && multi.getFileNames().hasNext()) {
			name = (String)multi.getFileNames().next();
			File f = multi.getFile(name);
	        if (f != null) {
	        	FileInputStream fis = null;
	        	try {
	        		attachment.setFilename(multi.getFileName(name));
		            attachment.setMimetype(multi.getContentType(name));
		            attachment.setFilesize(new Long(f.length()));
		            byte[] content = new byte[(int) f.length()];
		            fis = new FileInputStream(f);
		            int length = fis.read(content);
		            if(LOG.isDebugEnabled()) {
		            	LOG.debug(METHOD_NAME, "read length= " + length);
		            }
		            attachment.setContent(content);
	        	} finally {
	        		fis.close();
		            f.delete();
	        	}
	        }
		}
		//String name = (String) (multi==null?null:(multi.getFileNames().hasNext()?multi.getFileNames().next():null));
        //String name = "";
       /* Enumeration Enum = multi.getFileNames();
        if (Enum.hasMoreElements()) {
            name = Enum.nextElement().toString();
        }*/
       
        PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_EXIT);
        return attachment;
    }

    public static MultipartSO getMultipart(PortletRequest request, AttachmentSO[] attachments) throws IOException {
        final String METHOD_NAME = "getMultipart";
        PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_ENTRY);
        MultipartSO multipart = null;
        Map map = new HashMap();
        MultipartContent mpc = new MultipartContent(request.getInputStream());
        Vector v = null;
        if (request.getCharacterEncoding() == null) {
            v = mpc.getContents(DEFAULT_ENCODING);
        } else {
            v = mpc.getContents(request.getCharacterEncoding());
        }
        int index = 0;
        Enumeration contentEnum = v.elements();
        while (contentEnum.hasMoreElements()) {
            ContentElement elem = (ContentElement) contentEnum.nextElement();
            if (elem.getFileName() != null && elem.getMimeType() != null) {
                // for File types
                attachments[index].setFilename(elem.getFileName());
                attachments[index].setFilesize(new Long(elem.getContentSize()));
                attachments[index].setMimetype(elem.getMimeType());
                attachments[index++].setContent(elem.getContentValue());
            } else {
                // for String parameters
				if (elem.getName().startsWith(PARAM_NAME_PREFIX)
						&& elem.getName().endsWith(PARAM_NAME_SUFFIX)) {
					map.put(elem.getName().substring(
							PARAM_NAME_PREFIX.length(),
							elem.getName().length()
									- PARAM_NAME_SUFFIX.length()), elem
							.getContentValueAsString());
                } else {
                    map.put(elem.getName(), elem.getContentValueAsString());
                }
            }
        }

        if (index > 0 && map.size() > 0) {
            multipart = new MultipartSO(map, attachments);
        } else if (index > 0) {
            multipart = new MultipartSO(attachments);
        } else if (map.size() > 0) {
            multipart = new MultipartSO(map);
        } else {
            multipart = new MultipartSO();
        }

        PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_EXIT);
        return multipart;
    }
}
