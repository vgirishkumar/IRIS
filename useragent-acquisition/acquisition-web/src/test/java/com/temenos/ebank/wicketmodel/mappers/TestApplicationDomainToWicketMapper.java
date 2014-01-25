/**
 * 
 */
package com.temenos.ebank.wicketmodel.mappers;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.springframework.beans.BeanUtils;

import com.temenos.ebank.domain.Address;
import com.temenos.ebank.domain.Application;
import com.temenos.ebank.domain.Customer;
import com.temenos.ebank.domain.PreviousAddress;
import com.temenos.ebank.wicketmodel.AddressWicketModelObject;
import com.temenos.ebank.wicketmodel.ApplicationWicketModelObject;
import com.temenos.ebank.wicketmodel.CustomerWicketModel;
import com.temenos.ebank.wicketmodel.PreviousAddressWicketModelObject;

/**
 * @author vionescu
 * 
 */
public class TestApplicationDomainToWicketMapper {

	@Test
	public void testSimpleApplicationCurrentAccountCurrencies() {
		ApplicationDomainToWicketMapper mapper = new ApplicationDomainToWicketMapper();
		// Simple test, no customer or other components of Application
		Application a = new Application();
		a.setAccountCurrency("GBP");
		a.setAppId(10L);
		ApplicationWicketModelObject awm = mapper.domain2Wicket(a);
		assertEquals(a.getAccountCurrency(), awm.getAccountCurrency());
		assertEquals(a.getAppId(), awm.getAppId());
		assertNull(awm.getCustomer());
		assertNull(awm.getSecondCustomer());
		assertEquals(awm.getAccountCurrencies().size(), 1);
		// now, test that the app is the same when translated back
		Application a2 = new Application();
		mapper.wicket2Domain(awm, a2);
		assertEquals(a, a2);
		assertEquals(a.getAccountCurrency(), a2.getAccountCurrency());
		assertEquals(a.getAppId(), a2.getAppId());
		assertEquals(a.getCustomer(), a2.getCustomer());
		assertEquals(a.getSecondCustomer(), a2.getSecondCustomer());

		a.setAccountCurrencies(Arrays.asList(new String[] {"GBP", "EUR"}));

		awm = mapper.domain2Wicket(a);
		assertEquals(awm.getAccountCurrencies().size(), 2);

		a2 = new Application();
		mapper.wicket2Domain(awm, a2);

		assertEquals(a2.getAccountCurrencies().get(0), "GBP");
		assertEquals(a2.getAccountCurrencies().get(1), "EUR");

		// add a new currency to the model
		awm.getAccountCurrencies().add("RON");
		mapper.wicket2Domain(awm, a);
		assertEquals(a.getAccountCurrencies().get(0), "GBP");
		assertEquals(a.getAccountCurrencies().get(1), "EUR");
		assertEquals(a.getAccountCurrencies().get(2), "RON");
		
		// delete an existing currency
		awm.getAccountCurrencies().remove(0);
		mapper.wicket2Domain(awm, a);
		assertEquals(a.getAccountCurrencies().size(), 2);		
		assertEquals(a.getAccountCurrencies().get(0), "EUR");
		assertEquals(a.getAccountCurrencies().get(1), "RON");
	}
	

