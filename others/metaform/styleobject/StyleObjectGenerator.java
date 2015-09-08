

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;



/**
 * 
 * @author unknown
 * 
 * Change Log Description Change Date Author Change Desc 02/09/2005 Jeff Zhou
 * add method genTextArea(String paramName, int col, int height, int dispWidth,
 * String dataValue, boolean edit, String onClickScript, String onChangeScript,
 * String options)
 */
public class StyleObjectGenerator {
    private final static Logger.Module MOD = new Logger.Module("STYLEOBJECT");

    private final static PerformanceLogger PLOG = PerformanceLogger.getLogger(StyleObjectGenerator.class);

    private final static Logger LOG = Logger.getLogger(StyleObjectGenerator.class, MOD);

    final private static String TEXT_AREA_FILED_MAKRUP1 = "<textarea name='";

    final private static String TEXT_AREA_FILED_MAKRUP2 = "' cols='25' rows='5' class='text' style='width:280px'>";

    final private static String TEXT_AREA_FILED_MAKRUP3 = "</textarea>";

    final private static String HIDDEN_FIELD_CAPTION_MARKUP1A = "<tr valign='top' bgcolor='EFEFEF'><td width='10'></td><td width='";

    final private static String HIDDEN_FIELD_CAPTION_MARKUP1B = "' height='0' class='text'>";

    final private static String HIDDEN_FIELD_CAPTION_MARKUP2A = "</td><td width='";

    final private static String HIDDEN_FIELD_CAPTION_MARKUP2B = "' height='0' class='text'>";

    final private static String FIELD_CAPTION_MARKUP1A = "<tr valign='top' bgcolor='EFEFEF'><td width='10'>&nbsp;</td><td width='";

    final private static String FIELD_CAPTION_MARKUP1B = "' height='26' class='text'>";

    final private static String FIELD_CAPTION_MARKUP2A = "</td><td width='";

    final private static String FIELD_CAPTION_MARKUP2B = "' height='26' class='text'>";

    final private static String FIELD_CAPTION_MARKUP3 = "</td></tr>";

    final private static String REQUIRED_FIELD = "<font color='FF0000'>&nbsp;*</font>";

    final private static SimpleDateFormat dateFormatter = new SimpleDateFormat(TextFormat.DATE_FORMAT);

    final String TRN_FILE = "icrm:main";

    final private static String COMMON_BOX_DEFAULT_SELECT = "common.box.defaultSelect";

    private StyleObjectGenerator() {
    }

    public void genField(FieldSO field, String dataValue, boolean edit, PrintWriter pw, String formName, String imagePath, String fieldCaptionWidth,
            String fieldBodyWidth, ServletRequest request) {
        final String METHOD_NAME = "genField";
        //PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_ENTRY);

        if (FieldTypeSO.HIDDEN_FIELD.equals(field.getInputType())) {
            genHiddenFieldCaption(field, fieldCaptionWidth, fieldBodyWidth, pw);
        } else {
            genFieldCaption(field, fieldCaptionWidth, fieldBodyWidth, pw);
        }

        if (FieldTypeSO.TEXT_BOX.equals(field.getInputType()))
            this.genTextInputBox(field, dataValue, edit, pw);
        else if (FieldTypeSO.TEXT_AREA.equals(field.getInputType()))
            this.genTextArea(field, dataValue, edit, pw);
        else if (FieldTypeSO.TEXT_DATE.equals(field.getInputType()))
            this.genNewDateInput(field, dataValue, edit, pw, formName, imagePath, request);
        else if (FieldTypeSO.DROP_DOWN_BOX.equals(field.getInputType()))
            this.genDropDownBox(field, dataValue, edit, pw);
        else if (FieldTypeSO.READ_ONLY.equals(field.getInputType()))
            this.genTextInputBox(field, dataValue, false, pw);
        else if (FieldTypeSO.CHECKBOX.equals(field.getInputType()))
            this.genSelectBox(field, dataValue, edit, pw);
        else if (FieldTypeSO.RADIO_BUTTON.equals(field.getInputType()))
            this.genRadioButton(field, dataValue, edit, pw);
        else if (FieldTypeSO.HIDDEN_FIELD.equals(field.getInputType()))
            this.genHidden(field, dataValue, edit, pw);
        if (ICRMConstant.TRUE.equals(field.getInputRequired()) && edit)
            pw.print(REQUIRED_FIELD);

        pw.print(FIELD_CAPTION_MARKUP3);
        pw.println();

        //PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_EXIT);
    }

    private void genFieldCaption(FieldSO field, String fieldCaptionWidth, String fieldBodyWidth, PrintWriter pw) {
        final String METHOD_NAME = "genFieldCaption";
        //PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_ENTRY);

        pw.print(FIELD_CAPTION_MARKUP1A);
        pw.print(fieldCaptionWidth);
        pw.print(FIELD_CAPTION_MARKUP1B);

        if (field.getCaption() != null) {
            if(!"".equals(getText(field.getCaption()))){
                pw.print(getText(field.getCaption())); //Erik(20060327): i18n
            //supported
                pw.print(":");
            }
        }
        pw.print(FIELD_CAPTION_MARKUP2A);
        pw.print(fieldBodyWidth);
        pw.print(FIELD_CAPTION_MARKUP2B);

        //PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_EXIT);
    }

    private void genHiddenFieldCaption(FieldSO field, String fieldCaptionWidth, String fieldBodyWidth, PrintWriter pw) {
        final String METHOD_NAME = "genHiddenFieldCaption";
        //PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_ENTRY);

        pw.print(HIDDEN_FIELD_CAPTION_MARKUP1A);
        pw.print(fieldCaptionWidth);
        pw.print(HIDDEN_FIELD_CAPTION_MARKUP1B);
        pw.print(HIDDEN_FIELD_CAPTION_MARKUP2A);
        pw.print(fieldBodyWidth);
        pw.print(HIDDEN_FIELD_CAPTION_MARKUP2B);

        //PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_EXIT);
    }

    final private static String SECTION_CAPTION_MARKUP1 = "<tr bgColor='#efefef'><td colspan='3'>&nbsp;</td></tr><TR  bgColor='#efefef' ><td width='10'>&nbsp;</td><TD class=text bgColor=#efefef colSpan=2 height=20><STRONG>";

    final private static String SECTION_CAPTION_MARKUP2 = "</STRONG></TD></TR><tr bgColor='#efefef'><td colspan='3' height='10'>&nbsp;</td></tr>";

    public void genSectionCaption(String caption, PrintWriter pw, String imgPath) {
        final String METHOD_NAME = "";
        //PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_ENTRY);

        // pw.print(SECTION_CAPTION_MARKUP1);

        if (caption != null && !"".equals(caption)){
            pw.print("<tr bgColor='#efefef'><td colspan='3'><img src='"
                    + imgPath
                    + "/dot-transparent.gif' width='10' height='20'></td></tr><TR  bgColor='#efefef' ><td width='10'>&nbsp;</td><TD class=text bgColor=#efefef colSpan=2 height=20><STRONG>");
            pw.print(caption);
            pw.print("</STRONG></TD></TR><tr bgColor='#efefef'><td colspan='3' height='10'><img src='" + imgPath
            + "/dot-transparent.gif' width='10' height='5'></td></tr>");        	
        }         
        //PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_EXIT);
    }

    final private static String TEXT_BOX_FILED_MAKRUP1 = "<input name='";

    final private static String TEXT_BOX_FILED_MAKRUP2 = "' type='text' class='text' value='";

    final private static String TEXT_BOX_FILED_MAKRUP3 = "'>";

    protected void genTextInputBox(FieldSO f, String dataValue, boolean edit, PrintWriter pw/*
                                                                                             * ,
                                                                                             * Buzz
                                                                                             * b
                                                                                             */) {
        final String METHOD_NAME = "genTextInputBox";
        //PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_ENTRY);

        if (edit) {
            pw.print("<input name='");
            pw.print(f.getHttpParamName());
            pw.print("' type='text' class='text' value='");
            pw.print(TextUtil.convert2Html(dataValue));
            pw.print("' ");

            if (f.getFieldLength().intValue() > 0) {
                pw.print(" maxLength='");
                pw.print(f.getFieldLength());
                pw.print("' ");
            }

            if (f.getDisplayWidth().intValue() > 0) {
                pw.print(" style='WIDTH:");
                pw.print(f.getDisplayWidth());
                pw.print("px' ");
            }
            pw.print(" >");
        } else {
            pw.print("<strong>");
            pw.print(TextUtil.convert2Html((dataValue == null || "".equals(dataValue)) ? FrontendConstantSO.EMPTY_FIELD : dataValue));
            pw.print("</strong>");
        }

        //PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_EXIT);
    }

    public void genDropDownBox(FieldSO f, String dataValue, boolean edit, PrintWriter pw) {
        final String METHOD_NAME = "genDropDownBox";
        //PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_ENTRY);

        if (edit) {
            SelectionDataBlockSO optionList = f.getOptionList();

            String selectedId = "";
            for (int i = 0; i < optionList.getTotalRow(); i++) {
                SelectionDataRowSO dataRow = optionList.getRow(i);
                if (dataValue.equals(dataRow.getCode()))
                    selectedId = dataRow.getCode();
            }

            pw.print("<select name='");
            pw.print(XSSHelper.encoder().encodeForHTML(f.getHttpParamName()));
            pw.print("' type='text' class='text' ");
            if (f.getFieldLength().intValue() > 0) {
                pw.print(" maxLength='");
                pw.print(f.getFieldLength());
                pw.print("' ");
            }
            if (f.getDisplayWidth().intValue() > 0) {
                pw.print(" style=WIDTH:'");
                pw.print(f.getDisplayWidth());
                pw.print("px'");
            }
            pw.print(" >");
            pw.print(getDropDownOptions(optionList, selectedId));
            pw.print("</select>");
        } else {
            SelectionDataBlockSO dataBlk = f.getOptionList();
            boolean codeSelected = false;
            for (int i = 0; i < dataBlk.getTotalRow(); i++) {
                SelectionDataRowSO dataRow = dataBlk.getRow(i);
                if (dataRow.getCode().equals(dataValue)) {
                    pw.print("<strong>");
                    //pw.print(dataRow.getDescription());
                    //conver to html by Yong 2006-02-09
                    pw.print(XSSHelper.encoder().encodeForHTML(dataRow.getDescription()));
                    pw.print("</strong>");
                    codeSelected = true;
                }
            }
            if (!codeSelected)
                pw.print(FrontendConstantSO.EMPTY_FIELD);
        }

        //PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_EXIT);
    }

    // ////////////////////////////////////////////////////////////////////////////////
    // For custom portlet dropdown box generation
    // ////////////////////////////////////////////////////////////////////////////////
    public String genDropDownBox(SelectionDataBlockSO block, String selectedCode, String paranName, int dispWidth, String defaultOptionDescription,
            boolean defaultOption, boolean edit) {
        return genDropDownBox(block, selectedCode, paranName, "", dispWidth, defaultOptionDescription, "", defaultOption, edit);
    }

