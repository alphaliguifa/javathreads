
import java.util.HashMap;

public class CacheAgent implements IMemoryCacheAgent {
    private static CacheAgent agent;

    private HashMap caches;

    private CacheAgent() {
        caches = new HashMap();
    }

    public static IMemoryCacheAgent getInstance() {
        if (agent == null) {
            agent = new CacheAgent();
        }
        return agent;
    }

    public void register(String memoryCacheName, IMemoryCache cache) {
        caches.put(memoryCacheName, cache);
    }

    public Object getValue(String memoryCacheName, Object key, IValueGetter getter) {

        if (caches.containsKey(memoryCacheName) && key != null) {
            return getter.getValue(((IMemoryCache) caches.get(memoryCacheName)).get(key));
        }

        return null;
    }

}
