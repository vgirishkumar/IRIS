/**
 * 
 */
package com.temenos.ebank.domain;

import java.io.Serializable;

/**
 * Currency option for current account
 * 
 * @author vionescu
 * 
 */
public class CurrentAccountCurrencyOption implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long id;
	private Application application;
	private String accCurrency;


	//
	// /**
	// * Refreshes the selection status
	// */
	// public void refreshSelectedStatus() {
	// this.selected = accQuantity > 0;
	// }
	//
	public CurrentAccountCurrencyOption(Application application, String accCurrency) {
		this.application = application;
		this.accCurrency = accCurrency;
	}

	public CurrentAccountCurrencyOption() {
	}

	// /**
	// * Checks if this currency was selected. A currency is selected if at least one account was selected for that
	// currency
	// * @return
	// */
	// public boolean isSelected() {
	// return ;
	// }
	public Application getApplication() {
		return application;
	}

	public void setApplication(Application application) {
		this.application = application;
	}

	public String getAccCurrency() {
		return accCurrency;
	}

	public void setAccCurrency(String currency) {
		this.accCurrency = currency;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public String toString() {
		// leave this as it is, beacuse wicket needs a string for localizedLabelRenderer
		return accCurrency;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((application == null) ? 0 : application.hashCode());
		result = prime * result + ((accCurrency == null) ? 0 : accCurrency.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		CurrentAccountCurrencyOption other = (CurrentAccountCurrencyOption) obj;
		if (application == null) {
			if (other.application != null) {
				return false;
			}
		} else if (!application.equals(other.application)) {
			return false;
		}
		if (accCurrency == null) {
			if (other.accCurrency != null) {
				return false;
			}
		} else if (!accCurrency.equals(other.accCurrency)) {
			return false;
		}
		return true;
	}
}
