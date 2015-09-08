public interface IPartnerCacheEntrySO {
    public String toString();
    
    public String getClient3BO();
    
	public void setClient3BO(String client3BO);
	
	public String getClientAH();
    
    public void setClientAH(String clientAH); 
    
    public String getClientBO();
    
    public void setClientBO(String clientBO); 
    
    public String getClientPOA();
    
    public void setClientPOA(String clientPOA);
    
    
    public String getClientLPOA() ;
    
    public void setClientLPOA(String clientLPOA) ;
    
    public String getClientAS();
    
    public void setClientAS(String clientAS) ;
    
	public String getClientBOAS();

	public void setClientBOAS(String clientBOAS);

	public String getClientBOLPOA();

	public void setClientBOLPOA(String clientBOLPOA);

	public String getClientBOPOA();

	public void setClientBOPOA(String clientBOPOA);
    
    public String getClientCFR() ;
    
    public void setClientCFR(String clientCFR) ;

    public ICAProfileSO getCa();

    public String getCategory();

    public String getPhone();

    public String getDomicile();

    public Date getExpiryDate();

    public String getFamilyName();

    public String getFamilyNameAfterTranslate();

    public String getFirstName();

    public String getFirstNameAfterTranslate();

    public String getMiddleName();

    public String getMiddleNameAfterTranslate();

    public String getNationality();

    public String getNickName();

    public String getNickNameAfterTranslate();

    public String getPartnerId();

    public String getPotentialNNM();

    //Added by Quick on 2006-03-28
    public String getPotentialCCY();

    public String getPriority();

    public String getStatus();

    public String getOwner();

    public String getLegalId();
    
    public String getKcmYN();
    
    public void setKcmYN(String kcmYN);

    public void setCa(ICAProfileSO ca);

    public void setCategory(String category);

    public void setPhone(String phone);

    public void setDomicile(String domicile);

    public void setExpiryDate(Date expiryDate);

    public void setFamilyName(String familyName);

    public void setFamilyNameAfterTranslate(String familyNameAfterTranslate);

    public void setFirstName(String firstName);

    public void setFirstNameAfterTranslate(String firstNameAfterTranslate);

    public void setMiddleName(String middleName);

    public void setMiddleNameAfterTranslate(String middleNameAfterTranslate);

    public void setNationality(String nationality);

    public void setNickName(String nickName);

    public void setNickNameAfterTranslate(String nickNameAfterTranslate);

    public void setPartnerId(String partnerId);

    public void setPotentialNNM(String potentialNNM);

    //Added by Quick on 2006-03-28
    public void setPotentialCCY(String potentialCCY);

    public void setPriority(String priority);

    public void setStatus(String status);

    public void setOwner(String owner);

    public void setLegalId(String legalId);

    public boolean isDoNotContact();

    public boolean isPoolProspectYN();

    //    public void setDoNotContact(boolean doNotContact);

    public void setDoNotContact(String doNotContact);

    //    public void setPoolProspectYN(boolean poolProspectYN);

    public void setPoolProspectYN(String poolProspect);

    public String getStorageLocation();

    public void setStorageLocation(String storageLocation);

    public String getCaFullName();

    public String getName();

    public IProfileSO getProfile();

    //Added by Sunny Wu 2006-04-12
    public IClientProfileSO getClientProfile(); 
    
    public IProspectProfileSO getProspectProfile();
    
    /**
     * Fred Lee(2005-8-23) Add virtual::boolean getter/setter
     * 
     * @param virtual
     */
    public void setVirtual(String virtual);

    public boolean isVirtual();

    /**
     * Fred Lee(2005-8-23) Add referencePartnerID::String getter/setter
     * 
     * @param partnerId
     */
    public void setReferencePartnerID(String partnerId);

    public String getReferencePartnerID();
    
	/**
	 * Sunny Wu  (2006/04/12)
	 * @return Returns the clientRiskStatus.
	 */
	public String getClientRiskStatus();
	
	/**
	 * Sunny Wu  (2006/04/12)
	 * @param clientRiskStatus The clientRiskStatus to set.
	 */
	public void setClientRiskStatus(String clientRiskStatus);
	
	/**
	 * Added by Sunny Wu 2006-04-14
	 * @return Returns the clientRiskId.
	 */
	public String getClientRiskId();
	/**
	 * Added by Sunny Wu 2006-04-14
	 * @param clientRiskId The clientRiskId to set.
	 */
	public void setClientRiskId(String clientRiskId);
	
	/**
     * Added By Roy Chen on 2006-09-22 retrofit from 3.4 to 5.2
     * @return Returns the kycPartner.
     */
    public boolean isKycPartner();
    /**
     * Added By Roy Chen on 2006-09-22 retrofit from 3.4 to 5.2
     * @param kycPartner The kycPartner to set.
     */
    public void setKycPartner(boolean kycPartner);
    
    public void setKycReportableYN(String kycReportableYN);
    
    public String getKycReportableYN();
	
    /**
	 * @return Returns the otherPartnerStatus.
	 */
	public String getOtherPartnerStatus();
	/**
	 * @param otherPartnerStatus The otherPartnerStatus to set.
	 */
	public void setOtherPartnerStatus(String otherPartnerStatus) ;
	
	public IPartnerKeySO getPartnerKeySO();

	public boolean isOnlyBOSMRole();

	public String getCPACRole();
	
	
	/* add by John Zhu for PDE2 De-duplication */
	/**
	 * @return the pDEUniqueYN
	 */
	public String getPDEUniqueYN();

	/**
	 * @param uniqueYN the pDEUniqueYN to set
	 */
	public void setPDEUniqueYN(String uniqueYN);

	/**
	 * @return the pDEActivePtflCount
	 */
	public int getPDEActivePtflCount();

	/**
	 * @param activePtflCount the pDEActivePtflCount to set
	 */
	public void setPDEActivePtflCount(int activePtflCount);
	
	/**
	 * @return the dateOfBirth
	 */
	public Date getDateOfBirth();

	/**
	 * @param dateOfBirth the dateOfBirth to set
	 */
	public void setDateOfBirth(Date dateOfBirth);
	
	/**
	 * @return the pDEActivePtflCountStr
	 */
	public String getPDEActivePtflCountStr();

	/**
	 * @param activePtflCountStr the pDEActivePtflCountStr to set
	 */
	public void setPDEActivePtflCountStr(String activePtflCountStr);
	
	/**
	 * @return the pDEConfirmedGroupId
	 */
	public String getPDEConfirmedGroupId();

	/**
	 * @param confirmedGroupId the pDEConfirmedGroupId to set
	 */
	public void setPDEConfirmedGroupId(String confirmedGroupId);
	

	public String getPDEConfirmedMainPtnerId();
	
	/**
	 * @param confirmedMainPtnerId the pDEConfirmedMainPtnerId to set
	 */
	public void setPDEConfirmedMainPtnerId(String confirmedMainPtnerId);

	/**
	 * @return the pDERecomdGroupId
	 */
	public String getPDERecomdGroupId();

	/**
	 * @param recomdGroupId the pDERecomdGroupId to set
	 */
	public void setPDERecomdGroupId(String recomdGroupId);

	/**
	 * @return the pDERecomdMainPtnerId
	 */
	public String getPDERecomdMainPtnerId();

	/**
	 * @param recomdMainPtnerId the pDERecomdMainPtnerId to set
	 */
	public void setPDERecomdMainPtnerId(String recomdMainPtnerId);

	/**
	 * @return the pDERecomdRankStr
	 */
	public int getPDERecomdRank();

	/**
	 * @param recomdRankStr the pDERecomdRankStr to set
	 */
	public void setPDERecomdRank(int recomdRankStr);
	
	public void setPDERecomdRankStr(String recomdRankStr) ;
	
	/**
	 * @return the pDEOpenZRHPtflCount
	 */
	public int getPDEOpenZRHPtflCount();

	/**
	 * @param openZRHPtflCount the pDEOpenZRHPtflCount to set
	 */
	public void setPDEOpenZRHPtflCount(String openZRHPtflCount);
	
	
	/**
	 * @return the pDECAStatus
	 */
	public String getPDECAStatus();

	/**
	 * @param status the pDECAStatus to set
	 */
	public void setPDECAStatus(String status);

	/**
	 * @return the pDECAStatusModifier
	 */
	public String getPDECAStatusModifier();

	/**
	 * @param statusModifier the pDECAStatusModifier to set
	 */
	public void setPDECAStatusModifier(String statusModifier);

	/**
	 * @return the pDECAStatusModifyTime
	 */
	public Timestamp getPDECAStatusModifyTime();

	/**
	 * @param statusModifyTime the pDECAStatusModifyTime to set
	 */
	public void setPDECAStatusModifyTime(Timestamp statusModifyTime);
	/**
	 * @return the pDEWMOStatus
	 */
	public String getPDEWMOStatus();

	/**
	 * @param status the pDEWMOStatus to set
	 */
	public void setPDEWMOStatus(String status);

	/**
	 * @return the pDEWMOStatusModifier
	 */
	public String getPDEWMOStatusModifier();

	/**
	 * @param statusModifier the pDEWMOStatusModifier to set
	 */
	public void setPDEWMOStatusModifier(String statusModifier);

	/**
	 * @return the pDEWMOStatusModifyTime
	 */
	public Timestamp getPDEWMOStatusModifyTime();

	/**
	 * @param statusModifyTime the pDEWMOStatusModifyTime to set
	 */
	public void setPDEWMOStatusModifyTime(Timestamp statusModifyTime);

	/**
	 * @return the pDEAssignBy
	 */
	public String getPDEAssignBy();

	/**
	 * @param assignBy the pDEAssignBy to set
	 */
	public void setPDEAssignBy(String assignBy);

	/**
	 * @return the pDEEliminateStatus
	 */
	public String getPDEEliminateStatus();

	/**
	 * @param eliminateStatus the pDEEliminateStatus to set
	 */
	public void setPDEEliminateStatus(String eliminateStatus);
	
	/**
	 * @return the pDEAssignOn
	 */
	public Timestamp getPDEAssignOn();

	/**
	 * @param assignOn the pDEAssignOn to set
	 */
	public void setPDEAssignOn(Timestamp assignOn);
}
