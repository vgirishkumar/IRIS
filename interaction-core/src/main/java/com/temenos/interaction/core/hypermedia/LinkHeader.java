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


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Parser the Link header.  See http://tools.ietf.org/html/rfc5988
 * Based on original from RestEasy LinkHeaderDelegate
 */
public class LinkHeader
{
   private Map<String, Link> linksByRelationship = new HashMap<String, Link>();
   private Map<String, Link> linksByTitle = new HashMap<String, Link>();
   private Map<String, Link> linksByType = new HashMap<String, Link>();
   private List<Link> links = new ArrayList<Link>();

   public LinkHeader addLink(final Link link)
   {
      links.add(link);
      return this;
   }

   public LinkHeader addLink(final String title, final String rel, final String href, final String type)
   {
      final Link link = new Link(title, rel, href, type, null);
      return addLink(link);
   }

   public Link getLinkByTitle(String title)
   {
      return linksByTitle.get(title);
   }

   public Link getLinkByRelationship(String rel)
   {
      return linksByRelationship.get(rel);
   }

   /**
    * Index of links by relationship "rel" or "rev"
    *
    * @return
    */
   public Map<String, Link> getLinksByRelationship()
   {
      return linksByRelationship;
   }

   /**
    * Index of links by title
    *
    * @return
    */
   public Map<String, Link> getLinksByTitle()
   {
      return linksByTitle;
   }

   /**
    * Index of links by type
    *
    * @return
    */
   public Map<String, Link> getLinksByType()
   {
      return linksByType;
   }

   /**
    * All the links defined
    *
    * @return
    */
   public List<Link> getLinks()
   {
      return links;
   }

   public static LinkHeader valueOf(String val)
   {
      return LinkHeaderDelegate.from(val);
   }

   public String toString()
   {
      return LinkHeaderDelegate.getString(this);
   }
}