	@Test
	public void testComposedFields() {
		//tests how composed fields behave
		//this test was made because there was a bug for composed fields where the two fields might hold different values
		//that bug was due to the fact that there were two fields holding state and when updating the list the string field 
		//didn't get updated, so the string field was removed from the model
		ApplicationDomainToWicketMapper mapper = new ApplicationDomainToWicketMapper();
		mapper.setIncludeCustomer(true);
		Application a = new Application();
		a.setAppId(10L);
		a.setActivitiesOriginDeposit(Arrays.asList(new String[] {"SOLICITOR"}));
		a.setActivitiesWealth(Arrays.asList(new String[] {"LIFETIME_SAVINGS"}));
		Customer c = new Customer();
		c.setCustId(1L);
		a.setCustomer(c);
		c.setMarketingContactMethods(new ArrayList<String>(Arrays.asList((new String[] {"PHONE"}))));
		
		ApplicationWicketModelObject awm = mapper.domain2Wicket(a);
		assertEquals(awm.getActivitiesOriginDeposit().size(), 1);
		assertEquals(awm.getActivitiesWealth().size(), 1);
		assertEquals(awm.getCustomer().getMarketingContactMethods().size(), 1);
		awm.getActivitiesOriginDeposit().add("OWN_ACCOUNT");
		awm.getActivitiesWealth().add("BUSINESS_MANAGEMENT");
		awm.getCustomer().getMarketingContactMethods().add("EMAIL");
		
		mapper.wicket2Domain(awm, a);
		assertEquals(a.getActivitiesOriginDeposit().size(), 2);
		assertEquals(a.getActivitiesWealth().size(), 2);
		assertEquals(a.getCustomer().getMarketingContactMethods().size(), 2);
		
		awm.getActivitiesOriginDeposit().remove(0);
		awm.getActivitiesWealth().remove(0);
		awm.getCustomer().getMarketingContactMethods().remove(0);
		mapper.wicket2Domain(awm, a);
		assertEquals(a.getActivitiesOriginDeposit().size(), 1);
		assertEquals(a.getActivitiesOriginDeposit().get(0), "OWN_ACCOUNT");
		assertEquals(awm.getActivitiesWealth().size(), 1);
		assertEquals(a.getActivitiesWealth().get(0), "BUSINESS_MANAGEMENT");
		assertEquals(a.getCustomer().getMarketingContactMethods().get(0), "EMAIL");
	}	

	@Test
	public void testApplicationWithCustomer() {
		ApplicationDomainToWicketMapper mapper = new ApplicationDomainToWicketMapper();
		Application a = new Application();
		a.setAccountCurrency("GBP");
		a.setAppId(10L);
		Customer c = new Customer();
		c.setAppId(a.getAppId());
		c.setCountryOfBirth("UK");
		c.setCustId(5L);
		a.setCustomer(c);

		ApplicationWicketModelObject awm = mapper.domain2Wicket(a);
		assertEquals(a.getAccountCurrency(), awm.getAccountCurrency());
		assertEquals(a.getAppId(), awm.getAppId());
		assertNull(awm.getCustomer());
		assertNull(awm.getSecondCustomer());
		assertEquals(awm.getAccountCurrencies().size(), 1);
		// no, consider only customer
		mapper.setIncludeCustomer(true);
		awm = mapper.domain2Wicket(a);
		assertEquals(a.getAccountCurrency(), awm.getAccountCurrency());
		assertEquals(a.getAppId(), awm.getAppId());
		assertNotNull(awm.getCustomer());
		assertEquals(awm.getCustomer().getCountryOfBirth(), c.getCountryOfBirth());
		assertEquals(awm.getCustomer().getAppId(), c.getAppId());
		assertEquals(awm.getCustomer().getCustId(), c.getCustId());

		assertNotNull(awm.getSecondCustomer());
		assertTrue(awm.getSecondCustomer().isEmpty());
		// now, test that the app is the same when translated back
		Application a2 = new Application();
		mapper.wicket2Domain(awm, a2);
		assertEquals(a, a2);
		assertEquals(a.getAccountCurrency(), a2.getAccountCurrency());
		assertEquals(a.getAppId(), a2.getAppId());
		// TODO: de vazut daca am o cheie naturala in Customer ca sa pot
		// implementa equals
		// assertEquals(a.getCustomer(), a2.getCustomer());
		// assertEquals(a.getSecondCustomer(), a2.getSecondCustomer());
		assertEquals(a.getCustomer().getAppId(), a2.getCustomer().getAppId());
		assertEquals(a.getCustomer().getCustId(), a2.getCustomer().getCustId());
		assertNull(a2.getCustomer().getCorrespondenceAddress());
		assertNull(a2.getCustomer().getResidentialAddress());
		assertArrayEquals(a.getCustomer().getPreviousAddresses(), a2.getCustomer().getPreviousAddresses());
	}

