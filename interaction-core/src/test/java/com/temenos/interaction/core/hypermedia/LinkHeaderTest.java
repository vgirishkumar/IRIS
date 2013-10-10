package com.temenos.interaction.core.hypermedia;

/*
 * #%L
 * interaction-core
 * %%
 * Copyright (C) 2012 - 2013 Temenos Holdings N.V.
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


import org.junit.Assert;
import org.junit.Test;

public class LinkHeaderTest {

   @Test
   public void testTopic() throws Exception
   {
      LinkHeaderDelegate delegate = new LinkHeaderDelegate();
      LinkHeader header = delegate.fromString("<http://localhost:8081/linkheader/topic/sender>; rel=\"sender\"; title=\"sender\", <http://localhost:8081/linkheader/topic/poller>; rel=\"top-message\"; title=\"top-message\"");
      Link sender = header.getLinkByTitle("sender");
      Assert.assertNotNull(sender);
      Assert.assertEquals("http://localhost:8081/linkheader/topic/sender", sender.getHref());
      Assert.assertEquals("sender", sender.getRel());
      Link top = header.getLinkByTitle("top-message");
      Assert.assertNotNull(top);
      Assert.assertEquals("http://localhost:8081/linkheader/topic/poller", top.getHref());
      Assert.assertEquals("top-message", top.getRel());

   }

   @Test
   public void testTopic2() throws Exception
   {
      LinkHeaderDelegate delegate = new LinkHeaderDelegate();
      LinkHeader header = delegate.fromString("<http://localhost:8081/topics/test/poller/next?index=0>; rel=\"next-message\"; title=\"next-message\",<http://localhost:8081/topics/test/poller>; rel=\"generator\"; title=\"generator\"");
      Link next = header.getLinkByTitle("next-message");
      Assert.assertNotNull(next);
      Assert.assertEquals("http://localhost:8081/topics/test/poller/next?index=0", next.getHref());
      Assert.assertEquals("next-message", next.getRel());
      Link generator = header.getLinkByTitle("generator");
      Assert.assertNotNull(generator);
      Assert.assertEquals("http://localhost:8081/topics/test/poller", generator.getHref());
      Assert.assertEquals("generator", generator.getRel());


   }

   @Test
   public void testLinkheader() throws Exception
   {
      LinkHeaderDelegate delegate = new LinkHeaderDelegate();
      LinkHeader header = delegate.fromString("<http://example.com/TheBook/chapter2>; rel=\"previous\";\n" +
              "         title=\"previous chapter\"");

      Assert.assertTrue(header.getLinksByTitle().containsKey("previous chapter"));
      Assert.assertTrue(header.getLinksByRelationship().containsKey("previous"));
      Assert.assertEquals(header.getLinksByTitle().get("previous chapter").getHref(), "http://example.com/TheBook/chapter2");
      System.out.println(delegate.toString(header));
      String str = delegate.toString(header);
      header = delegate.fromString(str);
      Assert.assertTrue(header.getLinksByTitle().containsKey("previous chapter"));
      Assert.assertTrue(header.getLinksByRelationship().containsKey("previous"));
      Assert.assertEquals(header.getLinksByTitle().get("previous chapter").getHref(), "http://example.com/TheBook/chapter2");
   }

   @Test
   public void testLinkheader2() throws Exception
   {
      LinkHeaderDelegate delegate = new LinkHeaderDelegate();
      LinkHeader header = delegate.fromString("<http://example.org/>; rel=index;\n" +
              "             rel=\"start http://example.net/relation/other\"");
      Assert.assertTrue(header.getLinksByRelationship().containsKey("index"));
      Assert.assertTrue(header.getLinksByRelationship().containsKey("start"));
      Assert.assertTrue(header.getLinksByRelationship().containsKey("http://example.net/relation/other"));
      System.out.println(delegate.toString(header));
      String str = delegate.toString(header);
      header = delegate.fromString(str);
      Assert.assertTrue(header.getLinksByRelationship().containsKey("index"));
      Assert.assertTrue(header.getLinksByRelationship().containsKey("start"));
      Assert.assertTrue(header.getLinksByRelationship().containsKey("http://example.net/relation/other"));
   }

   @Test
   public void testLinkheader3() throws Exception
   {
      LinkHeaderDelegate delegate = new LinkHeaderDelegate();
      LinkHeader header = delegate.fromString("<http://example.org/>; rel=\"index\"; type=\"text/html\"");
      Assert.assertTrue(header.getLinksByType().containsKey("text/html"));
      System.out.println(delegate.toString(header));
      String str = delegate.toString(header);
      header = delegate.fromString(str);
      Assert.assertTrue(header.getLinksByType().containsKey("text/html"));
   }

   @Test
   public void testAdd()
   {
      final LinkHeader linkHeader = new LinkHeader();
      Assert.assertEquals(linkHeader.getLinks().size(), 0);
      linkHeader.addLink(new Link("one", "resl-1", "href-1", null, null));
      Assert.assertEquals(linkHeader.getLinks().size(), 1);
      linkHeader.addLink(new Link("two", "resl-2", "href-2", null, null));
      Assert.assertEquals(linkHeader.getLinks().size(), 2);
   }
}
