public class PartnerCacheEntrySO implements IPartnerCacheEntrySO, Serializable {
	private static final long serialVersionUID = 1L;

	protected String firstName;

	protected String firstNameAfterTranslate;

	protected String middleName;

	protected String middleNameAfterTranslate;

	protected String familyName;

	protected String familyNameAfterTranslate;

	protected String nickName;

	protected String nickNameAfterTranslate;

	protected String phone;

	protected ICAProfileSO ca = null;

	protected String domicile;

	protected String nationality;

	protected IPartnerKeySO partnerId;

	protected String status;

	protected String otherPartnerStatus;

	protected String priority;

	protected String potentialNNM;

	// Added by Quick on 2006-03-28
	protected String potentialCCY;

	protected Date expiryDate = null;

	protected String category;

	protected String storageLocation;

	protected String owner;

	protected String legalId;

	protected String poolProspectYN;

	protected String doNotContact;

	// protected boolean virtual; // Fred Lee(2005-8-23): Add property
	// virtual::boolean
	protected String referencePartnerID; // Fred Lee(2005-8-23): Add property

	// Sunny Wu (2006/04/12) clientRiskStatus & clientRiskId
	protected String clientRiskStatus;

	protected String clientRiskId;

	protected String name; // Added by Ronin

	protected String caFullName; // Added by Ronin

	protected boolean kycPartner; // Added By Roy Chen on 2006-09-22 retrofit
	// from 3.4 to 5.2

	protected String kycReportableYN;// added by Bell zhong on 2006-10-27

	protected String kcmYN;

	protected String clientAH;
	protected String clientBO;
	protected String clientPOA;
	protected String clientLPOA;
	protected String clientAS;
	protected String clientCFR;
	protected String client3BO;
	protected String clientBOPOA;
	protected String clientBOLPOA;
	protected String clientBOAS;

	public String getClient3BO() {
		return this.client3BO;
	}

	public void setClient3BO(String client3BO) {
		this.client3BO = client3BO;
	}

	public String getClientAH() {
		return clientAH;
	}

	public void setClientAH(String clientAH) {
		this.clientAH = clientAH;
	}

	public String getClientBO() {
		return clientBO;
	}

	public void setClientBO(String clientBO) {
		this.clientBO = clientBO;
	}

	public String getClientPOA() {
		return clientPOA;
	}

	public void setClientPOA(String clientPOA) {
		this.clientPOA = clientPOA;
	}

	public String getClientLPOA() {
		return clientLPOA;
	}

	public void setClientLPOA(String clientLPOA) {
		this.clientLPOA = clientLPOA;
	}

	public String getClientAS() {
		return clientAS;
	}

	public void setClientAS(String clientAS) {
		this.clientAS = clientAS;
	}

	public void setClientCFR(String clientCFR) {
		this.clientCFR = clientCFR;
	}

	public String getClientCFR() {
		return clientCFR;
	}

	public String getKcmYN() {
		return kcmYN;
	}

	public void setKcmYN(String kcmYN) {
		// this.kcmYN = kcmYN;
		// Use the string from the shared string pool
		this.kcmYN = PoolUtil.getYesNo(kcmYN);
	}

	public void setKycReportableYN(String kycReportableYN) {
		// this.kycReportableYN=kycReportableYN;
		// Use the string from the shared string pool
		this.kycReportableYN = PoolUtil.getYesNo(kycReportableYN);
		this.kycPartner = ICRMConstant.TRUE.equals(kycReportableYN);
	}

	public String getKycReportableYN() {
		return this.kycReportableYN;
	}

	/**
	 * Added By Roy Chen on 2006-09-22 retrofit from 3.4 to 5.2
	 * 
	 * @return Returns the kycPartner.
	 */
	public boolean isKycPartner() {
		return kycPartner;
	}

	/**
	 * Added By Roy Chen on 2006-09-22 retrofit from 3.4 to 5.2
	 * 
	 * @param kycPartner
	 *            The kycPartner to set.
	 */
	public void setKycPartner(boolean kycPartner) {
		this.kycPartner = kycPartner;
	}

	/**
	 * Added by Sunny Wu 2006-04-14
	 * 
	 * @return Returns the clientRiskStatus.
	 */
	public String getClientRiskStatus() {
		return clientRiskStatus;
	}

	/**
	 * Added by Sunny Wu 2006-04-14
	 * 
	 * @param clientRiskStatus
	 *            The clientRiskStatus to set.
	 */
	public void setClientRiskStatus(String clientRiskStatus) {
		// this.clientRiskStatus = clientRiskStatus;
		// Use the string from the shared string pool
		this.clientRiskStatus = PoolUtil.getStringFromPool(clientRiskStatus);
	}

	// referencePartnerID::String

	public String getReferencePartnerID() {
		return referencePartnerID;
	}

	public void setReferencePartnerID(String referencePartnerID) {
		this.referencePartnerID = referencePartnerID;
	}

	public boolean isVirtual() {
		return this.referencePartnerID != null;
	}