    // ////////////////////////////////////////////////////////////////////////////////
    public String genDropDownBox(SelectionDataBlockSO block, String selectedCode, String paranName, String options, int dispWidth,
            String defaultOptionDescription, boolean defaultOption, boolean edit) {
        return genDropDownBox(block, selectedCode, paranName, options, dispWidth, defaultOptionDescription, "", defaultOption, edit);
    }

    // ////////////////////////////////////////////////////////////////////////////////
    public String genDropDownBox(SelectionDataBlockSO block, String selectedCode, String paranName, String options, int dispWidth,
            String defaultOptionDescription, String onClickAction, boolean defaultOption, boolean edit) {
        final String METHOD_NAME = "genDropDownBox";       
        //PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_ENTRY);

        if (block == null) {
            if (edit) {
                block = new SelectionDataBlockSO("BLANK_DUMMAY", new SelectionDataRowSO[0], null);
            } else {
                //PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_EXIT);
                return "";
            }
        }

        StringBuffer strBuffer = new StringBuffer();

        if (edit) {
            strBuffer.append("<select name='" + XSSHelper.encoder().encodeForHTMLAttribute(TextUtil.filterNull(paranName)) + "' class='text' ");
            if (dispWidth > 0) {
                strBuffer.append("style='width:" + dispWidth + "px'");
            }

            strBuffer.append(" ");
            strBuffer.append(options);
            if (!"".equals(TextUtil.filterNull(onClickAction)))
                strBuffer.append(" onClick='" + onClickAction + "'");
            strBuffer.append(">\r\n");

            if (defaultOption) {
                if (defaultOptionDescription != null && !defaultOptionDescription.trim().equals("")) {
                    //                    strBuffer.append("<option value=''>" +
                    // defaultOptionDescription + "</option>\r\n");
                    //modify by Bell Zhong on 2006-04-03
                    strBuffer.append("<option value=''>" + XSSHelper.encoder().encodeForHTMLAttribute(TextUtil.filterNull(this.getText(defaultOptionDescription))) + "</option>\r\n");
                } else {
                    //                    strBuffer.append("<option
                    // value=''>Select...</option>\r\n");
                    strBuffer.append("<option value=''>" + this.getText(COMMON_BOX_DEFAULT_SELECT) + "</option>\r\n");
                }
            }

            for (int i = 0; block != null && i < block.getTotalRow(); i++) {
            	if(block.getRow(i) != null && block.getRow(i).getCode() != null){
            		strBuffer.append("<option value='");
                    strBuffer.append(XSSHelper.encoder().encodeForHTMLAttribute(TextUtil.filterNull(block.getRow(i).getCode())));
                    strBuffer.append("' ");
                    if (!"".equals(selectedCode) && block.getRow(i).getCode().equals(selectedCode))
                        strBuffer.append(" selected ");
                    strBuffer.append(" >");
                    //strBuffer.append(block.getRow(i).getDescription());
                    //              convert to html by Yong 2006-02-09
                    strBuffer.append(XSSHelper.encoder().encodeForHTML(TextUtil.filterNull(block.getRow(i).getDescription())));
                    strBuffer.append("</option>\r\n");
            	}
                
            }

            strBuffer.append("</select>\r\n");
        } else {
            boolean codeSelected = false;
            for (int i = 0; i < block.getTotalRow(); i++) {
                SelectionDataRowSO dataRow = block.getRow(i);
                if (dataRow.getCode().equals(selectedCode)) {
                    strBuffer.append("<strong>");
                    //strBuffer.append(dataRow.getDescription());
                    //                  convert to html by Yong 2006-02-09
                    strBuffer.append(XSSHelper.encoder().encodeForHTML(TextUtil.filterNull(dataRow.getDescription())));

                    strBuffer.append("</strong>");
                    codeSelected = true;
                    break;
                }
            }

            if (!codeSelected) {
                strBuffer.append("<strong>");
                strBuffer.append(FrontendConstantSO.EMPTY_FIELD);
                strBuffer.append("</strong>");
            }
        }

        //PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_EXIT);
        return strBuffer.toString();
    }

    // ////////////////////////////////////////////////////////////////////////////////

    // ////////////////////////////////////////////////////////////////////////////////
    public String genDropDownBox(SelectionDataBlockSO block, String selectedCode, String paranName, String options, int dispWidth,
            String defaultOptionDescription, String onClickAction, boolean defaultOption, boolean edit, String onChange, boolean isDsiable) {
        final String METHOD_NAME = "genDropDownBox";
        //PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_ENTRY);

        
        if (block == null) {
            if (edit) {
                block = new SelectionDataBlockSO("BLANK_DUMMAY", new SelectionDataRowSO[0], null);
            } else {
                //PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_EXIT);
                return "";
            }
        }

        if(LOG.isDebugEnabled())
        {
        	LOG.debug(METHOD_NAME,"edit:"+edit);
        	LOG.debug(METHOD_NAME,"onClickAction:"+onClickAction);
        	LOG.debug(METHOD_NAME,"onChange:"+onChange);
        	LOG.debug(METHOD_NAME,"isDsiable:"+isDsiable);
        	LOG.debug(METHOD_NAME,"defaultOption:"+defaultOption);
        	LOG.debug(METHOD_NAME,"defaultOptionDescription:"+defaultOptionDescription);
        	LOG.debug(METHOD_NAME,"dispWidth:"+dispWidth);
        	LOG.debug(METHOD_NAME,"options:"+options);
        	LOG.debug(METHOD_NAME,"selectedCode:"+selectedCode);
        	LOG.debug(METHOD_NAME,"paranName:"+paranName);
        }
        StringBuffer strBuffer = new StringBuffer();

        if (edit) {
            strBuffer.append("<select name='" + XSSHelper.encoder().encodeForHTMLAttribute(TextUtil.filterNull(paranName)) + "' class='text' ");
            if (dispWidth > 0) {
                strBuffer.append("style='width:" + dispWidth + "px'");
            }
            if (isDsiable) {
                strBuffer.append(" disabled ");
            }

            strBuffer.append(" ");
            strBuffer.append(options);
            if (!"".equals(TextUtil.filterNull(onClickAction)))
                strBuffer.append(" onClick='" + onClickAction + "'");
            if (!"".equals(TextUtil.filterNull(onChange)))
                strBuffer.append(" onChange='" + onChange + "'");
            strBuffer.append(">\r\n");

            if (defaultOption) {
                if (defaultOptionDescription != null && !defaultOptionDescription.trim().equals("")) {
                    //                    strBuffer.append("<option value=''>" +
                    // defaultOptionDescription + "</option>\r\n");
                    //modify by Bell Zhong on 2006-04-03
                    strBuffer.append("<option value=''>" + XSSHelper.encoder().encodeForHTMLAttribute(TextUtil.filterNull(this.getText(defaultOptionDescription))) + "</option>\r\n");
                } else {
                    //                    strBuffer.append("<option
                    // value=''>Select...</option>\r\n");
                    strBuffer.append("<option value=''>" + this.getText(COMMON_BOX_DEFAULT_SELECT) + "</option>\r\n");
                }
            }

            for (int i = 0; i < block.getTotalRow(); i++) {
                strBuffer.append("<option value='");
                strBuffer.append(XSSHelper.encoder().encodeForHTMLAttribute(TextUtil.filterNull(block.getRow(i).getCode())));
                strBuffer.append("' ");
                if (!"".equals(selectedCode) && block.getRow(i).getCode().equals(selectedCode))
                    strBuffer.append(" selected ");
                strBuffer.append(" >");
                //strBuffer.append(block.getRow(i).getDescription());
                //              convert to html by Yong 2006-02-09
                strBuffer.append(XSSHelper.encoder().encodeForHTML(TextUtil.filterNull(block.getRow(i).getDescription())));
                strBuffer.append("</option>\r\n");
            }

            strBuffer.append("</select>\r\n");
            /* if(LOG.isDebugEnabled())
            {
            	LOG.debug(METHOD_NAME,"strBuffer:"+strBuffer.toString());
            }*/
        } else {
            boolean codeSelected = false;
            for (int i = 0; i < block.getTotalRow(); i++) {
                SelectionDataRowSO dataRow = block.getRow(i);
                if (dataRow.getCode().equals(selectedCode)) {
                    strBuffer.append("<strong>");
                    //strBuffer.append(dataRow.getDescription());
                    //                  convert to html by Yong 2006-02-09
                    strBuffer.append(XSSHelper.encoder().encodeForHTML(TextUtil.filterNull(dataRow.getDescription())));

                    strBuffer.append("</strong>");
                    codeSelected = true;
                    break;
                }
            }

            if (!codeSelected) {
                strBuffer.append("<strong>");
                strBuffer.append(FrontendConstantSO.EMPTY_FIELD);
                strBuffer.append("</strong>");
            }
            if(LOG.isDebugEnabled())
            {
            	LOG.debug(METHOD_NAME,"strBuffer:"+strBuffer.toString());
            }
        }

        //PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_EXIT);
        return strBuffer.toString();
    }

    ///////////////////////////////////////////////////////////////////////////////////
    /**
     * 
     * @see getDropDownOptions(SelectionDataBlockSO,String,boolean)
     */
    public String getDropDownOptions(SelectionDataBlockSO block, String selectedCode) {
        return getDropDownOptions(block, selectedCode, true);
    }

    public String getDropDownOptions(SelectionDataBlockSO block, String selectedCode, boolean defaultOption) {
        final String METHOD_NAME = "getDropDownOptions";
        //PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_ENTRY);

        if (block == null) {
            //PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_EXIT);
            return "";
        }

        StringBuffer strBuffer = new StringBuffer();
        if (defaultOption) {
// modify by Bell Zhong on 2006-07-27            
//            strBuffer.append("<option value=''>Select...</option>\r\n");
            strBuffer.append("<option value=''>"+this.getText("commonjsp.box.select")+"</option>\r\n");
        }
        for (int i = 0; i < block.getTotalRow(); i++) {
            strBuffer.append("<option value='");
            strBuffer.append(block.getRow(i).getCode());
            strBuffer.append("' ");
            if (!"".equals(selectedCode) && block.getRow(i).getCode().equals(selectedCode))
                strBuffer.append(" selected ");
            strBuffer.append(" >");
            //strBuffer.append(block.getRow(i).getDescription());
            //          convert to html by Yong 2006-02-09
            strBuffer.append(TextUtil.convert2Html(block.getRow(i).getDescription()));

            strBuffer.append("</option>\r\n");
        }

        //PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_EXIT);
        return strBuffer.toString();
    }

