public class PortfolioCache implements IMemoryCache {

    private final static Logger.Module MOD = new Logger.Module("CACHE");

    private final static PerformanceLogger PLOG = PerformanceLogger.getLogger(PortfolioCache.class);

    private final static Logger LOG = Logger.getLogger(PortfolioCache.class, MOD);

    private static PortfolioCache cache;

    private Map accStaticSeqCacheMap;

    private Map portfolioCacheMap;
    
    private Map pendingPortfolioCacheMap;  //Added by Charse Wang on Sep 12th, 2007 . Store portfolio with pending status
    private Map openPortfolioCacheMap;  //Added by Charse Wang on Sep 12th, 2007. Store portfolio with open status

    private TreeSet allNameSortedPortfolioCache;
    
    private TreeSet allIdSortedPortfolioCache;

    private PortfolioCache(boolean sync) throws CacheSyncException {
        final String METHOD_NAME = "PortfolioCache";
        PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_ENTRY);
        portfolioCacheMap = new ConcurrentReaderHashMap();
        accStaticSeqCacheMap = new ConcurrentReaderHashMap();
        //add by Kuqi -- adding sorted map 
        Sorter sorterName = new PortfolioListSorter(RequestParamConstant.PORTFOLIO_SUMMARY_SORT_BY_PORTFOLIO_NAME, false,LocaleMap.get());
        allNameSortedPortfolioCache = new TreeSet(sorterName.getComparator()); 
        Sorter sorterId = new PortfolioListSorter(RequestParamConstant.PORTFOLIO_SUMMARY_SORT_BY_PORTFOLIO_ID, false,LocaleMap.get());
        allIdSortedPortfolioCache  = new TreeSet(sorterId.getComparator());
        // portfolioCacheMap = new Hashtable();
        // accStaticSeqCacheMap = new Hashtable();
        
        pendingPortfolioCacheMap = new ConcurrentReaderHashMap();  //Added by Charse Wang on Sep 12th, 2007 
        openPortfolioCacheMap =  new ConcurrentReaderHashMap();  //Added by Charse Wang on Sep 12th, 2007
        
        PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_EXIT);
    }

    public static IMemoryCache getInstance(boolean sync) throws CacheSyncException {
        if (cache == null) {
            cache = new PortfolioCache(sync);
            
            if (LOG.isDebugEnabled())
                LOG.debug("getInstance","register portfolio cache:"+ cache);
            
            PortfolioCacheSynchronizer.getInstance().register(cache);
            CacheAgent.getInstance().register(PortfolioCache.class.toString(), cache);
            
            if (sync) {
                PortfolioCacheSynchronizer.getInstance().syncCache(); // This
                // should
                // load all
                // portfolios
            }

            ClientHoldingSynchronizer.getInstance().register(cache);
            
            
        }
        return cache;
    }

    public static IMemoryCache getInstance() throws CacheSyncException {
        return getInstance(true);
    }

    private IPortfolioCacheEntrySO getByAccStaticSeq(String accStaticSeq) {
    	IPortfolioKeySO key = (IPortfolioKeySO) accStaticSeqCacheMap.get(accStaticSeq);
        if (key != null)
            return (IPortfolioCacheEntrySO) portfolioCacheMap.get(key);
        else
            return null;
    }

    public int loadAll(List portfolioList) {
        final String METHOD_NAME = "loadAll";
        PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_ENTRY);

        if (LOG.isDebugEnabled())
            LOG.debug(METHOD_NAME, "Loading all portfolios...");

        int size = portfolioList.size();

        
        // Synchronize cache map because some cache entries will have incomplete
        // data during loading
        synchronized (accStaticSeqCacheMap) {
            for (int i = 0; i < size; ++i) {
                IPortfolioCacheEntrySO portfolio = (IPortfolioCacheEntrySO) portfolioList.get(i);
                IPortfolioKeySO key = PoolUtil.getPortfolioKeySO(portfolio.getPortfolioId(), portfolio.getBookingCenter());
                accStaticSeqCacheMap.put(portfolio.getAccountStaticSeq(), key);
                portfolioCacheMap.put(key, portfolio);
                allNameSortedPortfolioCache.add(portfolio);
                allIdSortedPortfolioCache.add(portfolio);
//              Added by Charse Wang on Sep 12th, 2007
                //Begin
                if (ICRMConstant.PORTFOLIO_STATUS_PENDING.equals(portfolio.getStatus())) {//If it's pending now, put it to pending map 
                	pendingPortfolioCacheMap.put(key, Boolean.TRUE);
                } else if (ICRMConstant.PORTFOLIO_STATUS_OPEN.equals(portfolio.getStatus())) { //If it's open now, put it to open map 
                	openPortfolioCacheMap.put(key, Boolean.TRUE);
                }
                //End  
            }
        }
        
        LOG.error("loadAll", "Load portfolio to cache: " + portfolioCacheMap.size());
        
        if (LOG.isDebugEnabled())
            LOG.debug(METHOD_NAME, "Finish loading all portfolios.");

        PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_EXIT);
        return size;
    }

    private void copyNonUpdatableFields(IPortfolioCacheEntrySO oldEntry, IPortfolioCacheEntrySO newEntry) {
        newEntry.setCaId(oldEntry.getCaId());
        // newEntry.setPortfolioName(oldEntry.getPortfolioName());
        // newEntry.setOpeningDate(oldEntry.getOpeningDate());
        // newEntry.setClosingDate(oldEntry.getClosingDate());
        // newEntry.setReferenceCurrency(oldEntry.getReferenceCurrency());
        // newEntry.setPortfolioName(oldEntry.getPortfolioName());
        // newEntry.setStatus(oldEntry.getStatus());
        //newEntry.setTotalAum(oldEntry.getTotalAum());
    }

    public Object[] getKeys() {
        //return portfolioCacheMap.keySet().toArray(new IPortfolioCacheKeySO[0]);
    	return portfolioCacheMap.keySet().toArray(new IPortfolioKeySO[portfolioCacheMap.keySet().size()]);
    }

    public Object get(Object key) {
        if (key == null) {
            return null;
        }
        Object returnValue = null;
//      if (key instanceof IPortfolioCacheKeySO) {
        if (key instanceof IPortfolioKeySO) {
            returnValue = portfolioCacheMap.get(key);
        } else if (key instanceof String) {
            returnValue = getByAccStaticSeq((String) key);
        }
        return returnValue;
    }

    public Object[] select(Predicate filter, Sorter sorter) {
        final String METHOD_NAME = "select(Predicate, sorter)";
        PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_ENTRY);
        
        //Added by Charse Wang on Sep 12th, 2007
        //Begin
        	if (filter instanceof IAccessibleCollectionFilter) {
        		Collection collection = ((IAccessibleCollectionFilter)filter).getAccessibleCollection();
        		if (collection != null) {
        			if (IPortfolioKeyCollection.class.isInstance(collection)) {
        				boolean isAccessOpenEntries = false;
        				if (filter instanceof IAccessibleCollectionFilterEx) {
        					isAccessOpenEntries = ((IAccessibleCollectionFilterEx)filter).isAccessOpenEntries();
        				}
        				return select((IPortfolioKeyCollection)collection, filter, sorter, 
        						((IAccessibleCollectionFilter)filter).isAccessPendingEntries(), isAccessOpenEntries);
        			} else {
        				throw new UncheckedException(new IllegalArgumentException("AccessibleCollection should be an instance of IPortfolioKeyCollection"));
        			}
        		}
        	}
        //End
        
        Object[] entries = null;
        
        if(sorter!=null&&sorter instanceof PortfolioListSorter&&RequestParamConstant.PORTFOLIO_SUMMARY_SORT_BY_PORTFOLIO_NAME.equals(((PortfolioListSorter)sorter).getField()))
        {
            List list = new ArrayList();
            if(sorter != null){
                Object[] objs = allNameSortedPortfolioCache.toArray();
                if(!((PortfolioListSorter)sorter).isDescending()){
                    	if(objs!=null)
                    	    for(int i=0;i<objs.length;i++){
                //for (Iterator iter = allNameSortedPortfolioCache.values().iterator(); iter.hasNext();) {
                    	    	IPortfolioCacheEntrySO item = (IPortfolioCacheEntrySO) objs[i];                  
                    	    	if (filter.evaluate(item)) {
                    	    	    list.add(item);
                    	    	}
                    	    }
                }else{
                    if(objs!=null)
                        for(int i=objs.length-1;i>=0;i--){
                        //for (Iterator iter = allNameSortedPortfolioCache.values().iterator(); iter.hasNext();) {
                            IPortfolioCacheEntrySO item = (IPortfolioCacheEntrySO) objs[i];                  
                            if (filter.evaluate(item)) {
                                list.add(item);
                            }
                        }
                }
            }
            entries = list.toArray(new IPortfolioCacheEntrySO[list.size()]);
        }else if(sorter!=null&&sorter instanceof PortfolioListSorter&&RequestParamConstant.PORTFOLIO_SUMMARY_SORT_BY_PORTFOLIO_ID.equals(((PortfolioListSorter)sorter).getField()))
        {
            List list = new ArrayList();
            if(sorter != null){
                Object[] objs = allIdSortedPortfolioCache.toArray();
                if(!((PortfolioListSorter)sorter).isDescending()){
                    	if(objs!=null)
                    	    for(int i=0;i<objs.length;i++){
                //for (Iterator iter = allNameSortedPortfolioCache.values().iterator(); iter.hasNext();) {
                    	    	IPortfolioCacheEntrySO item = (IPortfolioCacheEntrySO) objs[i];                  
                    	    	if (filter.evaluate(item)) {
                    	    	    list.add(item);
                    	    	}
                    	    }
                }else{
                    if(objs!=null)
                        for(int i=objs.length-1;i>=0;i--){
                        //for (Iterator iter = allNameSortedPortfolioCache.values().iterator(); iter.hasNext();) {
                            IPortfolioCacheEntrySO item = (IPortfolioCacheEntrySO) objs[i];                  
                            if (filter.evaluate(item)) {
                                list.add(item);
                            }
                        }
                }
            }
            entries = list.toArray(new IPortfolioCacheEntrySO[list.size()]);
        }else{

		        entries = (IPortfolioCacheEntrySO[]) select(filter);
		        if (sorter != null) {
		            entries = (IPortfolioCacheEntrySO[]) sorter.sort(entries);
		        }
        }
        
        PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_EXIT);
        return entries;
    }

    public Object[] select(Predicate filter) {
        final String METHOD_NAME = "select(Predicate)";
        PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_ENTRY);
        if (filter == null) {
            PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_EXIT);
            return portfolioCacheMap.values().toArray(new IPortfolioCacheEntrySO[portfolioCacheMap.values().size()]);
        }
        
        //Added by Charse Wang on Sep 12th, 2007
        //Begin
        	if (filter instanceof IAccessibleCollectionFilter) {
        		Collection collection = ((IAccessibleCollectionFilter)filter).getAccessibleCollection();
        		if (collection != null) {
        			if (IPortfolioKeyCollection.class.isInstance(collection)) {
        				boolean isAccessOpenEntries = false;
        				if (filter instanceof IAccessibleCollectionFilterEx) {
        					isAccessOpenEntries = ((IAccessibleCollectionFilterEx)filter).isAccessOpenEntries();
        				}        				
        				return select((IPortfolioKeyCollection)collection, filter, 
        						((IAccessibleCollectionFilter)filter).isAccessPendingEntries(), isAccessOpenEntries);
        			} else {
        				throw new UncheckedException(new IllegalArgumentException("AccessibleCollection should be an instance of IPortfolioKeyCollection"));
        			}
        		}
        	}
        //End        
        
        
        	
        List list = new ArrayList();
        for (Iterator iter = portfolioCacheMap.values().iterator(); iter.hasNext();) {
            IPortfolioCacheEntrySO item = (IPortfolioCacheEntrySO) iter.next();                  
            if (filter.evaluate(item)) {
                list.add(item);
            }
        }
        PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_EXIT);
        return list.toArray(new IPortfolioCacheEntrySO[list.size()]);
    }

    public Object[] select(Collection keySet, Predicate filter, Sorter sorter) {

        final String METHOD_NAME = "select(Collection,Predicate,Sorter)";
        PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_ENTRY);

        List list = new ArrayList();
        for (Iterator iter = keySet.iterator(); iter.hasNext();) {
            //IPortfolioCacheKeySO key = (IPortfolioCacheKeySO) iter.next();
        	IPortfolioKeySO key = (IPortfolioKeySO) iter.next();
            IPortfolioCacheEntrySO entry = (IPortfolioCacheEntrySO) portfolioCacheMap.get(key);

            if (entry == null)
                continue;

            if (filter == null)
                list.add(entry);
            else {
                if (filter.evaluate(entry))
                    list.add(entry);
            }
        }

        IPortfolioCacheEntrySO[] entries = (IPortfolioCacheEntrySO[]) list.toArray(new IPortfolioCacheEntrySO[list.size()]);

        if (sorter != null)
            entries = (IPortfolioCacheEntrySO[]) sorter.sort(entries);

        PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_EXIT);
        return entries;
    }

    public int update(Object obj) {

        final String METHOD_NAME = "update";
        PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_ENTRY);
        int i = 0;
        IPortfolioCacheEntrySO portfolio = (IPortfolioCacheEntrySO) obj;
        String accStaticSeq = portfolio.getAccountStaticSeq();
        //IPortfolioCacheKeySO key = new PortfolioCacheKeySO(portfolio.getPortfolioId(), portfolio.getBookingCenter());
        IPortfolioKeySO key = PoolUtil.getPortfolioKeySO(portfolio.getPortfolioId(), portfolio.getBookingCenter());
        /*
         * Synchronized should be used because the maps can get corrupted if
         * more than one thread is trying to update the portfolio id of a
         * portfolio at the same time.
         */
        synchronized (accStaticSeqCacheMap) {
            // update accStaticSeq map
            //IPortfolioCacheKeySO oldKey = (IPortfolioCacheKeySO) accStaticSeqCacheMap.put(portfolio.getAccountStaticSeq(), key);
        	IPortfolioKeySO oldKey = (IPortfolioKeySO) accStaticSeqCacheMap.put(portfolio.getAccountStaticSeq(), key);
            if (oldKey == null) {
                // This is a new portfolio OR old key removed (maybe another
                // thread deleted the portfolio before this thread can update)
                // TODO check if this is correct...
                portfolioCacheMap.put(key, portfolio);
                allNameSortedPortfolioCache.add( portfolio);
                allIdSortedPortfolioCache.add(portfolio);
                
                //Added by Charse Wang on Sep 12th, 2007
                //Begin
                if (ICRMConstant.PORTFOLIO_STATUS_PENDING.equals(portfolio.getStatus())) {//If it's pending now, put it to pending map 
                	pendingPortfolioCacheMap.put(key, Boolean.TRUE);
                } else if (ICRMConstant.PORTFOLIO_STATUS_OPEN.equals(portfolio.getStatus())) {//If it's open now, put it to open map 
                	openPortfolioCacheMap.put(key, Boolean.TRUE);
                }
                //End
                
            } else if (oldKey.equals(key)) {
                // PortfolioSO id not changed
                copyNonUpdatableFields((IPortfolioCacheEntrySO) portfolioCacheMap.get(oldKey), portfolio);
                Object object = portfolioCacheMap.get(oldKey);
                if(object != null){
                    allNameSortedPortfolioCache.remove(object);
                    allIdSortedPortfolioCache.remove(object);
                }
                portfolioCacheMap.put(key, portfolio);
                allNameSortedPortfolioCache.add(portfolio);
                allIdSortedPortfolioCache.add(portfolio);
                //Added by Charse Wang on Sep 12th, 2007
                //Begin
                if (ICRMConstant.PORTFOLIO_STATUS_PENDING.equals(portfolio.getStatus())) {//If it's pending now, put it to pending map and try to remove it from open map 
                	pendingPortfolioCacheMap.put(key, Boolean.TRUE);
                	openPortfolioCacheMap.remove(key);
                } else {
                	pendingPortfolioCacheMap.remove(key); //It's not pending now, try to remove it , not to care about it's exsiting.
                	if (ICRMConstant.PORTFOLIO_STATUS_OPEN.equals(portfolio.getStatus())) {
                		openPortfolioCacheMap.put(key, Boolean.TRUE); ////If it's open now, put it to open map 
                	}
                }
                //End
            } else {
                // PortfolioSO id changed
                copyNonUpdatableFields((IPortfolioCacheEntrySO) portfolioCacheMap.get(oldKey), portfolio);
                portfolioCacheMap.remove(oldKey);
                Object object = portfolioCacheMap.get(oldKey);
                if(object != null){
                    allNameSortedPortfolioCache.remove(object);
                    allIdSortedPortfolioCache.remove(object);
                }
                portfolioCacheMap.put(key, portfolio);
                allNameSortedPortfolioCache.add(portfolio);
                allIdSortedPortfolioCache.add(portfolio);
                //Added by Charse Wang on Sep 12th, 2007
                //Begin
                pendingPortfolioCacheMap.remove(oldKey); // Just try to remove it , not to care about it's exsiting.
                openPortfolioCacheMap.remove(oldKey);
                if (ICRMConstant.PORTFOLIO_STATUS_PENDING.equals(portfolio.getStatus())) {//If it's pending now, put it to pending map 
                	pendingPortfolioCacheMap.put(key, Boolean.TRUE);
                } else if (ICRMConstant.PORTFOLIO_STATUS_OPEN.equals(portfolio.getStatus())) {//If it's open now, put it to open map 
                	openPortfolioCacheMap.put(key, Boolean.TRUE);
                }
                //End
            }
            ++i;
        }

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

    public synchronized int remove(Object obj) {

        final String METHOD_NAME = "remove";
        PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_ENTRY);
        int i = 0;

        IPortfolioCacheEntrySO entry = (IPortfolioCacheEntrySO) obj;
        String accStaticSeq = entry.getAccountStaticSeq();

        if (accStaticSeqCacheMap.containsKey(accStaticSeq)) {
            i = 1;
            //IPortfolioCacheKeySO key = (IPortfolioCacheKeySO) accStaticSeqCacheMap.remove(accStaticSeq);
            ;
            IPortfolioKeySO key = (IPortfolioKeySO) accStaticSeqCacheMap.remove(accStaticSeq);
            portfolioCacheMap.remove(key);
            
            Object object = portfolioCacheMap.get(key);
            if(object != null){
                allNameSortedPortfolioCache.remove(object); 
                allIdSortedPortfolioCache.remove(object);
            }
            //Added by Charse Wang on Sep 12th, 2007
            //Begin
            pendingPortfolioCacheMap.remove(key); // Just try to remove it , not to care about it's exsiting. 
            openPortfolioCacheMap.remove(key); // Just try to remove it , not to care about it's exsiting.
            //End
            
        }
        PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_EXIT);
        return i;
    }

    public int removeAll(Collection c) {
        final String METHOD_NAME = "removeAll";
        PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_ENTRY);
        int i = 0;
        for (Iterator iter = c.iterator(); iter.hasNext();) {
            if (remove(iter.next()) == 1)
                i++;
        }
        PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_EXIT);
        return i;
    }

    public boolean consistencyCheck() {
        final String METHOD_NAME = "consistencyCheck";
        PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_ENTRY);
        synchronized (accStaticSeqCacheMap) {
            for (Iterator iter = accStaticSeqCacheMap.keySet().iterator(); iter.hasNext();) {
                String accStaticSeq = (String) iter.next();
                //IPortfolioCacheKeySO cacheKey = (IPortfolioCacheKeySO) accStaticSeqCacheMap.get(accStaticSeq);
                IPortfolioKeySO cacheKey = (IPortfolioKeySO) accStaticSeqCacheMap.get(accStaticSeq);
                
                IPortfolioCacheEntrySO cacheEntry = (IPortfolioCacheEntrySO) portfolioCacheMap.get(cacheKey);
                if (cacheEntry == null) {
                    // Null entry?
                } else {
                	if (!cacheKey.getPortfolioNumber().equals(cacheEntry.getPortfolioId())
                            || !cacheKey.getBookingCenter().equals(cacheEntry.getBookingCenter())) {
                        // Inconsistent portfolio id
                    } else if (!cacheEntry.getAccountStaticSeq().equals(accStaticSeq)) {
                        // Inconsistent acc static seq
                    }
                }
            }

        }
        // TODO check if any portfolios in portfolioId map but not in
        // accstaticMap
        PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_EXIT);
        return true;
    }

    public Collection getCacheKeys(String indexName, Collection indexKeys) {
        // TODO Method not implemented
        return null;
    }

    public int size() {
        return (portfolioCacheMap == null) ? 0 : portfolioCacheMap.size();
    }