	public void setVirtual(String virtual) {
		// this.virtual = virtual; // Do Nothing
	}

	// public String toString() {
	// return "NameSO: " + firstName + middleName + familyName + ", " + nickName
	// + " - phone: " + phone + " - caName: " + getCaFullName()
	// + " - Domicile: " + domicile + " - Nationality: " + nationality + " -
	// PartnerId: " + partnerId + " - StatusSO: " + status
	// //Moddified by Quick on 2006-03-28
	// + " - PrioritySO: " + priority + " - PotentialNNM: " + potentialNNM + " -
	// PotentialCCY: " + potentialCCY + " - ExpiryDate: "
	// + expiryDate + " - Category: " + category + " - StorageLoacation: " +
	// storageLocation + " - Owner: " + owner + " - LegalId: "
	// + legalId + " - PoolProspectYN: " + poolProspectYN + " - Do Not Contact:
	// " + doNotContact + " - isVirtual : " + this.isVirtual()
	// + " - reference partner ID : " + this.getReferencePartnerID() +" -
	// clientRiskStatus: "+clientRiskStatus + " - KYCRelationship: " +
	// this.kycPartner;
	// }

	public ICAProfileSO getCa() {
		return ca;
	}

	public String getCategory() {
		return category;
	}

	public String getPhone() {
		return phone;
	}

	public String getDomicile() {
		return domicile;
	}

	public Date getExpiryDate() {
		return expiryDate;
	}