    public String getDropDownOptions(SelectionDataBlockSO block, String selectedCode, boolean defaultOption, String excludedCode) {
        final String METHOD_NAME = "getDropDownOptions";
        //PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_ENTRY);

        if (block == null) {
            //PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_EXIT);
            return "";
        }

        StringBuffer strBuffer = new StringBuffer();
        if (defaultOption) {
// modify by Bell Zhong on 2006-07-27            
//            strBuffer.append("<option value=''>Select...</option>\r\n");
            strBuffer.append("<option value=''>"+this.getText("commonjsp.box.select")+"</option>\r\n");
        }
        for (int i = 0; i < block.getTotalRow(); i++) {
            if (!block.getRow(i).getCode().equals(excludedCode)) {
                strBuffer.append("<option value='");
                strBuffer.append(XSSHelper.encoder().encodeForHTMLAttribute(block.getRow(i).getCode()));
                strBuffer.append("' ");
                if (!"".equals(selectedCode) && block.getRow(i).getCode().equals(selectedCode))
                    strBuffer.append(" selected ");
                strBuffer.append(" >");
                //strBuffer.append(block.getRow(i).getDescription());
                //              convert to html by Yong 2006-02-09
                strBuffer.append(XSSHelper.encoder().encodeForHTMLAttribute(block.getRow(i).getDescription()));

                strBuffer.append("</option>\r\n");
            }
        }

        //PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_EXIT);
        return strBuffer.toString();
    }
    
    //Added by Leo Song on 02-May-2007
    public String getDropDownOptions(SelectionDataBlockSO block, boolean defaultOption, String singleOption) 
    {
        if (block == null) 
        {
            return "";
        }

        StringBuffer strBuffer = new StringBuffer();
        if (defaultOption) {
            strBuffer.append("<option value=''>"+this.getText("commonjsp.box.select")+"</option>\r\n");
        }
        
        for (int i = 0; i < block.getTotalRow(); i++) {
            if (block.getRow(i).getCode().equals(singleOption)) {
            	
            	if(LOG.isDebugEnabled())
            	{
            		LOG.debug("getDropDownOptions","singleOption = " + singleOption);
            		LOG.debug("getDropDownOptions","block.getRow(i).getCode().equals(singleOption) = " + block.getRow(i).getCode().equals(singleOption));
            	}
                strBuffer.append("<option value='");
                strBuffer.append(block.getRow(i).getCode());
                strBuffer.append("' ");
                if (!"".equals(singleOption) && block.getRow(i).getCode().equals(singleOption))
                    strBuffer.append(" selected ");
                strBuffer.append(" >");
                strBuffer.append(TextUtil.convert2Html(block.getRow(i).getDescription()));
                strBuffer.append("</option>\r\n");
            }
        }
        return strBuffer.toString();
    }    
    
    protected void genSelectBox(FieldSO f, String dataValue, boolean edit, PrintWriter pw) {
        final String METHOD_NAME = "genSelectBox";
        //PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_ENTRY);

        if (edit) {
            pw.print("<input name='");
            pw.print(f.getHttpParamName());
            pw.print("' type='checkbox' class='text' value='");
            pw.print(f.getCheckBox().getOptionId());
            pw.print("' maxLength='");
            pw.print(f.getFieldLength());
            pw.print("' ");
            pw.print("WIDTH: '" + f.getDisplayWidth());
            pw.print("px'");
            if (dataValue.equalsIgnoreCase(ICRMConstant.TRUE))
                pw.print(" checked");
            if (!edit)
                pw.print(" disabled");
            pw.print(">");

            pw.print("<strong>");
            pw.print(f.getCheckBox().getOptionValue());
            pw.print("</strong>");
        } else {
            if (dataValue.equals(ICRMConstant.TRUE)) {
                pw.print("<strong>");
                pw.print(f.getCheckBox().getOptionValue());
                pw.print("</strong>");
            }
        }

        //PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_EXIT);
    }

    protected void genDateInput(FieldSO f, String dataValue, boolean edit, PrintWriter pw, String formName, String imagePath) {

        final String METHOD_NAME = "genDateInput";
        //PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_ENTRY);

        if (edit) {
            pw.print("<script> addCalendar('" + formName + "." + f.getHttpParamName() + "', 'WebTop+', '" + f.getHttpParamName() + "', '" + formName
                    + "');</script>");
            pw.print("<input name='");
            pw.print(f.getHttpParamName());
            pw.print("' type='text' class='text' value='");
            pw.print(dataValue);
            pw.print("' ");

            if (f.getFieldLength().intValue() > 0) {
                pw.print(" maxLength='");
                pw.print(f.getFieldLength());
                pw.print("' ");
            }
            if (f.getDisplayWidth().intValue() > 0) {
                pw.print(" style=WIDTH:'");
                pw.print(f.getDisplayWidth());
                pw.print("px'");
            }
            pw.print(">");
            pw.print("&nbsp;<a href=\"javascript:showCal('" + formName + "." + f.getHttpParamName() + "')\"><img src='" + imagePath
                    + "/Funktions_Icons/calendar_aktiv.gif' border='0'></a> ");
        } else {
            pw.print("<strong>");
            pw.print((dataValue == null || "".equals(dataValue)) ? FrontendConstantSO.EMPTY_FIELD : dataValue);
            pw.print("</strong>");
        }

        //PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_EXIT);
    }

    /**
     * Added By Roy Chen on 2007-03-13 for new calendar
     * @param f
     * @param dataValue
     * @param edit
     * @param pw
     * @param formName
     * @param imagePath
     */
    protected void genNewDateInput(FieldSO f, String dataValue, boolean edit, PrintWriter pw, String formName, String imagePath, ServletRequest request) {

        final String METHOD_NAME = "genNewDateInput";
        //PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_ENTRY);

        //<script language="javascript" type="text/javascript">
        //new CalendarManager('/wps/PA_1_0_IP/jsp/core/default/james/calendar.jsp','dd/MM/yyyy','https://cawbserver2.ubsams.com/icrm/core/image/default/james/arrow_previous.gif','https://cawbserver2.ubsams.com/icrm/core/image/default/james/arrow_next.gif');
        //</script>
        //<input type="text"  name="PROFILE_DATE_OF_BIRTH" id="PROFILE_DATE_OF_BIRTH" class="date-input" />&nbsp;<a onclick="document.JamesCalendarManager.open('PROFILE_DATE_OF_BIRTH', false,2002,2020,'13/03/2007','icrm','en_GB');return false;" ><img src="https://cawbserver2.ubsams.com/icrm/core/image/default/james/icon_calendar.gif" border="0" /></a>
        //<font color="FF0000">*</font>
        
        String EndYear=Config.getAppConfig().getValues().getStringValue(RequestParamConstant.COMMON_DATE_ENDYEAR,"2020");

        if (edit) {
            IWebContainer cntAc = WebContainer.getInstance();
            IWebResourceManager webMgrAc = cntAc.getWebResourceManager();
            //IUser user = UserFactory.getInstance().getUser(request);
//            String AccessDenied_url=webMgrAc.getJspResource("icrm:misc/AccessDenied.jsp", user.getLocale(), portletConfig.getContext()).getWebPath();
            HttpServletRequest httpRequst = (HttpServletRequest) request;
            
            pw.print("<script language=\"javascript\" type=\"text/javascript\"> new CalendarManager('" + httpRequst.getContextPath() + webMgrAc.getJspResource("icrm:james/calendar.jsp").getWebPath()+ "','dd/MM/yyyy','" + 
                    imagePath + "/james/arrow_previous.gif','"+ imagePath+ "/james/arrow_next.gif');</script>");
            pw.print("<input name='");
            pw.print(f.getHttpParamName());
            pw.print("' id='");
            pw.print(f.getHttpParamName());
            pw.print("' type='text' class='date-input' value='");
            pw.print(dataValue);
            pw.print("' ");

//            if (f.getFieldLength().intValue() > 0) {
//                pw.print(" maxLength='");
//                pw.print(f.getFieldLength());
//                pw.print("' ");
//            }
//            if (f.getDisplayWidth().intValue() > 0) {
//                pw.print(" style=WIDTH:'");
//                pw.print(f.getDisplayWidth());
//                pw.print("px'");
//            }
            pw.print("/>");
//            pw.print("&nbsp;<a href=\"javascript:showCal('" + formName + "." + f.getHttpParamName() + "')\"><img src='" + imagePath
//                    + "/Funktions_Icons/calendar_aktiv.gif' border='0'></a> ");
            pw.print("&nbsp;<a onclick=\"document.JamesCalendarManager.open('" + f.getHttpParamName() + "', false, 2002, "+EndYear+",'"+DateUtil.formatDate( new Date())+"','icrm','en_GB');return false;\"><img src='" + imagePath
                    + "/Funktions_Icons/calendar_aktiv.gif' border='0'></a> ");
        } else {
            pw.print("<strong>");
            pw.print((dataValue == null || "".equals(dataValue)) ? FrontendConstantSO.EMPTY_FIELD : dataValue);
            pw.print("</strong>");
        }

        //PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_EXIT);
    }
    
    
    public String genDateInput(String paramName, String dispWidth, Date dateValue, String formName, boolean isEdit, String imagePath) {
        String strDate = (dateValue == null) ? "" : dateFormatter.format(dateValue);

        return genDateInput(paramName, dispWidth, strDate, formName, isEdit, imagePath);
    }

    /**
     * @author Michael Feng
     * @param paramName
     * @param dispWidth
     * @param dateValue
     * @param formName
     * @param isEdit
     * @param imagePath
     * @param options
     * @return
     *  
     */
    public String genDateInput(String paramName, String dispWidth, String dateValue, String formName, boolean isEdit, String imagePath, String options) {
        return genDateInput(paramName, dispWidth, dateValue, formName, isEdit, imagePath, false, options);
    }

    public String genDateInput(String paramName, String dispWidth, String dateValue, String formName, boolean isEdit, String imagePath) {
        return genDateInput(paramName, dispWidth, dateValue, formName, isEdit, imagePath, false);
    }

    public String genDateInput(String paramName, String dispWidth, String dateValue, String formName, boolean isEdit, String imagePath,
            boolean checkDisabled) {
        return genDateInput(paramName, dispWidth, dateValue, formName, isEdit, imagePath, checkDisabled, "");
    }

