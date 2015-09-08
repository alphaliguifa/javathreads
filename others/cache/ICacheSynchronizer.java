import java.sql.Timestamp;


public interface ICacheSynchronizer {
    public int[] syncCache() throws CacheSyncException;

    public Timestamp getLastSyncTimestamp() throws CacheSyncException;

    public void register(IUpdatable updatable) throws CacheSyncException;
}
