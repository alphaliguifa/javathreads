public class AccessLocationSessionCache extends SessionCache {
    private static Logger LOG = Logger.getLogger(AccessLocationSessionCache.class);

    private final static PerformanceLogger PLOG = PerformanceLogger.getLogger(AccessLocationSessionCache.class);

    private Map accessLocMap = null;

    private static AccessLocationSessionCache accessLocCache = null;

    public synchronized static AccessLocationSessionCache getInstance() {
        if (accessLocCache == null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("getInstance", "create new CA ProfileSO Cache");
            }
            accessLocCache = new AccessLocationSessionCache();
        }
        return accessLocCache;
    }

    private AccessLocationSessionCache() {
        super();
        accessLocMap = new HashMap();
    }

    // with session checking
    public String getAccessLocationFromSession(String loginID, String remoteAddr, String sessionID) throws ClientException {
        final String METHOD_NAME = "getAccessLocationFromSession";
        //PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_ENTRY);

        String accessLoc = null;
        if (super.isSessionExist(loginID, sessionID)) {
            // Session of login user still exist, try to get profile from cache
            if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, "session exist");
            }
            accessLoc = getAccessLocation(loginID, remoteAddr);
        } else {
            // Session of login user not exist, get from client entitlement
            if (LOG.isDebugEnabled()) {
                LOG.debug("getProfileFromSession", "session not exist");
            }
            accessLoc = getAccessLocFromES(remoteAddr);
            addToCache(loginID, accessLoc);
            super.addToSessionCache(loginID, sessionID);
        }

        //PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_EXIT);
        return accessLoc;
    }

    // ignore session checking
    public String getAccessLocation(String loginID, String remoteAddr) throws ClientException {
        final String METHOD_NAME = "getAccessLocation";
        //PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_ENTRY);

        String accessLoc = (String) accessLocMap.get(loginID);
        if (accessLoc == null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, "get access location from ES");
            }
            accessLoc = getAccessLocFromES(remoteAddr);
            addToCache(loginID, accessLoc);
        }

        //PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_EXIT);
        return accessLoc;
    }

    private String getAccessLocFromES(String remoteAddr) throws ClientException {
        final String METHOD_NAME = "getAccessLocFromES";
        PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_ENTRY);
        String accessLoc = ESUtils.getAccessLocation(remoteAddr);
        PLOG.info(METHOD_NAME, PerformanceLogger.Event.METHOD_EXIT);
        return accessLoc;
    }

    private synchronized void addToCache(String userID, String accessLoc) {
        accessLocMap.put(userID, accessLoc);
    }

}