    public String genDateInput(String paramName, String dispWidth, String dateValue, String formName, boolean isEdit, String imagePath,
            boolean checkDisabled, String options) {
        final String METHOD_NAME = "genDateInput";
        //PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_ENTRY);
        paramName = XSSHelper.encoder().encodeForHTMLAttribute(TextUtil.filterNull(paramName));
        dateValue = XSSHelper.encoder().encodeForHTMLAttribute(TextUtil.filterNull(dateValue));
        formName = XSSHelper.encoder().encodeForHTMLAttribute(TextUtil.filterNull(formName));
        options = XSSHelper.encoder().encodeForHTMLAttribute(TextUtil.filterNull(options));
        imagePath = XSSHelper.encoder().encodeForHTMLAttribute(TextUtil.filterNull(imagePath));
        
        if (dateValue == null)
            dateValue = "";
        if (isEdit) {
            StringBuffer strBuffer = new StringBuffer();
            strBuffer.append("<script> addCalendar('" + formName + "." + paramName + "', 'iCAWB', '" + paramName + "', '" + formName
                    + "');</script>");
            strBuffer.append("<input type='text' class='text' maxLength='12' ");
            if (dispWidth != null)
                strBuffer.append(" size='" + dispWidth + "' ");
            strBuffer.append(" name='" + paramName + "' size='20' value='" + dateValue + "'");

            if (options != null) {
                if (options.length() > 0){
                	strBuffer.append(" ").append(options);
                }
                    
            }

            strBuffer.append(">");
            strBuffer.append("&nbsp;<a href=\"javascript:document."+formName+"."+paramName+".focus();" + (checkDisabled ? "if (!document." + formName + "." + paramName + ".disabled) " : " ")
                    + "showCal('" + formName + "." + paramName + "')\"><img src='" + imagePath
                    + "/Funktions_Icons/calendar_aktiv.gif' border='0'></a> ");
            //PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_EXIT);
            return strBuffer.toString();
        } else {
            //PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_EXIT);
            return " <strong><font class='text' >" + dateValue + "</font></strong>";
        }
    }

    public String genDateInput(String paramName, String dispWidth, String dateValue, String formName, boolean isEdit, String imagePath,
            boolean checkDisabled, String options, boolean isDisable) {
        final String METHOD_NAME = "genDateInput";
        //PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_ENTRY);

        if (dateValue == null)
            dateValue = "";
        if (isEdit) {
            StringBuffer strBuffer = new StringBuffer();
            strBuffer.append("<script> addCalendar('" + formName + "." + paramName + "', 'WebTop+', '" + paramName + "', '" + formName
                    + "');</script>");
            strBuffer.append("<input type='text' class='text' maxLength='12' ");
            if (isDisable) {
                strBuffer.append(" disabled ");
            }
            if (dispWidth != null)
                strBuffer.append(" size='" + dispWidth + "' ");
            strBuffer.append(" name='" + paramName + "' size='20' value='" + dateValue + "'");

            if (options != null) {
                if (options.length() > 0){
                	strBuffer.append(" ").append(options);
                }
                    
            }

            strBuffer.append(">");
            strBuffer.append("&nbsp;<a href=\"javascript:" + (checkDisabled ? "if (!document." + formName + "." + paramName + ".disabled) " : " ")
                    + "showCal('" + formName + "." + paramName + "')\"><img src='" + imagePath
                    + "/Funktions_Icons/calendar_aktiv.gif' border='0'></a> ");
            //PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_EXIT);
            return strBuffer.toString();
        } else {
            //PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_EXIT);
            return "<font class='text' >" + dateValue + "</font>";
        }
    }
    
    //Adde by Roy Chen 2006-05-31 Start 
    /**
     * @param paramName
     * @param dispWidth
     * @param fieldWidth
     * @param dateValue
     * @param formName
     * @param isEdit
     * @param imagePath
     * @param checkDisabled
     * @param options
     * @param isDisable
     * @return
     */
    public String genDateInput(String paramName, int dispWidth, int fieldWidth, String dateValue, String formName, boolean isEdit, String imagePath,
            boolean checkDisabled, String options, boolean isDisable) {
        final String METHOD_NAME = "genDateInput";
        //PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_ENTRY);

        if (dateValue == null)
            dateValue = "";
        if (isEdit) {
            StringBuffer strBuffer = new StringBuffer();
            strBuffer.append("<script> addCalendar('" + formName + "." + paramName + "', 'WebTop+', '" + paramName + "', '" + formName
                    + "');</script>");
            strBuffer.append("<input type='text' class='text' ");
            if (isDisable) {
                strBuffer.append(" disabled ");
            }
            if (dispWidth > 0)
                strBuffer.append(" style=WIDTH:'" + dispWidth + "px' ");
            if (fieldWidth > 0)
                strBuffer.append(" maxLength='" + fieldWidth + "' ");
            
            strBuffer.append(" name='").append(paramName).append("' value='").append(dateValue).append("'");

            if (options != null) {
                if (options.length() > 0)
                    strBuffer.append(" ").append(options);
            }

            strBuffer.append(">");
            strBuffer.append("&nbsp;<a href=\"javascript:" + (checkDisabled ? "if (!document." + formName + "." + paramName + ".disabled) " : " ")
                    + "showCal('" + formName + "." + paramName + "')\"><img src='" + imagePath
                    + "/Funktions_Icons/calendar_aktiv.gif' border='0'></a> ");
            //PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_EXIT);
            return strBuffer.toString();
        } else {
            //PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_EXIT);
            return "<font class='text' >" + dateValue + "</font>";
        }
    }
    //Adde by Roy Chen 2006-05-31 End 

    protected void genRadioButton(FieldSO field, String dataValue, boolean edit, PrintWriter pw) {
        //
    }

    public String genRadioButton(String paramName, String value, String option, boolean checked, boolean isEdit) {
        return "<input type='radio' name='" + paramName + "' value='" + value + "' " + option + (checked ? " checked" : "")
                + (isEdit ? "" : " disabled") + ">";
    }
    
    public String genRadioButtonWithAction(String paramName, String value, boolean checked, boolean isEdit,String onClickAction) {
        String script =  "<input type='radio' name='" + paramName + "' value='" + value + "' "  + (checked ? " checked" : "")
                + (isEdit ? "" : " disabled") + " onClick='"+ onClickAction + "' >";
        return script;
    }

    public String genRadioButton(String paramName, String value, boolean checked, boolean isEdit) {
        return genRadioButton(paramName, value, "", checked, isEdit);
    }

    protected void genHidden(FieldSO f, String value, boolean isEdit, PrintWriter pw) {
        pw.print("<input name='" + f.getHttpParamName() + "' type='hidden' value='" + value + "' />");
    }

    protected void genFormHeader(FieldSO f, PrintWriter pw) {
    }

    public String genCheckBoxWithDisplay(String paramName, String value, String valueDescription, boolean checked, boolean isEdit) {
    	value = XSSHelper.encoder().encodeForHTMLAttribute(TextUtil.filterNull(value));
    	if (isEdit) {
            return "<input name='" + paramName + "' type='checkbox' value='" + value + "' " + (checked ? "checked" : "") + " >";
        } else {
            return "<input name='" + paramName + "' type='checkbox' value='" + value + "' " + (checked ? "checked" : "") + " disabled>";
        }
    }

    public String genCheckBoxWithDisplay(String paramName, String value, String valueDescription, boolean checked, boolean isEdit,
            String onClickAction) {
    	value = XSSHelper.encoder().encodeForHTMLAttribute(TextUtil.filterNull(value));
    	if (isEdit) {
            return "<input name='" + paramName + "' type='checkbox' value='" + value + "' " + (checked ? "checked" : "") + " onClick='"
                    + onClickAction + "' >";
        } else {
            return "<input name='" + paramName + "' type='checkbox' value='" + value + "' " + (checked ? "checked" : "") + " disabled>";
        }
    }

    public String genCheckBox(String paramName, String value, String valueDescription, boolean checked, boolean isEdit) {
    	value = XSSHelper.encoder().encodeForHTMLAttribute(TextUtil.filterNull(value));
    	valueDescription = XSSHelper.encoder().encodeForHTMLAttribute(TextUtil.filterNull(valueDescription));
    	if (isEdit) {
            return "<input name='" + paramName + "' type='checkbox' value='" + value + "' " + (checked ? "checked" : "") + " >";
        } else {
            //            return "<strong>" + valueDescription + "</strong>";
            //modify by Bell Zhong on 2006-04-07 valueDescription is i18n key
            return "<strong>" + this.getText(valueDescription) + "</strong>";
        }
    }

    public String genCheckBox(String paramName, String value, String description, String checkedDescription, boolean checked, boolean isEdit) {
        return genCheckBox(paramName, value, description, checkedDescription, checked, isEdit, "");
    }

    public String genCheckBox(String paramName, String value, String description, String checkedDescription, boolean checked, boolean isEdit,
            String options) {
    	value = XSSHelper.encoder().encodeForHTMLAttribute(TextUtil.filterNull(value));
    	description = XSSHelper.encoder().encodeForHTMLAttribute(TextUtil.filterNull(description));
    	checkedDescription = XSSHelper.encoder().encodeForHTMLAttribute(TextUtil.filterNull(checkedDescription));
    	if (isEdit) {
            return "<input name='" + paramName + "' type='checkbox' value='" + value + "' " + (checked ? "checked" : "") + " onClick='" + options
            //                    + "' >&nbsp;<strong>" + checkedDescription + "</strong>";
                    //modify by Bell Zhong on 2006-04-07
                    // begin--checkedDescription and description are i18n key
                    + "' >&nbsp;<strong>" + this.getText(checkedDescription) + "</strong>";
        } else {
            if (checked) {
                //                return "&nbsp;<strong>" + checkedDescription + "</strong>";
                return "&nbsp;<strong>" + this.getText(checkedDescription) + "</strong>";
            } else {
                //                return "&nbsp;<strong>" + description + "</strong>";
                return "&nbsp;<strong>" + this.getText(description) + "</strong>";
                //modify by Bell Zhong on 2006-04-07 end
            }
        }
    }

    public String genTextInputBox(String paramName, int fieldWidth, int dispWidth, String dataValue, boolean isEdit) {
        return genTextInputBox(paramName, fieldWidth, dispWidth, dataValue, isEdit, false, "");
    }

    // ///////////////////////////////////////////////////////////////////////////
    public String genTextInputBox(String paramName, int fieldWidth, int dispWidth, String dataValue, boolean isEdit, boolean disabled) {
        return genTextInputBox(paramName, fieldWidth, dispWidth, dataValue, isEdit, false, "");
    }

    public String genTextInputBox(String paramName, int fieldWidth, int dispWidth, String dataValue, boolean isEdit, boolean disabled, String options) {
        return genTextInputBox(paramName, fieldWidth, dispWidth, dataValue, isEdit, disabled, options, true);
    }
    
