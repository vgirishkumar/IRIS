package com.temenos.interaction.core.media.xhtml;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.junit.Assert;
import org.junit.Test;
import org.odata4j.core.OEntities;
import org.odata4j.core.OEntity;
import org.odata4j.core.OEntityKey;
import org.odata4j.core.OLink;
import org.odata4j.core.OProperties;
import org.odata4j.core.OProperty;
import org.odata4j.edm.EdmEntitySet;
import org.odata4j.edm.EdmEntityType;
import org.odata4j.edm.EdmProperty;
import org.odata4j.edm.EdmSimpleType;

import com.temenos.interaction.core.entity.Entity;
import com.temenos.interaction.core.entity.EntityMetadata;
import com.temenos.interaction.core.entity.EntityProperties;
import com.temenos.interaction.core.entity.EntityProperty;
import com.temenos.interaction.core.entity.Metadata;
import com.temenos.interaction.core.entity.vocabulary.Vocabulary;
import com.temenos.interaction.core.entity.vocabulary.terms.TermComplexGroup;
import com.temenos.interaction.core.entity.vocabulary.terms.TermComplexType;
import com.temenos.interaction.core.entity.vocabulary.terms.TermIdField;
import com.temenos.interaction.core.entity.vocabulary.terms.TermValueType;
import com.temenos.interaction.core.hypermedia.Link;
import com.temenos.interaction.core.resource.CollectionResource;
import com.temenos.interaction.core.resource.EntityResource;

public class TestXHTMLProvider {

	@Test
	public void testWriteEntityResourceAcceptHTML() throws Exception {
		EntityResource<Entity> er = new EntityResource<Entity>("Customer", createEntity("123", "Fred"));
		List<Link> links = new ArrayList<Link>();
		links.add(new Link(null, "Fred", "Self", "/Customer(123)", null, null, "GET", null));
		er.setLinks(links);

		//Serialize metadata resource
		XHTMLProvider p = new XHTMLProvider(createMockFlightMetadata());
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		p.writeTo(er, EntityResource.class, Entity.class, null, MediaType.TEXT_HTML_TYPE, null, bos);

		String responseString = new String(bos.toByteArray(), "UTF-8");
		Assert.assertTrue(responseString.contains("<li><a href=\"/Customer(123)\">Fred</a></li>"));
		Assert.assertTrue(responseString.contains("WD8 1LK"));
	}

	@Test
	public void testWriteOEntityResourceAcceptHTML() throws Exception {
		EntityResource<OEntity> er = createMockEntityResourceOEntity(createMockEdmEntitySet());
		List<Link> links = new ArrayList<Link>();
		links.add(new Link(null, "Fred", "Self", "/Customer(123)", null, null, "GET", null));
		er.setLinks(links);

		//Serialize metadata resource
		XHTMLProvider p = new XHTMLProvider(createMockFlightMetadata());
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		p.writeTo(er, EntityResource.class, OEntity.class, null, MediaType.TEXT_HTML_TYPE, null, bos);

		String responseString = new String(bos.toByteArray(), "UTF-8");
		Assert.assertTrue(responseString.contains("<li><a href=\"/Customer(123)\">Fred</a></li>"));
		Assert.assertTrue(responseString.contains("<td>name</td><td><div contenteditable=\"true\">Fred</div></td>"));
	}
	
	@Test
	public void testWriteEntityResourceAcceptXHTML() throws Exception {
		EntityResource<Entity> er = new EntityResource<Entity>("Customer", createEntity("123", "Fred"));
		List<Link> links = new ArrayList<Link>();
		links.add(new Link(null, "Fred", "self", "/Customer(123)", null, null, "GET", null));
		er.setLinks(links);

		//Serialize metadata resource
		XHTMLProvider p = new XHTMLProvider(createMockFlightMetadata());
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		p.writeTo(er, EntityResource.class, Entity.class, null, MediaType.APPLICATION_XHTML_XML_TYPE, null, bos);

		String responseString = new String(bos.toByteArray(), "UTF-8");
		Assert.assertTrue(responseString.contains("<dd>WD8 1LK</dd>"));
		Assert.assertTrue(responseString.contains("<ul><li><a href=\"/Customer(123)\" rel=\"self\">Fred</a></li></ul>"));
		Assert.assertTrue(responseString.contains("<link rel=\"self\" href=\"/Customer(123)\">"));
	}
	
	@Test
	public void testWriteCollectionResourceAcceptHTML() throws Exception {
		Collection<EntityResource<Entity>> entities = new ArrayList<EntityResource<Entity>>();
		entities.add(createEntityResource(createEntity("123", "Fred"), "123"));
		entities.add(createEntityResource(createEntity("456", "Tom"), "456"));
		entities.add(createEntityResource(createEntity("789", "Bob"), "789"));
		CollectionResource<Entity> cr = new CollectionResource<Entity>("Customer", entities);
		cr.setEntityName("Customer");

		//Serialize metadata resource
		XHTMLProvider p = new XHTMLProvider(createMockFlightMetadata());
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		p.writeTo(cr, CollectionResource.class, Entity.class, null, MediaType.TEXT_HTML_TYPE, null, bos);

		String responseString = new String(bos.toByteArray(), "UTF-8");
		Assert.assertTrue(responseString.contains("Customer"));
		Assert.assertTrue(responseString.contains("Tom"));
		Assert.assertTrue(responseString.contains("navigate('/Customer(456)')"));
	}
	
