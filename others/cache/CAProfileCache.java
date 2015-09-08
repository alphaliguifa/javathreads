public class CAProfileCache extends SessionCache {
    private static Logger LOG = Logger.getLogger(CAProfileCache.class);

    private final static PerformanceLogger PLOG = PerformanceLogger.getLogger(CAProfileCache.class);

    private Map caProfileMap = null;

    private static CAProfileCache caProfileCache = null;

    public synchronized static CAProfileCache getInstance() {
        if (caProfileCache == null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("getInstance", "create new CA ProfileSO Cache");
            }
            caProfileCache = new CAProfileCache();
        }
        return caProfileCache;
    }

    private CAProfileCache() {
        super();
        caProfileMap = new HashMap();
    }

    // with session checking
    public CAProfileSO getProfileFromSession(String loginID, String sessionID) throws ClientBSVException, IBSFactoryException {
        final String METHOD_NAME = "getProfileFromSession";
        PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_ENTRY);

        CAProfileSO ca = null;
        if (super.isSessionExist(loginID, sessionID)) {
            // Session of login user still exist, try to get profile from cache
            if (LOG.isDebugEnabled()) {
                LOG.debug("getProfileFromSession", "session exist");
            }
            ca = getProfile(loginID);
        } else {
            // Session of login user not exist, get from client entitlement
            if (LOG.isDebugEnabled()) {
                LOG.debug("getProfileFromSession", "session not exist");
            }
            ca = getProfileFromClientEntitlement(loginID);
            addToCache(loginID, ca);
            super.addToSessionCache(loginID, sessionID);
        }

        PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_EXIT);
        return ca;
    }

    // ignore session checking
    public CAProfileSO getProfile(String loginID) throws ClientBSVException, IBSFactoryException {
        final String METHOD_NAME = "getProfile";
        PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_ENTRY);

        CAProfileSO ca = (CAProfileSO) caProfileMap.get(loginID);
        if (ca == null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("getProfile", "get profile from client entitlement");
            }
            ca = getProfileFromClientEntitlement(loginID);
            addToCache(loginID, ca);
        }

        PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_EXIT);
        return ca;
    }

    private CAProfileSO getProfileFromClientEntitlement(String userID) throws ClientBSVException, IBSFactoryException {
        final String METHOD_NAME = "getProfileFromClientEntitlement";
        PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_ENTRY);
        try {
            if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, " is called");
            }
            CAProfileSO ca = this.getUserEntitlementBSV().getCa(userID);
            return ca;
        } catch (UserEntitlementBSVException e) {
            throw new ClientBSVException(e);
        } finally {
            PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_EXIT);
        }
    }

    private static IClientEntitlementBSV getClientEntitlementBSV() {
        IBusinessServiceManager bsvmgr = GenericContainer.getInstance().getBusinessServiceManager();
        return (IClientEntitlementBSV) bsvmgr.getBusinessService(IClientEntitlementBSV.class);
    }

    private IUserEntitlementBSV getUserEntitlementBSV() {
        IBusinessServiceManager bsvmgr = GenericContainer.getInstance().getBusinessServiceManager();
        return (IUserEntitlementBSV) bsvmgr.getBusinessService(IUserEntitlementBSV.class);
    }

    private synchronized void addToCache(String userID, CAProfileSO profile) {
        caProfileMap.put(userID, profile);
    }

}
