

import java.io.Serializable;



public class FieldSO implements Serializable {
    private String fieldID;

    private String caption;

    private String dataObject; /* Database Column NameSO */

    private String dataField; /* Database Column NameSO */

    private String inputType; /* Front-end Input Type */

    private Integer dataType;

    private Integer fieldLength; /* FieldSO Length */

    private Integer fieldHeigth; /* FieldSO Height */

    private Integer displayWidth; /* in pixel */

    private Integer displayHeigth; /* in pixel */

    private String viewFormat; /* Display Format */

    private String inputRequired; /* whether this field is compulsory */

    private String inputRecommended;

    private String defValue; /* Default Value */

    // private OptionSO options[];
    private SelectionDataBlockSO optionList;

    private CheckBoxSO checkBox;

    private String httpParamName = null;

    public FieldSO() {

    }

    public FieldSO(String fieldID, String caption, String dataObject, String dataField, String inputType, Integer dataType, Integer fieldLength,
            Integer fieldHeigth, Integer displayWidth, Integer displayHeigth, String viewFormat, String inputRequired, String inputRecommended,
            String defValue, String httpParamName
            // , OptionSO options[]
            , SelectionDataBlockSO optionList, CheckBoxSO checkBox) {
        this.fieldID = fieldID;
        this.caption = caption;
        this.dataObject = dataObject;
        this.dataField = dataField;
        this.inputType = inputType;
        this.dataType = dataType;
        this.fieldLength = fieldLength;
        this.fieldHeigth = fieldHeigth;
        this.displayWidth = displayWidth;
        this.displayHeigth = displayHeigth;

        this.viewFormat = viewFormat;
        this.inputRecommended = inputRecommended;
        this.inputRequired = inputRequired;
        this.defValue = defValue;
        this.httpParamName = httpParamName;
        // this.options = options;
        this.optionList = optionList;

        this.checkBox = checkBox;
    }

    /*
     * public FieldSO( String fieldID , String caption , String dataObject ,
     * String dataField , String inputType , int fieldLength , int fieldHeigth ,
     * String viewFormat , String inputRequired , String defValue , String
     * httpParamName) { this.fieldID = fieldID; this.caption = caption;
     * this.dataObject = dataObject; this.dataField = dataField; this.inputType =
     * inputType; this.fieldLength = fieldLength; this.fieldHeigth =
     * fieldHeigth; this.viewFormat = viewFormat; this.inputRequired =
     * inputRequired; this.defValue = defValue; // this.optionList = optionList;
     * this.httpParamName = httpParamName; }
     */
    public String getFieldID() {
        return this.fieldID;
    }

    public String getCaption() {
        return this.caption;
    }

    public String getDataObject() {
        return this.dataObject;
    }

    public String getDataField() {
        return this.dataField;
    }

    public Integer getDataType() {
        return this.dataType;
    }

    public String getInputType() {
        return this.inputType;
    }

    public Integer getFieldLength() {
        return this.fieldLength;
    }

    public Integer getFieldHeight() {
        return this.fieldHeigth;
    }

    public Integer getDisplayWidth() {
        return this.displayWidth;
    }

    public Integer getDisplayHeigth() {
        return this.displayHeigth;
    }

    public String getViewFormat() {
        return this.viewFormat;
    }

    public String getInputRequired() {
        return this.inputRequired;
    }

    public String getDefValue() {
        return this.defValue;
    }

    /*
     * public OptionSO[] getOptions() { return this.options; }
     */

    public SelectionDataBlockSO getOptionList() {
        return this.optionList;
    }

    public CheckBoxSO getCheckBox() {
        return this.checkBox;
    }

    public String getHttpParamName() {
        return this.httpParamName;
    }

    /**
     * Returns the inputRecommended.
     * 
     * @return String
     */
    public String getInputRecommended() {
        return inputRecommended;
    }

//    public String toString() {
//        StringBuffer sb = new StringBuffer();
//
//        sb.append("fieldID=" + fieldID);
//        sb.append("\r\n");
//        sb.append("caption=" + caption);
//        sb.append("\r\n");
//        sb.append("dataObject=" + dataObject);
//        sb.append("\r\n");
//        sb.append("dataField=" + dataField);
//        sb.append("\r\n");
//        sb.append("inputType=" + inputType);
//        sb.append("\r\n");
//        sb.append("dataType=" + dataType);
//        sb.append("\r\n");
//        sb.append("fieldLength=" + fieldLength);
//        sb.append("\r\n");
//        sb.append("fieldHeigth=" + fieldHeigth);
//        sb.append("\r\n");
//        sb.append("displayWidth=" + displayWidth);
//        sb.append("\r\n");
//        sb.append("displayHeigth=" + displayHeigth);
//        sb.append("\r\n");
//        sb.append("viewFormat=" + viewFormat);
//        sb.append("\r\n");
//        sb.append("displayHeigth=" + displayHeigth);
//        sb.append("\r\n");
//        sb.append("inputRequired=" + inputRequired);
//        sb.append("\r\n");
//        sb.append("inputRecommended=" + inputRecommended);
//        sb.append("\r\n");
//        sb.append("defValue=" + defValue);
//        sb.append("\r\n");
//        sb.append("httpParamName=" + httpParamName);
//        sb.append("\r\n");
//        sb.append("optionList=" + optionList);
//        sb.append("\r\n");
//        sb.append("checkBox=" + checkBox);
//        sb.append("\r\n");
//
//        return sb.toString();
//    }

