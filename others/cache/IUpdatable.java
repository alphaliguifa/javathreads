import java.util.Collection;
import java.util.List;

public interface IUpdatable {
    /**
     * loadAll is for initial loading or reloading of all records in cache. This
     * is different from updateAll because all fields in the input records are
     * loaded into cache.
     * 
     * @param l
     * @return number of records loaded.
     */
    public int loadAll(List l);

    public int update(Object obj);

    public int remove(Object obj);

    public int updateAll(Collection c);

    public int removeAll(Collection c);

}
