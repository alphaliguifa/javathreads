import java.util.Collection;



public interface IMemoryCache extends IUpdatable {
    public Object[] getKeys();

    public Object get(Object key);

    public Object[] select(Predicate filter);

    public Object[] select(Predicate filter, Sorter sorter);

    public Object[] select(Collection keySet, Predicate filter, Sorter sorter);

    public Collection getCacheKeys(String indexName, Collection indexKeys);

    public int size();
}