    // ///////////////////////////////////////////////////////////////////////////
    public String genTextInputBox(String paramName, int fieldWidth, int dispWidth, String dataValue, boolean isEdit, boolean disabled, String options, boolean showStar) {
        final String METHOD_NAME = "genTextInputBox";
        //PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_ENTRY);

        StringWriter strWriter = new StringWriter();
        PrintWriter pw = new PrintWriter(strWriter);
        if (isEdit) {
            pw.print("<input name=\"");
            pw.print(paramName);
            pw.print("\" type=\"text\" class=\"text\" ");
            if (dataValue != null) {
                pw.print(" value=\"");
                pw.print(TextUtil.convert2Html(dataValue));
                pw.print("\" ");
            }

            if (fieldWidth > 0) {
                pw.print(" maxLength=\"");
                pw.print(fieldWidth);
                pw.print("\" ");
            }

            if (dispWidth > 0) {
                pw.print(" style=\"WIDTH:");
                pw.print(dispWidth);
                pw.print("px\" ");
            }

            if (options != null && options.length() > 0) {
                pw.print(" ");
                pw.print(options);
                pw.print(" ");
            }

            if (disabled) {
                pw.print(" disabled ");
            }
            pw.print(" >");
        } else {
            if (dataValue != null) {
                pw.print("<strong id='");
                pw.print(paramName + "_icrm_label");
                pw.print("'>");
                pw.print((dataValue == null || "".equals(dataValue)) ? FrontendConstantSO.EMPTY_FIELD : TextUtil.convert2Html(dataValue));
                pw.print("</strong>");
            }
        }

        //PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_EXIT);
        return strWriter.toString();
    }
    
    /**
     * @param showEmptyStr , if true, will show empty String, else show "--"
     * @return
     */
    public String genTextInputBox(String paramName, int fieldWidth, int dispWidth, String dataValue, boolean isEdit, boolean disabled, String options, boolean showStar,boolean showEmptyStr ) {
        final String METHOD_NAME = "genTextInputBox";
        //PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_ENTRY);
       
        StringWriter strWriter = new StringWriter();
        PrintWriter pw = new PrintWriter(strWriter);
        if (isEdit) {
            pw.print("<input name=\"");
            pw.print(paramName);
            pw.print("\" type=\"text\" class=\"text\" ");
            if (dataValue != null) {
                pw.print(" value=\"");
                pw.print(TextUtil.convert2Html(dataValue));
                pw.print("\" ");
            }

            if (fieldWidth > 0) {
                pw.print(" maxLength=\"");
                pw.print(fieldWidth);
                pw.print("\" ");
            }

            if (dispWidth > 0) {
                pw.print(" style=\"WIDTH:");
                pw.print(dispWidth);
                pw.print("px\" ");
            }

            if (options != null && options.length() > 0) {
                pw.print(" ");
                pw.print(options);
                pw.print(" ");
            }

            if (disabled) {
                pw.print(" disabled ");
            }
            pw.print(" >");
        } else {
        	String emptyStr = (showEmptyStr == true ? "" : FrontendConstantSO.EMPTY_FIELD);
            if (dataValue != null) {
                pw.print("<strong id='");
                pw.print(paramName + "_icrm_label");
                pw.print("'>");
                pw.print(("".equals(dataValue)) ? emptyStr : TextUtil.convert2Html(dataValue));
                pw.print("</strong>");
            }
        }

        //PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_EXIT);
        return strWriter.toString();
    }

    // ///////////////////////////////////////////////////////////////////////////
    /*
     * public String genHTMLTextInputBox(String paramName , int fieldWidth , int
     * dispWidth , String dataValue , boolean isEdit ) { StringWriter strWriter =
     * new StringWriter(); PrintWriter pw= new PrintWriter(strWriter); if
     * (isEdit) { pw.print(" <input name=\""); pw.print(paramName); pw.print("\"
     * type=\"text\" class=\"text\" "); if(dataValue != null) { pw.print("
     * value=\""); pw.print(TextUtil.convert2Html(dataValue)); pw.print("\" "); }
     * 
     * 
     * if(fieldWidth > 0) { pw.print(" maxLength=\""); pw.print( fieldWidth );
     * pw.print("\" "); }
     * 
     * if( dispWidth > 0) { pw.print(" style=\"WIDTH:"); pw.print(dispWidth);
     * pw.print("\" "); }
     * 
     * pw.print(" >"); } else { if(dataValue != null) { pw.print(" <strong>");
     * pw.print(TextUtil.convert2Html((dataValue==null || dataValue=="") ?
     * FrontendConstantSO.EMPTY_FIELD:dataValue )); pw.print(" </strong>"); } }
     * return strWriter.toString(); }
     */
    // ///////////////////////////////////////////////////////////////////////////
    public String getDropDownOptions(SelectionDataBlockSO block, Set selectedCodeSet) {
        return getDropDownOptions(block, selectedCodeSet, true);
    }

    public String getDropDownOptions(SelectionDataBlockSO block, Set selectedCodeSet, boolean defaultOption) {
        final String METHOD_NAME = "getDropDownOptions";
        //PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_ENTRY);

        if (block == null)
            return "";
        StringBuffer strBuffer = new StringBuffer();
        if (defaultOption) {
            strBuffer.append("<option value=''>Select ...</option>");
        }
        for (int i = 0; i < block.getTotalRow(); i++) {
            SelectionDataRowSO row = block.getRow(i);
            strBuffer.append("<option value='");
            strBuffer.append(row.getCode());
            strBuffer.append("' ");
            if (selectedCodeSet.contains(row.getCode()))
                strBuffer.append(" selected ");
            strBuffer.append(" >");
            //strBuffer.append(row.getDescription());
            //          convert to html by Yong 2006-02-09
            strBuffer.append(TextUtil.convert2Html(block.getRow(i).getDescription()));

            strBuffer.append("</option>\r\n");
        }

        //PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_EXIT);
        return strBuffer.toString();
    }

    public String genMultiSelectBox(String paramName, String width, SelectionDataBlockSO block, String[] selectedCode, int size) {
        return this.genMultiSelectBox(paramName, width, block, selectedCode, size, null, false, false, true);
    }

    public String genMultiSelectBox(String paramName, String width, SelectionDataBlockSO block, String[] selectedCode, int size, boolean isEdit) {
        return this.genMultiSelectBox(paramName, width, block, selectedCode, size, null, false, false, isEdit);
    }

    public String genMultiSelectBox(String paramName, String width, SelectionDataBlockSO block, String[] selectedCode, int size,
            String defaultCaption, boolean allOption, boolean allOptionSelected, boolean isEdit) {
        return this.genMultiSelectBox(paramName, width, block, selectedCode, size, null, defaultCaption, false, false, isEdit);
    }

    public String genMultiSelectBox(String paramName, String width, SelectionDataBlockSO block, String[] selectedCode, int size, String option,
            String defaultCaption, boolean allOption, boolean allOptionSelected, boolean isEdit) {
        final String METHOD_NAME = "genMultiSelectBox";
        //PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_ENTRY);
        paramName = XSSHelper.encoder().encodeForHTMLAttribute(TextUtil.filterNull(paramName));
        defaultCaption = XSSHelper.encoder().encodeForHTMLAttribute(TextUtil.filterNull(defaultCaption));
        option = XSSHelper.encoder().encodeForHTMLAttribute(TextUtil.filterNull(option));
        
        Set selectedCodeSet = new HashSet();
        for (int i = 0; selectedCode != null && i < selectedCode.length; i++)
            selectedCodeSet.add(selectedCode[i]);

        if (block == null) {
            //PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_EXIT);
            return "";
        }
        StringBuffer strBuffer = new StringBuffer();

        if (isEdit) {
            strBuffer.append("<select name='" + paramName + "' class='text' ");

            strBuffer.append("style='width:" + width + "px'");

            strBuffer.append(" multiple ");
            if (size <= 1)
                size = 3;

            strBuffer.append(" size='").append(size).append("' ");

            if (option != null)
                strBuffer.append(option).append(" ");
            strBuffer.append(" >");
            if (defaultCaption != null) {
                strBuffer.append("<option value=''>" + defaultCaption + "</option>");
            }
            if (allOption) {
                strBuffer.append("<option value='ALL' " + (allOptionSelected ? "selected" : "") + ">ALL</option>");
            }
            strBuffer.append(getDropDownOptions(block, selectedCodeSet, false));
            strBuffer.append("</select>");
            //PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_EXIT);
            return strBuffer.toString();
        } else {
            for (int i = 0; i < block.getTotalRow(); i++) {
                SelectionDataRowSO row = block.getRow(i);
                if (selectedCodeSet.contains(XSSHelper.encoder().encodeForHTML(row.getCode()))) {
                    strBuffer.append("<strong>" + XSSHelper.encoder().encodeForHTML(row.getDescription()) + "</strong><br>");
                }
            }
            //PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_EXIT);
            return strBuffer.toString();
        }
    }

    /**
     * Method getDropDownOptions.
     * 
     * @param block
     * @param selectedCodeSet
     * @param b
     * @return Object
     */
    /*
     * private Object getDropDownOptions( SelectionDataBlockSO block, Set
     * selectedCodeSet, boolean b) { return null; }
     */

    public String genSelectBox(String paramName, String width, SelectionDataBlockSO block, String selectedCode, boolean edit) {
        final String METHOD_NAME = "genSelectBox";
        //PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_ENTRY);
        String returnString = null;
        if (edit) {
            //PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_EXIT);
            returnString = genSelectBox(paramName, width, block, selectedCode);
        } else {
            if (block == null || selectedCode == null) {
                //PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_EXIT);
                return "&nbsp;";
            }
            String description = block.getDescription(selectedCode);
            //          add by Yong 2006-02-09
            description = TextUtil.convert2Html(description);
            returnString = "<font class='text' >" + description + "</font>";
        }
        //PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_EXIT);
        return returnString;
    }
    
    //added by chuanyong you in ABR project
    public String genSelectBox(String paramName, String width, List selectList, String selectedCode, boolean edit) 
        {final String METHOD_NAME = "genSelectBox";
        
        //PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_ENTRY);
        StringBuffer strBuffer = new StringBuffer();
        

        if(edit) 
        {
        	
            strBuffer.append("<select name='" + paramName + "' class='text' ");
             strBuffer.append("style='width:" + width + "px'");        
            strBuffer.append(" >");
            
            if(selectedCode==null||"".equals(selectedCode))
            {
            	strBuffer.append("<option value='' selected >Select..</option>");
            }
      	    for (int i = 0; i < selectList.size(); i++) {
        		SelectionDataRowSO row;
                strBuffer.append("<option value='");
                row = (SelectionDataRowSO)selectList.get(i); 
                strBuffer.append(row.getCode());
                strBuffer.append("' ");
                if (!"".equals(selectedCode) && XSSHelper.encoder().encodeForHTMLAttribute(TextUtil.filterNull(row.getCode())).equals(selectedCode))
                    strBuffer.append(" selected ");
                strBuffer.append(" >");
                strBuffer.append(TextUtil.convert2Html(row.getDescription()));
                strBuffer.append("</option>\r\n");
               }     	              

           strBuffer.append("</select>");  
        
        } 
        else{
        	strBuffer.append("<strong>");
        	strBuffer.append((selectedCode == null || "".equals(selectedCode)) ? FrontendConstantSO.EMPTY_FIELD : selectedCode);
        	strBuffer.append("</strong>");        	
        	strBuffer.append("<input type='hidden' name='"+paramName+"' value='"+TextUtil.filterNull(selectedCode)+"'>");
        	}
        		
        return strBuffer.toString();
    }
    
    
    

