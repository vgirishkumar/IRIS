package org.odata4j.test.unit.consumer.jdbc;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.ext.RuntimeDelegate;

import junit.framework.Assert;

import org.core4j.Enumerable;
import org.core4j.Func;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.odata4j.consumer.ODataConsumer;
import org.odata4j.consumer.ODataConsumerAdapter;
import org.odata4j.core.OEntity;
import org.odata4j.core.OEntityId;
import org.odata4j.core.OEntityIds;
import org.odata4j.core.OEntityKey;
import org.odata4j.core.OProperties;
import org.odata4j.core.OQueryRequest;
import org.odata4j.edm.EdmDataServices;
import org.odata4j.edm.EdmEntitySet;
import org.odata4j.expression.BoolCommonExpression;
import org.odata4j.expression.Expression;
import org.odata4j.producer.command.ProducerCommandContext;
import org.odata4j.producer.exceptions.NotFoundException;
import org.odata4j.producer.jdbc.JdbcModelToMetadata;
import org.odata4j.producer.jdbc.JdbcProducer;
import org.odata4j.producer.jdbc.LoggingCommand;
import org.odata4j.test.unit.Asserts;

public class JdbcConsumerTest {

  private static final String CUSTOMER = "Customer";
  private static final String CUSTOMER_ID = "CustomerId";
  private static final String CUSTOMER_NAME = "CustomerName";

  private static final String CUSTOMER_PRODUCT = "CustomerProduct";

  private static String constantToPascalCase(String constantCase) {
    String[] tokens = constantCase.split("_");
    StringBuilder sb = new StringBuilder();
    for (String token : tokens) {
      if (token.isEmpty())
        continue;
      sb.append(Character.toUpperCase(token.charAt(0)));
      if (token.length() > 1)
        sb.append(token.substring(1).toLowerCase());
    }
    return sb.toString();
  }

  @BeforeClass
  public static void setupClass() {
    // a ResponseBuilder instance is required by negative tests checking for an exception (e.g. NotFoundException)
    ResponseBuilder rbMock = mock(ResponseBuilder.class);
    RuntimeDelegate rdMock = mock(RuntimeDelegate.class);
    when(rdMock.createResponseBuilder()).thenReturn(rbMock);
    RuntimeDelegate.setInstance(rdMock);
  }

  @AfterClass
  public static void cleanTables(){
	  JdbcTest.dropExample();
  }
  
