package com.temenos.interaction.core.media.hal;

public class Children {

	private String name;
	private int age;
	private String shoeSize;
	
	public Children(String name, int age, String shoeSize) {
		this.name = name;
		this.age = age;
		this.shoeSize = shoeSize;
	}
	
	public String getName() {
		return name;
	}

	public int getAge() {
		return age;
	}

	public String getShoeSize() {
		return shoeSize;
	}
}
