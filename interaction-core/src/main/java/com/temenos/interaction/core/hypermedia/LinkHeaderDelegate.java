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


import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.RuntimeDelegate;

import com.temenos.interaction.core.MultivaluedMapImpl;

/**
 * Parser the Link header.  See http://tools.ietf.org/html/rfc5988
 * Based on original from RestEasy LinkHeaderDelegate
 */
public class LinkHeaderDelegate implements RuntimeDelegate.HeaderDelegate<LinkHeader>
{
   private static class Parser
   {
      private int curr;
      private String value;
      private LinkHeader header = new LinkHeader();

      public Parser(String value)
      {
         this.value = value;
      }

      public LinkHeader getHeader()
      {
         return header;
      }

      public void parse()
      {
         String href = null;
         MultivaluedMap<String, String> attributes = new MultivaluedMapImpl<String>();
         while (curr < value.length())
         {

            char c = value.charAt(curr);
            if (c == '<')
            {
               if (href != null)
                  throw new IllegalArgumentException("Uanble to parse Link header.  Too many links in declaration: " + value);
               href = parseLink();
            }
            else if (c == ';' || c == ' ')
            {
               curr++;
               continue;
            }
            else if (c == ',')
            {
               populateLink(href, attributes);
               href = null;
               attributes = new MultivaluedMapImpl<String>();
               curr++;
            }
            else
            {
               parseAttribute(attributes);
            }
         }
         populateLink(href, attributes);


      }

      protected void populateLink(String href, MultivaluedMap<String, String> attributes)
      {
         List<String> rels = attributes.get("rel");
         List<String> revs = attributes.get("rev");
         String title = attributes.getFirst("title");
         if (title != null) attributes.remove("title");
         String type = attributes.getFirst("type");
         if (type != null) attributes.remove("type");

         Set<String> relationships = new HashSet<String>();
         if (rels != null)
         {
            relationships.addAll(rels);
            attributes.remove("rel");
         }
         if (revs != null)
         {
            relationships.addAll(revs);
            attributes.remove("rev");
         }

         for (String relationship : relationships)
         {
            StringTokenizer tokenizer = new StringTokenizer(relationship);
            while (tokenizer.hasMoreTokens())
            {
               String rel = tokenizer.nextToken();
               Link link = new Link(title, rel, href, type, attributes);
               header.getLinksByRelationship().put(rel, link);
               header.getLinksByTitle().put(title, link);
               header.getLinksByType().put(type, link);
               header.getLinks().add(link);
            }

         }
      }

      public String parseLink()
      {
         int end = value.indexOf('>', curr);
         if (end == -1) throw new IllegalArgumentException("Unable to parse Link header.  No end to link: " + value);
         String href = value.substring(curr + 1, end);
         curr = end + 1;
         return href;
      }

      public void parseAttribute(MultivaluedMap<String, String> attributes)
      {
         int end = value.indexOf('=', curr);
         if (end == -1 || end + 1 >= value.length())
            throw new IllegalArgumentException("Unable to parse Link header.  No end to parameter: " + value);
         String name = value.substring(curr, end);
         name = name.trim();
         curr = end + 1;
         String val = null;
         if (curr >= value.length())
         {
            val = "";
         }
         else
         {

            if (value.charAt(curr) == '"')
            {
               if (curr + 1 >= value.length())
                  throw new IllegalArgumentException("Unable to parse Link header.  No end to parameter: " + value);
               curr++;
               end = value.indexOf('"', curr);
               if (end == -1)
                  throw new IllegalArgumentException("Unable to parse Link header.  No end to parameter: " + value);
               val = value.substring(curr, end);
               curr = end + 1;
            }
            else
            {
               StringBuffer buf = new StringBuffer();
               while (curr < value.length())
               {
                  char c = value.charAt(curr);
                  if (c == ',' || c == ';') break;
                  buf.append(value.charAt(curr));
                  curr++;
               }
               val = buf.toString();
            }
         }
         attributes.add(name, val);

      }

   }

   public LinkHeader fromString(String value) throws IllegalArgumentException
   {
      return from(value);
   }

   public static LinkHeader from(String value) throws IllegalArgumentException
   {
      Parser parser = new Parser(value);
      parser.parse();
      return parser.getHeader();

   }

   public String toString(LinkHeader value)
   {
      return getString(value);
   }

   public static String getString(LinkHeader value)
   {
      StringBuffer buf = new StringBuffer();
      for (Link link : value.getLinks())
      {
         if (buf.length() > 0) buf.append(", ");
         buf.append(link.toString());
      }
      return buf.toString();
   }
}