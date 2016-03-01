package com.temenos.interaction.core.hypermedia;

/*
 * #%L
 * interaction-core
 * %%
 * Copyright (C) 2012 - 2014 Temenos Holdings N.V.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */


import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.odata4j.core.OCollection;
import org.odata4j.core.OCollections;
import org.odata4j.core.OComplexObjects;
import org.odata4j.core.OProperties;
import org.odata4j.core.OProperty;
import org.odata4j.edm.EdmCollectionType;
import org.odata4j.edm.EdmComplexType;
import org.odata4j.edm.EdmEntitySet;
import org.odata4j.edm.EdmEntityType;
import org.odata4j.edm.EdmProperty;
import org.odata4j.edm.EdmSimpleType;

public class TestHypermediaTemplateHelper {

	@Test
	public void testGetBaseUri() {
		assertEquals("http://localhost:8080/responder/rest/", 
				HypermediaTemplateHelper.getTemplatedBaseUri("http://localhost:8080/responder/rest", "http://localhost:8080/responder/rest/"));
	}

	@Test
	public void testGetBaseUriSimple() {
		assertEquals("http://localhost:8080/responder/rest/", 
				HypermediaTemplateHelper.getTemplatedBaseUri("http://localhost:8080/responder/rest", "http://localhost:8080/responder/rest/test"));
	}

	@Test
	public void testGetTemplatedBaseUri() {
		assertEquals("http://localhost:8080/responder/rest/MockCompany001/", 
				HypermediaTemplateHelper.getTemplatedBaseUri("http://localhost:8080/responder/rest/{companyid}", "http://localhost:8080/responder/rest/MockCompany001/"));
	}

	@Test
	public void testGetTemplatedBaseUriSimple() {
		assertEquals("http://localhost:8080/responder/rest/MockCompany001/", 
				HypermediaTemplateHelper.getTemplatedBaseUri("http://localhost:8080/responder/rest/{companyid}", "http://localhost:8080/responder/rest/MockCompany001/test"));
	}

	@Test
	public void testGetTemplatedBaseUriNested() {
		assertEquals("http://localhost:8080/responder/rest/MockCompany001/", 
				HypermediaTemplateHelper.getTemplatedBaseUri("http://localhost:8080/responder/rest/{companyid}", "http://localhost:8080/responder/rest/MockCompany001/test/blah"));
	}

	@Test
	public void testGetTemplatedBaseUriQueryParameters() {
		assertEquals("http://localhost:8080/responder/rest/MockCompany001/", 
				HypermediaTemplateHelper.getTemplatedBaseUri("http://localhost:8080/responder/rest/{companyid}", "http://localhost:8080/responder/rest/MockCompany001/test?blah=123"));
	}

	@Test
	public void testGetTemplatedBaseUriLookBehind() {
		assertEquals("http://127.0.0.1:9081/hothouse-iris/Hothouse.svc/GB0010001/", 
				HypermediaTemplateHelper.getTemplatedBaseUri("http://127.0.0.1:9081/hothouse-iris/Hothouse.svc/{companyid}/", "http://127.0.0.1:9081/hothouse-iris/Hothouse.svc/GB0010001/root"));
	}
	
	@Test
	public void testGetTemplatedBaseUriLookBehind_FundsTransferNew() {
		assertEquals("http://portal3.xlb2.lo/tapt24-iris/tapt24.svc/LU0010001/", 
				HypermediaTemplateHelper.getTemplatedBaseUri("http://portal3.xlb2.lo/tapt24-iris/tapt24.svc/{companyid}/", "http://portal3.xlb2.lo/tapt24-iris/tapt24.svc/LU0010001/FundsTransfer_FtTaps()/new"));
	}
	
	@Test
	public void testGetTemplatedBaseUriLookBehind_FundsTransferValidate() {
		assertEquals("http://portal3.xlb2.lo/tapt24-iris/tapt24.svc/LU0010001/", 
				HypermediaTemplateHelper.getTemplatedBaseUri("http://portal3.xlb2.lo/tapt24-iris/tapt24.svc/{companyid}/", "http://portal3.xlb2.lo/tapt24-iris/tapt24.svc/LU0010001/FundsTransfer_FtTaps('FT1336500058')/validate"));
	}
	
	@Test
	public void testGetTemplatedBaseUriLookBehind_FundsTransferFtTaps() {
		assertEquals("http://portal3.xlb2.lo/tapt24-iris/tapt24.svc/LU0010001/", 
				HypermediaTemplateHelper.getTemplatedBaseUri("http://portal3.xlb2.lo/tapt24-iris/tapt24.svc/{companyid}/", "http://portal3.xlb2.lo/tapt24-iris/tapt24.svc/LU0010001/FundsTransfer_FtTaps()"));
	}
	
	@Test
	public void testGetTemplatedBaseUriLookBehind_FundsTransferFtTapsNewHol() {
		assertEquals("http://portal3.xlb2.lo/tapt24-iris/tapt24.svc/LU0010001/", 
				HypermediaTemplateHelper.getTemplatedBaseUri("http://portal3.xlb2.lo/tapt24-iris/tapt24.svc/{companyid}/", "http://portal3.xlb2.lo/tapt24-iris/tapt24.svc/LU0010001/FundsTransfer_FtTaps()/new/hold?id=FT1336500058"));
	}
	