    public String genSelectBox(String paramName, String width, SelectionDataBlockSO block, String selectedCode) {
        final String METHOD_NAME = "genSelectBox";
        //PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_ENTRY);

        StringBuffer strBuffer = new StringBuffer();
        strBuffer.append("<select name='" + paramName + "' class='text' ");
        strBuffer.append("style='width:" + width + "px'");
        strBuffer.append(" >");
        if (block != null)
            strBuffer.append(getDropDownOptions(block, selectedCode));
        strBuffer.append("</select>");

        //PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_EXIT);
        return strBuffer.toString();
    }

    public String getRadioButtonList(String paramName, SelectionDataBlockSO block, String selectedCode, boolean edit) {
        String returnString = null;
        if (edit)
            returnString = getRadioButtonList(paramName, block, selectedCode);
        else {
            String description = block.getDescription(selectedCode);
            //convert to html by Yong 2006-02-09
            description = TextUtil.convert2Html(description);
            returnString = "<font class='text' >" + description + "</font>";
        }
        return returnString;
    }

    public String getRadioButtonList(String paramName, SelectionDataBlockSO block, String selectedCode) {
        final String METHOD_NAME = "getRadioButtonList";
        //PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_ENTRY);

        if (paramName == null || block == null) {
            //PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_EXIT);
            return "";
        }
        StringBuffer strBuffer = new StringBuffer();
        for (int i = 0; i < block.getTotalRow(); i++) {
            SelectionDataRowSO row = block.getRow(i);
            strBuffer.append("<input type=radio value='");
            strBuffer.append(row.getCode());
            strBuffer.append("' ");
            strBuffer.append("name= '");
            strBuffer.append(paramName);
            strBuffer.append("'");
            if (!"".equals(selectedCode) && row.getCode().equals(selectedCode))
                strBuffer.append(" checked ");
            strBuffer.append(" ><font class ='text' ><strong>");
            //strBuffer.append(row.getDescription());
            //          convert to html by Yong 2006-02-09
            strBuffer.append(TextUtil.convert2Html(row.getDescription()));
            strBuffer.append("</strong></font>\r\n");
        }

        //PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_EXIT);
        return strBuffer.toString();
    }

    private static StyleObjectGenerator generator = null;

    public static synchronized StyleObjectGenerator getInstance() {
        final String METHOD_NAME = "getInstance";
        //PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_ENTRY);

        if (generator == null)
            generator = new StyleObjectGenerator();

        //PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_EXIT);
        return generator;
    }

    // /////////////////////////////////////////////////////////////////////////////////////
    protected void genTextArea(FieldSO f, String dataValue, boolean edit, PrintWriter pw) {
        final String METHOD_NAME = "genTextArea";
        //PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_ENTRY);

        pw.print("<textarea name='");
        pw.print(f.getHttpParamName());
        // pw.print("' cols='"+f.getFieldLength()+"'
        // rows='"+f.getFieldHeight()+"' class='text' ");
        pw.print("' rows='" + f.getDisplayHeigth() + "' class='text' ");

        if (f.getDisplayWidth().intValue() > 0) {
            pw.print(" style='width:" + f.getDisplayWidth() + "px'");
        }

        pw.print(">");
        pw.print((dataValue == null) ? "" : TextUtil.convert2Html(dataValue));
        pw.print("</textarea>");
        //PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_EXIT);
    }

    // /////////////////////////////////////////////////////////////////////////////////////
    public String genTextArea(String paramName, int col, int height, int dispWidth, String dataValue, boolean edit) {
        // final String METHOD_NAME = "genTextArea";
        // PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_ENTRY);
        //		
        // Writer writer= new StringWriter();
        // PrintWriter pw = new PrintWriter(writer);
        //
        // pw.print("<textarea name='");
        // pw.print(paramName);
        // pw.print("' cols='"+col+"' rows='"+height+"' class='text' ");
        //			
        // if(dispWidth> 0 ) {
        // pw.print(" style='width:"+dispWidth+"px'");
        // }
        // if (!edit)
        // {
        // pw.print(" onkeypress='blur();return false;' onclick='blur();return
        // false;' readonly");
        // }
        // pw.print(">");
        // pw.print((dataValue==null)? "":TextUtil.convert2Html(dataValue));
        // pw.print("</textarea>");
        //	    
        // PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_EXIT);
        // return writer.toString();
        return genTextArea(paramName, col, height, dispWidth, dataValue, edit, "");
    }

    public String genTextArea(String paramName, int col, int height, int dispWidth, String dataValue, boolean edit, boolean isDisabled) {

        return genTextArea(paramName, col, height, dispWidth, dataValue, edit, "", isDisabled);
    }
    
    // /////////////////////////////////////////////////////////////////////////////////////
    public String genTextArea(String paramName, int col, int height, int dispWidth, String dataValue, boolean edit, String options) {
        final String METHOD_NAME = "genTextArea";
        //PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_ENTRY);

        Writer writer = new StringWriter();
        PrintWriter pw = new PrintWriter(writer);

        pw.print("<textarea name='");
        pw.print(paramName);
        pw.print("' cols='" + col + "' rows='" + height + "' class='text' ");

        if (dispWidth > 0) {
            pw.print(" style='width:" + dispWidth + "px'");
        }

        if (!edit) {
            pw.print(" onkeypress='blur();return false;' onclick='blur();return false;' readonly");
        }

        if (options != null && options.length() > 0) {
            pw.print(" ");
            pw.print(options);
        }

        pw.print(">");
        pw.print((dataValue == null) ? "" : TextUtil.convert2Html(dataValue));
        pw.print("</textarea>");

        //PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_EXIT);
        return writer.toString();
    }

    public String genTextArea(String paramName, int col, int height, int dispWidth, String dataValue, boolean edit, String options,String classStyle) {
        final String METHOD_NAME = "genTextArea";
        //PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_ENTRY);
        
        Writer writer = new StringWriter();
        PrintWriter pw = new PrintWriter(writer);
        pw.print("<textarea name='");
        pw.print(paramName);
        pw.print("' cols='" + col + "' rows='" + height + "' class='" + classStyle +"' ");

        if (dispWidth > 0) {
            pw.print(" style='width:" + dispWidth + "px'");
        }

        if (!edit) {
            pw.print(" onkeypress='blur();return false;' onclick='blur();return false;' readonly");
        }

        if (options != null && options.length() > 0) {
            pw.print(" ");
            pw.print(options);
        }

        pw.print(">");
        pw.print((dataValue == null) ? "" : TextUtil.convert2Html(dataValue));
        pw.print("</textarea>");

        //PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_EXIT);
        return writer.toString();
    }
    
