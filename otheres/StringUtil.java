

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;




public class StringUtil {
    /**
     * 
     * <pre>
     * 
     *  
     *   
     *    
     *     
     *      
     *       
     *        
     *         
     *          
     *           
     *            
     *             
     *              
     *               
     *                StringUtils.isNumeric(null)   = false
     *                StringUtils.isNumeric(&quot;&quot;)     = true
     *                StringUtils.isNumeric(&quot;  &quot;)   = false
     *                StringUtils.isNumeric(&quot;123&quot;)  = true
     *                StringUtils.isNumeric(&quot;12 3&quot;) = false
     *                StringUtils.isNumeric(&quot;ab2c&quot;) = false
     *                StringUtils.isNumeric(&quot;12-3&quot;) = false
     *                StringUtils.isNumeric(&quot;12.3&quot;) = false
     *                
     *               
     *              
     *             
     *            
     *           
     *          
     *         
     *        
     *       
     *      
     *     
     *    
     *   
     *  
     * </pre>
     * 
     * @param str
     * @return true is the input string is a digit
     */
    
    private final static Logger.Module MOD = new Logger.Module(ModuleId.MOD_UTIL);
    private static Logger LOG = Logger.getLogger(StringUtil.class, MOD);
    
    public static boolean isNumeric(String str) {
        if (str == null) {
            return false;
        }
        int sz = str.length();
        for (int i = 0; i < sz; i++) {
            if (Character.isDigit(str.charAt(i)) == false) {
                return false;
            }
        }
        return true;
    }

    public static String replaceAll(String s, String oldText, String newText) {
        StringBuffer sb = new StringBuffer();
        int i = 0, oldTextLength = oldText.length(), textLength = s.length();

        while (true) {
            int pos = s.indexOf(oldText, i);

            if (pos < 0)
                break;

            sb.append(s.substring(i, pos));
            sb.append(newText);

            i = pos + oldTextLength;
        }

        for (int j = i; j < textLength; j++)
            sb.append(s.charAt(j));

        return sb.toString();
    }

    /**
     * 
     * <pre>
     * 
     *  
     *   
     *    
     *     
     *       StringUtil.replaceAll(&quot;&quot;, &quot;&quot;, &quot;_NS_&quot;)           return  &quot;&quot;
     *       StringUtil.replaceAll(null, &quot;&quot;, &quot;_NS_&quot;)                   return  null
     *       StringUtil.replaceAll(&quot;ADFASDFFF&quot;, &quot;F&quot;, null))            return  ADFASDFFF
     *       StringUtil.replaceAll(&quot;ADFASDFFF&quot;, null, &quot;_N_&quot;)           return  ADFASDFFF
     *       StringUtil.replaceAll(&quot;ADFASDFFF&quot;, &quot;&quot;, &quot;_NS_&quot;)  return  ADFASDFFF
     *     
     *       StringUtil.replaceAll(&quot;ADFASDFFF&quot;, &quot;F&quot;, &quot;_&quot;)                                  return AD_ASD___
     *       StringUtil.replaceAll(&quot;ADFASDFFF&quot;, &quot;DF&quot;, &quot;&circ;_&circ;&quot;)                               return A&circ;_&circ;S&circ;_&circ;F
     *       
     *      
     *     
     *    
     *   
     *  
     * </pre>
     * 
     * @param src
     *            The target string
     * @param oldText
     *            The string will be replace
     * @param newText
     *            The string will be replace to
     * @return the result string
     */
    public static String replaceAllChar(String src, String oldText, String newText) {

        // Modify by yangzhao 2006-04-03
        if (src == null || isEmpty(oldText) || newText == null) {
            return src;
        }

        final int len = newText.length();
        StringBuffer sb = new StringBuffer();
        int found = -1;
        int start = 0;
        while ((found = src.indexOf(oldText, start)) != -1) {
            sb.append(src.substring(start, found));
            sb.append(newText);
            start = found + len;
        }

        sb.append(src.substring(start));

        return sb.toString();

        /*
         * int i = 0, oldTextLength = oldText.length(), textLength = s.length();
         * 
         * while (true) { int pos = s.indexOf(oldText, i);
         * 
         * if (pos < 0) break;
         * 
         * sb.append(s.substring(i, pos)); sb.append(newText);
         * 
         * i = pos + oldTextLength; }
         * 
         * for (int j = i; j < textLength; j++) sb.append(s.charAt(j));
         * 
         * return sb.toString();
         */
    }

