package com.temenos.messagingLayer.securityContext;

import com.temenos.messagingLayer.pojo.ObjectFactory;
import com.temenos.messagingLayer.pojo.Ofsml13NillableToken;
import com.temenos.messagingLayer.pojo.Ofsml13SecurityContext;

/**
 * Sets the user name and password.
 * @author anitha
 *
 */
public class SecurityContext
{
    private String userName;
    private String password;

	public void setUserName(String userName) {
	this.userName = userName;
	}
	public void setPassword(String password)
	{
		this.password = password;
	}
	public String getUserName() {
		return userName;
		}
	public String getPassword()
	{
			return password;
	}
	
	// set the user options like user name,password and company
	public  Ofsml13SecurityContext generateSecurityContext(String userName, String password, String language) {
		Ofsml13SecurityContext secContext = new Ofsml13SecurityContext();
		Ofsml13NillableToken nillTokenUser = setUserdetails(userName);
		secContext.getContent().add(
				(new ObjectFactory())
						.createOfsml13SecurityContextUserName(nillTokenUser));
		Ofsml13NillableToken nillToken = setUserdetails(password);
		secContext.getContent().add(
				(new ObjectFactory())
						.createOfsml13SecurityContextPassword(nillToken));
		Ofsml13NillableToken nillTokenLang = setUserdetails(language);
		// language is passed in company code  of user options
		if(language != null)
		{
			secContext.getContent().add((new ObjectFactory()).createOfsml13SecurityContextCompany(nillTokenLang));
		}
		return secContext;
	}
	protected Ofsml13NillableToken setUserdetails(String userdetail) {
		Ofsml13NillableToken nillToken = new Ofsml13NillableToken();
		nillToken.setValue(userdetail);
		return nillToken;
	}
}