    /**
     * @return Returns the fieldHeigth.
     */
    public Integer getFieldHeigth() {
        return fieldHeigth;
    }

    /**
     * @param fieldHeigth
     *            The fieldHeigth to set.
     */
    public void setFieldHeigth(Integer fieldHeigth) {
        this.fieldHeigth = fieldHeigth;
    }

    /**
     * @param caption
     *            The caption to set.
     */
    public void setCaption(String caption) {
        this.caption = caption;
    }

    /**
     * @param checkBox
     *            The checkBox to set.
     */
    public void setCheckBox(CheckBoxSO checkBox) {
        this.checkBox = checkBox;
    }

    /**
     * @param dataField
     *            The dataField to set.
     */
    public void setDataField(String dataField) {
        this.dataField = dataField;
    }

    /**
     * @param dataObject
     *            The dataObject to set.
     */
    public void setDataObject(String dataObject) {
        this.dataObject = dataObject;
    }

    /**
     * @param dataType
     *            The dataType to set.
     */
    public void setDataType(Integer dataType) {
        this.dataType = dataType;
    }

    /**
     * @param defValue
     *            The defValue to set.
     */
    public void setDefValue(String defValue) {
        this.defValue = defValue;
    }

    /**
     * @param displayHeigth
     *            The displayHeigth to set.
     */
    public void setDisplayHeigth(Integer displayHeigth) {
        this.displayHeigth = displayHeigth;
    }

    /**
     * @param displayWidth
     *            The displayWidth to set.
     */
    public void setDisplayWidth(Integer displayWidth) {
        this.displayWidth = displayWidth;
    }

    /**
     * @param fieldID
     *            The fieldID to set.
     */
    public void setFieldID(String fieldID) {
        this.fieldID = fieldID;
    }

    /**
     * @param fieldLength
     *            The fieldLength to set.
     */
    public void setFieldLength(Integer fieldLength) {
        this.fieldLength = fieldLength;
    }

    /**
     * @param httpParamName
     *            The httpParamName to set.
     */
    public void setHttpParamName(String httpParamName) {
        this.httpParamName = httpParamName;
    }

    /**
     * @param inputRecommended
     *            The inputRecommended to set.
     */
    public void setInputRecommended(String inputRecommended) {
        this.inputRecommended = inputRecommended;
    }

    /**
     * @param inputRequired
     *            The inputRequired to set.
     */
    public void setInputRequired(String inputRequired) {
        this.inputRequired = inputRequired;
    }

    /**
     * @param inputType
     *            The inputType to set.
     */
    public void setInputType(String inputType) {
        this.inputType = inputType;
    }

    /**
     * @param optionList
     *            The optionList to set.
     */
    public void setOptionList(SelectionDataBlockSO optionList) {
        this.optionList = optionList;
    }

    /**
     * @param viewFormat
     *            The viewFormat to set.
     */
    public void setViewFormat(String viewFormat) {
        this.viewFormat = viewFormat;
    }

    /**
     * @generated by CodeSugar http://sourceforge.net/projects/codesugar */

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[FieldSO:");
        buffer.append(" fieldID: ");
        buffer.append(fieldID);
        buffer.append(" caption: ");
        buffer.append(caption);
        buffer.append(" dataObject: ");
        buffer.append(dataObject);
        buffer.append(" dataField: ");
        buffer.append(dataField);
        buffer.append(" inputType: ");
        buffer.append(inputType);
        buffer.append(" dataType: ");
        buffer.append(dataType);
        buffer.append(" fieldLength: ");
        buffer.append(fieldLength);
        buffer.append(" fieldHeigth: ");
        buffer.append(fieldHeigth);
        buffer.append(" displayWidth: ");
        buffer.append(displayWidth);
        buffer.append(" displayHeigth: ");
        buffer.append(displayHeigth);
        buffer.append(" viewFormat: ");
        buffer.append(viewFormat);
        buffer.append(" inputRequired: ");
        buffer.append(inputRequired);
        buffer.append(" inputRecommended: ");
        buffer.append(inputRecommended);
        buffer.append(" defValue: ");
        buffer.append(defValue);
        buffer.append(" optionList: ");
        buffer.append(optionList);
        buffer.append(" checkBox: ");
        buffer.append(checkBox);
        buffer.append(" httpParamName: ");
        buffer.append(httpParamName);
        buffer.append("]");
        return buffer.toString();
    }
}