    /////////////////////////////////////////////////////////////////////////////////////////////
    public String genTextArea(String paramName, int col, int height, int dispWidth, String dataValue, boolean edit, String options, boolean isDisable) {
        final String METHOD_NAME = "genTextArea";
        //PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_ENTRY);

        Writer writer = new StringWriter();
        PrintWriter pw = new PrintWriter(writer);

        pw.print("<textarea name='");
        pw.print(paramName);
        pw.print("' cols='" + col + "' rows='" + height + "' class='text' ");

        if (dispWidth > 0) {
            pw.print(" style='width:" + dispWidth + "px'");
        }

        if (isDisable) {
            pw.print(" disabled ");
        }
        if (!edit) {
            pw.print(" onkeypress='blur();return false;' onclick='blur();return false;' readonly");
        }

        if (options != null && options.length() > 0) {
            pw.print(" ");
            pw.print(options);
        }

        pw.print(">");
        pw.print((dataValue == null) ? "" : TextUtil.convert2Html(dataValue));
        pw.print("</textarea>");

        //PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_EXIT);
        return writer.toString();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////

    public String genTextArea(String paramName, int col, int height, int dispWidth, String dataValue, boolean edit, String onClickScript,
            String onChangeScript, String options) {
        Writer writer = new StringWriter();
        PrintWriter pw = new PrintWriter(writer);

        pw.print("<textarea name='");
        pw.print(paramName);
        pw.print("' cols='" + col + "' rows='" + height + "' class='text' ");

        if (dispWidth > 0) {
            pw.print(" style='width:" + dispWidth + "px'");
        }
        if (!edit) {
            pw.print(" onclick='blur();return false;' onchange='blur();return false;' ");
        } else {
            pw.print(" onclick='" + onClickScript + "' onchange='" + onChangeScript + "' ");
        }
        pw.print(" ");
        pw.print(options);
        pw.print(">");
        pw.print((dataValue == null) ? "" : TextUtil.convert2Html(dataValue));
        pw.print("</textarea>");

        return writer.toString();
    }

    public String genAnchor(String url, String description, String img, String options, boolean isEdit) {
        final String METHOD_NAME = "genAnchor";
        //PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_ENTRY);

        StringBuffer buf = new StringBuffer(255);

        if (ICRMConstant.EMPTY_STRING.equals(description) && ICRMConstant.EMPTY_STRING.equals(img)) {
            buf.append("<a href=\">");
            buf.append(TextUtil.filterNull(url));
            buf.append("\" ");
            buf.append(TextUtil.filterNull(options));
            buf.append(">");
            buf.append(TextUtil.filterNull(img));
            buf.append("--");
            buf.append("</a>");
        }

        if (isEdit) {
            buf.append("<a href=\">");
            buf.append(TextUtil.filterNull(url));
            buf.append("\" ");
            buf.append(TextUtil.filterNull(options));
            buf.append(">");
            buf.append(TextUtil.filterNull(img));
            buf.append(TextUtil.convert2Html(description));
            buf.append("</a>");

            //PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_EXIT);
            return buf.toString();
        } else {
            //PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_EXIT);
            return TextUtil.convert2Html(description);
        }
    }

    public String genOnclick(String scriptName, String pattern) {
    	scriptName = XSSHelper.encoder().encodeForJavaScript(scriptName);
    	pattern = XSSHelper.encoder().encodeForJavaScript(pattern);
        String onclickScript = "onclick=\"javascript:" + scriptName + "('" + pattern + "');\"";
        return onclickScript;
    }

    public String genIndexingBar(Filter filter, String filterPattern, String onclickScript, String imagePath) {
        final String METHOD_NAME = "genIndexingBar";
        //PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_ENTRY);

        String availablePatterns[] = filter.getAvailablePatterns();
        Set matchedPatterns = SetUtil.toSet(filter.getMatchedPatterns());
        StringBuffer sb = new StringBuffer();
        sb.append("<TABLE cellSpacing=0 cellPadding=0 border=0>").append("\n");
        sb.append("<TBODY>").append("\n");
        sb.append("<TR>").append("\n");
        sb.append("<TD align=right>").append("\n");
        // sb.append("<SPAN class=onpagetopno>" + "A|&lt;" + "</SPAN>");
        sb.append("<A class=pagetopno-arrow href=\"#\" " + genOnclick(onclickScript, filter.getFirstSectionPattern()) + ">|&lt;</A>");
        sb.append("</TD>").append("\n");
        sb.append("<TD align=right>").append("<IMG height=10 src=\"" + imagePath + "/dot-transparent.gif\" width=10>").append("</TD>").append("\n");
        sb.append("<TD align=right>").append("\n");
        sb.append("<A class=pagetopno-arrow href=\"#\" " + genOnclick(onclickScript, filter.getPreviousSectionPattern()) + ">&lt;</A>");
        sb.append("</TD>").append("\n");
        sb.append("<TD align=right>").append("&nbsp;").append("</TD>").append("\n");
        sb.append("<TD align=right>").append("<IMG height=10 src=\"" + imagePath + "/dot-transparent.gif\" width=10>").append("</TD>").append("\n");
        for (int i = 0; i < availablePatterns.length; i++) {
            sb.append("<TD align=right>").append("\n");
            if (availablePatterns[i].equals(filterPattern)) {
                sb.append("<SPAN class=onpagetopno>" + availablePatterns[i] + "</SPAN>");
            } else if (matchedPatterns.contains(availablePatterns[i])) {
                sb.append("<A class=pagetopno href=\"#\" " + genOnclick(onclickScript, availablePatterns[i]) + ">" + availablePatterns[i] + "</A>");
            } else {
                sb.append("<SPAN class=pagetopno>" + availablePatterns[i] + "</SPAN>");
            }
            sb.append("</TD>").append("\n");
            sb.append("<TD align=right>").append("<IMG height=10 src=\"" + imagePath + "/dot-transparent.gif\" width=10>").append("</TD>").append(
                    "\n");
        }
        sb.append("<TD align=right>").append("\n");
        sb.append("<A class=pagetopno-arrow href=\"#\" " + genOnclick(onclickScript, filter.getNextSectionPattern()) + ">&gt;</A>");
        sb.append("</TD>").append("\n");
        sb.append("<TD align=right>").append("<IMG height=10 src=\"" + imagePath + "/dot-transparent.gif\" width=10>").append("</TD>").append("\n");
        sb.append("<TD align=right>").append("\n");
        sb.append("<A class=pagetopno-arrow href=\"#\" " + genOnclick(onclickScript, filter.getLastSectionPattern()) + ">&gt;|</A>");
        sb.append("</TD>").append("\n");
        sb.append("</TR>").append("\n");
        sb.append("</TBODY>").append("\n");
        sb.append("</TABLE>").append("\n");

        //PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_EXIT);
        return sb.toString();
    }

    public String genPagingBar(Filter filter, String filterPattern, String onclickScript, String imagePath) {
    	imagePath = XSSHelper.encoder().encodeForHTML(imagePath);
        final String METHOD_NAME = "genPagingBar";
        //PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_ENTRY);

        String availablePatterns[] = filter.getAvailablePatterns();
        Set matchedPatterns = SetUtil.toSet(filter.getMatchedPatterns());
        StringBuffer sb = new StringBuffer();
        sb.append("<TABLE cellSpacing=0 cellPadding=0 border=0>").append("\n");
        sb.append("<TBODY>").append("\n");
        sb.append("<TR>").append("\n");
        sb.append("<TD class=pagetopno-arrow vAlign=bottom align=right>").append("\n");
        if (!filter.isFirstSection()) {
            sb.append("<A class=pagetopno-arrow href=\"#\" " + genOnclick(onclickScript, filter.getFirstSectionPattern()) + ">|&lt;</A>")
                    .append("\n");
            sb.append("<A class=pagetopno-arrow href=\"#\" " + genOnclick(onclickScript, filter.getPreviousSectionPattern()) + ">&lt;</A>").append(
                    "\n");
        } else {
            //            
            //            sb.append("|&lt;").append("\n");
            //            sb.append("&lt;").append("\n");
        }
        // sb.append("<A class=pagetopno-arrow href=\"#\" " +
        // genOnclick(onclickScript, filter.getFirstSectionPattern()) +
        // ">|&lt;</A>").append("\n");
        // sb.append("<A class=pagetopno-arrow href=\"#\" " +
        // genOnclick(onclickScript, filter.getPreviousSectionPattern()) +
        // ">&lt;</A>").append("\n");
        // sb.append(filter.getFirstSectionURL()).append("\n");
        // sb.append(filter.getPreviousSectionURL()).append("\n");
        sb.append("<IMG height=10 src=\"" + imagePath + "/dot-transparent.gif\" width=10 align=absMiddle border=0>").append("\n");
        sb.append("</TD>");
        sb.append("<TD class=pagetopno align=right>").append("\n");
        for (int i = 0; i < availablePatterns.length; i++) {
            if (availablePatterns[i].equals(filterPattern))  
                sb.append("<SPAN class=onpagetopno>" + availablePatterns[i] + "</SPAN>");
            else
                sb.append("<A class=pagetopno href=\"#\" " + genOnclick(onclickScript, availablePatterns[i]) + ">" + availablePatterns[i] + "</A>");
            if (i < availablePatterns.length - 1)
                sb.append("/").append("\n");
            else
                sb.append("\n");
        }

        sb.append("<IMG height=10 src=\"" + imagePath + "/dot-transparent.gif\" width=10 align=absMiddle>").append("\n");
        sb.append("<SPAN class=pagetopno-arrow>").append("\n");
        if (!filter.isLastSection()) {
            sb.append("<A class=pagetopno-arrow href=\"#\" " + genOnclick(onclickScript, filter.getNextSectionPattern()) + ">&gt;</A>").append("\n");
            sb.append("<A class=pagetopno-arrow href=\"#\" " + genOnclick(onclickScript, filter.getLastSectionPattern()) + ">&gt;|</A>").append("\n");
        } else {//          
            //            sb.append("&gt;").append("\n");
            //            sb.append("&gt;|").append("\n");
        }
        // sb.append(filter.getNextSectionURL()).append("\n");
        // sb.append(filter.getLastSectionURL()).append("\n");
        sb.append("<IMG height=10 src=\"" + imagePath + "/dot-transparent.gif\" width=10 align=absMiddle>").append("\n");
        sb.append("</SPAN>").append("\n");
        sb.append("</TD>").append("\n");
        sb.append("</TR>").append("\n");
        sb.append("</TBODY>").append("\n");
        sb.append("</TABLE>").append("\n");        
        //PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_EXIT);
        return sb.toString();
    }

    public String genNewIndexingBar(IndexFilterPredicate indexPredicate, String onclickScript, String imagePath) {
        StringBuffer sb = new StringBuffer();
        String[] initialArray = indexPredicate.getAvailableIndices();
        Set initialSet = SetUtil.toSet(indexPredicate.getMatchedIndices());
        String initial = indexPredicate.getSelectedIndex();
        sb.append("<table height=\"21\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">").append("\n");
        sb.append(genNewIndexingBarBorder(indexPredicate, imagePath));

        sb.append("<tr class=\"tag-on\">").append("\n");
        for (int i = 0; i < initialArray.length; i++) {
            if (initialArray[i].equals(initial)) {
                if (initialArray[i].equals(IndexFilterPredicate.ALL_INDEX)) {
                    sb.append("<td width=\"30\" align=\"center\" bgcolor=\"99ADC2\">" + initialArray[i] + "</td>").append("\n");
                } else {
                    sb.append("<td width=\"20\" align=\"center\" bgcolor=\"99ADC2\">" + initialArray[i] + "</td>").append("\n");
                }
            } else if (initialArray[i].equals(IndexFilterPredicate.ALL_INDEX)) {
                if (initialSet.contains(initialArray[i])) {
                    sb.append(
                            "<td width=\"30\" align=\"center\" bgcolor=\"BFCCD9\" class=\"tag-off\"><A href=\"#\" "
                                    + genOnclick(onclickScript, initialArray[i]) + " class=\"tag-off\">" + initialArray[i] + "</A></td>")
                            .append("\n");
                } else {
                    sb.append(
                            "<td width=\"30\" align=\"center\" bgcolor=\"BFCCD9\" class=\"tag-off\"><font color=\"#999999\">" + initialArray[i]
                                    + "</font></td>").append("\n");
                }
            } else {
                if (initialSet.contains(initialArray[i])) {
                    sb.append(
                            "<td width=\"20\" align=\"center\" bgcolor=\"BFCCD9\" class=\"tag-off\"><A href=\"#\" "
                                    + genOnclick(onclickScript, initialArray[i]) + " class=\"tag-off\">" + initialArray[i] + "</A></td>")
                            .append("\n");
                } else {
                    sb.append(
                            "<td width=\"20\" align=\"center\" bgcolor=\"BFCCD9\" class=\"tag-off\"><font color=\"#999999\">" + initialArray[i]
                                    + "</font></td>").append("\n");
                }
            }
            sb.append(
                    "<td width=\"1\" bgcolor=\"#FFFFFF\" class=\"tag-off\"><img src=\"" + imagePath
                            + "/dot-transparent.gif\" width=\"1\" height=\"1\"></td>").append("\n");
        }
        sb.append("</tr>").append("\n");
        sb.append(genNewIndexingBarBorder(indexPredicate, imagePath)).append("\n");
        sb.append("</table>").append("\n");
        return sb.toString();
    }

    private String genNewIndexingBarBorder(IndexFilterPredicate indexPredicate, String imagePath) {
        String[] initialArray = indexPredicate.getAvailableIndices();
        String initial = indexPredicate.getSelectedIndex();
        StringBuffer sb = new StringBuffer();
        sb.append("<tr class=\"tag-on\">").append("\n");
        for (int i = 0; i < initialArray.length; i++) {
            if (initialArray[i].equals(initial)) {
                sb.append(
                        "<td align=\"center\" bgcolor=\"99ADC2\" class=\"tag-off\"><img src=\"" + imagePath
                                + "/dot-transparent.gif\" height=\"1\"></td>").append("\n");
            } else {
                sb.append(
                        "<td align=\"center\" bgcolor=\"FFFFFF\" class=\"tag-off\"><img src=\"" + imagePath
                                + "/dot-transparent.gif\" height=\"1\"></td>").append("\n");
            }
            sb.append("<td bgcolor=\"#FFFFFF\" class=\"tag-off\"><img src=\"" + imagePath + "/dot-transparent.gif\" width=\"1\" height=\"1\"></td>")
                    .append("\n");
        }
        sb.append("</tr>").append("\n");
        return sb.toString();
    }

    public String genTopPagingBar(Paginator paginator, String onclickArrowScript, String onclickPageNumScript, String imagePath) {
        return genNewPagingBar(paginator, onclickArrowScript, onclickPageNumScript, imagePath, false);
    }

    public String genBottomPagingBar(Paginator paginator, String onclickArrowScript, String onclickPageNumScript, String imagePath) {
        return genNewPagingBar(paginator, onclickArrowScript, onclickPageNumScript, imagePath, true);
    }

    private String genNewPagingBar(Paginator paginator, String onclickArrowScript, String onclickPageNumScript, String imagePath, boolean isFooter) {
        Set arrowSet = paginator.getArrowSet();
        int subPageNumR1 = paginator.getPageRangeStart();
        int subPageNumR2 = paginator.getPageRangeEnd();
        int subPageNum = paginator.getCurrentPage();
        StringBuffer sb = new StringBuffer();

        String pageArrowClass = "\"pagetopno-arrow\"";
        String pageNumClass = "\"pagetopno\"";
        String onPageNumClass = "\"onpagetopno\"";

        if (isFooter) {
            pageArrowClass = "\"pagebtmno2-arrow\"";
            pageNumClass = "\"pagebtmno2\"";
            onPageNumClass = "\"onpagebtmno2\"";
        }

        sb.append("<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\">").append("\n");
        sb.append("<tr>").append("\n");
        sb.append("<td height=\"20\" width=\"32\" align=\"right\"><span class=" + pageArrowClass + ">").append("\n");
        if (arrowSet != null && arrowSet.contains("F")) {
            sb.append("<a href=\"#\" class=" + pageArrowClass + " " + genOnclick(onclickArrowScript, "F") + ">|&lt;</a>").append("&nbsp;");
        } else {
            // sb.append("<a href=\"#\"
            // class="+pageArrowClass+">|&lt;</a>").append("&nbsp;");
        }
        if (arrowSet != null && arrowSet.contains("P")) {
            sb.append("<a href=\"#\" class=" + pageArrowClass + " " + genOnclick(onclickArrowScript, "P") + ">&lt;</a>");
        } else {
            // sb.append("<a href=\"#\" class="+pageArrowClass+">&lt;</a>");
        }
        sb.append("</span></td>");
        sb.append("<td><img src=\"" + imagePath + "/dot-transparent.gif\" width=\"5\"></td>");
        sb.append("<td align=\"center\" class=" + pageNumClass + ">");
        for (int i = subPageNumR1; i <= subPageNumR2; i++) {
            if (i != subPageNumR1) {
                sb.append("&nbsp;").append("/").append("&nbsp;");
            }
            if (i == subPageNum) {
                sb.append("<span class=" + onPageNumClass + ">" + i + "</span>");
            } else {
                sb.append("<a href=\"#\" class=" + pageNumClass + " " + genOnclick(onclickPageNumScript, "" + i) + ">" + i + "</a>");
            }
        }
        sb.append("</td>");
        sb.append("<td><img src=\"" + imagePath + "/dot-transparent.gif\" width=\"5\"></td>");
        sb.append("<td width=\"32\" class=" + pageNumClass + "><span class=" + pageArrowClass + ">");
        if (arrowSet != null && arrowSet.contains("N")) {
            sb.append("<a href=\"#\" class=" + pageArrowClass + " " + genOnclick(onclickArrowScript, "N") + ">&gt;</a>").append("&nbsp;");
        } else {
            // sb.append("<a href=\"#\"
            // class="+pageArrowClass+">&gt;</a>").append("&nbsp;");
        }
        if (arrowSet != null && arrowSet.contains("L")) {
            sb.append("<a href=\"#\" class=" + pageArrowClass + " " + genOnclick(onclickArrowScript, "L") + ">&gt;|</a>");
        } else {
            // sb.append("<a href=\"#\" class="+pageArrowClass+">&gt;|</a>");
        }
        sb.append("</span></td>");
        sb.append("</tr>");
        sb.append("</table>");
        //LOG.error("********", sb.toString());
        return sb.toString();
    }

    public static void main(String[] args) {
       
        Filter filter = new ObjectInitialFilter(new PartnerNameGetterSO());
        String chars = args[0];
        IProfileSO[] profiles = new IProfileSO[chars.length()];
        for (int i = 0; i < profiles.length; i++) {
            IProfileSO profile = new ProfileSO();
            profile.setFamilyName(chars.substring(i, i + 1) + "00000" + i + " FM_NAME");
            profile.setFirstName(chars.substring(i, i + 1) + "00000" + i + " FIRST_NAME");
            profile.setPartnerId(chars.substring(i, i + 1) + "00000" + i);
            profiles[i] = profile;
        }

        profiles = (IProfileSO[]) filter.filter(profiles, args[1]).toArray(new IProfileSO[0]);
        //System.out.println(g.genIndexingBar(filter, args[1], "changeFilter", "<%=imagePath%>"));
        for (int i = 0; i < profiles.length; i++) {
          //  System.out.println(new PartnerNameGetterSO().get(profiles[i]));
        }
       // System.out.println(filter.getDefaultPattern(profiles));
    }

    private String getText(String key) {
    	if (key!=null && key.length()>0)
    	{
	        Locale locale = LocaleMap.get();
	        IContainer cnt = GenericContainer.getInstance();
	        ITextBundleManager txtBundleMgr = cnt.getTextBundleManager();
	        ITextBundle txtBundle = txtBundleMgr.getTextBundle(TRN_FILE, locale);
	        String text = txtBundle.getText(key);        
	        return text;
    	}
    	else
    		return "";
    }
    
    public String genFontColor(boolean logic, boolean editable) {
    	StringBuffer sb = new StringBuffer();
    	if (!editable) {
    		sb.append("<font color='808080'>*</font>");
		} else {
			if (logic) {
				sb.append("<font color='FF0000'>*</font>");
			} else {
				sb.append("<font style='display:none' color='808080'>*</font>");
			}
		}   	
    	return sb.toString();
    }
   
    public String genFontColor(boolean logic) {
    	StringBuffer sb = new StringBuffer();
    	if (logic) {
    		sb.append("<font color='FF0000'>*</font>");
		} else {
			sb.append("<font color='808080'>*</font>");
		}
    	return sb.toString();
    }
    
    public String genTextInputBoxWithDisplay(String paramName, int fieldWidth, int dispWidth, String dataValue, 
    		boolean isEdit, boolean disabled) {
        final String METHOD_NAME = "genTextInputBoxWithDisplay";
        PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_ENTRY);

        StringWriter strWriter = new StringWriter();
        PrintWriter pw = new PrintWriter(strWriter);

        pw.print("<input name=\"");
        pw.print(paramName);
        pw.print("\" type=\"text\" class=\"text\" ");
        if (dataValue != null) {
            pw.print(" value=\"");
            pw.print(TextUtil.convert2Html(dataValue));
            pw.print("\" ");
        }

        if (fieldWidth > 0) {
            pw.print(" maxLength=\"");
            pw.print(fieldWidth);
            pw.print("\" ");
        }

        if (dispWidth > 0) {
            pw.print(" style=\"WIDTH:");
            pw.print(dispWidth);
            pw.print("px\" ");
        }
        
        if (!isEdit) {
            pw.print(" onkeypress='blur();return false;' onclick='blur();return false;' readonly");
        }

        if (disabled) {
            pw.print(" disabled ");
        }
        pw.print(" >");
        
        PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_EXIT);
        
        return strWriter.toString();
    }
    
