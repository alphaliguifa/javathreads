public class PartnerCacheSynchronizer extends ICRMDataCacheSynchronizer {

    private static final Logger LOG = Logger.getLogger(PartnerCacheSynchronizer.class);

    private final static PerformanceLogger PLOG = PerformanceLogger.getLogger(PartnerCacheSynchronizer.class);

    private static ICacheSynchronizer cacheSync;

    private IUpdatable updatable;

    private Timestamp lastUpdateTime;
    
    //code merge by Jesse on 2007-01-24
//    private Timestamp[] dbLastUpdateTime;

    private PartnerCacheSynchronizer() {
        lastUpdateTime = new Timestamp(System.currentTimeMillis());
//        String[] dbLocation=getDbLocations();
//        dbLastUpdateTime = new Timestamp[dbLocation.length];
//		
//		for(int i=0; i<dbLastUpdateTime.length; i++){
//		    try{
//		        dbLastUpdateTime[i] = getPartnerCacheDSV(dbLocation[i]).getDatabaseTime();
//		    }catch(Exception e)
//		    {
//		        LOG.error("PartnerCacheSynchronizer","get database current time error",e);
//		        dbLastUpdateTime[i]=lastUpdateTime;
//		    }
//		}
    }

   // private static int[] _LAZY_LOCK = new int[]{};
    public static synchronized ICacheSynchronizer getInstance() {
        if (cacheSync == null) {
           // synchronized (_LAZY_LOCK) {
                if (cacheSync==null) {
                    cacheSync = new PartnerCacheSynchronizer();
                    long startTime = System.currentTimeMillis();
                    LOG.info("getInstance","FIRST TIME PARTNER CACHE SYNCHRONIZATION START");
                    int[] result = null;
                    try {
                        PLOG.info("getInstance - first time sync", PerformanceLogger.Event.METHOD_ENTRY);
                        result = cacheSync.syncCache();
                        PLOG.info("getInstance - first time sync", PerformanceLogger.Event.METHOD_EXIT);
                    } catch (CacheSyncException e) {
//                        e.printStackTrace();  //To change body of catch statement use Options | File Templates.
                    	LOG.error("getInstance","loading error:",e);
                    }
                    LOG.info("getInstance", "FIRST TIME PARTNER CACHE SYNCHRONIZATION ENDED IN " +
                            millisToReadableTime(System.currentTimeMillis()-startTime) + ": " + result);
                }
           // }
        }
        return cacheSync;
    }

    private String[] getDbLocations() {
        return DataStorageLocationValidator.getInstance().getSupportedDBLocation();
    }

    //code merge by Jesse on 2007-01-24
    private IPartnerCacheDSV getPartnerCacheDAO(String location) throws IDAOFactoryException {
        //        ConfigFileDAOFactory factory = ConfigFileDAOFactory.getInstance();
        //        IPartnerCacheDSV dao = (IPartnerCacheDSV)
        // factory.createDAO(IPartnerCacheDSV.class, location);
        IPartnerCacheDSV dsv = (IPartnerCacheDSV) DSVFactory.getInstance().getDataService(IPartnerCacheDSV.class, location);
        return dsv;
    }

    public Timestamp getLastSyncTimestamp() {
        return lastUpdateTime;
    }

    public void register(IUpdatable updatable) {
        this.updatable = updatable;
    }

    public int[] syncCacheBody() {
    	final String METHOD_NAME = "syncCacheBody";
        PLOG.info("syncCacheBody", PerformanceLogger.Event.METHOD_ENTRY);
        
        if(LOG.isDebugEnabled()){
        	LOG.debug(METHOD_NAME, "Current Timestamp:" + System.currentTimeMillis() + ",LastedUpdateTime:" + lastUpdateTime.getTime());
        }
        if (System.currentTimeMillis() - lastUpdateTime.getTime() < Config.getCacheSyncTimeOut()) {
            PLOG.info("syncCacheBody", PerformanceLogger.Event.METHOD_EXIT);

            return new int[] { 0, 0 };
        }

        List updateList = new ArrayList();
        List deleteList = new ArrayList();
        List updatedKycList = new ArrayList();
        int updateCount = 0, deletedCount = 0;
        Timestamp oldTime = lastUpdateTime;
//        Timestamp newTime = new Timestamp(System.currentTimeMillis());
        Timestamp newTime = new Timestamp(System.currentTimeMillis() - Config.getCacheSyncWithIn());
        
        if(LOG.isDebugEnabled()){
        	LOG.debug(METHOD_NAME,"It's time to update ");
        }
        //code merge by Jesse on 2007-01-24
        PLOG.info("syncCacheBody_syncCacheDAO", PerformanceLogger.Event.METHOD_ENTRY);
        
        try {
            String[] locs = getDbLocations();
            
            for (int i = 0; i < locs.length; ++i) {
            	IPartnerCacheDSV dsv = getPartnerCacheDAO(locs[i]); 
                List[] tmpLists = dsv.getUpdatedProfiles(oldTime);
                updateList.addAll(tmpLists[0]);
                deleteList.addAll(tmpLists[1]);
                updatedKycList.addAll(tmpLists[2]);
            }
            PLOG.info("UpdateCount" + updateList.size(), PerformanceLogger.Event.METHOD_ENTRY);
            PLOG.info("UpdateCount" + updateList.size(), PerformanceLogger.Event.METHOD_EXIT);
            
            PLOG.info("DeleteCount" + deleteList.size(), PerformanceLogger.Event.METHOD_ENTRY);
            PLOG.info("DeleteCount" + deleteList.size(), PerformanceLogger.Event.METHOD_EXIT);
            
            PLOG.info("KYCCount" + updatedKycList.size(), PerformanceLogger.Event.METHOD_ENTRY);
            PLOG.info("KYCCount" + updatedKycList.size(), PerformanceLogger.Event.METHOD_EXIT);
            
            PLOG.info("Time" + oldTime, PerformanceLogger.Event.METHOD_ENTRY);
            PLOG.info("Time" + oldTime, PerformanceLogger.Event.METHOD_EXIT);
            
            lastUpdateTime = newTime;
        } catch (DataServiceException e) {
            //        } catch (ICRMDAOException e) {
            LOG.error("syncCacheBody", "Error getting updated profiles", e);
        } catch (IDAOFactoryException e) {
            LOG.error("syncCacheBody", "Error getting updated profiles", e);
        }
        PLOG.info("syncCacheBody_syncCacheDAO", PerformanceLogger.Event.METHOD_EXIT);

        PLOG.info("syncCacheBody_syncHashMap", PerformanceLogger.Event.METHOD_ENTRY);
        updateCount = updatable.updateAll(updateList);
        deletedCount = updatable.removeAll(deleteList);
        PLOG.info("syncCacheBody_syncKycMap", PerformanceLogger.Event.METHOD_ENTRY);
        IMemoryCache memory = (IMemoryCache)updatable;
       	
       	for(int i=0; i<updatedKycList.size(); i++){
	       	Map map = (Map)updatedKycList.get(i);
	       	if(map!=null && map.size()>0){
	       		Set set = map.keySet();
	       		Iterator itr = set.iterator();
		        while(itr.hasNext()){
		        	Object partnerId = itr.next();
		        	IPartnerCacheEntrySO partnerCacheSO = (IPartnerCacheEntrySO)memory.get(partnerId);
		        	if (partnerCacheSO != null) {
		        		String kyc1 = partnerCacheSO.getKycReportableYN();
		        		String kyc2 = (String)map.get(partnerId);
		        		partnerCacheSO.setKycReportableYN(kyc2);
		        		updateCount = updateCount + memory.update(partnerCacheSO);
		        	}
		        }
	       	}
       	}
        PLOG.info("syncCacheBody_syncHashMap", PerformanceLogger.Event.METHOD_EXIT);

        PLOG.info("syncCacheBody", PerformanceLogger.Event.METHOD_EXIT);
        return new int[] { updateCount, deletedCount };
    }

}
