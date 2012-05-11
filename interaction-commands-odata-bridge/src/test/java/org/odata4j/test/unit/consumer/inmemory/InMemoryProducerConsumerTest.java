package org.odata4j.test.unit.consumer.inmemory;

import junit.framework.Assert;

import org.core4j.Enumerable;
import org.core4j.Func;
import org.junit.Test;
import org.odata4j.consumer.ODataConsumer;
import org.odata4j.consumer.ODataConsumerAdapter;
import org.odata4j.core.OEntity;
import org.odata4j.core.OEntityKey;
import org.odata4j.producer.inmemory.InMemoryProducer;

public class InMemoryProducerConsumerTest {

  
  @Test
  public void testEntitySetsQuery() {

    final SimpleEntity e1 = new SimpleEntity();
    
    String namespace = "testSetNameAndType";
    InMemoryProducer p = new InMemoryProducer(namespace);
    ODataConsumer c = new ODataConsumerAdapter(p);
    
    p.register(SimpleEntity.class, "entitySetName", "entityTypeName", new Func<Iterable<SimpleEntity>>() {
      @Override
      public Iterable<SimpleEntity> apply() {
        return Enumerable.create(e1);
      }
    }, "Id");

    Assert.assertEquals(1, c.getEntitySets().count());
    Assert.assertEquals("entitySetName", c.getEntitySets().first().getTitle());
    Assert.assertEquals("entitySetName", c.getEntitySets().first().getHref());

  }  
  
  @Test
  public void testSimpleEntity() {

    final SimpleEntity e1 = new SimpleEntity();
    final SimpleEntity e2 = new SimpleEntity();
    final SimpleEntity e3 = new SimpleEntity();
    
    String namespace = "testSetNameAndType";
    InMemoryProducer p = new InMemoryProducer(namespace);
    ODataConsumer c = new ODataConsumerAdapter(p);
    
    p.register(SimpleEntity.class, "entitySetName", "entityTypeName", new Func<Iterable<SimpleEntity>>() {
      @Override
      public Iterable<SimpleEntity> apply() {
        return Enumerable.create(e1, e2, e3);
      }
    }, "Id");

    Assert.assertEquals(3, c.getEntities("entitySetName").execute().count());
    Assert.assertNotNull(c.getEntity("entitySetName", OEntityKey.create(e1.getId())).execute());

    Assert.assertNotNull(p.getMetadata().findEdmEntitySet("entitySetName"));
    Assert.assertNotNull(p.getMetadata().findEdmEntityType(namespace + ".entityTypeName"));
  }
    
  private static class SimpleEntity {
    private final int integer;

    public SimpleEntity() {
      this(0);
    }

    public SimpleEntity(int integer) {
      this.integer = integer;
    }

    public String getId() {
      return String.valueOf(System.identityHashCode(this));
    }

    public String getString() {
      return "string-" + getId();
    }

    public boolean getBool() {
      return false;
    }

    public int getInteger() {
      return integer;
    }
  }
}
