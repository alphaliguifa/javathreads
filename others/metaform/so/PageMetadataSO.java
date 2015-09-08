import java.io.Serializable;
import java.util.HashMap;

public class PageMetadataSO implements Serializable {
    private HashMap pageList = new HashMap();

    public PageMetadataSO() {

    }

    public PageMetadataSO(FormPageSO[] page) {
        for (int i = 0; i < page.length; i++) {
            if (page[i] == null || page[i].getPageID() == null)
                continue;
            pageList.put(page[i].getPageID(), page[i]);
        }
    }

    public FormPageSO getPage(String pageID) {
        if (pageID == null)
            return null;
        return (FormPageSO) pageList.get(pageID);
    }

    public String[] getAllPageID() {
        return (String[]) pageList.keySet().toArray(new String[0]);
    }
}