	@Test
	public void testWriteCollectionResourceAcceptXHTML() throws Exception {
		Collection<EntityResource<Entity>> entities = new ArrayList<EntityResource<Entity>>();
		entities.add(createEntityResource(createEntity("123", "Fred"), "123"));
		entities.add(createEntityResource(createEntity("456", "Tom"), "456"));
		entities.add(createEntityResource(createEntity("789", "Bob"), "789"));
		CollectionResource<Entity> cr = new CollectionResource<Entity>("Customer", entities);

		//Serialize metadata resource
		XHTMLProvider p = new XHTMLProvider(createMockFlightMetadata());
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		p.writeTo(cr, CollectionResource.class, Entity.class, null, MediaType.APPLICATION_XHTML_XML_TYPE, null, bos);

		String responseString = new String(bos.toByteArray(), "UTF-8");
		Assert.assertTrue(responseString.contains("<li><dl><dt>id</dt><dd>123</dd><dt>address</dt><dl><dt>houseNumber</dt><dd>45</dd><dt>postcode</dt><dd>WD8 1LK</dd></dl><dt>name</dt><dd>Fred</dd></dl><ul><li><a href=\"/Customer(123)\" rel=\"self\">123</a></li></ul></li>"));
	}
	
	private Metadata createMockFlightMetadata() {
		//Define vocabulary for this entity
		Metadata metadata = new Metadata("Customers");
		EntityMetadata vocs = new EntityMetadata("Customer");
				
		Vocabulary vocName = new Vocabulary();
		vocName.setTerm(new TermComplexType(false));
		vocName.setTerm(new TermIdField(true));
		vocs.setPropertyVocabulary("name", vocName);
		
		Vocabulary vocAddress = new Vocabulary();
		vocAddress.setTerm(new TermComplexType(true));
		vocs.setPropertyVocabulary("address", vocAddress);
		
		Vocabulary vocNumbert = new Vocabulary();
		vocNumbert.setTerm(new TermComplexGroup("address"));
		vocNumbert.setTerm(new TermValueType(TermValueType.INTEGER_NUMBER));
		vocs.setPropertyVocabulary("number", vocNumbert);
		
		Vocabulary vocStreet = new Vocabulary();
		vocStreet.setTerm(new TermComplexGroup("address"));
		vocStreet.setTerm(new TermValueType(TermValueType.TEXT));
		vocs.setPropertyVocabulary("street", vocStreet);
		
		Vocabulary vocTown = new Vocabulary();
		vocTown.setTerm(new TermComplexGroup("address"));
		vocTown.setTerm(new TermValueType(TermValueType.TEXT));
		vocs.setPropertyVocabulary("town", vocStreet);
		
		Vocabulary vocPostCode = new Vocabulary();
		vocPostCode.setTerm(new TermComplexGroup("address"));
		vocPostCode.setTerm(new TermValueType(TermValueType.TEXT));
		vocs.setPropertyVocabulary("postCode", vocPostCode);
		
		Vocabulary vocDob = new Vocabulary();
		vocDob.setTerm(new TermComplexType(false));
		vocs.setPropertyVocabulary("dateOfBirth", vocDob);
		
		Vocabulary vocSector = new Vocabulary();
		vocSector.setTerm(new TermComplexType(false));
		vocs.setPropertyVocabulary("sector", vocSector);
		
		Vocabulary vocIndustry = new Vocabulary();
		vocIndustry.setTerm(new TermComplexType(false));
		vocs.setPropertyVocabulary("industry", vocIndustry);
		
		metadata.setEntityMetadata(vocs);
		
		return metadata;
	}
	
	private Entity createEntity(String id, String name) {
		EntityProperties addressFields = new EntityProperties();
		addressFields.setProperty(new EntityProperty("postcode", "WD8 1LK"));
		addressFields.setProperty(new EntityProperty("houseNumber", "45"));
		
		EntityProperties customerFields = new EntityProperties();
		customerFields.setProperty(new EntityProperty("id", id));
		customerFields.setProperty(new EntityProperty("name", name));
		customerFields.setProperty(new EntityProperty("address", addressFields));
		return new Entity("Customer", customerFields);
	}
	
	private EntityResource<Entity> createEntityResource(Entity entity, String id) {
		EntityResource<Entity> entityResource = new EntityResource<Entity>(entity.getName(), entity);
		Collection<Link> links = new ArrayList<Link>();
		links.add(new Link(null, id, "self", "/" + entity.getName() + "(" + id + ")", null, null, "GET", null));
		entityResource.setLinks(links);
		return entityResource;
	}
	
	private EdmEntitySet createMockEdmEntitySet() {
		// Create an entity set
		List<EdmProperty.Builder> eprops = new ArrayList<EdmProperty.Builder>();
		EdmProperty.Builder ep = EdmProperty.newBuilder("id").setType(EdmSimpleType.STRING);
		eprops.add(ep);
		EdmEntityType.Builder eet = EdmEntityType.newBuilder().setNamespace("InteractionTest").setName("Customer").addKeys(Arrays.asList("id")).addProperties(eprops);
		EdmEntitySet.Builder eesb = EdmEntitySet.newBuilder().setName("Customer").setEntityType(eet);
		return eesb.build();
	}

	private EntityResource<OEntity> createMockEntityResourceOEntity(EdmEntitySet ees) {
		//Create an OEntity
		OEntityKey entityKey = OEntityKey.create("123");
		List<OProperty<?>> properties = new ArrayList<OProperty<?>>();
		properties.add(OProperties.string("id", "123"));
		properties.add(OProperties.string("name", "Fred"));
		OEntity entity = OEntities.create(ees, entityKey, properties, new ArrayList<OLink>());
		EntityResource<OEntity> er = new EntityResource<OEntity>("Customer", entity) {};
		return er;
	}
}
