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
public class AddressWicketModelObject implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((adrId == null) ? 0 : adrId.hashCode());
		result = prime * result + ((country == null) ? 0 : country.hashCode());
		result = prime * result + ((line1 == null) ? 0 : line1.hashCode());
		result = prime * result + ((line2 == null) ? 0 : line2.hashCode());
		result = prime * result + ((county == null) ? 0 : county.hashCode());
		result = prime * result + ((getDistrict() == null) ? 0 : getDistrict().hashCode());
		result = prime * result + ((postcode == null) ? 0 : postcode.hashCode());
		result = prime * result + ((town == null) ? 0 : town.hashCode());
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
		AddressWicketModelObject other = (AddressWicketModelObject) obj;
		if (adrId == null) {
			if (other.adrId != null) {
				return false;
			}
		} else if (!adrId.equals(other.adrId)) {
			return false;
		}
		if (country == null) {
			if (other.country != null) {
				return false;
			}
		} else if (!country.equals(other.country)) {
			return false;
		}
		if (line1 == null) {
			if (other.line1 != null) {
				return false;
			}
		} else if (!line1.equals(other.line1)) {
			return false;
		}
		if (line2 == null) {
			if (other.line2 != null) {
				return false;
			}
		} else if (!line2.equals(other.line2)) {
			return false;
		}
		if (county == null) {
			if (other.county != null) {
				return false;
			}
		} else if (!county.equals(other.county)) {
			return false;
		}
		if (district == null) {
			if (other.district != null) {
				return false;
			}
		} else if (!district.equals(other.district)) {
			return false;
		}
		if (postcode == null) {
			if (other.postcode != null) {
				return false;
			}
		} else if (!postcode.equals(other.postcode)) {
			return false;
		}
		if (town == null) {
			if (other.town != null) {
				return false;
			}
		} else if (!town.equals(other.town)) {
			return false;
		}
		return true;
	}

	private Long adrId;
	private String country;
	private String line1;
	private String line2;
	private String county;
	private String district;
	private String town;
	private String postcode;

	public AddressWicketModelObject() {
	}

	public AddressWicketModelObject(Long adrId, String country) {
		this.adrId = adrId;
		this.country = country;
	}

	public AddressWicketModelObject(Long adrId, String country, String line1, String line2, String county, String district, String town,
			String postcode) {
		this.adrId = adrId;
		this.country = country;
		this.line1 = line1;
		this.line2 = line2;
		this.county = county;
		this.district = district;
		this.town = town;
		this.postcode = postcode;
	}

	/**
	 * Checks if the object is "empty" or not, i.e. if it was populated by the application or not
	 * 
	 * @return
	 */
	public boolean isEmpty() {
		if (adrId != null) {
			return false;
		}
		if (StringUtils.isNotBlank(country) || StringUtils.isNotBlank(line1)) {
			return false;
		}
		return true;
	}

	public Long getAdrId() {
		return this.adrId;
	}

	public void setAdrId(Long adrId) {
		this.adrId = adrId;
	}

	public String getCountry() {
		return this.country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getLine1() {
		return this.line1;
	}

	public void setLine1(String line1) {
		this.line1 = line1;
	}

	public String getLine2() {
		return this.line2;
	}

	public void setLine2(String line2) {
		this.line2 = line2;
	}

	public String getTown() {
		return this.town;
	}

	public void setTown(String town) {
		this.town = town;
	}

	public String getPostcode() {
		return this.postcode;
	}

	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}

	public void setCounty(String county) {
		this.county = county;
	}

	public String getCounty() {
		return county;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getDistrict() {
		return district;
	}

}
