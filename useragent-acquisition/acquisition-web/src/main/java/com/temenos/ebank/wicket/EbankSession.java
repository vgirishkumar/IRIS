package com.temenos.ebank.wicket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.Request;
import org.apache.wicket.Session;
import org.apache.wicket.protocol.http.RequestLogger.ISessionLogInfo;
import org.apache.wicket.protocol.http.WebSession;

import com.temenos.ebank.domain.Application;

/**
 * Client aquisition's custom {@link WebSession}
 * @author vionescu
 */
@SuppressWarnings({"unchecked","rawtypes"})
public class EbankSession extends WebSession implements ISessionLogInfo {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static EbankSession get() {
		return (EbankSession) Session.get();
	}
	
	private List submittedAppRefs;
	
	private Application clientAquisitionApplication;

	private Map trackingParameters;

	public Application getClientAquisitionApplication() {
		return clientAquisitionApplication;
	}

	public void setClientAquisitionApplication(Application clientAquisitionApplication) {
		this.clientAquisitionApplication = clientAquisitionApplication;
		// TODO: it seems that dirty is needed for cluster deployment. What to do when application changes?
		// I guess a call to this method every time should do the trick
		dirty();
	}

	public EbankSession(Request request) {
		super(request);
		// Support for changing locale does not exist. It would be a change request.
		setLocale(Locale.ENGLISH);
		submittedAppRefs = new ArrayList();
		trackingParameters = new HashMap();
	}

	/**
	 * Provides a string representation of this session. This info is used by the request logger
	 * 
	 * @see org.apache.wicket.protocol.http.RequestLogger.ISessionLogInfo#getSessionInfo()
	 */
	public Object getSessionInfo() {
		if (clientAquisitionApplication != null) {
			StringBuilder sessInfo = new StringBuilder();
			String appref = clientAquisitionApplication.getAppRef();
			if (StringUtils.isNotBlank(appref)) {
				sessInfo.append("appref:").append(appref);
			}
			if (clientAquisitionApplication.getCustomer() != null) {
				String emailAddress = clientAquisitionApplication.getCustomer().getEmailAddress();
				if (StringUtils.isNotBlank(emailAddress)) {
					if (sessInfo.length() > 0) {
						sessInfo.append(';');
					}
					sessInfo.append("emailAddress:").append(emailAddress);
				}
			}
			return sessInfo.toString();
		}
		return null;
	}

	public Object getParameter(Object key) {
		return trackingParameters.get(key);
	}
	
	public Object putParameter(Object key, Object value) {
		dirty();
		return trackingParameters.put(key, value);
	}

	public boolean isSubmittedAppRef(String appRef) {
		if (submittedAppRefs == null) {
			return false;
		}
		return (submittedAppRefs.indexOf(appRef) > -1);
	}

	public void addAppRefToSubmittedList(String appRef) {
		submittedAppRefs.add(appRef);
	}
}
