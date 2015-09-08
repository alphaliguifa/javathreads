public class PartnerCache extends CacheObservable implements IMemoryCache {

    public static final String INDEX_CLIENT_PROSPECT = "INDEX_CLIENT_PROSPECT";

    private final static Logger LOG = Logger.getLogger(PartnerCache.class);

    private final static PerformanceLogger PLOG = PerformanceLogger.getLogger(PartnerCache.class);

    private static PartnerCache cache;

    private Map partnerCacheMap;

    private Map indexMap;
    
    private TreeSet allNameSortedPartnerCache;
    
    private TreeSet allIdSortedPartnerCache;
    
    public static final String Go = "1";

    private PartnerCache() throws CacheSyncException {

        final String METHOD_NAME = "PartnerCache";
        PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_ENTRY);

        this.indexMap = new ConcurrentReaderHashMap();
        this.indexMap.put(PartnerCache.INDEX_CLIENT_PROSPECT, new ClientProspectIndex(PartnerCache.INDEX_CLIENT_PROSPECT));
        this.addObserver((Observer) this.indexMap.get(PartnerCache.INDEX_CLIENT_PROSPECT));

        partnerCacheMap = new ConcurrentReaderHashMap();

        //add by Kuqi -- adding sorted map 
        Sorter sorterName = new PartnerCacheFieldSorter(RequestParamConstant.PROSPECT_PROFILE_LISTING_SORT_BY_NAME, false,LocaleMap.get());
        allNameSortedPartnerCache = new TreeSet(sorterName.getComparator()); 
        Sorter sorterId = new PartnerCacheFieldSorter(RequestParamConstant.PROSPECT_PROFILE_LISTING_SORT_BY_PARTNER_ID, false,LocaleMap.get());
        allIdSortedPartnerCache  = new TreeSet(sorterId.getComparator());

        loadAll();
        CacheAgent.getInstance().register(PartnerCache.class.toString(), this);
        PartnerCacheSynchronizer.getInstance().register(this);

        PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_EXIT);

    }

    public static synchronized IMemoryCache getInstance() throws CacheSyncException {
        if (cache == null) {
            cache = new PartnerCache();
        }
        return cache;
    }

    public int loadAll(List partnerList) {
        // TODO Remember to synchronize ther observer
        return 0;
    }

    public void loadAll() {
        final String METHOD_NAME = "loadAll";
        PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_ENTRY);

        try {
            List profileList = getPartnerCacheDAO().getAllProfiles();

            int size = profileList.size();
            if(LOG.isDebugEnabled())
                LOG.debug(METHOD_NAME,"load all partner to cache , size is : "+size);
            for (int i = 0; i < size; ++i) {
                IPartnerCacheEntrySO profile = (IPartnerCacheEntrySO) profileList.get(i);
                //String key = profile.getPartnerId();
                IPartnerKeySO key = PoolUtil.getPartnerKeySO(profile.getPartnerId());
                partnerCacheMap.put(key, profile);
                allNameSortedPartnerCache.add(profile);
                allIdSortedPartnerCache.add(profile);
            }

            // Fred Lee(2005-8-26): Mark changed to notify all the observers
            this.fire(new CacheEvent(this.partnerCacheMap, ICacheEvent.UPDATE_ALL));
            
            LOG.error("loadAll", "Load partner to cache: " + partnerCacheMap.size());
            
        } catch (DataServiceException e) {
            LOG.error("loadAllProfiles", "Error retrieve profiles from DAO", e);
            //		} catch (IDAOFactoryException e) {
            //			LOG.error("loadAllProfiles", "Error getting DAO", e);
        }
        PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_EXIT);
    }

    private IPartnerCacheDSV getPartnerCacheDAO() {
        //		ConfigFileDAOFactory factory = ConfigFileDAOFactory.getInstance();
        //		IPartnerCacheDSV dao =
        //			(IPartnerCacheDSV) factory.createDAO(IPartnerCacheDSV.class,
        // DataStorageLocationValidator.getInstance().getSupportedDBLocation());
        IPartnerCacheDSV dsv = (IPartnerCacheDSV) DSVFactory.getInstance().getDataService(IPartnerCacheDSV.class,
                DataStorageLocationValidator.getInstance().getSupportedDBLocation());
        if(LOG.isDebugEnabled())
        {
            String [] locations=DataStorageLocationValidator.getInstance().getSupportedDBLocation();
            if(locations!=null)
            {
                int size=locations.length;
                LOG.debug("getPartnerCacheDAO","get supported db location size is :"+size);
                for(int i=0;i<size;i++)
                {
                    LOG.debug("getPartnerCacheDAO","get supported db location ["+i+"]="+locations[i]);
                }
            }else{
                LOG.debug("getPartnerCacheDAO","get supported db location is null.");
            }
        }
        return dsv;
    }

    // private IPartnerCacheDSV getPortfolioCacheDAO(String location) throws
    // IDAOFactoryException {
    // ConfigFileDAOFactory factory = ConfigFileDAOFactory.getInstance();
    // IPartnerCacheDSV dsv = (IPartnerCacheDSV)
    // factory.createDAO(IPartnerCacheDSV.class, location);
    // return dsv;
    // }
    //
    public Object[] getKeys() {
        //return partnerCacheMap.keySet().toArray(new String[0]);
    	return partnerCacheMap.keySet().toArray(new IPartnerKeySO[0]);
    }

    public Object get(Object key) {
//        if(LOG.isDebugEnabled()) LOG.debug("get","get partner from cache , key is :"+key);
        if (key == null) {
            return null;
        }
        //return partnerCacheMap.get(key);
        Object returnValue = null;
        if (key instanceof IPartnerKeySO) {
            returnValue = partnerCacheMap.get(key);
        } else if (key instanceof String) {
            returnValue = getByPartnerId((String) key);
        }
        return returnValue;
    }
    
    private IPartnerCacheEntrySO getByPartnerId(String partnerId) {
        IPartnerKeySO key = PoolUtil.getPartnerKeySO(partnerId);
        if (key != null)
            return (IPartnerCacheEntrySO) partnerCacheMap.get(key);
        else
            return null;
    }

    public Collection selectCollection(Predicate filter) {
        PLOG.info("select", PerformanceLogger.Event.METHOD_ENTRY);
        if (filter == null)
            return partnerCacheMap.values();

        List list = new ArrayList();
        for (Iterator iter = partnerCacheMap.values().iterator(); iter.hasNext();) {
            IPartnerCacheEntrySO item = (IPartnerCacheEntrySO) iter.next();
            if (filter.evaluate(item))
                list.add(item);
        }
        PLOG.info("select", PerformanceLogger.Event.METHOD_EXIT);
        return list;
    }
    
    public Collection selectCollection(Predicate filter, Sorter sorter) {
        PLOG.info("select", PerformanceLogger.Event.METHOD_ENTRY);

        //Object[] entries = null;

        Collection collection=null;
        if (sorter != null){
            
            if(sorter instanceof PartnerCacheFieldSorter&&RequestParamConstant.PROSPECT_PROFILE_LISTING_SORT_BY_NAME.equals(((PartnerCacheFieldSorter)sorter).getField())){
                List list = new ArrayList();
                if(sorter != null){
                    Object[] objs = allNameSortedPartnerCache.toArray();
                    if(!((PartnerCacheFieldSorter)sorter).isDescending()){
                        	if(objs!=null)
                        	    for(int i=0;i<objs.length;i++){
                        	        IPartnerCacheEntrySO item = (IPartnerCacheEntrySO) objs[i];                  
                        	    	if (filter == null || (filter != null && filter.evaluate(item))) {
                        	    	    list.add(item);
                        	    	}
                        	    }
                    }else{
                        if(objs!=null)
                            for(int i=objs.length-1;i>=0;i--){
                                IPartnerCacheEntrySO item = (IPartnerCacheEntrySO) objs[i];                  
                                if (filter == null || (filter != null && filter.evaluate(item))) {
                                    list.add(item);
                                }
                            }
                    }
                }
                collection = list;
            }else if(sorter instanceof PartnerCacheFieldSorter&&RequestParamConstant.PROSPECT_PROFILE_LISTING_SORT_BY_PARTNER_ID.equals(((PartnerCacheFieldSorter)sorter).getField())){
                List list = new ArrayList();
                if(sorter != null){
                    Object[] objs = allIdSortedPartnerCache.toArray();
                    if(!((PartnerCacheFieldSorter)sorter).isDescending()){
                        	if(objs!=null)
                        	    for(int i=0;i<objs.length;i++){
                        	        IPartnerCacheEntrySO item = (IPartnerCacheEntrySO) objs[i];                  
                        	        if (filter == null || (filter != null && filter.evaluate(item))) {
                        	    	    list.add(item);
                        	    	}
                        	    }
                    }else{
                        if(objs!=null)
                            for(int i=objs.length-1;i>=0;i--){
                                IPartnerCacheEntrySO item = (IPartnerCacheEntrySO) objs[i];                  
                                if (filter == null || (filter != null && filter.evaluate(item))) {
                                    list.add(item);
                                }
                            }
                    }
                }
                collection = list;
            }else{
            
                Set set = new TreeSet(sorter.getComparator()); 
        
                for (Iterator iter = partnerCacheMap.values().iterator(); iter.hasNext();) {
                    IPartnerCacheEntrySO item = (IPartnerCacheEntrySO) iter.next();
                    if (filter == null || (filter != null && filter.evaluate(item))) 
                        set.add(item);
                }

                collection =  set;
            }
        }else{
        	collection = selectCollection(filter);
        }
        
        PLOG.info("select", PerformanceLogger.Event.METHOD_EXIT);
        return collection;
    }
    
    public Object[] select(Predicate filter, Sorter sorter) {
        PLOG.info("select", PerformanceLogger.Event.METHOD_ENTRY);
        Object[] result= selectCollection(filter).toArray(new IPartnerCacheEntrySO[0]);
        PLOG.info("select", PerformanceLogger.Event.METHOD_EXIT);
        return result;
    }
    
    public Object[] select(Predicate filter) {
        PLOG.info("select", PerformanceLogger.Event.METHOD_ENTRY);
        Object[] result= selectCollection(filter).toArray(new IPartnerCacheEntrySO[0]);
        PLOG.info("select", PerformanceLogger.Event.METHOD_EXIT);
        return result;
    }
    
    public Object[] select(Collection keySet, Predicate filter, Sorter sorter) {

        String METHOD_NAME = "select";
        PLOG.info("select", PerformanceLogger.Event.METHOD_ENTRY);
        Object[] result= selectCollection(keySet,filter,sorter).toArray(new IPartnerCacheEntrySO[0]);
        PLOG.info("select", PerformanceLogger.Event.METHOD_EXIT);
        return result;
        
    }
    
    public Collection selectCollection(Collection keySet, Predicate filter, Sorter sorter) {

        String METHOD_NAME = "select";
        PLOG.info("select", PerformanceLogger.Event.METHOD_ENTRY);
        if(LOG.isDebugEnabled())
        {
            LOG.debug(METHOD_NAME,"get filter partner from cache ,keySet is : "+keySet+"; filter is :"+filter+"; sorter is :"+sorter);
        }
        //IPartnerCacheEntrySO[] entries = null;
        Collection collection=null;
        
        if (keySet == null || keySet.size() < 1) {
            return collection=new ArrayList();
        }
        
        if (sorter != null){
            Set set = new TreeSet(sorter.getComparator()); 
	        for (Iterator iter = keySet.iterator(); iter.hasNext();) {
	            //IPartnerKeySO key = (IPartnerKeySO) iter.next();
	        	
	            Object key = (Object) iter.next();
	            
	            
	            IPartnerCacheEntrySO entry = (IPartnerCacheEntrySO)this.get(key);
//	            IPartnerCacheEntrySO entry = (IPartnerCacheEntrySO) partnerCacheMap.get(key);
	
//	            if (LOG.isDebugEnabled()) {
//   				 LOG.debug(METHOD_NAME, "EntryKey:" + key + ",Filter type:" +
//		             (filter == null ? "null" : filter.getClass().getName()) +
//		             ",Entry:" + DebugUtil.getSOValues(entry));
//	            }
	            
	           /* if (LOG.isDebugEnabled()) {
	            	LOG.debug(METHOD_NAME, "partner id is:"+(entry == null ? "" : entry.getPartnerId()) +"EntryKey:" + key + ",Filter type:" +
	            			(filter == null ? "null" : filter.getClass().getName()));
	            }*/
	            if (entry == null)
	                continue;
	
	            if (filter == null)
	                set.add(entry);
	            else {
	                if (filter.evaluate(entry))
	                    set.add(entry);
	            }
	        }
	
	        collection = set;
        }else{
        
	        List list = new ArrayList();
	        for (Iterator iter = keySet.iterator(); iter.hasNext();) {
	            //IPartnerKeySO key = (IPartnerKeySO) iter.next();
	            Object key = (Object) iter.next();

	            IPartnerCacheEntrySO entry = (IPartnerCacheEntrySO) this.get(key);
	            
//    			if (LOG.isDebugEnabled()) {
//    				 LOG.debug(METHOD_NAME, "EntryKey:" + key + ",Filter type:" +
//		             (filter == null ? "null" : filter.getClass().getName()) +
//		             ",Entry:" + DebugUtil.getSOValues(entry));
//    			}
	          /*  if (LOG.isDebugEnabled()) {
	            	LOG.debug(METHOD_NAME, "partner id is:"+(entry == null ? "" : entry.getPartnerId())+"EntryKey:" + key + ",Filter type:" +
	            			(filter == null ? "null" : filter.getClass().getName()));
	            }*/	            			
	            if (entry == null)
	                continue;
	
	            if (filter == null)
	                list.add(entry);
	            else {
	                if (filter.evaluate(entry))
	                    list.add(entry);
	                
		            if (LOG.isDebugEnabled()) {
		            	LOG.debug(METHOD_NAME, "add success partner id is:"+(entry == null ? "" : entry.getPartnerId())+"EntryKey:");
		            }	                
	            }		            
	        }
	        /*if (LOG.isDebugEnabled()) {
	            LOG.debug(METHOD_NAME, "PartnerCacheEntrySO Size After filter:" + list.size());
	        }*/
	        collection = list;
        }
//        PLOG.info("select.sort", PerformanceLogger.Event.METHOD_ENTRY);
//        if (sorter != null)
//            entries = (IPartnerCacheEntrySO[]) sorter.sort(entries);
//        PLOG.info("select.sort", PerformanceLogger.Event.METHOD_EXIT);
        PLOG.info("select", PerformanceLogger.Event.METHOD_EXIT);

        return collection;
    }
    
    public synchronized int remove(Object obj) {
        final String METHOD_NAME = "remove(Object)";
        PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_ENTRY);
        int i = 0;

        IPartnerCacheEntrySO entry = (IPartnerCacheEntrySO) obj;
        //String partnerId = entry.getPartnerId();
        IPartnerKeySO key = entry.getPartnerKeySO();

        if (partnerCacheMap.containsKey(key)) {
            i = 1;
            Object object = partnerCacheMap.get(key);
            if(object != null){
                allNameSortedPartnerCache.remove(object);
                allIdSortedPartnerCache.remove(object);
            }
            partnerCacheMap.remove(key);

            this.fire(new CacheEvent(entry, ICacheEvent.REMOVE));
        }
        PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_EXIT);
        return i;
    }

    public int removeAll(Collection c) {
        final String METHOD_NAME = "removeAll(Collection)";
        PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_ENTRY);
        int i = 0;
        for (Iterator iter = c.iterator(); iter.hasNext();) {
            if (remove(iter.next()) == 1)
                i++;
        }
        PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_EXIT);
        return i;
    }

    public synchronized int update(Object obj) {
        final String METHOD_NAME = "update";
        PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_ENTRY);
        int i = 0;
        IPartnerCacheEntrySO partner = (IPartnerCacheEntrySO) obj;
        //String partnerId = partner.getPartnerId();
        IPartnerKeySO key = partner.getPartnerKeySO();

        Object object = partnerCacheMap.get(key);
        
        if(object!=null){
            allNameSortedPartnerCache.remove(object);
            allIdSortedPartnerCache.remove(object);
        }
        allNameSortedPartnerCache.add(partner);
        allIdSortedPartnerCache.add(partner);
        partnerCacheMap.put(key, partner);
        ++i;

        this.fire(new CacheEvent(partner, ICacheEvent.UPDATE));

        PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_EXIT);
        return i;
    }

    public int updateAll(Collection c) {
        int i = 0;
        for (Iterator iter = c.iterator(); iter.hasNext();) {
            if (update(iter.next()) == 1)
                i++;
        }
        return i;
    }

    /**
     * To retrieve chached key by index name
     */
    public Collection getCacheKeys(String indexName, Collection indexKeys) {

        final String METHOD_NAME = "getCacheKeys";
        PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_ENTRY);
        IIndex index = (IIndex) this.indexMap.get(indexName);

        String[] indexKeyArray = (String[]) indexKeys.toArray(new String[indexKeys.size()]);

        Collection cacheKeys = new ArrayList();

        for (int i = 0; i < indexKeyArray.length; i++) {
            IPartnerCacheEntrySO entry = (IPartnerCacheEntrySO) index.getValue(indexKeyArray[i]);
            if (entry != null) {
                cacheKeys.add(entry.getPartnerId());
            }
        }

        PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_EXIT);
        return cacheKeys;
    }

    public int size() {
        return (partnerCacheMap == null) ? 0 : partnerCacheMap.size();
    }
}
