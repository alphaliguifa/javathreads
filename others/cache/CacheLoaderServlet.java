public class CacheLoaderServlet extends BaseServlet {
    private static Logger LOG = Logger.getLogger(CacheLoaderServlet.class);

    private static PollingSynchronizer poller;

    private static int POLL_INTERVAL_SECONDS;

    private boolean debugMode = LOG.isDebugEnabled();

    /**
     * @see javax.servlet.GenericServlet#void ()
     */
    public final void onInit(ServletConfig cfg) throws ServletException {
        //super.oninit(cfg);
        super.onInit(cfg);

        try {

            //IWebContainer cnt = WebContainer.getInstance();

            //super.init(this.getServletConfig());

            POLL_INTERVAL_SECONDS = Config.getChdPollingInterval();

            if (LOG.isDebugEnabled()) {
                LOG.debug("init", "-----------START ICRM cache load---------");
            }
           
            IMemoryCache fimCache = FIMCache.getInstance();
            IMemoryCache partnerCache = PartnerCache.getInstance();
            IMemoryCache prpCache = PRPCache.getInstance();
            IMemoryCache portfolioCache = PortfolioCache.getInstance(false);
            IMemoryCache mandateCache = MandateCache.getInstance(false);
            IMemoryCache dueDiligenceCache = DueDiligenceCache.getInstance();
            IMemoryCache caCache = CaCache.getInstance();
            if (LOG.isDebugEnabled()) {
                LOG.debug("init", "-----------END ICRM cache load---------");
            }
           

            ICacheSynchronizer portfolioSynchronizer = PortfolioCacheSynchronizer.getInstance();
            ICacheSynchronizer partnerSynchronizer = PartnerCacheSynchronizer.getInstance();
            ICacheSynchronizer prpSynchronizer = PRPCacheSynchronizer.getInstance();
            ICacheSynchronizer mandateSynchronizer = MandateCacheSynchronizer.getInstance();
            ICacheSynchronizer fimSynchronizer = FIMCacheSynchronizer.getInstance();
            ICacheSynchronizer dueDiligenceSynchronizer = DueDiligenceCacheSynchronizer.getInstance();
            ICacheSynchronizer caSynchronizer = CaCacheSynchronizer.getInstance();
            
            portfolioSynchronizer.syncCache();           

            mandateSynchronizer.syncCache();
           
            //add by Jesse Hou on 2007-10-25
            StaticDataHelper.getInstance();
            
            
            //	        if (!FeaturesConfig.isPortfolioMFSourceEnable()) {
            //	            if (debugMode) {
            //	                LOG.debug("init", "-----------Starting Polling
            // ClientHoldingSynchronizer ---------");
            //	            }
            //	            ICacheSynchronizer chdSynchronizer =
            // ClientHoldingSynchronizer.getInstance();
            //	            poller = new PollingSynchronizer(1000 * POLL_INTERVAL_SECONDS,
            // chdSynchronizer);
            //	        } else {
            //	            if (debugMode) {
            //	                LOG.debug("init", "-----------Starting Polling
            // MasterFileSynchronizer ---------");
            //	            }
            //	            ICacheSynchronizer mfSynchronizer =
            // MasterFileSynchronizer.getInstance();
            //	            poller = new PollingSynchronizer(1000 * POLL_INTERVAL_SECONDS,
            // mfSynchronizer);
            //	        }
            //	        poller.start();
            if (LOG.isDebugEnabled()) {
                LOG.debug("init", "-----------ICRM cache load finished ---------");
            }
            

        } catch (CacheSyncException e) {
            LOG.error("init", "Fail to init servlet", e);
        }

    }

    public void performTask(HttpServletRequest arg0, HttpServletResponse arg1) throws ServletException {
        // TODO Auto-generated method stub

    }

}