	@Test
	public void testApplicationWithAddresses() {
		ApplicationDomainToWicketMapper mapper = new ApplicationDomainToWicketMapper();
		mapper.setIncludeCustomer(true);
		mapper.setIncludeContactDetails(true);
		Application a = new Application();
		a.setAccountCurrency("GBP");
		a.setAppId(10L);
		Customer c = new Customer();
		c.setAppId(a.getAppId());
		c.setCountryOfBirth("UK");
		c.setCustId(5L);
		c.setResidentialStatus("OWNER_MORTGAGE");
		a.setCustomer(c);

		Address residentialAddress = new Address();
		residentialAddress.setAdrId(33L);
		residentialAddress.setCountry("UK");
		residentialAddress.setLine1("Sheffield, Crucible theathre");
		c.setResidentialAddress(residentialAddress);

		Address correspAddress = new Address();
		correspAddress.setAdrId(10L);
		correspAddress.setCountry("FR");
		correspAddress.setLine1("Paris, brioche a la madeleine");
		c.setCorrespondenceAddress(correspAddress);

		c.setIsCorrespondenceAddressSameAsResidential(false);

		PreviousAddress pa1 = new PreviousAddress();
		pa1.setAdrId(1L);
		pa1.setCountry("RO");
		pa1.setCustomer(c);
		pa1.setDuration(1);
		pa1.setLine1("Sema parc curtiard");
		pa1.setTown("Bucharest");

		PreviousAddress pa2 = new PreviousAddress();
		pa2.setAdrId(2L);
		pa2.setCountry("BG");
		pa2.setCustomer(c);
		pa2.setDuration(2);
		pa2.setLine1("Velikoto Plevnovo");
		pa2.setTown("Varna");
		c.setPreviousAddresses(new PreviousAddress[] { pa1, pa2 });

		// no address included by mapper
		ApplicationWicketModelObject awm = mapper.domain2Wicket(a);
		assertEquals(a.getAccountCurrency(), awm.getAccountCurrency());
		assertEquals(a.getAppId(), awm.getAppId());
		assertNotNull(awm.getCustomer());
		assertEquals(awm.getCustomer().getCountryOfBirth(), c.getCountryOfBirth());
		assertEquals(awm.getCustomer().getAppId(), c.getAppId());
		assertEquals(awm.getCustomer().getCustId(), c.getCustId());

		assertTrue(!awm.getContactDetails().getCorrespondenceAddress().isEmpty());
		assertTrue(!awm.getContactDetails().getAddress().isEmpty());
		assertTrue(!awm.getContactDetails().getPreviousAddresses().isEmpty());
		
		assertFalse(awm.getContactDetails().getIsCorrespondenceAddressSameAsResidential());

		assertEquals(awm.getContactDetails().getCorrespondenceAddress().getLine1(), c.getCorrespondenceAddress()
				.getLine1());
		assertEquals(awm.getContactDetails().getCorrespondenceAddress().getCountry(), c.getCorrespondenceAddress()
				.getCountry());

		assertEquals(awm.getContactDetails().getAddress().getLine1(), c.getResidentialAddress().getLine1());
		assertEquals(awm.getContactDetails().getAddress().getCountry(), c.getResidentialAddress().getCountry());

		assertEquals(awm.getContactDetails().getPreviousAddresses().size(), 2);
		assertEquals(awm.getContactDetails().getPreviousAddresses().get(0).getLine1(),
				c.getPreviousAddresses()[0].getLine1());
		assertEquals(awm.getContactDetails().getPreviousAddresses().get(0).getCountry(),
				c.getPreviousAddresses()[0].getCountry());

		// now, test that the app is the same when translated back
		Application a2 = new Application();
		mapper.wicket2Domain(awm, a2);
		assertEquals(a, a2);
		assertEquals(a.getCustomer().getCorrespondenceAddress().getAdrId(), a2.getCustomer().getCorrespondenceAddress()
				.getAdrId());
		assertEquals(a.getCustomer().getCorrespondenceAddress().getCountry(), a2.getCustomer()
				.getCorrespondenceAddress().getCountry());
		assertEquals(a.getCustomer().getCorrespondenceAddress().getLine1(), a2.getCustomer().getCorrespondenceAddress()
				.getLine1());

		assertEquals(a.getCustomer().getResidentialAddress().getAdrId(), a2.getCustomer().getResidentialAddress()
				.getAdrId());
		assertEquals(a.getCustomer().getResidentialAddress().getCountry(), a2.getCustomer().getResidentialAddress()
				.getCountry());
		assertEquals(a.getCustomer().getResidentialAddress().getLine1(), a2.getCustomer().getResidentialAddress()
				.getLine1());

		assertEquals(a.getCustomer().getPreviousAddresses()[0].getAdrId(),
				a2.getCustomer().getPreviousAddresses()[0].getAdrId());
		assertEquals(a.getCustomer().getPreviousAddresses()[0].getCountry(),
				a2.getCustomer().getPreviousAddresses()[0].getCountry());
		assertEquals(a.getCustomer().getPreviousAddresses()[0].getLine1(),
				a2.getCustomer().getPreviousAddresses()[0].getLine1());
	}