	@Test
	public void testGetTemplatedBaseUriLastCharacter() {
		assertEquals("http://localhost:8080/example/interaction-odata-multicompany.svc/MockCompany001/", 
				HypermediaTemplateHelper.getTemplatedBaseUri("http://localhost:8080/example/interaction-odata-multicompany.svc/{companyid}/", "http://localhost:8080/example/interaction-odata-multicompany.svc/MockCompany001"));
	}
		
	@Test
	public void testTemplateReplace() {
		Map<String,Object> properties = new HashMap<String,Object>();
		properties.put("companyid", "GB0010001");
		assertEquals("http://127.0.0.1:9081/hothouse-iris/Hothouse.svc/GB0010001/",
				HypermediaTemplateHelper.templateReplace("http://127.0.0.1:9081/hothouse-iris/Hothouse.svc/{companyid}/", properties));
	}

	@Test
	public void testSpecialCharacterTemplateReplace() {
		Map<String,Object> properties = new HashMap<String,Object>();
		properties.put("companyid", "GB0010001");
		assertEquals("http://127.0.0.1:9081/hothouse-iris/Hothouse.svc/GB0010001/$metadata",
				HypermediaTemplateHelper.templateReplace("http://127.0.0.1:9081/hothouse-iris/Hothouse.svc/{companyid}/$metadata", properties));
	}

	@Test
	public void testPartialTemplateReplace() {
		Map<String,Object> properties = new HashMap<String,Object>();
		properties.put("companyid", "GB0010001");
		assertEquals("http://127.0.0.1:9081/hothouse-iris/Hothouse.svc/GB0010001/flights/{id}",
				HypermediaTemplateHelper.templateReplace("http://127.0.0.1:9081/hothouse-iris/Hothouse.svc/{companyid}/flights/{id}", properties));
	}
	
	@Test
    public void testMultiValueTemplateReplace() {
	    
	    EdmEntitySet setType;
	    EdmEntityType airportsType;
	    EdmCollectionType airportCollectionType;
	    EdmComplexType airportType;
	    
	    List<EdmProperty.Builder> subprops = new ArrayList<EdmProperty.Builder>();
        subprops.add(EdmProperty.newBuilder("AirportName").setType(EdmSimpleType.STRING));
        airportType = EdmComplexType.newBuilder().setNamespace("InteractionTest").setName("AirportsAirport").addProperties(subprops).build();
        
        List<EdmProperty.Builder> eprops = new ArrayList<EdmProperty.Builder>();
        eprops.add(EdmProperty.newBuilder("airport").setType(new EdmCollectionType(EdmProperty.CollectionKind.Bag, airportType)));
                   
        EdmEntityType.Builder eet = EdmEntityType.newBuilder().setNamespace("InteractionTest").setName("Airports").addKeys(Arrays.asList("ID")).addProperties(eprops);
        EdmEntitySet.Builder ees = EdmEntitySet.newBuilder().setName("Airports").setEntityType(eet);

        setType = ees.build();
        airportsType = (EdmEntityType)setType.getType();
        airportCollectionType = (EdmCollectionType)airportsType.findDeclaredProperty("airport").getType();
        airportType = (EdmComplexType)airportCollectionType.getItemType();
        
        List<OProperty<?>> oPropertiesCity1 = new ArrayList<OProperty<?>>();
        oPropertiesCity1.add(OProperties.string("AirportName", "London"));
        
        List<OProperty<?>> oPropertiesCity2 = new ArrayList<OProperty<?>>();
        oPropertiesCity2.add(OProperties.string("AirportName", "Lisbon"));
        
        List<OProperty<?>> oPropertiesCity3 = new ArrayList<OProperty<?>>();
        oPropertiesCity3.add(OProperties.string("AirportName", "Madrid"));

        OCollection<?> city1 = OCollections.newBuilder(airportCollectionType).
            add(OComplexObjects.create(airportType, oPropertiesCity1)).build();
        
        OCollection<?> city2 = OCollections.newBuilder(airportCollectionType).
            add(OComplexObjects.create(airportType, oPropertiesCity2)).build();
        
        OCollection<?> city3 = OCollections.newBuilder(airportCollectionType).
            add(OComplexObjects.create(airportType, oPropertiesCity3)).build();
	    
        Map<String,Object> properties = new LinkedHashMap<String,Object>();
        properties.put("multivaluegroup1", city1);
        properties.put("multivaluegroup2", city2);
        properties.put("multivaluegroup3", city3);
        
        assertEquals("http://127.0.0.1:9081/hothouse-iris/Hothouse.svc/{companyid}/flights/London",
                HypermediaTemplateHelper.templateReplace("http://127.0.0.1:9081/hothouse-iris/Hothouse.svc/{companyid}/flights/{AirportName}", properties));
    }
}
