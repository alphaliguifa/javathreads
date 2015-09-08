import com.ubs.james.commons.misc.impl.PerformanceLogger;

/**
 * bee20061206: added to
 *  1. generalize ICRM data related cache
 *  2. support "runOnceInCurrentThread" (roict) mode.
 * User: chowky
 * Date: Dec 6, 2006
 * Time: 6:25:48 PM
 * To change this template use Options | File Templates.
 */
public abstract class ICRMDataCacheSynchronizer implements ICacheSynchronizer {

    private final static PerformanceLogger PLOG = PerformanceLogger.getLogger(ICRMDataCacheSynchronizer.class);
    /**
     * May change to configurable in future.
     * @return is runOnceInCurrentThread logic enabled for this synchronizer
     */
    private boolean isEnableRoict() { return true; }

    /**
     * May change to configurable in future.
     * @return the time period in milliseconds,
     * that the last run result will not be reused (invalidate again) after the period has eclapsed.
     */
    private long getRoictReuseLimitMillis() {  return 60000; } // 1 minute

    public int[] syncCache() {
        PLOG.info("syncCache", PerformanceLogger.Event.METHOD_ENTRY);
        int[] result = null;
        incRoictTotalCount();
        if (isEnableRoict()) {
            if (isRoictStarted()) {
                if (isRoictSynced()) {
                    if (System.currentTimeMillis() - getRoictLastSyncTime() > getRoictReuseLimitMillis()) {
                        incRoictOutdateCount();
                    } else {
                        incRoictSkipCount();
                        result = new int[] {0,0};
                    }
                }
            }
            if (result==null) {
                result = syncCacheBody();
                updateRoictLastSyncTime(System.currentTimeMillis());
            }
        } else {
            result = syncCacheBody();
        }
        PLOG.info("syncCache", PerformanceLogger.Event.METHOD_EXIT);
        return result;
    }

    /////////////////////////////////////////////////////////////////
    // helper...
    /////////////////////////////////////////////////////////////////
    protected String convertSyncResultToString(int[] result) {
        if (result==null) return "not finished";
        else if (result.length==2) return "U/D "+result[0]+"/"+result[1];
        else {  // unexpected
            StringBuffer sb = new StringBuffer("result.length="+result.length);
            for (int i=0; i<result.length; i++) {
                sb.append("["+i+"]"+result[i]);
            }
            return sb.toString();
        }
    }


    protected static String millisToReadableTime(long millis) {
         return String.valueOf((double)millis/1000d) + "s";
    }

    /////////////////////////////////////////////////////////////////
    // ...helper
    /////////////////////////////////////////////////////////////////

    /////////////////////////////////////////////////////////////////
    // supporting "runOnceInCurrentThread"...
    /////////////////////////////////////////////////////////////////
    public boolean isRoictStarted() { return ((Boolean)(_TL_RoictStartedFlag.get())).booleanValue(); }
    public void startRoict() {
        PLOG.info("startRoict", PerformanceLogger.Event.METHOD_ENTRY);
        _TL_RoictStartedFlag.set(Boolean.TRUE);
        _TL_RoictLastSyncTime.set(INIT_LAST_SYNC_TIME);
        PLOG.info("startRoict", PerformanceLogger.Event.METHOD_EXIT);
    }
    public void endRoict() {
        PLOG.info("endRoict", PerformanceLogger.Event.METHOD_ENTRY);
        _TL_RoictStartedFlag.set(Boolean.FALSE);
        _TL_RoictLastSyncTime.set(INIT_LAST_SYNC_TIME);
        PLOG.info("endRoict", PerformanceLogger.Event.METHOD_EXIT);
    }

    private ThreadLocal _TL_RoictStartedFlag = new ThreadLocal() {
        protected synchronized Object initialValue() { return Boolean.FALSE; }
    };
    private Long INIT_LAST_SYNC_TIME=new Long(0);
    private ThreadLocal _TL_RoictLastSyncTime = new ThreadLocal() {
        protected synchronized Object initialValue() { return INIT_LAST_SYNC_TIME; }
    };
    private void updateRoictLastSyncTime(long lastUpdateTime) {
        _TL_RoictLastSyncTime.set(new Long(lastUpdateTime));
    }
    public long getRoictLastSyncTime() { return ((Long)(_TL_RoictLastSyncTime.get())).longValue(); }

    public boolean isRoictSynced() {
        return !(getRoictLastSyncTime()==INIT_LAST_SYNC_TIME.longValue());
    }

    private int roictSkipCount=0;
    public int getRoictSkipCount() { return roictSkipCount; }
    private void incRoictSkipCount() { roictSkipCount++; }
    private int roictOutdateCount=0;
    public int getRoictOuotdateCount() { return roictOutdateCount; }
    private void incRoictOutdateCount() { roictOutdateCount++; }
    private int roictTotalCount=0;
    public int getRoictTotalCount() { return roictTotalCount; }
    private void incRoictTotalCount() {
        if (roictTotalCount > 9999999999l) {
            roictSkipCount=0; roictOutdateCount=0; roictTotalCount=0;
        }
        roictTotalCount++;
    }
    public String getCountSummary() {
        return "(S/T,O ="+getRoictSkipCount()+"/"+getRoictTotalCount()+", "+getRoictOuotdateCount()+")";
    }

    /////////////////////////////////////////////////////////////////
    // ..."runOnceInCurrentThread"
    /////////////////////////////////////////////////////////////////


    abstract public int[] syncCacheBody();
}