  @Test
  public void jdbcProducer() {

    JdbcTest.populateExample();

    JdbcModelToMetadata modelToMetadata = new JdbcModelToMetadata() {
      @Override
      public String rename(String dbName) {
        return constantToPascalCase(dbName);
      }
    };

    JdbcProducer localProducer = JdbcProducer.newBuilder()
        .jdbc(JdbcTest.HSQL_DB)
        .insert(ProducerCommandContext.class, new LoggingCommand())
        .register(JdbcModelToMetadata.class, modelToMetadata)
        .build();

    ODataConsumer consumer = new ODataConsumerAdapter(localProducer);
    
    // getMetadata
    EdmDataServices metadata = consumer.getMetadata();
    Assert.assertNotNull(metadata);
    JdbcTest.dump(metadata);
    EdmEntitySet customerEntitySet = metadata.findEdmEntitySet(CUSTOMER);
    Assert.assertNotNull(customerEntitySet);
    Assert.assertEquals(CUSTOMER, customerEntitySet.getName());

    // getEntity - simple key
    OEntity entity = consumer.getEntity(CUSTOMER, OEntityKey.create(1)).execute();
    Assert.assertNotNull(entity);
    Assert.assertEquals("Customer One", entity.getProperty(CUSTOMER_NAME).getValue());

    // getEntity - not found
    Asserts.assertThrows(NotFoundException.class, getEntity(consumer, CUSTOMER, OEntityKey.create(-1), null));

    // getEntity - found, but filtered out
    BoolCommonExpression filter = Expression.boolean_(false);
    //Asserts.assertThrows(NotFoundException.class, getEntity(consumer, CUSTOMER, OEntityKey.create(1), "false"));

    // getEntity - complex key
    entity = consumer.getEntity(CUSTOMER_PRODUCT, OEntityKey.create("CustomerId", 1, "ProductId", 1)).execute();
    Assert.assertNotNull(entity);

    // getEntities - no query
    Enumerable<OEntity> entities = consumer.getEntities(CUSTOMER).execute();
    Assert.assertNotNull(entities);
    Assert.assertEquals(2, entities.count());
    //Assert.assertEquals(CUSTOMER, entitiesResponse.getEntitySet().getName());

    // getEntities - not found
    Asserts.assertThrows(NotFoundException.class, getEntities(consumer, "badEntitySet", null));

    // getEntities - id = 1
    filter = Expression.eq(Expression.simpleProperty(CUSTOMER_ID), Expression.literal(1));
    entities = consumer.getEntities(CUSTOMER).filter(CUSTOMER_ID + " eq 1").execute();
    Assert.assertNotNull(entities);
    Assert.assertEquals(1, entities.count());
    Assert.assertEquals("Customer One", entities.first().getProperty(CUSTOMER_NAME).getValue());

    // getEntities - name = 'Customer Two'
    entities = consumer.getEntities(CUSTOMER).filter(CUSTOMER_NAME + " eq 'Customer Two'").execute();
    Assert.assertNotNull(entities);
    Assert.assertEquals(1, entities.count());
    Assert.assertEquals("Customer Two", entities.first().getProperty(CUSTOMER_NAME).getValue());

    // getEntities - 1 = id
    filter = Expression.eq(Expression.literal(1), Expression.simpleProperty(CUSTOMER_ID));
    entities = consumer.getEntities(CUSTOMER).filter(CUSTOMER_ID + " eq 1").execute();
    Assert.assertNotNull(entities);
    Assert.assertEquals(1, entities.count());
    Assert.assertEquals("Customer One", entities.first().getProperty(CUSTOMER_NAME).getValue());

    // getEntities - no results
    filter = Expression.eq(Expression.simpleProperty(CUSTOMER_ID), Expression.literal(-1));
    entities = consumer.getEntities(CUSTOMER).filter(CUSTOMER_ID + " eq -1").execute();
    Assert.assertNotNull(entities);
    Assert.assertEquals(0, entities.count());

    // getEntities - id <> 1
    filter = Expression.ne(Expression.simpleProperty(CUSTOMER_ID), Expression.literal(1));
    entities = consumer.getEntities(CUSTOMER).filter(CUSTOMER_ID + " ne 1").execute();
    Assert.assertNotNull(entities);
    Assert.assertEquals(1, entities.count());
    Assert.assertEquals("Customer Two", entities.first().getProperty(CUSTOMER_NAME).getValue());

    // getEntities - id > 1
    filter = Expression.gt(Expression.simpleProperty(CUSTOMER_ID), Expression.literal(1));
    entities = consumer.getEntities(CUSTOMER).filter(CUSTOMER_ID + " gt 1").execute();
    Assert.assertNotNull(entities);
    Assert.assertEquals(1, entities.count());

    // getEntities - id >= 1
    filter = Expression.ge(Expression.simpleProperty(CUSTOMER_ID), Expression.literal(1));
    entities = consumer.getEntities(CUSTOMER).filter(CUSTOMER_ID + " ge 1").execute();
    Assert.assertNotNull(entities);
    Assert.assertEquals(2, entities.count());

    // getEntities - id < 2
    filter = Expression.lt(Expression.simpleProperty(CUSTOMER_ID), Expression.literal(2));
    entities = consumer.getEntities(CUSTOMER).filter(CUSTOMER_ID + " lt 2").execute();
    Assert.assertNotNull(entities);
    Assert.assertEquals(1, entities.count());

    // getEntities - id <= 2
    filter = Expression.le(Expression.simpleProperty(CUSTOMER_ID), Expression.literal(2));
    entities = consumer.getEntities(CUSTOMER).filter(CUSTOMER_ID + " le 2").execute();
    Assert.assertNotNull(entities);
    Assert.assertEquals(2, entities.count());

    // createEntity - id = 3
    entity = consumer.createEntity(CUSTOMER)
    		.properties(OProperties.int32(CUSTOMER_ID, 3))
    		.properties(OProperties.string(CUSTOMER_NAME, "Customer Three"))
    		.execute();
    Assert.assertNotNull(entity);
    entities = consumer.getEntities(CUSTOMER).execute();
    Assert.assertNotNull(entities);
    Assert.assertEquals(3, entities.count());
    
    // updateEntity - id = 3
    /*
    boolean success = consumer.updateEntity(entity)
    	.properties(OProperties.string(CUSTOMER_NAME, "Customer Three (modified)"))
    	.execute();
    Assert.assertTrue(success);
    entities = consumer.getEntities(CUSTOMER).execute();
    Assert.assertNotNull(entities);
    Assert.assertEquals(3, entities.count());
    entities = consumer.getEntities(CUSTOMER).filter(CUSTOMER_ID + " eq 3").execute();
    Assert.assertNotNull(entities);
    Assert.assertEquals(1, entities.count());
    Assert.assertEquals("Customer Three (modified)", entities.first().getProperty(CUSTOMER_NAME).getValue());
	*/
    
    // deleteEntity - id = 3
    consumer.deleteEntity(CUSTOMER, OEntityKey.create(3)).execute();
    entities = consumer.getEntities(CUSTOMER).execute();
    Assert.assertNotNull(entities);
    Assert.assertEquals(2, entities.count());
    
    // createEntity - id = 3
    entity = consumer.createEntity(CUSTOMER)
            .properties(OProperties.int32(CUSTOMER_ID, 3))
            .properties(OProperties.string(CUSTOMER_NAME, "Customer Three"))
            .execute();
    Assert.assertNotNull(entity);
    entities = consumer.getEntities(CUSTOMER).execute();
    Assert.assertNotNull(entities);
    Assert.assertEquals(3, entities.count());
    
    // deleteEntity - id = 3
    consumer.deleteEntity(CUSTOMER, 3).execute();
    entities = consumer.getEntities(CUSTOMER).execute();
    Assert.assertNotNull(entities);
    Assert.assertEquals(2, entities.count());
    
    // createEntity - id = 3
    entity = consumer.createEntity(CUSTOMER)
            .properties(OProperties.int32(CUSTOMER_ID, 3))
            .properties(OProperties.string(CUSTOMER_NAME, "Customer Three"))
            .execute();
    Assert.assertNotNull(entity);
    entities = consumer.getEntities(CUSTOMER).execute();
    Assert.assertNotNull(entities);
    Assert.assertEquals(3, entities.count());
    
    // deleteEntity - id = 3
    consumer.deleteEntity(OEntityIds.create(CUSTOMER, 3)).execute();
    entities = consumer.getEntities(CUSTOMER).execute();
    Assert.assertNotNull(entities);
    Assert.assertEquals(2, entities.count());
  }

  private static Func<Enumerable<OEntity>> getEntities(final ODataConsumer consumer, final String entitySet, final String filter) {
    return new Func<Enumerable<OEntity>>() {
      @Override
      public Enumerable<OEntity> apply() {
    	OQueryRequest<OEntity> request = consumer.getEntities(entitySet);
    	if (filter != null){
    		request.filter(filter);   		
    	}
        return request.execute();
      }};
  }

  private static Func<OEntity> getEntity(final ODataConsumer consumer, final String entitySet, final OEntityKey key, final String filter) {
    return new Func<OEntity>() {
      @Override
      public OEntity apply() {
        return consumer.getEntity(entitySet, key).execute();
      }};
  }

}
