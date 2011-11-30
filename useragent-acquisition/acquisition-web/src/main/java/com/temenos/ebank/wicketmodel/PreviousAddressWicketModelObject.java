/**
 * 
 */
package com.temenos.ebank.wicketmodel;


/**
 * @author vionescu
 * 
 */
public class PreviousAddressWicketModelObject extends AddressWicketModelObject {

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((duration == null) ? 0 : duration.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		PreviousAddressWicketModelObject other = (PreviousAddressWicketModelObject) obj;
		if (duration == null) {
			if (other.duration != null) {
				return false;
			}
		} else if (!duration.equals(other.duration)) {
			return false;
		}
		return true;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer duration;

	public PreviousAddressWicketModelObject() {
	}

	public PreviousAddressWicketModelObject(Integer duration) {
		this.duration = duration;
	}

	public void setDuration(Integer duration) {
		this.duration = duration;
	}

	public Integer getDuration() {
		return duration;
	}

}
