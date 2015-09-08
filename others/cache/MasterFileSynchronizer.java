public class MasterFileSynchronizer extends PortfolioCacheSynchronizer {
    private static Logger LOG = Logger.getLogger(MasterFileSynchronizer.class);

    protected static ICacheSynchronizer cacheSync;

    // private Map reportDateMap;
    private Date checkDate;

    public static ICacheSynchronizer getInstance() {
        if (cacheSync == null) {
            cacheSync = new MasterFileSynchronizer();
            try {
                ((MasterFileSynchronizer) cacheSync).checkUpdated();
            } catch (PortfolioBSVException e) {
                LOG.error("syncCache", "Exception thrown while checking update");
            } catch (Exception e) {
                LOG.error("syncCache", "Exception thrown while checking update");
            }
        }
        return cacheSync;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ubs.ifop.application.icrm.util.cache.intf.ICacheSynchronizer#syncCache()
     */
    public int[] syncCache() {
        try {
            boolean reportDateChanged = checkUpdated();
            if (reportDateChanged) {
                return super.syncCache();
            }
        } catch (PortfolioBSVException e) {
            LOG.error("syncCache", "Exception thrown while checking update");
        } catch (Exception e) {
            LOG.error("syncCache", "Exception thrown while checking update");
        }
        return new int[] { 0, 0 };
    }

    // For debug only
    public int[] syncCacheForced() {
        return super.syncCache();
    }

    public boolean checkUpdated() throws PortfolioBSVException {
        // check MF_BATCH_CONTROL
        if (LOG.isDebugEnabled()) {
            LOG.debug("checkUpdate", "Checking latest master file update time...");
        }
        //        Date latestDate = getBS().getLatestMasterFileUpdatedTime();
        //        if (checkDate == null) {
        //            checkDate = latestDate;
        //        } else if (latestDate.after(checkDate)) {
        //            checkDate = latestDate;
        //            return true;
        //        }
        return false;
    }

    // public boolean checkReportDate() throws ICRMSystemException {
    // LOG.debug("checkReportDate", "Checking report date for update...");
    // Map latestReportDateMap = new
    // com.ubs.ifop.application.icrm.partner.impl.bsv.PortfolioBSV().getReportDateAndModifiedDate();
    //
    // if (reportDateMap == null) {
    // // Getting the report date map for the first time
    // reportDateMap = latestReportDateMap;
    // //return true;
    // } else {
    // for (Iterator iter = latestReportDateMap.keySet().iterator();
    // iter.hasNext();) {
    // String bookingCenter = (String)iter.next();
    // Object[] newDates = (Object[])latestReportDateMap.get(bookingCenter);
    // Object[] oldDates = (Object[])reportDateMap.get(bookingCenter);
    //				
    // Date newReportDate = (Date)newDates[0];
    // Date oldReportDate = (Date)oldDates[0];
    // Timestamp newLastModifiedTime = (Timestamp)newDates[1];
    // Timestamp oldLastModifiedTime = (Timestamp)oldDates[1];
    // //LOG.error("checkReportDate", "Checking report dates for
    // bookingCenter="+bookingCenter +": newReportDate="+newReportDate + "
    // oldReportDate="+oldReportDate + "
    // newLastModifiedTime="+newLastModifiedTime +"
    // oldLastModifiedTime="+oldLastModifiedTime);
    // if (newReportDate == null) {
    // LOG.debug("checkReportDate", "Report date is null for "+ bookingCenter+ "
    // newReportDate="+newReportDate+" oldReportDate="+oldReportDate);
    // } else {
    // if (!oldReportDate.equals(oldReportDate)) {
    // // Report date changed
    // LOG.debug("checkReportDate", "Report date change detected:
    // bookingCenter="+bookingCenter + " newReportDate="+newReportDate + "
    // oldReportDate="+oldReportDate);
    // reportDateMap = latestReportDateMap;
    // return true;
    // } else {
    // // Report date is the same
    // if (newLastModifiedTime == null || oldLastModifiedTime == null) {
    // LOG.debug("checkReportDate", "Last modified time is NULL for "+
    // bookingCenter+ " newLastModifiedTime="+newLastModifiedTime+"
    // oldLastModifiedTime="+oldLastModifiedTime);
    // } else if (newLastModifiedTime.after(oldLastModifiedTime)) {
    // LOG.debug("checkReportDate", "Last modified time change detected:
    // bookingCenter="+bookingCenter + "
    // newLastModifiedTime="+newLastModifiedTime+"
    // oldLastModifiedTime="+oldLastModifiedTime);
    // reportDateMap = latestReportDateMap;
    // return true;
    // }
    // }
    // }
    // }
    // }
    //		
    // return false;
    // }

    // This method always return null so it will reload the entire cache when
    // caching.
    public Timestamp getLastSyncTimestamp() {
        return null;
    }

    // Dummy method: this synchronizer does not use the timestamp to
    public void setLastSyncTimestamp(Timestamp t) {
        return;
    }

}
