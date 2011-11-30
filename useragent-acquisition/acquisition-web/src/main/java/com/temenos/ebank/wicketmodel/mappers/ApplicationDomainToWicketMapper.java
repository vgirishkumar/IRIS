/**
 * 
 */
package com.temenos.ebank.wicketmodel.mappers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.wicket.model.IModel;
import org.springframework.beans.BeanUtils;

import com.temenos.ebank.domain.Address;
import com.temenos.ebank.domain.Application;
import com.temenos.ebank.domain.Customer;
import com.temenos.ebank.domain.PreviousAddress;
import com.temenos.ebank.wicketmodel.AddressWicketModelObject;
import com.temenos.ebank.wicketmodel.ApplicationWicketModelObject;
import com.temenos.ebank.wicketmodel.ContactDetailsModelObject;
import com.temenos.ebank.wicketmodel.CustomerWicketModel;
import com.temenos.ebank.wicketmodel.PreviousAddressWicketModelObject;

/**
 * Mapper between domain object {@link Application} and the wicket model {@link ApplicationWicketModelObject}
 * 
 * @author vionescu
 * 
 */
@SuppressWarnings("rawtypes")
public class ApplicationDomainToWicketMapper implements
		IDomainToWicketMapper<Application, ApplicationWicketModelObject> {

	// these flags are set by spring, their values differ from one step to another
	// we only need some of them set to true when converting from domain to wicket and around
	// for example, in step three, we don't need to convert residential, correspondence addresses
	// we only need to convert employer address in a customer. so, in applicationContext.xml
	// only the includeEmployerAddress flag is set to true.
	private boolean includeCustomer;
	private boolean includeContactDetails;
	private boolean includeEmployerAddress;

	private static String[] ignoredPropsForApplication = new String[] { "contactDetails", "jointContactDetails",
			"customer", "secondCustomer"};
	private static String[] ignoredPropsForCustomer = new String[] { "employerAddress", "otherEmploymentStatus" };
	private static String[] ignoredPropsForAddress = new String[] { "customer" };

	private static Set<String> arrayToSet(String[] src) {
		Set<String> dest = new HashSet<String>();
		CollectionUtils.addAll(dest, src);
		return dest;
	}

	public static Set<String> getIgnoredPropsForCustomer() {
		return arrayToSet(ignoredPropsForCustomer);
	}

	public static Set<String> getIgnoredPropsForApplication() {
		return arrayToSet(ignoredPropsForApplication);
	}

	public static Set<String> getIgnoredPropsForAddress() {
		return arrayToSet(ignoredPropsForAddress);
	}

	public boolean isIncludeCustomer() {
		return includeCustomer;
	}

	public void setIncludeCustomer(boolean includeCustomer) {
		this.includeCustomer = includeCustomer;
	}

	public void setIncludeContactDetails(boolean includeContactDetails) {
		this.includeContactDetails = includeContactDetails;
	}

	public boolean isIncludeContactDetails() {
		return includeContactDetails;
	}

	public void setIncludeEmployerAddress(boolean includeEmployerAddress) {
		this.includeEmployerAddress = includeEmployerAddress;
	}

	public boolean isIncludeEmployerAddress() {
		return includeEmployerAddress;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.temenos.ebank.pages.clientAquisition.wicketModels.IDomainToWicketMapper#getWicketModelObject(java.lang.Object
	 * )
	 */
	public ApplicationWicketModelObject domain2Wicket(Application a) {
		ApplicationWicketModelObject awm = new ApplicationWicketModelObject();
		CustomerWicketModel cwm = null;
		// List<CurrentAccountCurrencyOptionWicketModel> cacoswm = null;
		CustomerWicketModel cwmSecondCustomer = null;
		if (includeCustomer) {
			cwm = new CustomerWicketModel();
			awm.setCustomer(cwm);
			cwmSecondCustomer = new CustomerWicketModel();
			awm.setSecondCustomer(cwmSecondCustomer);
		}

		if (a == null) {
			return awm;
		}

		BeanUtils.copyProperties(a, awm, ignoredPropsForApplication);

		if (includeCustomer) {
			if (a.getCustomer() != null) {
				customerDomain2Wicket(a.getCustomer(), cwm);
			}

			if (a.getSecondCustomer() != null) {
				customerDomain2Wicket(a.getSecondCustomer(), cwmSecondCustomer);
			}
		}

		if (includeContactDetails) {
			ContactDetailsModelObject contactDetailsMO = new ContactDetailsModelObject();
			contactDetailsDomain2Wicket(a.getCustomer(), contactDetailsMO, a);
			awm.setContactDetails(contactDetailsMO);
			if (!awm.getIsSole()) {
				ContactDetailsModelObject secondContactDetailsMO = new ContactDetailsModelObject();
				contactDetailsDomain2Wicket(a.getSecondCustomer(), a.getCustomer(), secondContactDetailsMO, a);
				awm.setJointContactDetails(secondContactDetailsMO);
			}
		}

		// if (StringUtils.isNotBlank(a.getAccountCurrency())) {
		// CurrentAccountCurrencyOptionWicketModel cacowm = new CurrentAccountCurrencyOptionWicketModel();
		// cacowm.setAccCurrency(a.getAccountCurrency());
		// cacoswm.add(cacowm);
		// }
		//
		//
		// if (StringUtils.isNotBlank(a.getAccountCurrency2())) {
		// CurrentAccountCurrencyOptionWicketModel cacowm = new CurrentAccountCurrencyOptionWicketModel();
		// cacowm.setAccCurrency(a.getAccountCurrency2());
		// cacoswm.add(cacowm);
		// }
		//
		// if (StringUtils.isNotBlank(a.getAccountCurrency3())) {
		// CurrentAccountCurrencyOptionWicketModel cacowm = new CurrentAccountCurrencyOptionWicketModel();
		// cacowm.setAccCurrency(a.getAccountCurrency3());
		// cacoswm.add(cacowm);
		// }

		return awm;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.temenos.ebank.pages.clientAquisition.wicketModels.IDomainToWicketMapper#updateDomainObjectFromModel(java.
	 * lang.Object, java.lang.Object)
	 */
	public void wicket2Domain(ApplicationWicketModelObject awm, Application a) {
		if (a == null) {
			throw new RuntimeException("Application should not be null");
		}

		BeanUtils.copyProperties(awm, a, ignoredPropsForApplication);

		if (includeCustomer) {
			Customer c = a.getCustomer();
			CustomerWicketModel cwm = awm.getCustomer();
			if (!cwm.isEmpty()) {
				if (c == null) {
					c = new Customer();
					a.setCustomer(c);
				}
				customerWicket2Domain(cwm, c);
			} else {
				a.setCustomer(null);
			}
			Customer secondCustomer = a.getSecondCustomer();
			CustomerWicketModel cwmSecondCustomer = awm.getSecondCustomer();
			if (!cwmSecondCustomer.isEmpty()) {
				if (secondCustomer == null) {
					secondCustomer = new Customer();
					a.setSecondCustomer(secondCustomer);
				}
				customerWicket2Domain(cwmSecondCustomer, secondCustomer);
			} else {
				a.setSecondCustomer(null);
			}
		}

		if (includeContactDetails) {
			contactDetailsWicket2Domain(awm.getContactDetails(), a.getCustomer(), awm);
			if (!awm.getIsSole()) {
				contactDetailsWicket2Domain(awm.getJointContactDetails(), a.getSecondCustomer(), a.getCustomer(), awm);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.temenos.ebank.pages.clientAquisition.wicketModels.IDomainToWicketMapper#updateDomainObjectFromModel(org.apache
	 * .wicket.model.IModel, java.lang.Object)
	 */
	public void wicketModel2Domain(IModel wicketModel, Application a) {
		if (wicketModel == null) {
			return;
		}
		wicket2Domain((ApplicationWicketModelObject) wicketModel.getObject(), a);
	}

	private void customerDomain2Wicket(Customer c, CustomerWicketModel cwm) {
		BeanUtils.copyProperties(c, cwm, ignoredPropsForCustomer);

		if (includeEmployerAddress) {
			AddressWicketModelObject awmEmployerAddress = new AddressWicketModelObject();
			if (c.getEmployerAddress() != null) {
				addressDomain2Wicket(c.getEmployerAddress(), awmEmployerAddress);
			}
			cwm.setEmployerAddress(awmEmployerAddress);
		}
	}

	private void customerWicket2Domain(CustomerWicketModel cwm, Customer c) {
		BeanUtils.copyProperties(cwm, c, ignoredPropsForCustomer);
		if (includeEmployerAddress) {
			AddressWicketModelObject awmEmployerAddress = cwm.getEmployerAddress();
			if (awmEmployerAddress != null && !awmEmployerAddress.isEmpty()) {
				Address employerAddress = c.getEmployerAddress();
				if (employerAddress == null) {
					employerAddress = new Address();
					c.setEmployerAddress(employerAddress);
				}
				addressWicket2Domain(awmEmployerAddress, employerAddress);
			} else {
				c.setEmployerAddress(null);
			}
		}
	}

	private void addressDomain2Wicket(Address a, AddressWicketModelObject awm) {
		BeanUtils.copyProperties(a, awm, ignoredPropsForAddress);
	}

	private void addressWicket2Domain(AddressWicketModelObject awm, Address a) {
		BeanUtils.copyProperties(awm, a, ignoredPropsForAddress);
	}

	/**
	 * @param customer
	 *            Constructor for first customer
	 */
	public void contactDetailsDomain2Wicket(Customer customer, ContactDetailsModelObject contactDetails, Application a) {
		contactDetailsDomain2Wicket(customer, null, contactDetails, a);
	}

	/**
	 * @param customer
	 * @param firstCustomer
	 *            - the joint contact details depends on the data of the first customer (addresses can be same as first)
	 * @param contactDetails
	 */
	public void contactDetailsDomain2Wicket(Customer customer, Customer firstCustomer,
			ContactDetailsModelObject contactDetails, Application a) {
		// copy properties except addresses
		BeanUtils.copyProperties(customer, contactDetails, new String[] { "correspondenceAddress",
				"residentialAddress", "previousAddresses" });
		contactDetails.setEmailConf(customer.getEmailAddress());

		if (customer.getPreviousAddresses() != null) {
			contactDetails.setPreviousAddresses(new ArrayList<PreviousAddressWicketModelObject>());
			for (int i = 0; i < customer.getPreviousAddresses().length; i++) {
				PreviousAddressWicketModelObject previousAddresWMO = new PreviousAddressWicketModelObject();
				BeanUtils.copyProperties(customer.getPreviousAddresses()[i], previousAddresWMO,
						new String[] { "customer" });
				contactDetails.getPreviousAddresses().add(previousAddresWMO);
			}
		}

		if (customer.getResidentialAddress() != null) {
			BeanUtils.copyProperties(customer.getResidentialAddress(), contactDetails.getAddress());
		} else {
			contactDetails.setAddress(new AddressWicketModelObject());
		}

		if (customer.getIsCorrespondenceAddressSameAsResidential()) {
			contactDetails.setCorrespondenceAddress(contactDetails.getAddress());
		} else if (customer.getCorrespondenceAddress() != null) {
			BeanUtils.copyProperties(customer.getCorrespondenceAddress(), contactDetails.getCorrespondenceAddress());
		} else {
			contactDetails.setCorrespondenceAddress(new AddressWicketModelObject());
		}

	}

	public void contactDetailsWicket2Domain(ContactDetailsModelObject contactDetailsMO, Customer customer,
			ApplicationWicketModelObject awm) {
		contactDetailsWicket2Domain(contactDetailsMO, customer, null, awm);
	}

	public void contactDetailsWicket2Domain(ContactDetailsModelObject contactDetailsMO, Customer customer,
			Customer firstCustomer, ApplicationWicketModelObject awm) {

		BeanUtils.copyProperties(contactDetailsMO, customer, new String[] { "correspondenceAddress",
				"residentialAddress", "previousAddresses" });

		if (firstCustomer == null) {
			if (!contactDetailsMO.getAddress().isEmpty()) {
				Address address = customer.getResidentialAddress();
				if (address == null) {
					address = new Address();
					customer.setResidentialAddress(address);
				}
				addressWicket2Domain(contactDetailsMO.getAddress(), address);
			} else {
				customer.setResidentialAddress(null);
			}
		} else {
			if (awm.getSecondResidentialSameAsFirstResidential()) {
				customer.setResidentialAddress(firstCustomer.getResidentialAddress());
			} else {
				if (!contactDetailsMO.getAddress().isEmpty()) {
					Address address = customer.getResidentialAddress();
					if (address == null) {
						address = new Address();
						customer.setResidentialAddress(address);
					}
					addressWicket2Domain(contactDetailsMO.getAddress(), address);
				} else {
					customer.setResidentialAddress(null);
				}
			}
		}
		if (firstCustomer == null) {
			if (contactDetailsMO.getIsCorrespondenceAddressSameAsResidential()) {
				customer.setCorrespondenceAddress(customer.getResidentialAddress());
			} else {
				if (!contactDetailsMO.getCorrespondenceAddress().isEmpty()) {
					Address correspondenceAddress = customer.getCorrespondenceAddress();
					if (correspondenceAddress == null) {
						correspondenceAddress = new Address();
						customer.setCorrespondenceAddress(correspondenceAddress);
					}
					addressWicket2Domain(contactDetailsMO.getCorrespondenceAddress(), correspondenceAddress);
				} else {
					customer.setCorrespondenceAddress(null);
				}
			}
		} else {
			if (contactDetailsMO.getIsCorrespondenceAddressSameAsResidential()) {
				customer.setCorrespondenceAddress(customer.getResidentialAddress());
			} else {
				if (awm.getSecondCorrespondenceSameAsFirst()) {
					customer.setCorrespondenceAddress(firstCustomer.getCorrespondenceAddress());
				} else {
					if (!contactDetailsMO.getCorrespondenceAddress().isEmpty()) {
						Address correspondenceAddress = customer.getCorrespondenceAddress();
						if (correspondenceAddress == null) {
							correspondenceAddress = new Address();
							customer.setCorrespondenceAddress(correspondenceAddress);
						}
						addressWicket2Domain(contactDetailsMO.getCorrespondenceAddress(), correspondenceAddress);
					} else {
						customer.setCorrespondenceAddress(null);
					}
				}
			}
		}

		// copy existing so that Hibernate does not throw
		if (ArrayUtils.isNotEmpty(customer.getPreviousAddresses())) {
			if (CollectionUtils.isNotEmpty(contactDetailsMO.getPreviousAddresses())){
				// match existing items with new ones, in order to avoid hibernate exceptions
				for (PreviousAddress previousAddress : customer.getPreviousAddresses()) {
					boolean foundMatch = false;
					for (PreviousAddressWicketModelObject previousAddressMO : contactDetailsMO.getPreviousAddresses()) {
						foundMatch = (previousAddress.getAdrId() != null)
								&& previousAddress.getAdrId().equals(previousAddressMO.getAdrId());
						if (foundMatch) {
							BeanUtils.copyProperties(previousAddressMO, previousAddress, new String[] { "customer" });
							previousAddress.setCustomer(customer);
							break;
						}
					}
				}
			}
			else{
				//we have to clear the DAO when model object is empty
				customer.setPreviousAddresses(new PreviousAddress[0]);
			}
		}
		// add newly created previous addresses
		if (CollectionUtils.isNotEmpty(contactDetailsMO.getPreviousAddresses())) {
			if (ArrayUtils.isNotEmpty(customer.getPreviousAddresses())) {
				if (customer.getPreviousAddresses().length < contactDetailsMO.getPreviousAddresses().size()) {
					for (int i = customer.getPreviousAddresses().length; i < contactDetailsMO.getPreviousAddresses()
							.size(); i++) {
						PreviousAddress prevAddr = new PreviousAddress();
						BeanUtils.copyProperties(contactDetailsMO.getPreviousAddresses().get(i), prevAddr,
								new String[] { "customer" });
						prevAddr.setCustomer(customer);
						PreviousAddress[] copy = (PreviousAddress[]) ArrayUtils.add(customer.getPreviousAddresses(),
								prevAddr);
						customer.setPreviousAddresses(copy);
					}
				}
			} else {
				customer.setPreviousAddresses(new PreviousAddress[contactDetailsMO.getPreviousAddresses().size()]);
				for (int i = 0; i < contactDetailsMO.getPreviousAddresses().size(); i++) {
					customer.getPreviousAddresses()[i] = new PreviousAddress();
					BeanUtils.copyProperties(contactDetailsMO.getPreviousAddresses().get(i),
							customer.getPreviousAddresses()[i], new String[] { "customer" });
					customer.getPreviousAddresses()[i].setCustomer(customer);
				}
			}

		}
	}
}