    public static void main(String[] args)throws Exception {
        
//        String outputStr=fillString("12345","0",true,9);
//        System.out.println("output string is :"+outputStr);
//        String a = "sdfjksdfjsdfjisFjsdfksd:Fdsfksdfjsdkfieurerewrewr";
//        System.out.println(replaceAll(a, ":F", "@R"));
//
//        // Add by yangzhao 2006-04-03
//        // just for test the replaceAll method
//        String s = "ADFASDFFF";
//        System.out.println(StringUtil.replaceAllChar("ADFASDFFF", "F", "_"));
//        System.out.println(StringUtil.replaceAllChar("ADFASDFFF", "F", "^_^"));
//        System.out.println(StringUtil.replaceAllChar("ADFASDFFF", "DF", "^_^"));
//
//        System.out.println(StringUtil.replaceAllChar("", "", "_NS_").equals(""));
//        System.out.println(StringUtil.replaceAllChar(null, "", "_NS_"));
//        System.out.println(StringUtil.replaceAllChar("ADFASDFFF", "F", null));
//        System.out.println(StringUtil.replaceAllChar("ADFASDFFF", null, "_N_"));
//
//        System.out.println(StringUtil.replaceAllChar("ADFASDFFF", "", "_NS_"));
        // test end
//        
//    	String url = "<!-- debug group Work Business --><TR><TD width='20' height='20' class='text'><IMG SRC='/webtop/core/image/default/treeview/l_minus.gif' border=0></TD>"+
//      "<TD align='left' width='20' height='20' class='text'><IMG SRC='/webtop/core/image/default/treeview/partners.gif' border=0></TD>"+
//      "<TD height='20' class='text'>&nbsp;<STRONG>Work Business</STRONG></TD>"+
//      "<TD  height='20' colspan='3'></TD></TR><TR><TD width='20' height='20' class='text'></TD>"+
//      "<TD height='20' class='text'><IMG SRC='/webtop/core/image/default/treeview/l_joiner.gif' border=0></TD>"+
//      "<TD align='left' height='20' class='text'>" +
//      "<a href='javascript:void(0);' onClick=\"return null,'20562','');\" class=\"linkblue-s\">Daniel, Shimabukuro(Company)<strong>(Cancelled)</strong></a></TD>"+
//      "<TD align='left' height='20' class='text' nowrap>&nbsp;PartnerSO ID: PJ013001</TD>"+
//      "<TD align='left' height='20' class='text' nowrap>&nbsp;&nbsp;&nbsp;" +
//      "<a href=\"/wps/myportal/!ut/p/_s.7_0_A/7_0_13M?CONTROLLER.PARTNER_TYPE=PARTNER_TYPE_PROSPECT&CONTROLLER.PARTNER_ID=PJ013001&CONTROLLER.PARTNER_LOCATION=JUP\">Overview</a>" +
//      "&nbsp;&nbsp;" +
//      "<a href=\"/wps/myportal/!ut/p/_s.7_0_A/7_0_13O?CONTROLLER.PARTNER_TYPE=PARTNER_TYPE_PROSPECT&CONTROLLER.PARTNER_ID=PJ013001\">"+
//      "View Own Relationship</a>" +
//      "&nbsp;&nbsp;</TD>"+
//      "<TD align='left' height='20' class='text'>&nbsp;CA : </TD>"+
//      "</TR></TABLE><BR>";
          	
//          	String[] strs = StringUtil.url2WBURL(url);
          	
//          	for(int i=0; i<strs.length; i++){
//          		System.out.println(strs[i]);
//          		System.out.println("****************************************");
//          	}
//        String url = "https://cawbserver.ubsams.com/wps/myportal/prospect/profile/?CONTROLLER.PARTNER_TYPE=PARTNER_TYPE_PROSPECT&CONTROLLER.PARTNER_ID=PJ013001";
//        String[] strs = StringUtil.parsePartnerInfo(url);
//  	
//	  	for(int i=0; i<strs.length; i++){
//	  		System.out.println(strs[i]);
//	  		System.out.println("****************************************");
//	  	}
    }

