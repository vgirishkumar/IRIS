package com.temenos.interaction.core.hypermedia;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.MultivaluedMap;

/**
 * A Link (see http://tools.ietf.org/html/rfc5988)
 * @author aphethean
 */
public class Link {

	private final Transition transition;
	
	// title (5.4 Target Attributes)
	private final String title;
	// rel (5.3 Relation Type)
	private final String rel;
	// href (5.1 Target IRI)
	private final String href;
	// type (5.4 Target Attributes)
	private final String[] produces;
	
	// extensions
	private final String method;
	private final String[] consumes;
	private final MultivaluedMap<String, String> extensions;

	/**
	 * Construct a simple link used for GET operations.
	 * @param title
	 * @param rel
	 * @param href
	 * @param type
	 * @param extensions
	 */
	public Link(String title, String rel, String href, String type, MultivaluedMap<String, String> extensions) {
		this(null, title, rel, href, null, (type != null ? type.split(" ") : null), HttpMethod.GET, null);
	}

	/**
	 * Construct a link from a transition.
	 * @param transition
	 */
	public Link(Transition transition, String rel, String href, String method) {
		this(transition, transition.getId(), rel, href, null, null, method, null);
	}

	public Link(Transition transition, String title, String rel, String href, String[] consumes,
			String[] produces, String method, MultivaluedMap<String, String> extensions) {
		this.transition = transition;
		this.title = title;
		this.rel = rel;
		this.href = href;
		this.consumes = consumes;
		this.produces = produces;
		this.method = method;
		this.extensions = extensions;
	}

	public Transition getTransition() {
		return transition;
	}

	public String getRel() {
		return rel;
	}

	public String getMethod() {
		return method;
	}

	public String getHref() {
		return href;
	}

	/**
	 * Obtain the transition, i.e. the link relative to the REST service.
	 * 
	 * @param href Full URL
	 * @param basePath  Path to REST service
	 * @return Path of transition relative to REST service 
	 */
	public String getHrefTransition(String basePath) {
		String regex = "(?<=" + basePath + "/)\\S+";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(href);
		while (m.find()) {
			return m.group();
		}    
		return href;
	}
	 
	public String[] getConsumes() {
		return consumes;
	}

	public String[] getProduces() {
		return produces;
	}

	public String getTitle() {
		return title;
	}

	public MultivaluedMap<String, String> getExtensions() {
		return extensions;
	}

	   public String toString()
	   {
	      StringBuffer buf = new StringBuffer("<");
	      buf.append(href).append(">");
	      if (rel != null)
	      {
	         buf.append("; rel=\"").append(rel).append("\"");
	      }
	      if (produces != null && produces.length > 0)
	      {
	         buf.append("; type=\"").append(produces[0]);
	    	 for (int i = 1; i < produces.length; i++)
	    		 buf.append(",").append(produces[i]);
	         buf.append("\"");
	      }
	      if (title != null)
	      {
	         buf.append("; title=\"").append(title).append("\"");
	      }
	      if (extensions != null) {
		      for (String key : getExtensions().keySet())
		      {
		         List<String> values = getExtensions().get(key);
		         for (String val : values)
		         {
		            buf.append("; ").append(key).append("=\"").append(val).append("\"");
		         }
		      }
	      }
	      return buf.toString();
	   }

}
