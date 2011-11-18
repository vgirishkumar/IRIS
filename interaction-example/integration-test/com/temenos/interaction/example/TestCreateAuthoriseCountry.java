package com.temenos.interaction.example;

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
import org.odata4j.producer.jpa.JPAProducer;
import org.odata4j.producer.resources.ODataProducerProvider;

public class TestCreateAuthoriseCountry extends JerseyTest {

    private WebResource r1;
    private static EntityManagerFactory emf;
    
    @Before
    public void setUp() throws Exception {
    	super.setUp();
    }
    
    @After
    public void tearDown() throws Exception {
    	super.tearDown();
    }
    
    @BeforeClass
    public static void initialise() throws Exception {
      String persistenceUnitName = "EmailServiceHibernate";
      String namespace = "Email";

      emf = Persistence.createEntityManagerFactory(persistenceUnitName);

      EntityManager em = emf.createEntityManager();
      
      Query q = em.createQuery("select e from Email e");
		List<EmailMessage> emailList = q.getResultList();
		for (EmailMessage email : emailList) {
			System.out.println(email);
		}
		System.out.println("Size: " + emailList.size());
      
      //JPAProducer producer = new JPAProducer(emf, namespace, 20);

      //ODataProducerProvider.setInstance(producer);
    }
    @AfterClass
    public static void destroy() throws Exception {
    }
    
    public TestCreateAuthoriseCountry() throws Exception {
    	super("example", "rest", "com.temenos");
        ClientConfig cc = new DefaultClientConfig();
        // use the following jaxb context resolver
        cc.getClasses().add(JAXBContextResolver.class);
        Client c = Client.create(cc);
        r1 = c.resource(CommonUtils.getBaseURI("example", "rest"));
        r1.addFilter(new LoggingFilter());
        
	}
    
    /**
     * Test check GET on the "country" resource in "test/plain" format.
     */
    @Test
    public void testGetCountryTextFormat() {
        // get a string representation
        String country = r1.path("country/123").
                accept("application/json").get(String.class);
        assertEquals("{\"centralBankCode\":\"newCentralBankCode_123\",\"businessCentre\":\"newBusinessCentre_123\"}", country);
    }

    /**
     * Test check GET on the "country" resource in "application/json" format.
     */
    @Test
    public void testGetCountryJSONFormat() {
        GenericType<Country> genericType = new GenericType<Country>() {};
        Country country = r1.path("country/123").
                accept("application/json").get(genericType);
        assertEquals("newCentralBankCode_123", country.getCentralBankCode());
    }

    /**
     * Test check GET on the "country" resource in "application/json" format.
     */
    @Test
    public void testGetCountryXMLFormat() {
        GenericType<CountryResource> genericType = new GenericType<CountryResource>() {};
        CountryResource countryResource = r1.path("country/123").
                accept("application/xml").get(genericType);
        Country country = countryResource.getCountry();
        assertEquals("newCentralBankCode_123", country.getCentralBankCode());
    }

    /**
     * Create the country, get the country, delete the country
     */
    @Test
    public void testCGDCountry() {
    	Country c = new Country();
    	c.setBusinessCentre("London");
    	c.setCurrencyCode("nosh");
    	c.setCentralBankCode("BOE");
    	
    	// create the country '1'
    	r1.path("country/1").accept("application/json").method("INPUT", c);

    	// get the country
        GenericType<CountryResource> genericType = new GenericType<CountryResource>() {};
        CountryResource countryResource = r1.path("country/1").
                accept("application/xml").get(genericType);
        Country country = countryResource.getCountry();
        assertEquals("BOE", country.getCentralBankCode());

        // delete country '1'
        r1.path("country/1").method("DELETE");
    }
    
}