    /**
     * get language from user locale
     * 
     * @param locale
     * @return
     * @author xiangxunzhong
     */
    public static String getLanguageStrByLocal(String locale) {
        int seperator = locale.indexOf("_");
        if (seperator == -1)
            return locale;
        return locale.substring(0, seperator);
    }

    /**
     * 
     * <b>Str result </b>
     * <p>"" true
     * <p>" " true
     * <p>* null true
     * <p>" a " false
     * 
     * @param input
     * @return boolean Add by yangzhao 2006-03-30
     */
    public static boolean isEmpty(String input) {
        if (input == null || input.trim().length() == 0)
            return true;
        return false;
    }

    /**
     * @see StringUtil.isEmpty(String input)
     * @param input
     * @return Add by yangzhao 2006-03-30
     */
    public static boolean isEmpty(StringBuffer input) {
        if (input == null || input.toString().trim().length() == 0)
            return true;
        return false;

    }
    
    public static String str2URL(String url, String paramName1, String paramValue1){
    	return url+"?"+paramName1+"="+paramValue1;
    }
    
    public static String str2URL(String url, String paramName1, String paramValue1, String paramName2, String paramValue2){
    	return str2URL(url, paramName1, paramValue1)+"&"+paramName2+"="+paramValue2;
    }
    
    public static String str2URL(String url, String paramName1, String paramValue1, String paramName2, String paramValue2, String paramName3, String paramValue3){
    	return str2URL(url, paramName1, paramValue1, paramName2, paramValue2)+"&"+paramName3+"="+paramValue3;
    }
    
    public static String str2URL(String url, String paramName1, String paramValue1, String paramName2, String paramValue2, String paramName3, String paramValue3,
            						String paramName4,String paramValue4,String paramName5,String paramValue5){
    	return str2URL(url, paramName1, paramValue1, paramName2, paramValue2,paramName3,paramValue3)+"&"+paramName4+"="+paramValue4+"&"+paramName5+"="+paramValue5;
    }

    //Added by Kenry Zou, for jsp logic 
    public static String[] url2WBURL(String str){
        if(LOG.isDebugEnabled())
            LOG.debug("url2WBURL","original html is :"+str);
        String linkPrefix = "<a href=\"";
        String linkSufix = "\">";
        String temp = str;
        List list = new ArrayList();
        
        while(true){
        	int pos1 = temp.indexOf(linkPrefix);
        	if(pos1==0){
        		if(str.startsWith(linkPrefix))
                	list.add("");
        	}
        	
        	if(pos1<0){
        		list.add(temp);
        		break;
        	}
        	if(LOG.isDebugEnabled())
                LOG.debug("url2WBURL","temp.substring(0, pos1) string is :"+temp.substring(0, pos1));
        	list.add(temp.substring(0, pos1));
        	
        	//Be noted pos2 must gt 0. See ICRMTreeView.
        	int pos2 = temp.indexOf(linkSufix, pos1);
        	String link = temp.substring(pos1+linkPrefix.length(), pos2);
        	if(LOG.isDebugEnabled())
                LOG.debug("url2WBURL","link string is :"+link);
            list.add(link.trim());
            
            if(temp.length()<pos2+linkSufix.length())
            	break;
            
            temp = temp.substring(pos2+linkSufix.length());    
            if(LOG.isDebugEnabled())
                LOG.debug("url2WBURL","temp html is :"+temp);
        }

        return (String[])list.toArray(new String[0]);
    }
    
