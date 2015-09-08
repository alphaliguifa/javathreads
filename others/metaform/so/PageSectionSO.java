import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class PageSectionSO implements Serializable {
    private String sectionID;

    private String sectionCaption;

    private String dataObject;

    private String customMade;

    private FieldSO[] field;

    private Map fieldMap;

    private String pageID;

    public PageSectionSO() {

    }

    public PageSectionSO(String pageID, String sectionID, String sectionCaption, String dataObject, String customMade, FieldSO[] field) {
        this.pageID = pageID;
        this.sectionID = sectionID;
        this.sectionCaption = sectionCaption;
        this.dataObject = dataObject;
        this.customMade = customMade;
        this.field = field;
        this.fieldMap = new HashMap();
        for (int i = 0; i < this.field.length; i++)
            this.fieldMap.put(this.field[i].getFieldID(), this.field[i]);
    }

    public String getSectionCaption() {
        return this.sectionCaption;
    }

    public String getSectionID() {
        return this.sectionID;
    }

    public String getDataObject() {
        return this.dataObject;
    }

    public String getCustomMade() {
        return this.customMade;
    }

    public FieldSO[] getFields() {
        return this.field;
    }

    public FieldSO getField(String fieldId) {
        return (FieldSO) this.fieldMap.get(fieldId);
    }

    public String getPageID() {
        return pageID;
    }

    public void setPageID(String pageID) {
        this.pageID = pageID;
    }

    /**
     * @return Returns the field.
     */
    public FieldSO[] getField() {
        return field;
    }

    /**
     * @param field
     *            The field to set.
     */
    public void setField(FieldSO[] field) {
        this.field = field;
    }

    /**
     * @return Returns the fieldMap.
     */
    public Map getFieldMap() {
        return fieldMap;
    }

    /**
     * @param fieldMap
     *            The fieldMap to set.
     */
    public void setFieldMap(Map fieldMap) {
        this.fieldMap = fieldMap;
    }

    /**
     * @param customMade
     *            The customMade to set.
     */
    public void setCustomMade(String customMade) {
        this.customMade = customMade;
    }

    /**
     * @param dataObject
     *            The dataObject to set.
     */
    public void setDataObject(String dataObject) {
        this.dataObject = dataObject;
    }

    /**
     * @param sectionCaption
     *            The sectionCaption to set.
     */
    public void setSectionCaption(String sectionCaption) {
        this.sectionCaption = sectionCaption;
    }

    /**
     * @param sectionID
     *            The sectionID to set.
     */
    public void setSectionID(String sectionID) {
        this.sectionID = sectionID;
    }

    /**
     * @generated by CodeSugar http://sourceforge.net/projects/codesugar */

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[PageSectionSO:");
        buffer.append(" sectionID: ");
        buffer.append(sectionID);
        buffer.append(" sectionCaption: ");
        buffer.append(sectionCaption);
        buffer.append(" dataObject: ");
        buffer.append(dataObject);
        buffer.append(" customMade: ");
        buffer.append(customMade);
        buffer.append(" { ");
        for (int i0 = 0; field != null && i0 < field.length; i0++) {
            buffer.append(" field[" + i0 + "]: ");
            buffer.append(field[i0]);
        }
        buffer.append(" } ");
        buffer.append(" fieldMap: ");
        buffer.append(fieldMap);
        buffer.append(" pageID: ");
        buffer.append(pageID);
        buffer.append("]");
        return buffer.toString();
    }
}