	public String getFamilyName() {
		return familyName;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public String getNationality() {
		return nationality;
	}

	public String getNickName() {
		return nickName;
	}

	public String getPartnerId() {
		return partnerId.getPartnerId();
	}

	public String getPotentialNNM() {
		return potentialNNM;
	}

	// Added by Quick 2006-03-28
	public String getPotentialCCY() {
		return potentialCCY;
	}

	public String getPriority() {
		return priority;
	}

	public String getStatus() {
		return status;
	}

	public void setCa(ICAProfileSO ca) {
		this.ca = ca;
	}

	public void setCategory(String category) {
		// this.category = category;
		// Use the string from the shared string pool
		this.category = PoolUtil.getStringFromPool(category);
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public void setDomicile(String domicile) {
		// this.domicile = domicile;
		// Use the string from the shared string pool
		this.domicile = PoolUtil.getStringFromPool(domicile);
	}

	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}

	public void setFamilyName(String familyName) {
		this.familyName = familyName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public void setNationality(String nationality) {
		// this.nationality = nationality;
		// Use the string from the shared string pool
		this.nationality = PoolUtil.getStringFromPool(nationality);
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public void setPartnerId(String partnerId) {
		// this.partnerId = partnerId;
		this.partnerId = PoolUtil.getPartnerKeySO(partnerId);
	}

	public IPartnerKeySO getPartnerKeySO() {
		return this.partnerId;
	}

	public void setPotentialNNM(String potentialNNM) {
		this.potentialNNM = potentialNNM;
	}

	// Added by Quick on 2006-03-28
	public void setPotentialCCY(String potentialCCY) {
		// this.potentialCCY = potentialCCY;
		// Use the string from the shared string pool
		this.potentialCCY = PoolUtil.getStringFromPool(potentialCCY);
	}

	public void setPriority(String priority) {
		// this.priority = priority;
		// Use the string from the shared string pool
		this.priority = PoolUtil.getStringFromPool(priority);
	}

	public void setStatus(String status) {
		// this.status = status;
		// Use the string from the shared string pool
		this.status = PoolUtil.getStringFromPool(status);
	}

	public String getStorageLocation() {
		return storageLocation;
	}

	public void setStorageLocation(String storageLocation) {
		// this.storageLocation = storageLocation;
		// Use the string from the shared string pool
		this.storageLocation = PoolUtil.getStringFromPool(storageLocation);
	}

	// public String getCaFullName() {
	// StringBuffer fullName = new StringBuffer();
	// if (this.ca != null) {
	// String firstName = (ca.getFirstName() == null) ? "" : ca.getFirstName();
	// String lastName = (ca.getLastName() == null) ? "" : ca.getLastName();
	// if (firstName.length() > 0 && lastName.length() > 0) {
	// // fullName.append(firstName).append(" ").append(lastName);
	// fullName.append(lastName).append(", ").append(firstName);
	// } else if (firstName.length() > 0) {
	// fullName.append(firstName);
	// } else if (lastName.length() > 0) {
	// fullName.append(lastName);
	// }
	// }
	// if (fullName.length() > 0) {
	// return fullName.toString();
	// } else {
	// return null;
	// }
	// }

	public String getCaFullName() {
		return caFullName;
	}

	public void setCaFullName() {
		/*
		 * StringBuffer fullName = new StringBuffer(); if (this.ca != null) {
		 * String firstName = (ca.getFirstName() == null) ? "" :
		 * ca.getFirstName(); String lastName = (ca.getLastName() == null) ? "" :
		 * ca.getLastName(); if (firstName.length() > 0 && lastName.length() >
		 * 0) { // fullName.append(firstName).append(" ").append(lastName);
		 * fullName.append(lastName).append(", ").append(firstName); } else if
		 * (firstName.length() > 0) { fullName.append(firstName); } else if
		 * (lastName.length() > 0) { fullName.append(lastName); } }
		 */
		String fullName = "";
		if (this.ca != null) {
			fullName = TextUtil.formatUserName(ca.getFirstName(), ca.getLastName());
		}
		if (fullName.length() > 0) {
			caFullName = fullName.toString();
		} else {
			caFullName = "";
		}
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getLegalId() {
		return legalId;
	}

	public void setLegalId(String legalId) {
		this.legalId = legalId;
	}

	public boolean isDoNotContact() {
		return "Y".equals(doNotContact);
	}

	public boolean isPoolProspectYN() {
		return "Y".equals(poolProspectYN);
	}

	public void setDoNotContact(String doNotContact) {
		// this.doNotContact = doNotContact;
		// Use the string from the shared string pool
		this.doNotContact = PoolUtil.getStringFromPool(doNotContact);
	}

	// public void setDoNotContact(String yn) {
	// setDoNotContact("Y".equals(yn));
	// }

	public void setPoolProspectYN(String poolProspectYN) {
		// this.poolProspectYN = poolProspectYN;
		// Use the string from the shared string pool
		this.poolProspectYN = PoolUtil.getYesNo(poolProspectYN);
	}

	// public void setPoolProspectYN(String yn) {
	// setPoolProspectYN("Y".equals(yn));
	// }

	public IProfileSO getProfile() {
		IProfileSO profile = new ProfileSO();
		profile.setFirstName(firstName);
		profile.setMiddleName(middleName);
		profile.setFamilyName(familyName);
		profile.setNickName(nickName);
		profile.setCa(ca);
		profile.setDomicile(domicile);
		profile.setNationality(nationality);
		profile.setPartnerId(partnerId.getPartnerId());
		profile.setStatus(status);
		profile.setPriority(priority);
		profile.setPotentialNNM(potentialNNM);
		// Added by Quick on 2006-03-28
		profile.setPotentialCCY(potentialCCY);
		profile.setExpiryDate(expiryDate);
		profile.setCategory(category);
		profile.setStorageLocation(storageLocation);
		profile.setOwner(owner);
		profile.setDoNotContact(Boolean.valueOf(doNotContact));
		profile.setKcm(Boolean.TRUE);

		profile.setClient3BO(client3BO);
		profile.setClientAH(clientAH);
		profile.setClientAS(clientAS);
		profile.setClientBO(clientBO);
		profile.setClientCFR(clientCFR);
		profile.setClientLPOA(clientLPOA);
		profile.setClientPOA(clientPOA);

		// add for 3.0
		profile.setClientBOPOA(getClientBOPOA());
		profile.setClientBOLPOA(getClientBOLPOA());
		profile.setClientBOAS(getClientBOAS());

		if (phone != null) {
			IPartnerAddressSO address = new PartnerAddressSO();
			address.setPhoneNo1(phone);
			profile.setPartnerAddress(address);
			// IAddressPhone contact = new AddressPhone();
			// contact.setPhone01(phone);
			// profile.setContact(contact);
		}
		return profile;
	}

	public IProspectProfileSO getProspectProfile() {
		IProspectProfileSO profile = new ProspectProfileSO();
		profile.setFirstName(firstName);
		profile.setMiddleName(middleName);
		profile.setFamilyName(familyName);
		profile.setNickName(nickName);
		profile.setCa(ca);
		profile.setDomicile(domicile);
		profile.setNationality(nationality);
		profile.setPartnerId(partnerId.getPartnerId());
		profile.setStatus(status);
		profile.setPriority(priority);
		profile.setPotentialNNM(potentialNNM);
		// Added by Quick on 2006-03-28
		profile.setPotentialCCY(potentialCCY);
		profile.setExpiryDate(expiryDate);
		profile.setCategory(category);
		profile.setStorageLocation(storageLocation);
		profile.setOwner(owner);
		profile.setDoNotContact(Boolean.valueOf(doNotContact));
		profile.setReferencePartnerID(this.referencePartnerID);
		profile.setVirtual(Boolean.valueOf(this.referencePartnerID != null));
		profile.setKcm(Boolean.TRUE);

		profile.setClient3BO(client3BO);
		profile.setClientAH(clientAH);
		profile.setClientAS(clientAS);
		profile.setClientBO(clientBO);
		profile.setClientCFR(clientCFR);
		profile.setClientLPOA(clientLPOA);
		profile.setClientPOA(clientPOA);

		// add for 3.0
		profile.setClientBOPOA(getClientBOPOA());
		profile.setClientBOLPOA(getClientBOLPOA());
		profile.setClientBOAS(getClientBOAS());

		if (phone != null) {
			IPartnerAddressSO address = new PartnerAddressSO();
			address.setPhoneNo1(phone);
			profile.setPartnerAddress(address);
			// IAddressPhone contact = new AddressPhone();
			// contact.setPhone01(phone);
			// profile.setContact(contact);
		}
		return profile;
	}

	// Added by Sunny Wu 2006-04-12
	public IClientProfileSO getClientProfile() {
		IClientProfileSO profile = new ClientProfileSO();
		profile.setFirstName(firstName);
		profile.setMiddleName(middleName);
		profile.setFamilyName(familyName);
		profile.setNickName(nickName);
		profile.setCa(ca);
		profile.setDomicile(domicile);
		profile.setNationality(nationality);
		profile.setPartnerId(partnerId.getPartnerId());
		profile.setStatus(status);
		profile.setPriority(priority);
		profile.setPotentialNNM(potentialNNM);
		// Added by Quick on 2006-03-28
		profile.setPotentialCCY(potentialCCY);
		profile.setExpiryDate(expiryDate);
		profile.setCategory(category);
		profile.setStorageLocation(storageLocation);
		profile.setOwner(owner);
		profile.setDoNotContact(Boolean.valueOf(doNotContact));
		profile.setKcm(Boolean.TRUE);
		profile.setClientRiskStatus(clientRiskStatus);
		profile.setClientRiskId(clientRiskId);

		profile.setClient3BO(client3BO);
		profile.setClientAH(clientAH);
		profile.setClientAS(clientAS);
		profile.setClientBO(clientBO);
		profile.setClientCFR(clientCFR);
		profile.setClientLPOA(clientLPOA);
		profile.setClientPOA(clientPOA);
		// add for 3.0
		profile.setClientBOPOA(getClientBOPOA());
		profile.setClientBOLPOA(getClientBOLPOA());
		profile.setClientBOAS(getClientBOAS());

		if (phone != null) {
			IPartnerAddressSO address = new PartnerAddressSO();
			address.setPhoneNo1(phone);
			profile.setPartnerAddress(address);
		}
		// Added By Roy chen on 2006-09-22
		profile.setKycPartner(kycPartner);
		return profile;
	}

	public String getName() {
		if (name == null)
			setName();
		return name;
	}

	public void setName() {
		final String SPACE = " ";
		StringBuffer fullName = new StringBuffer();
		String firstName = getFirstName();
		String lastName = getFamilyName();
		String middleName = getMiddleName();
		if (!isEmpty(lastName) && !isEmpty(firstName) && !isEmpty(middleName)) {
			fullName.append(lastName).append(", ").append(firstName).append(SPACE).append(middleName);
		} else if (!isEmpty(lastName) && !isEmpty(firstName)) {
			fullName.append(lastName).append(", ").append(firstName);
		} else if (!isEmpty(lastName) && !isEmpty(middleName)) {
			fullName.append(lastName).append(", ").append(middleName);
		} else if (!isEmpty(firstName)) {
			fullName.append(firstName);
		} else if (!isEmpty(lastName)) {
			fullName.append(lastName);
		}

		if (fullName.length() > 0) {
			name = fullName.toString();
		} else {
			name = "";
		}
	}

	// public String getName() {
	// final String SPACE = " ";
	// StringBuffer fullName = new StringBuffer();
	// String firstName = getFirstName();
	// String lastName = getFamilyName();
	// String middleName = getMiddleName();
	// if (!isEmpty(lastName) && !isEmpty(firstName) && !isEmpty(middleName)) {
	// fullName.append(lastName).append(",
	// ").append(firstName).append(SPACE).append(middleName);
	// } else if (!isEmpty(lastName) && !isEmpty(firstName)) {
	// fullName.append(lastName).append(", ").append(firstName);
	// } else if (!isEmpty(lastName) && !isEmpty(middleName)) {
	// fullName.append(lastName).append(", ").append(middleName);
	// } else if (!isEmpty(firstName)) {
	// fullName.append(firstName);
	// } else if (!isEmpty(lastName)) {
	// fullName.append(lastName);
	// }
	//
	// if (fullName.length() > 0) {
	// return fullName.toString();
	// } else {
	// return null;
	// }
	// }

	private boolean isEmpty(String str) {
		return str == null || str.length() == 0;
	}

	// added by jite 2006-04-03 for mapping search
	/**
	 * @return Returns the familyNameAfterTranslate.
	 */
	public String getFamilyNameAfterTranslate() {
		return familyNameAfterTranslate;
	}

	/**
	 * @param familyNameAfterTranslate
	 *            The familyNameAfterTranslate to set.
	 */
	public void setFamilyNameAfterTranslate(String familyNameAfterTranslate) {
		this.familyNameAfterTranslate = familyNameAfterTranslate;
	}

	/**
	 * @return Returns the firstNameAfterTranslate.
	 */
	public String getFirstNameAfterTranslate() {
		return firstNameAfterTranslate;
	}

	/**
	 * @param firstNameAfterTranslate
	 *            The firstNameAfterTranslate to set.
	 */
	public void setFirstNameAfterTranslate(String firstNameAfterTranslate) {
		this.firstNameAfterTranslate = firstNameAfterTranslate;
	}

	/**
	 * @return Returns the middleNameAfterTranslate.
	 */
	public String getMiddleNameAfterTranslate() {
		return middleNameAfterTranslate;
	}

	/**
	 * @param middleNameAfterTranslate
	 *            The middleNameAfterTranslate to set.
	 */
	public void setMiddleNameAfterTranslate(String middleNameAfterTranslate) {
		this.middleNameAfterTranslate = middleNameAfterTranslate;
	}

	/**
	 * @return Returns the nickNameAfterTranslate.
	 */
	public String getNickNameAfterTranslate() {
		return nickNameAfterTranslate;
	}

	/**
	 * @param nickNameAfterTranslate
	 *            The nickNameAfterTranslate to set.
	 */
	public void setNickNameAfterTranslate(String nickNameAfterTranslate) {
		this.nickNameAfterTranslate = nickNameAfterTranslate;
	}

	/**
	 * Added by Sunny Wu 2006-04-14
	 * 
	 * @return Returns the clientRiskId.
	 */
	public String getClientRiskId() {
		return clientRiskId;
	}

	/**
	 * Added by Sunny Wu 2006-04-14
	 * 
	 * @param clientRiskId
	 *            The clientRiskId to set.
	 */
	public void setClientRiskId(String clientRiskId) {
		this.clientRiskId = clientRiskId;
	}

	/**
	 * @return Returns the otherPartnerStatus.
	 */
	public String getOtherPartnerStatus() {
		return otherPartnerStatus;
	}

	/**
	 * @param otherPartnerStatus
	 *            The otherPartnerStatus to set.
	 */
	public void setOtherPartnerStatus(String otherPartnerStatus) {
		this.otherPartnerStatus = otherPartnerStatus;
	}

	/**
	 * @generated by CodeSugar http://sourceforge.net/projects/codesugar
	 */

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("[PartnerCacheEntrySO:");
		buffer.append(" firstName: ");
		buffer.append(firstName);
		buffer.append(" firstNameAfterTranslate: ");
		buffer.append(firstNameAfterTranslate);
		buffer.append(" middleName: ");
		buffer.append(middleName);
		buffer.append(" middleNameAfterTranslate: ");
		buffer.append(middleNameAfterTranslate);
		buffer.append(" familyName: ");
		buffer.append(familyName);
		buffer.append(" familyNameAfterTranslate: ");
		buffer.append(familyNameAfterTranslate);
		buffer.append(" nickName: ");
		buffer.append(nickName);
		buffer.append(" nickNameAfterTranslate: ");
		buffer.append(nickNameAfterTranslate);
		buffer.append(" phone: ");
		buffer.append(phone);
		buffer.append(" ca: ");
		buffer.append(ca);
		buffer.append(" domicile: ");
		buffer.append(domicile);
		buffer.append(" nationality: ");
		buffer.append(nationality);
		buffer.append(" partnerId: ");
		buffer.append(partnerId.getPartnerId());
		buffer.append(" status: ");
		buffer.append(status);
		buffer.append(" otherPartnerStatus: ");
		buffer.append(otherPartnerStatus);
		buffer.append(" priority: ");
		buffer.append(priority);
		buffer.append(" potentialNNM: ");
		buffer.append(potentialNNM);
		buffer.append(" potentialCCY: ");
		buffer.append(potentialCCY);
		buffer.append(" expiryDate: ");
		buffer.append(expiryDate);
		buffer.append(" category: ");
		buffer.append(category);
		buffer.append(" storageLocation: ");
		buffer.append(storageLocation);
		buffer.append(" owner: ");
		buffer.append(owner);
		buffer.append(" legalId: ");
		buffer.append(legalId);
		buffer.append(" poolProspectYN: ");
		buffer.append(poolProspectYN);
		buffer.append(" doNotContact: ");
		buffer.append(doNotContact);
		buffer.append(" referencePartnerID: ");
		buffer.append(referencePartnerID);
		buffer.append(" clientRiskStatus: ");
		buffer.append(clientRiskStatus);
		buffer.append(" clientRiskId: ");
		buffer.append(clientRiskId);
		buffer.append(" name: ");
		buffer.append(name);
		buffer.append(" caFullName: ");
		buffer.append(caFullName);
		buffer.append(" kycPartner: ");
		buffer.append(kycPartner);
		buffer.append(" kycReportableYN: ");
		buffer.append(kycReportableYN);
		buffer.append(" kcmYN: ");
		buffer.append(kcmYN);

		buffer.append(" clientAH: ");
		buffer.append(clientAH);
		buffer.append(" clientBO: ");
		buffer.append(clientBO);
		buffer.append(" clientPOA: ");
		buffer.append(clientPOA);
		buffer.append(" clientLPOA: ");
		buffer.append(clientLPOA);
		buffer.append(" clientAS: ");
		buffer.append(clientAS);
		// add for 3.0
		buffer.append(" clientBOPOA: ");
		buffer.append(clientBOPOA);
		buffer.append(" clientBOLPOA: ");
		buffer.append(clientBOLPOA);
		buffer.append(" clientBOAS: ");
		buffer.append(clientBOAS);
		buffer.append(" clientCFR: ");
		buffer.append(clientCFR);
		buffer.append(" client3BO: ");
		buffer.append(client3BO);

		buffer.append(" virtual:");
		buffer.append(isVirtual());
		buffer.append("]");
		return buffer.toString();
	}

	public String getClientBOAS() {
		return clientBOAS;
	}

	public void setClientBOAS(String clientBOAS) {
		this.clientBOAS = clientBOAS;
	}

	public String getClientBOLPOA() {
		return clientBOLPOA;
	}

	public void setClientBOLPOA(String clientBOLPOA) {
		this.clientBOLPOA = clientBOLPOA;
	}

	public String getClientBOPOA() {
		return clientBOPOA;
	}

	public void setClientBOPOA(String clientBOPOA) {
		this.clientBOPOA = clientBOPOA;
	}

	public boolean isOnlyBOSMRole() {
		int index = 0;		//make sure the ONLY BOSM role, with index counting it.		
		index = ICRMConstant.TRUE.equals(getClientBOPOA())? index+1:index;
		index = ICRMConstant.TRUE.equals(getClientBOLPOA())? index+1:index;
		index = ICRMConstant.TRUE.equals(getClientBOAS())? index+1:index;
				
		if(index==1 && (ICRMConstant.TRUE.equals(getClientAH()) || ICRMConstant.TRUE.equals(getClient3BO()) 
				|| ICRMConstant.TRUE.equals(getClientAS()) || ICRMConstant.TRUE.equals(getClientBO()) 
				|| ICRMConstant.TRUE.equals(getClientLPOA()) || ICRMConstant.TRUE.equals(getClientPOA())))
			index++;
	
		return index==1;
	}

	public String getCPACRole() {
		boolean hasASRole = false;
		
		StringBuffer buffer = new StringBuffer();
		if (ICRMConstant.TRUE.equals(getClientAH())) {
			buffer.append(ICRMConstant.PARTNER_ROLE_AH).append(ICRMConstant.SEMICOLON);
		}
		if (ICRMConstant.TRUE.equals(getClientBO())) {
			
			if (buffer.indexOf(ICRMConstant.PARTNER_ROLE_AH) == -1) {
				buffer.append(ICRMConstant.PARTNER_ROLE_AH).append(ICRMConstant.SEMICOLON);
			}
			buffer.append(ICRMConstant.PARTNER_ROLE_BO).append(ICRMConstant.SEMICOLON);
		}
		if (ICRMConstant.TRUE.equals(getClientPOA())) {
			buffer.append(ICRMConstant.CPAC_PARTNER_ROLE_POA).append(ICRMConstant.SEMICOLON);
		}
		if (ICRMConstant.TRUE.equals(getClientLPOA())) {
			buffer.append(ICRMConstant.CPAC_PARTNER_ROLE_LPOA).append(ICRMConstant.SEMICOLON);
		}
		if (ICRMConstant.TRUE.equals(getClientAS())) {
			buffer.append(ICRMConstant.PARTNER_ROLE_AS).append(ICRMConstant.SEMICOLON);
			hasASRole = true;
		}
		if (ICRMConstant.TRUE.equals(getClientBOPOA())) {
			buffer.append(ICRMConstant.PARTNER_ROLE_BO_POA).append(ICRMConstant.SEMICOLON);

			if (buffer.indexOf((ICRMConstant.PARTNER_ROLE_BO + ICRMConstant.SEMICOLON)) == -1)
				buffer.append(ICRMConstant.PARTNER_ROLE_BO).append(ICRMConstant.SEMICOLON);
			if (buffer.indexOf(ICRMConstant.CPAC_PARTNER_ROLE_POA) == -1)
				buffer.append(ICRMConstant.CPAC_PARTNER_ROLE_POA).append(ICRMConstant.SEMICOLON);
		}
		if (ICRMConstant.TRUE.equals(getClientBOLPOA())) {
			buffer.append(ICRMConstant.PARTNER_ROLE_BO_LPOA).append(ICRMConstant.SEMICOLON);
			
			if (buffer.indexOf((ICRMConstant.PARTNER_ROLE_BO + ICRMConstant.SEMICOLON)) == -1)
				buffer.append(ICRMConstant.PARTNER_ROLE_BO).append(ICRMConstant.SEMICOLON);
			if (buffer.indexOf(ICRMConstant.CPAC_PARTNER_ROLE_LPOA) == -1)
				buffer.append(ICRMConstant.CPAC_PARTNER_ROLE_LPOA).append(ICRMConstant.SEMICOLON);
		}
		if (ICRMConstant.TRUE.equals(getClientBOAS())) {
			buffer.append(ICRMConstant.PARTNER_ROLE_BO_AS).append(ICRMConstant.SEMICOLON);
			
			if (buffer.indexOf((ICRMConstant.PARTNER_ROLE_BO + ICRMConstant.SEMICOLON)) == -1)
				buffer.append(ICRMConstant.PARTNER_ROLE_BO).append(ICRMConstant.SEMICOLON);
			if (!hasASRole)
				buffer.append(ICRMConstant.PARTNER_ROLE_AS).append(ICRMConstant.SEMICOLON);
		}
		if (ICRMConstant.TRUE.equals(getClient3BO())) {
			if (buffer.indexOf((ICRMConstant.PARTNER_ROLE_BO + ICRMConstant.SEMICOLON)) == -1)
				buffer.append(ICRMConstant.PARTNER_ROLE_BO).append(ICRMConstant.SEMICOLON);
			if (buffer.indexOf(ICRMConstant.PARTNER_ROLE_3BO) == -1) {
				buffer.append(ICRMConstant.PARTNER_ROLE_3BO).append(ICRMConstant.SEMICOLON);
			}
			
		}if (isOnlyBOSMRole()) {
			if (buffer.indexOf(ICRMConstant.PARTNER_ROLE_BOSM) == -1) {
				buffer.append(ICRMConstant.PARTNER_ROLE_BOSM).append(ICRMConstant.SEMICOLON);
			}
		}
		
		if (buffer.length() > 1) {
			return buffer.substring(0, buffer.length() - 1);
		}
		return buffer.toString();
	}
	
	/* add by John Zhu for PDE2 De-duplication */
	protected String PDEUniqueYN = null;
	protected Date dateOfBirth = null;
	protected String PDEConfirmedGroupId = null;
	protected String PDEConfirmedMainPtnerId = null;
	protected String PDERecomdGroupId = null;
	protected String PDERecomdMainPtnerId = null;
	protected String PDERecomdRankStr = null;
	protected String PDEActivePtflCountStr = null;
	protected String PDEOpenZRHPtflCount = null;
	
	/* add by John Zhu for PDE2 June Release */
	protected String PDECAStatus = null;
	protected String PDECAStatusModifier = null;
	protected Timestamp PDECAStatusModifyTime = null;
	protected String PDEWMOStatus = null;
	protected String PDEWMOStatusModifier = null;
	protected Timestamp PDEWMOStatusModifyTime = null;
	protected String PDEAssignBy = null;
	protected String PDEEliminateStatus = null;
	
	/* add by John Zhu for PDE2 August Release */
	protected Timestamp PDEAssignOn = null;

	/**
	 * @return the pDEUniqueYN
	 */
	public String getPDEUniqueYN() {
		return PDEUniqueYN;
	}

	/**
	 * @param uniqueYN the pDEUniqueYN to set
	 */
	public void setPDEUniqueYN(String uniqueYN) {
		PDEUniqueYN = uniqueYN;
	}

	/**
	 * @return the pDEActivePtflCount
	 */
	public int getPDEActivePtflCount() {
		return this.string2Int(this.PDEActivePtflCountStr);
	}
	
	/**
	 * @param in
	 * @return
	 */
	protected int string2Int(String in) {
		int def = 0;
		if (in==null||in.trim().length()==0) return def;
		try {
			return Integer.parseInt(in);
		} catch (NumberFormatException ex) {
			return def;
		}
	}

	/**
	 * @param activePtflCount the pDEActivePtflCount to set
	 */
	public void setPDEActivePtflCount(int activePtflCount) {
		this.PDEActivePtflCountStr = String.valueOf(activePtflCount);
	}

	/**
	 * @return the dateOfBirth
	 */
	public Date getDateOfBirth() {
		return dateOfBirth;
	}

	/**
	 * @param dateOfBirth the dateOfBirth to set
	 */
	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	/**
	 * @return the pDEActivePtflCountStr
	 */
	public String getPDEActivePtflCountStr() {
		return PDEActivePtflCountStr;
	}

	/**
	 * @param activePtflCountStr the pDEActivePtflCountStr to set
	 */
	public void setPDEActivePtflCountStr(String activePtflCountStr) {
		PDEActivePtflCountStr = activePtflCountStr;
	}

	/**
	 * @return the pDEConfirmedGroupId
	 */
	public String getPDEConfirmedGroupId() {
		return PDEConfirmedGroupId;
	}

	/**
	 * @param confirmedGroupId the pDEConfirmedGroupId to set
	 */
	public void setPDEConfirmedGroupId(String confirmedGroupId) {
		PDEConfirmedGroupId = confirmedGroupId;
	}

	/**
	 * @return the pDEConfirmedMainPtnerId
	 */
	public String getPDEConfirmedMainPtnerId() {
		return PDEConfirmedMainPtnerId;
	}

	/**
	 * @param confirmedMainPtnerId the pDEConfirmedMainPtnerId to set
	 */
	public void setPDEConfirmedMainPtnerId(String confirmedMainPtnerId) {
		PDEConfirmedMainPtnerId = confirmedMainPtnerId;
	}

	/**
	 * @return the pDERecomdGroupId
	 */
	public String getPDERecomdGroupId() {
		return PDERecomdGroupId;
	}

	/**
	 * @param recomdGroupId the pDERecomdGroupId to set
	 */
	public void setPDERecomdGroupId(String recomdGroupId) {
		PDERecomdGroupId = recomdGroupId;
	}

	/**
	 * @return the pDERecomdMainPtnerId
	 */
	public String getPDERecomdMainPtnerId() {
		return PDERecomdMainPtnerId;
	}

	/**
	 * @param recomdMainPtnerId the pDERecomdMainPtnerId to set
	 */
	public void setPDERecomdMainPtnerId(String recomdMainPtnerId) {
		PDERecomdMainPtnerId = recomdMainPtnerId;
	}

	/**
	 * @return the pDERecomdRankStr
	 */
	public String getPDERecomdRankStr() {
		return PDERecomdRankStr;
	}

	/**
	 * @param recomdRankStr the pDERecomdRankStr to set
	 */
	public void setPDERecomdRankStr(String recomdRankStr) {
		PDERecomdRankStr = recomdRankStr;
	}

	/* (non-Javadoc)
	 * @see com.ubs.ifop.application.icrm.partner.intf.so.IPartnerCacheEntrySO#getPDERecomdRank()
	 */
	public int getPDERecomdRank() {
		return this.string2Int(this.PDERecomdRankStr);
	}

	/* (non-Javadoc)
	 * @see com.ubs.ifop.application.icrm.partner.intf.so.IPartnerCacheEntrySO#setPDERecomdRank(int)
	 */
	public void setPDERecomdRank(int recomdRankStr) {
		this.PDERecomdRankStr = String.valueOf(recomdRankStr);
	}

	/**
	 * @return the pDEOpenZRHPtflCount
	 */
	public int getPDEOpenZRHPtflCount() {
		return this.string2Int(PDEOpenZRHPtflCount);
	}

	/**
	 * @param openZRHPtflCount the pDEOpenZRHPtflCount to set
	 */
	public void setPDEOpenZRHPtflCount(String openZRHPtflCount) {
		PDEOpenZRHPtflCount = openZRHPtflCount;
	}

	/**
	 * @return the pDECAStatus
	 */
	public String getPDECAStatus() {
		return PDECAStatus;
	}

	/**
	 * @param status the pDECAStatus to set
	 */
	public void setPDECAStatus(String status) {
		PDECAStatus = status;
	}

	/**
	 * @return the pDECAStatusModifier
	 */
	public String getPDECAStatusModifier() {
		return PDECAStatusModifier;
	}

	/**
	 * @param statusModifier the pDECAStatusModifier to set
	 */
	public void setPDECAStatusModifier(String statusModifier) {
		PDECAStatusModifier = statusModifier;
	}

	/**
	 * @return the pDECAStatusModifyTime
	 */
	public Timestamp getPDECAStatusModifyTime() {
		return PDECAStatusModifyTime;
	}

	/**
	 * @param statusModifyTime the pDECAStatusModifyTime to set
	 */
	public void setPDECAStatusModifyTime(Timestamp statusModifyTime) {
		PDECAStatusModifyTime = statusModifyTime;
	}

	/**
	 * @return the pDEWMOStatus
	 */
	public String getPDEWMOStatus() {
		return PDEWMOStatus;
	}

	/**
	 * @param status the pDEWMOStatus to set
	 */
	public void setPDEWMOStatus(String status) {
		PDEWMOStatus = status;
	}

	/**
	 * @return the pDEWMOStatusModifier
	 */
	public String getPDEWMOStatusModifier() {
		return PDEWMOStatusModifier;
	}

	/**
	 * @param statusModifier the pDEWMOStatusModifier to set
	 */
	public void setPDEWMOStatusModifier(String statusModifier) {
		PDEWMOStatusModifier = statusModifier;
	}

	/**
	 * @return the pDEWMOStatusModifyTime
	 */
	public Timestamp getPDEWMOStatusModifyTime() {
		return PDEWMOStatusModifyTime;
	}

	/**
	 * @param statusModifyTime the pDEWMOStatusModifyTime to set
	 */
	public void setPDEWMOStatusModifyTime(Timestamp statusModifyTime) {
		PDEWMOStatusModifyTime = statusModifyTime;
	}

	/**
	 * @return the pDEAssignBy
	 */
	public String getPDEAssignBy() {
		return PDEAssignBy;
	}

	/**
	 * @param assignBy the pDEAssignBy to set
	 */
	public void setPDEAssignBy(String assignBy) {
		PDEAssignBy = assignBy;
	}

	/**
	 * @return the pDEEliminateStatus
	 */
	public String getPDEEliminateStatus() {
		return PDEEliminateStatus;
	}

	/**
	 * @param eliminateStatus the pDEEliminateStatus to set
	 */
	public void setPDEEliminateStatus(String eliminateStatus) {
		PDEEliminateStatus = eliminateStatus;
	}

	/**
	 * @return the pDEAssignOn
	 */
	public Timestamp getPDEAssignOn() {
		return PDEAssignOn;
	}

	/**
	 * @param assignOn the pDEAssignOn to set
	 */
	public void setPDEAssignOn(Timestamp assignOn) {
		PDEAssignOn = assignOn;
	}

	
}