    public String genDropDownBoxFromMap(Map map, String selectedCode, String paranName, int dispWidth,
            String defaultOptionDescription, boolean defaultOption, boolean edit) {
        final String METHOD_NAME = "genDropDownBoxFromMap";       
        //PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_ENTRY);

        if (map == null) {
            map = new HashMap();
        }
        List partnerRoleList = new ArrayList();
        if(map.size()>0) {
        	partnerRoleList.addAll(map.keySet());
        }
        Collections.sort(partnerRoleList);

        StringBuffer strBuffer = new StringBuffer();

        if (edit) {
            strBuffer.append("<select name='" + XSSHelper.encoder().encodeForHTMLAttribute(TextUtil.filterNull(paranName)) + "' class='text' ");
            if (dispWidth > 0) {
                strBuffer.append("style='width:" + dispWidth + "px'");
            }

            strBuffer.append(" ");
            strBuffer.append(">\r\n");

            if (defaultOption) {
                if (defaultOptionDescription != null && !defaultOptionDescription.trim().equals("")) {
                    //                    strBuffer.append("<option value=''>" +
                    // defaultOptionDescription + "</option>\r\n");
                    //modify by Bell Zhong on 2006-04-03
                    strBuffer.append("<option value=''>" + XSSHelper.encoder().encodeForHTMLAttribute(defaultOptionDescription) + "</option>\r\n");
                } else {
                    //                    strBuffer.append("<option
                    // value=''>Select...</option>\r\n");
                    strBuffer.append("<option value=''>" + this.getText(COMMON_BOX_DEFAULT_SELECT) + "</option>\r\n");
                }
            }

            for(int i=0;i<partnerRoleList.size();i++) {
            	String pid = (String)partnerRoleList.get(i);
        		strBuffer.append("<option value='");
        		strBuffer.append(XSSHelper.encoder().encodeForHTMLAttribute(TextUtil.filterNull(pid)));
        		strBuffer.append("' ");
        		if(pid.equals(selectedCode)) {
        			strBuffer.append(" selected ");
        		}
        		strBuffer.append(" >");
        		strBuffer.append(XSSHelper.encoder().encodeForHTML(TextUtil.filterNull(map.get(pid))));
                strBuffer.append("</option>\r\n");
            }
            
            
          /*  for (int i = 0; map != null && i < map.size(); i++) {
            	if(block.getRow(i) != null && block.getRow(i).getCode() != null){
            		strBuffer.append("<option value='");
                    strBuffer.append(XSSHelper.encoder().encodeForHTMLAttribute(TextUtil.filterNull(block.getRow(i).getCode())));
                    strBuffer.append("' ");
                    if (!"".equals(selectedCode) && block.getRow(i).getCode().equals(selectedCode))
                        strBuffer.append(" selected ");
                    strBuffer.append(" >");
                    //strBuffer.append(block.getRow(i).getDescription());
                    //              convert to html by Yong 2006-02-09
                    strBuffer.append(XSSHelper.encoder().encodeForHTML(TextUtil.filterNull(block.getRow(i).getDescription())));
                    strBuffer.append("</option>\r\n");
            	}
                
            }*/

            strBuffer.append("</select>\r\n");
        } else {
            boolean codeSelected = false;
            for (int i = 0; i < partnerRoleList.size(); i++) {
            	String pid = (String)partnerRoleList.get(i);
                if (pid.equals(selectedCode)) {
                    strBuffer.append("<strong>");
                    //strBuffer.append(dataRow.getDescription());
                    //                  convert to html by Yong 2006-02-09
                    strBuffer.append(XSSHelper.encoder().encodeForHTML(TextUtil.filterNull(map.get(pid))));

                    strBuffer.append("</strong>");
                    codeSelected = true;
                    break;
                }
            }

            if (!codeSelected) {
                strBuffer.append("<strong>");
                strBuffer.append(XSSHelper.encoder().encodeForHTMLAttribute(defaultOptionDescription));
                strBuffer.append("</strong>");
            }
        }

        //PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_EXIT);
        return strBuffer.toString();
    }
    
}