	@Test
	public void testApplicationCorrespondenceAddressSameAsResidential(){

		ApplicationDomainToWicketMapper mapper = new ApplicationDomainToWicketMapper();
		mapper.setIncludeCustomer(true);
		mapper.setIncludeContactDetails(true);
		Application a = new Application();
		a.setAccountCurrency("GBP");
		a.setAppId(10L);
		Customer c = new Customer();
		c.setAppId(a.getAppId());
		c.setCountryOfBirth("UK");
		c.setCustId(5L);
		c.setResidentialStatus("OWNER_MORTGAGE");
		a.setCustomer(c);

		Address residentialAddress = new Address();
		residentialAddress.setAdrId(33L);
		residentialAddress.setCountry("UK");
		residentialAddress.setLine1("Sheffield, Crucible theathre");
		c.setResidentialAddress(residentialAddress);

		c.setIsCorrespondenceAddressSameAsResidential(true);
		
		c.setCorrespondenceAddress(residentialAddress);

		// no address included by mapper
		ApplicationWicketModelObject awm = mapper.domain2Wicket(a);
		
		assertNotNull(awm.getCustomer());
		assertTrue(!awm.getContactDetails().getCorrespondenceAddress().isEmpty());
		assertTrue(!awm.getContactDetails().getAddress().isEmpty());
		
		assertTrue(awm.getContactDetails().getIsCorrespondenceAddressSameAsResidential());
/*
		assertEquals(awm.getContactDetails().getCorrespondenceAddress().getLine1(), c.getCorrespondenceAddress()
				.getLine1());
		assertEquals(awm.getContactDetails().getCorrespondenceAddress().getCountry(), c.getCorrespondenceAddress()
				.getCountry());*/

		assertEquals(awm.getContactDetails().getAddress().getLine1(), c.getResidentialAddress().getLine1());
		assertEquals(awm.getContactDetails().getAddress().getCountry(), c.getResidentialAddress().getCountry());

		// now, test that the app is the same when translated back
		Application a2 = new Application();

		mapper.wicket2Domain(awm, a2);
		assertEquals(a, a2);
		assertEquals(a.getCustomer().getCorrespondenceAddress().getAdrId(), a2.getCustomer().getCorrespondenceAddress()
				.getAdrId());
		assertEquals(a.getCustomer().getCorrespondenceAddress().getCountry(), a2.getCustomer()
				.getCorrespondenceAddress().getCountry());
		assertEquals(a.getCustomer().getCorrespondenceAddress().getLine1(), a2.getCustomer().getCorrespondenceAddress()
				.getLine1());

		assertEquals(a.getCustomer().getResidentialAddress().getAdrId(), a2.getCustomer().getResidentialAddress()
				.getAdrId());
		assertEquals(a.getCustomer().getResidentialAddress().getCountry(), a2.getCustomer().getResidentialAddress()
				.getCountry());
		assertEquals(a.getCustomer().getResidentialAddress().getLine1(), a2.getCustomer().getResidentialAddress()
				.getLine1());

	}
	