    //For rule refer to james: jamesWbOpenWindow()
    public static String[] parsePartnerInfo(String url){
    	String PARTNER_TYPE = "CONTROLLER.PARTNER_TYPE";
    	String PARTNER_ID = "CONTROLLER.PARTNER_ID";
    	String PARTNER_LOCATION = "CONTROLLER.PARTNER_LOCATION";
    	String PARTNER_VIEW_TYPE="CONTROLLER.VIEW_URL_TYPE";
    	String PLUS = "&";
    	String EQUAL = "=";
    	
    	String[] infos = new String[3];
    	int pos1 = url.indexOf(PARTNER_TYPE);
    	int pos2 = url.indexOf(PLUS, pos1);
    	
    	String value = url.substring(pos1+PARTNER_TYPE.length(), pos2);
    	infos[0] = value.substring(value.indexOf(EQUAL)+1).trim();
// modify by Bell Zhong on 2006-08-11    	
    	pos1=url.indexOf(PARTNER_VIEW_TYPE);
    	if(pos1>0)
    	{
	    	pos2=url.indexOf(PLUS,pos1);
	    	value=url.substring(pos1+PARTNER_VIEW_TYPE.length(),pos2);
	    	infos[2]=value.substring(value.indexOf(EQUAL)+1).trim();
    	}
    	if(LOG.isDebugEnabled())
    	    LOG.debug("parsePartnerInfo","CONTROLLER.VIEW_URL_TYPE ="+infos[2]);
    	pos1 = url.indexOf(PARTNER_ID);
//    	pos2 = url.indexOf(PLUS, pos1);

//    	value = url.substring(pos1+PARTNER_ID.length(), pos2);
    	value = url.substring(pos1+PARTNER_ID.length());
    	infos[1] = value.substring(value.indexOf(EQUAL)+1).trim();

    	return infos;
    }
    
    public static String fillString(String input,String fillStr,boolean isBefore,int totalLen)
    {
        String output=null;
        if(input!=null&&!"".equals(input.trim()))
        {
            int len=input.length();
            if(len<totalLen)
            {
                output=input;
                for(int i=0;i<totalLen-len;i++)
                {
                    if(isBefore)
                        output=fillStr+output;
                    else
                        output=output+fillStr;
                }
            }
        }
        return output;    
    }
    private static NumberFormat numFormatter = new DecimalFormat("000000000");
    public static String padWithZero(long seq)
    {
        return numFormatter.format(seq);
    }
    
    private static final String SPACER = "\\";
    
    /**
     * @param path the full file path
     * @return the file name
     */
    public static String getFileNameFromFullPath(String path) {
    	String name = path;
    	int index = path.lastIndexOf(SPACER);
    	if(path != null && index != -1 && index<path.length()-1) {
    		name = path.substring(index +1);
    	}
    	return name;
    }
    
    /**
     * XSS encode for List
     * @param selectedAssociationIDs
     * @return
     */
    public static List encodeForXSS(List selectedAssociationIDs){
    	StringBuilder sb = new StringBuilder();
        String strEncodeSelectedAssociation = null;
        List encodeSelectedAssociation = new ArrayList();;
       	if(selectedAssociationIDs != null && selectedAssociationIDs.size() > 0) {
       		for(int i = 0;i < selectedAssociationIDs.size();i++) {
       			Object o = selectedAssociationIDs.get(i);
       			if(i != selectedAssociationIDs.size()) {
       				sb.append(o.toString());
       				sb.append(",");
       			} else {
       				sb.append(o.toString());
       			}
       		}    		
       		strEncodeSelectedAssociation = XSSHelper.encoder().encodeForHTMLAttribute(sb.toString());
       		
       		String[] values = strEncodeSelectedAssociation.split(",");
       		encodeSelectedAssociation = Arrays.asList(values);    		
       	} 
       	return encodeSelectedAssociation;
    } 
}
