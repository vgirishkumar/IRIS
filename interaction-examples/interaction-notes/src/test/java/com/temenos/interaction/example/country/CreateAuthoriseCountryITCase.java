package com.temenos.interaction.example.country;

import static org.junit.Assert.*;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.junit.Test;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.LoggingFilter;
import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.impl.util.CommonUtils;
import com.temenos.interaction.example.country.Country;
import com.temenos.interaction.example.email.EmailMessage;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

public class CreateAuthoriseCountryITCase extends JerseyTest {

	/* Allows standalone Jersey Test
	@BeforeClass
	public static void initialiseTestDB() {
    	// bootstrap the NoteProducerFactory which creates the JPA entity manager (the CREATE TABLE)
    	new NoteProducerFactory();
	}
	 */

	@Before
	public void initTest() {
		// TODO make this configurable
		// test with external server 
    	webResource = Client.create().resource("http://localhost:8080/example/rest"); 
	}
	
	@After
	public void tearDown() {}
      
    public CreateAuthoriseCountryITCase() throws Exception {
    	/* Allows standalone Jersey Test
    	super("example", "rest", "com.temenos.interaction.example");
		*/
        // enable logging on base web resource
    	System.setProperty("enableLogging", "ya");
	}
    
    /**
     * Test check GET on the "country" resource in "test/plain" format.
     */
    //@Test
    public void testGetCountryTextFormat() {
        // get a string representation
        String country = webResource.path("countries/123").
                accept("application/json").get(String.class);
        assertEquals("{\"centralBankCode\":\"newCentralBankCode_123\",\"businessCentre\":\"newBusinessCentre_123\"}", country);
    }

    /**
     * Test check GET on the "country" resource in "application/json" format.
     */
    //@Test
    public void testGetCountryJSONFormat() {
        GenericType<Country> genericType = new GenericType<Country>() {};
        Country country = webResource.path("countries/123").
                accept("application/json").get(genericType);
        assertEquals("newCentralBankCode_123", country.getCentralBankCode());
    }

    /**
     * Test check GET on the "country" resource in "application/json" format.
     */
    @Test
    public void testGetCountryXMLFormat() {
        GenericType<CountryResource> genericType = new GenericType<CountryResource>() {};
        CountryResource countryResource = webResource.path("countries/123").
                accept("application/xml").get(genericType);
        Country country = countryResource.getCountry();
        assertEquals("newCentralBankCode_123", country.getCentralBankCode());
    }

    /**
     * Create the country, get the country, delete the country
     */
    //@Test
    public void testCGDCountry() {
    	Country c = new Country();
    	c.setBusinessCentre("London");
    	c.setCurrencyCode("nosh");
    	c.setCentralBankCode("BOE");
    	
    	// create the country '1'
    	webResource.path("country/1").accept("application/json").method("INPUT", c);

    	// get the country
        GenericType<CountryResource> genericType = new GenericType<CountryResource>() {};
        CountryResource countryResource = webResource.path("countries/1").
                accept("application/xml").get(genericType);
        Country country = countryResource.getCountry();
        assertEquals("BOE", country.getCentralBankCode());

        // delete country '1'
        webResource.path("countries/1").method("DELETE");
    }
    
}