	@Test
	public void testApplicationSecondResidentialAddressSameAsFirst(){
		
	}
	
	@Test
	public void testApplicationSecondCorrespondenceAddressSameAsFirst(){
		
	}

	@Test
	public void testAddAddresses() {
		ApplicationDomainToWicketMapper mapper = new ApplicationDomainToWicketMapper();
		mapper.setIncludeCustomer(true);
		mapper.setIncludeContactDetails(true);

		Application a = new Application();
		a.setAccountCurrency("GBP");
		a.setAppId(10L);
		Customer c = new Customer();
		c.setAppId(a.getAppId());
		c.setCountryOfBirth("UK");
		c.setCustId(5L);
		a.setCustomer(c);

		ApplicationWicketModelObject awm = mapper.domain2Wicket(a);

		assertEquals(a.getAccountCurrency(), awm.getAccountCurrency());
		assertEquals(a.getAppId(), awm.getAppId());
		assertNotNull(awm.getCustomer());
		assertEquals(awm.getCustomer().getCountryOfBirth(), c.getCountryOfBirth());
		assertEquals(awm.getCustomer().getAppId(), c.getAppId());
		assertEquals(awm.getCustomer().getCustId(), c.getCustId());

		AddressWicketModelObject residentialAddress = new AddressWicketModelObject();
		residentialAddress.setAdrId(33L);
		residentialAddress.setCountry("UK");
		residentialAddress.setLine1("Sheffield, Crucible theathre");
		awm.getContactDetails().setAddress(residentialAddress);

		AddressWicketModelObject correspAddress = new AddressWicketModelObject();
		correspAddress.setAdrId(10L);
		correspAddress.setCountry("FR");
		correspAddress.setLine1("Paris, brioche a la madeleine");
		awm.getContactDetails().setCorrespondenceAddress(correspAddress);
		awm.getContactDetails().setIsCorrespondenceAddressSameAsResidential(false);

		PreviousAddressWicketModelObject pa1 = new PreviousAddressWicketModelObject();
		pa1.setAdrId(1L);
		pa1.setCountry("RO");
		// pa1.setCustomer(a.getCustomer());
		pa1.setDuration(1);
		pa1.setLine1("Sema parc curtiard");
		pa1.setTown("Bucharest");

		PreviousAddressWicketModelObject pa2 = new PreviousAddressWicketModelObject();
		pa2.setAdrId(2L);
		pa2.setCountry("BG");
		// pa2.setCustomer(a.getCustomer());
		pa2.setDuration(2);
		pa2.setLine1("Velikoto Plevnovo");
		pa2.setTown("Varna");
		List<PreviousAddressWicketModelObject> previousAddresses = new ArrayList<PreviousAddressWicketModelObject>();
		previousAddresses.add(pa1);
		previousAddresses.add(pa2);
		awm.getContactDetails().setPreviousAddresses(previousAddresses);

		mapper.wicket2Domain(awm, a);

		assertEquals(a.getCustomer().getCorrespondenceAddress().getAdrId(), awm.getContactDetails()
				.getCorrespondenceAddress().getAdrId());
		assertEquals(a.getCustomer().getCorrespondenceAddress().getCountry(), awm.getContactDetails()
				.getCorrespondenceAddress().getCountry());
		assertEquals(a.getCustomer().getCorrespondenceAddress().getLine1(), awm.getContactDetails()
				.getCorrespondenceAddress().getLine1());

		assertEquals(a.getCustomer().getResidentialAddress().getAdrId(), awm.getContactDetails().getAddress()
				.getAdrId());
		assertEquals(a.getCustomer().getResidentialAddress().getCountry(), awm.getContactDetails().getAddress()
				.getCountry());
		assertEquals(a.getCustomer().getResidentialAddress().getLine1(), awm.getContactDetails().getAddress()
				.getLine1());

		assertEquals(a.getCustomer().getPreviousAddresses()[0].getAdrId(), awm.getContactDetails()
				.getPreviousAddresses().get(0).getAdrId());
		assertEquals(a.getCustomer().getPreviousAddresses()[0].getCountry(), awm.getContactDetails()
				.getPreviousAddresses().get(0).getCountry());
		assertEquals(a.getCustomer().getPreviousAddresses()[0].getLine1(), awm.getContactDetails()
				.getPreviousAddresses().get(0).getLine1());
	}

