package com.temenos.interaction.core.hypermedia;

public class Booking {

	private String bookingId = "";

	public Booking(String id) {
		bookingId = id;
	}
	
	public String getBookingId() {
		return bookingId;
	}

	public void setBookingId(String bookingId) {
		this.bookingId = bookingId;
	}
	
}
