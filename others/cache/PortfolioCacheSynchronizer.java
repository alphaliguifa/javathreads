public class PortfolioCacheSynchronizer extends ICRMDataCacheSynchronizer {

    private final static Logger.Module MOD = new Logger.Module("CACHE");

    private static final Logger LOG = Logger.getLogger(PortfolioCacheSynchronizer.class, MOD);

    private final static PerformanceLogger PLOG = PerformanceLogger.getLogger(PortfolioCacheSynchronizer.class);

    protected static ICacheSynchronizer cacheSync;

    protected IUpdatable updatable;

    protected Timestamp lastUpdateTime;
    //code merge by Jesse on 2007-01-24
//    private Timestamp[] dbLastUpdateTime;

    protected PortfolioCacheSynchronizer() {
        lastUpdateTime = null;
//        String[] dbLocation=getDbLocations();
//        dbLastUpdateTime = new Timestamp[dbLocation.length];
    }

    private static int[] _LAZY_LOCK = new int[]{};
    public static synchronized ICacheSynchronizer getInstance() {
        if (cacheSync == null) {
            synchronized (_LAZY_LOCK) {
                if (cacheSync==null) {
                    cacheSync = new PortfolioCacheSynchronizer();
//                    long startTime = System.currentTimeMillis();
//                    LOG.info("getInstance","FIRST TIME PARTNER CACHE SYNCHRONIZATION START");
//                    int[] result = null;
//                    try {
//                        PLOG.info("getInstance - first time sync", PerformanceLogger.Event.METHOD_ENTRY);
//                        result = cacheSync.syncCache();
//                        PLOG.info("getInstance - first time sync", PerformanceLogger.Event.METHOD_EXIT);
//                    } catch (CacheSyncException e) {
//                        e.printStackTrace();  //To change body of catch statement use Options | File Templates.
//                    }
//                    LOG.info("getInstance", "FIRST TIME PARTNER CACHE SYNCHRONIZATION ENDED IN " +
//                            millisToReadableTime(System.currentTimeMillis()-startTime) + ": " + result);
                }
            }
        }
        return cacheSync;
    }

    private String[] getDbLocations() {
        return DataStorageLocationValidator.getInstance().getSupportedDBLocation();
    }

    private IPortfolioCacheDSV getPortfolioCacheDSV(String location) {
        final String METHOD_NAME = "getPortfolioCacheDSV";
        PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_ENTRY);
        IPortfolioCacheDSV dsv = (IPortfolioCacheDSV) DSVFactory.getInstance().getDataService(IPortfolioCacheDSV.class, location);
        PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_EXIT);
        return dsv;
    }
    
    private IPortfolioSearchBSV getPortfolioCacheBSV() {
    	IPortfolioSearchBSV bsv = (IPortfolioSearchBSV) GenericContainer.getInstance().getBusinessServiceManager().getBusinessService(
    			IPortfolioSearchBSV.class);
        return bsv;
    }

    public Timestamp getLastSyncTimestamp() {
        return lastUpdateTime;
    }

    protected void setLastSyncTimestamp(Timestamp t) {
        lastUpdateTime = t;
    }

    public void register(IUpdatable updatable) {
        
        if (LOG.isDebugEnabled())
            LOG.debug("register","updatable =  "+updatable);
        this.updatable = updatable;
    }

//    public int[] syncCache() {
    //code merge by Jesse on 2007-01-24
    public int[] syncCacheBody() {
        final String METHOD_NAME = "syncCacheBody";
        PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_ENTRY);

        Timestamp oldTime = getLastSyncTimestamp();

        if (oldTime != null && System.currentTimeMillis() - oldTime.getTime() < Config.getCacheSyncTimeOut()) {
            PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_EXIT);

            return new int[] { 0, 0 };
        }

        List updateList = new ArrayList();
        List deleteList = new ArrayList();
        int updateCount = 0, deletedCount = 0;  

//        Timestamp newTime = new Timestamp(System.currentTimeMillis());
        Timestamp newTime = new Timestamp(System.currentTimeMillis() - Config.getCacheSyncWithIn());
        
        boolean loadDelta = true;
        if (oldTime == null) {
            // Load all portfolios
            boolean debugMode = LOG.isDebugEnabled();
            if (debugMode) {
                LOG.debug(METHOD_NAME, "-----Waiting to enter synchronized block for load all portfolios------ oldTime=" + oldTime + " , newTime="
                        + newTime);
            }
            synchronized (PortfolioCacheSynchronizer.class) {
                if (getLastSyncTimestamp() == null) {
                    try {
                        if (debugMode) {
                            LOG.debug(METHOD_NAME, "-----Starting load all portfolios------ oldTime=" + oldTime + " , newTime=" + newTime);
                        }
                        List allEntry = getBSV().getAllPortfolioCacheEntry();

                        if (debugMode) {
                            LOG.debug(METHOD_NAME, "-----Loaded all portfolios, now updating cache------ oldTime=" + oldTime + " , newTime="
                                    + newTime);
                            LOG.debug(METHOD_NAME, "-----Loaded all portfolios the updatable is " + updatable);
                        }
                        updateCount = updatable.loadAll(allEntry);
                        if (debugMode) {
                            LOG.debug(METHOD_NAME, "-----Finish load all portfolios------ oldTime=" + oldTime + " , newTime=" + newTime);
                        }

                    } catch (PortfolioBSVException e) {
                        LOG.error(METHOD_NAME, "Error getting updated portfolios oldTime=" + oldTime + " , newTime=" + newTime, e);
                    } catch (IBSFactoryException e) {
                        LOG.error(METHOD_NAME, "Error getting updated portfolios oldTime=" + oldTime + " , newTime=" + newTime, e);
                    }
                    setLastSyncTimestamp(newTime);
                    loadDelta = false;
                }
            }
        }
        
        if (loadDelta) {
            // Load changed portfolios
            try {
                String[] locs = getDbLocations();
                for (int i = 0; i < locs.length; ++i) {
                    //List[] tmpLists = getPortfolioCacheDSV(locs[i]).getUpdatedPortfolios(oldTime);
                	List[] tmpLists = getPortfolioCacheBSV().getUpdatedPortfoliosAgent(locs[i], oldTime);
                    updateList.addAll(tmpLists[0]);
                    deleteList.addAll(tmpLists[1]);
                }

                setLastSyncTimestamp(newTime);
            } catch (Exception e) {
                LOG.error(METHOD_NAME, "Error getting updated portfolios oldTime=" + oldTime + " , newTime=" + newTime, e);
            }

            updateCount = updatable.updateAll(updateList);
            deletedCount = updatable.removeAll(deleteList);
        }

        PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_EXIT);
        return new int[] { updateCount, deletedCount };
    }

    protected IPortfolioSearchBSV getBSV() throws IBSFactoryException {
        IPortfolioSearchBSV bsv = (IPortfolioSearchBSV) GenericContainer.getInstance().getBusinessServiceManager().getBusinessService(
                IPortfolioSearchBSV.class);
        return bsv;
    }

}
