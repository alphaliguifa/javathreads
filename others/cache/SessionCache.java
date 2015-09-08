public class SessionCache {
    private static Logger LOG = Logger.getLogger(SessionCache.class);

    private Map sessionMap = null;

    public SessionCache() {
        sessionMap = new HashMap();
    }

    public String getSessionID(String userID) {
        return (String) sessionMap.get(userID);
    }

    public boolean isSessionExist(String userID, String sessionID) {
        String cachedSessionID = getSessionID(userID);
        return sessionID.equals(cachedSessionID);
    }

    public synchronized void addToSessionCache(String userID, String sessionID) {
        sessionMap.put(userID, sessionID);
    }

    public synchronized void clearSessionCache(String userID) {
        if (sessionMap.containsKey(userID)) {
            sessionMap.remove(userID);
        }
    }
}