	/**
	 * Tests that the model classes are in sync with the wicket models
	 */
	@Test
	public void testModelsInSyncWithDomain() {
		// TODO: check if we can/should have customer pojos in synch
		// testModelInSyncInternal(Customer.class, CustomerWicketModel.class,
		// ApplicationDomainToWicketMapper.getIgnoredPropsForCustomer());
		testModelInSyncInternal(Address.class, AddressWicketModelObject.class,
				ApplicationDomainToWicketMapper.getIgnoredPropsForAddress());
		testModelInSyncInternal(PreviousAddress.class, PreviousAddressWicketModelObject.class,
				ApplicationDomainToWicketMapper.getIgnoredPropsForAddress());
		testModelInSyncInternal(Application.class, ApplicationWicketModelObject.class,
				ApplicationDomainToWicketMapper.getIgnoredPropsForApplication());
	}

	private void testModelInSyncInternal(Class<?> domainClass, Class<?> modelClass, Set<String> ignoredProps) {
		PropertyDescriptor[] pds = BeanUtils.getPropertyDescriptors(domainClass);
		for (PropertyDescriptor pd : pds) {
			if (ignoredProps.contains(pd.getName())) {
				continue;
			}
			PropertyDescriptor pdInWicketModel = BeanUtils.getPropertyDescriptor(modelClass, pd.getName());
			assertNotNull("Class " + modelClass.getName() + "." + pd.getName()
					+ " Property should exist in wicket model", pdInWicketModel);
			assertTrue("Class " + modelClass.getName() + ".Property " + pd.getName()
					+ " should have same type in wicket model",
					isCompatibleType(pd.getPropertyType(), pdInWicketModel.getPropertyType()));
		}
	}

	private static Map<Class<?>, Class<?>> equivTypes = new HashMap<Class<?>, Class<?>>();
	static {

		equivTypes.put(Application.class, ApplicationWicketModelObject.class);
		equivTypes.put(Customer.class, CustomerWicketModel.class);
		equivTypes.put(Address.class, AddressWicketModelObject.class);
		equivTypes.put(PreviousAddress[].class, PreviousAddressWicketModelObject[].class);
		equivTypes.put(PreviousAddress.class, PreviousAddressWicketModelObject.class);

	}

	/**
	 * Checks whether the type of a property in the wicket model is compatible with the type in the domain object
	 * 
	 * @param domainType
	 * @param wicketModelType
	 * @return
	 */
	private static boolean isCompatibleType(Class<?> domainType, Class<?> wicketModelType) {
		if (equivTypes.containsKey(domainType)) {
			return wicketModelType.equals(equivTypes.get(domainType));
		} else {
			return domainType.equals(wicketModelType);
		}
	}

	/**
	 * Test method for
	 * {@link com.temenos.ebank.wicketmodel.mappers.ApplicationDomainToWicketMapper#wicket2Domain(com.temenos.ebank.wicketmodel.ApplicationWicketModelObject, com.temenos.ebank.domain.Application)}
	 * .
	 */
	@Test
	public void testWicket2Domain() {

	}

}
