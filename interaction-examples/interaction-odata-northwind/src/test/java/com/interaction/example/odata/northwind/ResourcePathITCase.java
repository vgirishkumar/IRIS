package com.interaction.example.odata.northwind;

import junit.framework.Assert;

import org.junit.Test;

//TODO enable disabled and commented test cases
public class ResourcePathITCase extends AbstractNorthwindRuntimeTest {

	public ResourcePathITCase(RuntimeFacadeType type) {
		super(type);
	}

	@Test
	public void ResourcePathCollectionTest() {
//		String inp = "ResourcePathCollectionTest";
//		String uri = "Categories";
		//testJSONResult(endpointUri, uri, inp);
//		testAtomResult(endpointUri, uri, inp);
	}

	//@Test
	public void ResourcePathNavPropSingleTest() {
		String inp = "ResourcePathNavPropSingleTest";
		String uri = "Categories(1)/CategoryName";
		//testJSONResult(endpointUri, uri, inp);
		testAtomResult(endpointUri, uri, inp);
	}

	//@Test
	public void ResourcePathNavPropCollectionTest() {
		String inp = "ResourcePathNavPropCollectionTest";
		String uri = "Categories(1)/Products?$filter=ProductID gt 0";
		testJSONResult(endpointUri, uri, inp);
	}

	//@Test
	public void ResourcePathComplexTypeTest() {
		String inp = "ResourcePathComplexTypeTest";
		String uri = "Categories(1)/Products(1)/Supplier/Address";
		testJSONResult(endpointUri, uri, inp);
	}

	//@Test
	public void ResourcePathCollectionCountTest() {
		String uri = "Categories/$count";
		String result = getCount(endpointUri, uri);
		Assert.assertEquals("8", result);

		uri = "Categories/$count/";
		result = getCount(endpointUri, uri);
		Assert.assertEquals("8", result);
	}

	//@Test
	public void ResourcePathCollectionCountFilteredTest() {
		String uri = "Categories/$count?$filter=CategoryID gt 2";
		String result = getCount(endpointUri, uri);
		Assert.assertEquals("6", result);
	}

	//@Test
	public void ResourcePathCollectionCountTopTest() {
		String uri = "Categories/$count?$top=5";
		String result = getCount(endpointUri, uri);
		Assert.assertEquals("5", result);

		uri = "Categories/$count/?$top=0";
		result = getCount(endpointUri, uri);
		Assert.assertEquals("0", result);

		uri = "Categories/$count/?$top=100";
		result = getCount(endpointUri, uri);
		Assert.assertEquals("8", result);
	}

	//@Test
	public void ResourcePathCollectionCountSkipTest() {
		String uri = "Categories/$count?$skip=3";
		String result = getCount(endpointUri, uri);
		Assert.assertEquals("5", result);

		uri = "Categories/$count/?$skip=100";
		result = getCount(endpointUri, uri);
		Assert.assertEquals("0", result);

		uri = "Categories/$count/?$skip=0";
		result = getCount(endpointUri, uri);
		Assert.assertEquals("8", result);
	}

	//@Test
	public void ResourcePathNavPropCollectionCountTest() {
		String uri = "Categories(1)/Products/$count?$filter=ProductID gt 0";
		String result = getCount(endpointUri, uri);
		Assert.assertEquals("12", result);
	}

	//@Test
	public void ResourcePathNavPropCollectionCountTopTest() {
		String uri = "Categories(1)/Products/$count?$top=10&$filter=ProductID gt 0";
		String result = getCount(endpointUri, uri);
		Assert.assertEquals("10", result);

		uri = "Categories(1)/Products/$count?$top=100&$filter=ProductID gt 0";
		;
		result = getCount(endpointUri, uri);
		Assert.assertEquals("12", result);

		uri = "Categories(1)/Products/$count?$top=0&$filter=ProductID gt 0";
		;
		result = getCount(endpointUri, uri);
		Assert.assertEquals("0", result);
	}

	//@Test
	public void ResourcePathNavPropCollectionCountSkipTest() {
		String uri = "Categories(1)/Products/$count?$skip=10&$filter=ProductID gt 0";
		String result = getCount(endpointUri, uri);
		Assert.assertEquals("2", result);

		uri = "Categories(1)/Products/$count?$skip=100&$filter=ProductID gt 0";
		;
		result = getCount(endpointUri, uri);
		Assert.assertEquals("0", result);

		uri = "Categories(1)/Products/$count?$skip=0&$filter=ProductID gt 0";
		;
		result = getCount(endpointUri, uri);
		Assert.assertEquals("12", result);
	}
}
