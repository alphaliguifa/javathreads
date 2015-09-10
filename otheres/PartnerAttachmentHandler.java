

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class PartnerAttachmentHandler extends ICRMBaseHandler {

    private final static Logger.Module MOD = new Logger.Module(ModuleId.MOD_HANDLER);

    private final static Logger LOG = Logger.getLogger(PartnerAttachmentHandler.class, MOD);

    private final static PerformanceLogger PLOG = PerformanceLogger.getLogger(PartnerAttachmentHandler.class);

    public static final String ACTION = RequestParamConstant.ACTION_NAME;

    public static final String ACTION_ADD_ATTACHMENT = ActionConstant.ACTION_ADD_ATTACHMENT;

    public static final String ACTION_DOWNLOAD_ATTACHMENT = ActionConstant.ACTION_DOWNLOAD_ATTACHMENT;

    public static final String RETURN_URL = "RETURN_URL";

    public static final String ERROR_MSG = "ERROR_MSG";

    private static final String UPLOAD_FOLDER = Config.getFileUploadPath();

    private static final int MAX_FILE_SIZE = ICRMConstant.UPLOAD_FILE_MAX_SIZE;//10 *

    // 1024
    // *
    // 1024;

    private static final int UPLOAD_FILE = ICRMConstant.UPLOAD_FILE;

    //    private IPartnerAttachmentBSV attachmentBS;

    private IPartnerAttachmentBSV getPartnerAttachmentBS() {
        final String METHOD_NAME = "getPartnerAttachmentBS";
        PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_ENTRY);
        IBusinessServiceManager bsvmgr = GenericContainer.getInstance().getBusinessServiceManager();
        IPartnerAttachmentBSV attachmentBS = (IPartnerAttachmentBSV) bsvmgr.getBusinessService(IPartnerAttachmentBSV.class);

        PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_EXIT);
        return attachmentBS;
    }

    public void _performTask(ServletContext sc, HttpServletRequest request, HttpServletResponse response) throws HandlerException {
        final String METHOD_NAME = "_performTask";
        PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_ENTRY);
        IUser user = UserFactory.getInstance().getUser(request);
        try {
            String action = request.getParameter(ACTION);
            if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, "begin _performTask in action1111111==== " + action);
                LOG.debug(METHOD_NAME, "begin _performTask in action1111111==== " + (action == null));
            }
            MultipartRequest multi = null;
            //          Kevin Wang 2006-01-10:flag for MultipartRequest's IOException
            boolean ioexceptionflag = false;
            if (action == null) {
                try {
                    //multi = new MultipartRequest(request, UPLOAD_FOLDER, MAX_FILE_SIZE, "UTF8", new DefaultFileRenamePolicy());
                	multi = new MultipartRequest(request, MAX_FILE_SIZE);
                	action = multi.getParameter(ACTION);
                } catch (IOException ioe) {
                    LOG.error(METHOD_NAME, "Error in MultipartRequest = " + action);
                    ioexceptionflag = true;
                }
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, "_performTask in action = " + action);
                LOG.debug(METHOD_NAME, "_performTask in max size = " + MAX_FILE_SIZE);
            }
            if (action == null || action.trim().length() < 1) {
                handleToAddAttachmentAction(request, response, ioexceptionflag);
            } else if (ACTION_ADD_ATTACHMENT.equals(action)) {
                handleAddAttachmentAction(request, response, multi);
            } else if (ACTION_DOWNLOAD_ATTACHMENT.equals(action)) {
                handleDownloadAttachmentAction(request, response);
                return;
            }

            String returnURL = (String) request.getAttribute(RETURN_URL);
            if (returnURL != null) {
                getRequestDispatcher(returnURL, sc).forward(request, response);
            }
        } catch (java.io.IOException ioe) {
            LOG.error(METHOD_NAME, "login id=" + user.getUserId(), ioe);
            ioe.printStackTrace();
            request.setAttribute(ERROR_MSG, "IO Error:" + ioe.toString());
            throw new HandlerException("IO exception occurs", ioe);
        } catch (ServletException se) {
            LOG.error(METHOD_NAME, "login id=" + user.getUserId(), se);
            se.printStackTrace();
            request.setAttribute(ERROR_MSG, "Servlet Error:" + se.toString());
            throw new HandlerException("Servlet exception occurs", se);
        } catch (Exception e) {
            LOG.error(METHOD_NAME, "login id=" + user.getUserId(), e);
            e.printStackTrace();
            request.setAttribute(ERROR_MSG, "Unknown Error:" + e.toString());
            throw new HandlerException("Unknown exception occurs", e);
        }

        PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_EXIT);
    }

    private void handleToAddAttachmentAction(HttpServletRequest request, HttpServletResponse response, boolean exceptionflag) {
        final String METHOD_NAME = "handleToAddAttachmentAction";
        PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_ENTRY);

        PartnerAttachmentHelperBeanSO helperBean = new PartnerAttachmentHelperBeanSO();
        helperBean.htmlToBean(request);
        request.setAttribute(helperBean.getClass().getName(), helperBean);
        if (!exceptionflag) {
            request.setAttribute(RETURN_URL, JspPageConstant.PARTNER_ADD_ATTACHMENT_PAGE);
        } else {
            int maxinm = MAX_FILE_SIZE / (1024 * 1024);
            //"You are not allowed to upload attachment(s) larger than "
            String errormsg = this.getTextBundle().getText("PartnerAttachmentHandler.msg.NotAllowUploadAttachment") + maxinm + "Mb.";
            request.setAttribute(RequestParamConstant.RETURN_URL, JspPageConstant.PARTNER_AFTER_ADD_ATTACHMENT_PAGE);
            Vector v = new Vector();
            v.add(errormsg);
            request.setAttribute("WARNING", v);
            request.setAttribute(ERROR_MSG, errormsg);
        }
        PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_EXIT);
    }

    private void handleAddAttachmentAction(HttpServletRequest request, HttpServletResponse response, MultipartRequest multi) throws Exception {

        final String METHOD_NAME = "handleAddAttachmentAction";
        PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_ENTRY);
        //        IUser user = UserFactory.getInstance().getUser(request);
        
        PartnerAttachmentHelperBeanSO helperBean = new PartnerAttachmentHelperBeanSO();
        helperBean.htmlToBean(multi);

        // attachment list
        List alist = new ArrayList();
        List oversizeList = new ArrayList();
        
        //andy : l_curTime ,l_beginTime used for test the performance 
        long l_curTime = System.currentTimeMillis();
        long l_beginTime = l_curTime;
        
        for (int i = 1; i <= 3; i++) {
            String name = "fileloc" + i;
            String title = multi.getParameter("title" + i);
            String description = multi.getParameter("description" + i);
            boolean isFile = "file".equals(multi.getParameter("file_or_link" + i));

            // title is mandatory field
            if (title != null && !"".equals(title.trim())) {
                PartnerAttachmentSO attachment = new PartnerAttachmentSO();
                attachment.setTitle(title);
                attachment.setDescription(description);
                boolean isOverSize = false;

                if (isFile) {
                    File f = multi.getFile(name);
                    if (f != null) {
                        String originalFilename = StringUtil.getFileNameFromFullPath(multi.getFileName(name));
                        if (LOG.isDebugEnabled()) {
                            LOG.debug(METHOD_NAME, "file size = " + f.length());
                        }
                        if (f.length() > MAX_FILE_SIZE) {
                            // if (f.length() > 0) {
                            isOverSize = true;
                            oversizeList.add(originalFilename);
                            f.delete();
                            f= null;
                            continue;
                        } else {
                            String type = multi.getContentType(name);
                            attachment.setFilename(originalFilename);
                            attachment.setMimetype(type);
                            attachment.setFilesize(new Long(f.length()));

                            byte[] content = new byte[(int) f.length()];
                            FileInputStream fis = new FileInputStream(f);
                            fis.read(content);
                            attachment.setContent(content);
                            fis.close();
                            f.delete();                            
                            fis = null;
                            f = null;
                        }
                    }
                } else {
                    String hyperlink = multi.getParameter("hyperlink" + i);
                    attachment.setHyperlink(hyperlink);
                }
                if (!isOverSize) {
                    alist.add(attachment);
                }
            }
        } // end of file upload for-loop
        if(LOG.isDebugEnabled()){
            
            LOG.debug(METHOD_NAME,"file upload for-loop time expend ("+(System.currentTimeMillis()-l_curTime)+")--- from "+l_curTime +" to "+System.currentTimeMillis() );
            l_curTime = System.currentTimeMillis();
        }
        // call bsv to add working attachments
        if (alist.size() > 0) {

            String filenum = request.getParameter(RequestParamConstant.ATTACHMENTS_SIZE);
            int i_filenum = 0;
            if (filenum != null && filenum.trim().length() != 0) {
                try {
                    i_filenum = Integer.valueOf(filenum.trim());
                } catch (NumberFormatException e1) {
                	LOG.error(METHOD_NAME, "can not pares file number form parameter string filenum:" + filenum);
                }
            }
            if (i_filenum + alist.size() > UPLOAD_FILE) {
                //"You are not allowed to upload more than "  " attachments."
                String msg = this.getTextBundle().getText("PartnerAttachmentHandler.msg.NotAllowUpload") + UPLOAD_FILE + this.getTextBundle().getText("PartnerAttachmentHandler.msg.Attachment");
                request.setAttribute(RequestParamConstant.ERROR_MSG, msg);
                Vector v = (Vector) request.getAttribute("CRITICAL");
                if (v == null) {
                    v = new Vector();
                    request.setAttribute("CRITICAL", v);
                }
                v.add(msg);
                alist.clear();
                alist = null;
            } else {
                String attachmentIds[] = getPartnerAttachmentBS().addWorkingAttachmentList(helperBean.getUserId(), helperBean.getPartnerId(),
                        (PartnerAttachmentSO[]) alist.toArray(new PartnerAttachmentSO[0])
                        // ,(IUserContextSO)WPSHelper.singleton().getHttpSession(request).getAttribute(ICRMConstant.USER_CONTEXT)
                        , this.getStorageLocation(getUserContext(request, multi)));

                request.setAttribute(RequestParamConstant.PARTNER_ATTACHMENTS, this.makeAttachmentIds(attachmentIds));
                alist.clear();
                alist = null;
            }

        }
        if(LOG.isDebugEnabled()){
            
            LOG.debug(METHOD_NAME,"file save to db time expend ("+(System.currentTimeMillis()-l_curTime)+")--- from "+l_curTime +" to "+System.currentTimeMillis() );
            l_curTime = System.currentTimeMillis();
        }        
        // set error message to the request
        if (oversizeList.size() > 0) {
            StringBuffer msg = new StringBuffer();
            //"The following files size are large than "    "MB, can not be uploaded : <br>"
            msg.append( this.getTextBundle().getText("PartnerAttachmentHandler.msg.FileLarger") + MAX_FILE_SIZE / (1024 * 1024) + this.getTextBundle().getText("PartnerAttachmentHandler.msg.MBNotUpload") );
            for (Iterator it = oversizeList.iterator(); it.hasNext();) {
                msg.append((String) it.next());
                msg.append("<br>");
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, msg.toString());
            }
            Vector v = new Vector();
            request.setAttribute(PartnerAttachmentHandler.ERROR_MSG, msg.toString());
            v.add(msg.toString());
            request.setAttribute("CRITICAL", v);
        }
        request.setAttribute(helperBean.getClass().getName(), helperBean);
        request.setAttribute(RETURN_URL, JspPageConstant.PARTNER_AFTER_ADD_ATTACHMENT_PAGE);
        
        if(LOG.isDebugEnabled()){
            
            LOG.debug(METHOD_NAME," time expend ("+(System.currentTimeMillis()-l_beginTime)+")--- from "+l_beginTime +" to "+System.currentTimeMillis() );            
        }
        PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_EXIT);
    }

    private String makeAttachmentIds(String attachmentIds[]) {
        final String METHOD_NAME = "";
        PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_ENTRY);
        if (attachmentIds == null)
            return "";
        String result = "";
        for (int i = 0; i < attachmentIds.length; i++) {
            result += ",".concat(attachmentIds[i]);
        }
        PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_EXIT);
        return result;
    }

    private void handleDownloadAttachmentAction(HttpServletRequest request, HttpServletResponse response) throws Exception {

        final String METHOD_NAME = "handleDownloadAttachmentAction";
        PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_ENTRY);
        IUser user = UserFactory.getInstance().getUser(request);
        String attchmentId = request.getParameter(RequestParamConstant.ATTACHMENT_ID);

        try {
            PartnerAttachmentSO attachment = getPartnerAttachmentBS().getAttachment(attchmentId, getStorageLocation(getUserContext(request)));

            // set response header
            response.setContentType(attachment.getMimetype());
            response.setHeader("Content-Disposition", "xxxxxx; filename=\"" + attachment.getFilename() + "\"");
            response.setHeader("Pragma", "Public");

            try {
                // write content to output stream
                int contentLen = attachment.getContent().length;
                BufferedOutputStream bos = new BufferedOutputStream(response.getOutputStream());
                bos.write(attachment.getContent(), 0, contentLen);
                bos.flush();
            } catch (IOException e) {
                LOG.error(METHOD_NAME, "login id=" + user.getUserId() + " : IO Exception occur when download file, attchmentId=" + attchmentId, e);
                e.printStackTrace();
            }
        } catch (Exception e) {
            LOG.error(METHOD_NAME, "login id=" + user.getUserId() + " : Unknown error! attchmentId=" + attchmentId, e);
            e.printStackTrace();
        }

        PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_EXIT);
    }

}