//  Added by Charse Wang on Sep 12th, 2007
//  Begin
      private Object[] select(IPortfolioKeyCollection accessibleCollection,
			Predicate filter, Sorter sorter, boolean isAccessPending, boolean isAccessOpen) {
      	Sorter tempSorter = null;
		if (sorter != null) {
			if (sorter instanceof PortfolioListSorter
					&& RequestParamConstant.PORTFOLIO_SUMMARY_SORT_BY_PORTFOLIO_NAME
							.equals(((PortfolioListSorter) sorter).getField())) {
				tempSorter = new PortfolioListSorter(
						RequestParamConstant.PORTFOLIO_SUMMARY_SORT_BY_PORTFOLIO_NAME, 
						((PortfolioListSorter) sorter).isDescending(),
						LocaleMap.get());
			} else if (sorter instanceof PortfolioListSorter
					&& RequestParamConstant.PORTFOLIO_SUMMARY_SORT_BY_PORTFOLIO_ID
							.equals(((PortfolioListSorter) sorter).getField())) {
				tempSorter = new PortfolioListSorter(
						RequestParamConstant.PORTFOLIO_SUMMARY_SORT_BY_PORTFOLIO_ID,
						((PortfolioListSorter) sorter).isDescending(),
						LocaleMap.get());
			} else {
				tempSorter = sorter;
			}
		}
		int initialSize = accessibleCollection.size();
		if (filter != null) {
			initialSize = initialSize / 2;
		}
		List result = new ArrayList(initialSize);
		select(accessibleCollection, filter, isAccessPending, isAccessOpen, result);
		Object[] ret = result.toArray(new IPortfolioCacheEntrySO[result.size()]);
		if (tempSorter != null) {
			ret = tempSorter.sort(ret);
		}
		return ret;
	}

	private Object[] select(IPortfolioKeyCollection accessibleCollection,
			Predicate filter, boolean isAccessPending, boolean isAccessOpen) {
		int initialSize = accessibleCollection.size();
		if (filter != null) {
			initialSize = initialSize / 2;
		}
		List result = new ArrayList(initialSize);
		select(accessibleCollection, filter, isAccessPending, isAccessOpen, result);
		return result.toArray(new IPortfolioCacheEntrySO[result.size()]);
	}

	private void select(IPortfolioKeyCollection accessibleCollection,
			Predicate filter, boolean isAccessPending, boolean isAccessOpen, List resultList) {
		
		if(LOG.isDebugEnabled())
		{
			LOG.debug("select","LTPT select()");
		}
		
		for (Iterator iter = accessibleCollection.iterator(); iter.hasNext();) {
			IPortfolioKeySO key = (IPortfolioKeySO)iter.next();
			IPortfolioCacheEntrySO item = (IPortfolioCacheEntrySO) portfolioCacheMap.get(key);
			if (item != null) {
				if (filter == null || filter.evaluate(item)) {
					resultList.add(item);
				}
			}
		}
		if (isAccessPending) {
			for(Iterator iter = pendingPortfolioCacheMap.keySet().iterator(); iter.hasNext();) {
				IPortfolioKeySO key = (IPortfolioKeySO)iter.next();
				if (accessibleCollection.contains(key)) {
					continue;
				}
				IPortfolioCacheEntrySO item = (IPortfolioCacheEntrySO) portfolioCacheMap.get(key);
				if (item != null) {
					if (filter == null || filter.evaluate(item)) {
						resultList.add(item);
					}
				}				
			}
		}
		if (isAccessOpen) {
			for(Iterator iter = openPortfolioCacheMap.keySet().iterator(); iter.hasNext();) {
				IPortfolioKeySO key = (IPortfolioKeySO)iter.next();
				if (accessibleCollection.contains(key)) {
					continue;
				}
				IPortfolioCacheEntrySO item = (IPortfolioCacheEntrySO) portfolioCacheMap.get(key);
				if (item != null) {
					if (filter == null || filter.evaluate(item)) {
						resultList.add(item);
					}
				}				
			}
		}
	}
//  End   
}
