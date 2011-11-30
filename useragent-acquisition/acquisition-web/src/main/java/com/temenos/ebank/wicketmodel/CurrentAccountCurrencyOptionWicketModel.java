/**
 * 
 */
package com.temenos.ebank.wicketmodel;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

/**
 * @author vionescu
 * 
 */
public class CurrentAccountCurrencyOptionWicketModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Boolean selected;
	private String accCurrency;

	/**
	 * Checks if the object is "empty" or not, i.e. if it was populated by the application or not
	 * 
	 * @return
	 */
	public boolean isEmpty() {
		return StringUtils.isBlank(accCurrency);
	}

	// flag indicating whether the currency was selected or not. This field is not persisted in the database
	// private boolean selected = false;

	public Boolean isSelected() {
		return Boolean.TRUE.equals(selected);
	}

	public void setSelected(Boolean selected) {
		this.selected = selected;
	}


	public CurrentAccountCurrencyOptionWicketModel() {
	}

	public CurrentAccountCurrencyOptionWicketModel(String accCurrency) {
		this.accCurrency = accCurrency;
	}
	public String getAccCurrency() {
		return accCurrency;
	}

	public void setAccCurrency(String currency) {
		this.accCurrency = currency;
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
		CurrentAccountCurrencyOptionWicketModel other = (CurrentAccountCurrencyOptionWicketModel) obj;
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
