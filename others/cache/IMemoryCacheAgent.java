public interface IMemoryCacheAgent {
    public void register(String memoryCacheName, IMemoryCache cache);

    public Object getValue(String memoryCacheName, Object key, IValueGetter getter);
}
