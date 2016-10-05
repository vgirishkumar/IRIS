package com.temenos.useragent.generic.mediatype;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.temenos.useragent.generic.Link;
import com.temenos.useragent.generic.internal.LinkImpl;
import com.theoryinpractise.halbuilder.api.ReadableRepresentation;
import com.theoryinpractise.halbuilder.api.RepresentationFactory;
import com.theoryinpractise.halbuilder.json.JsonRepresentationReader;
import com.theoryinpractise.halbuilder.json.JsonRepresentationWriter;
import com.theoryinpractise.halbuilder.standard.StandardRepresentationFactory;

/**
 * Utility class for supporting HAL JSON media type handlers.
 * 
 * @author ssethupathi
 *
 */
public class HalJsonUtil {

	/**
	 * Extracts links from {@link ReadableRepresentation representation} and
	 * creates collection of {@link Link link}.
	 * 
	 * @param representation
	 * @return links
	 */
	public static List<Link> extractLinks(ReadableRepresentation representation) {
		if (representation == null) {
			throw new IllegalArgumentException("Invalid representation 'null'");
		}
		List<Link> links = new ArrayList<Link>();
		for (com.theoryinpractise.halbuilder.api.Link halLink : representation
				.getLinks()) {
			links.add(new LinkImpl.Builder(halLink.getHref())
					.title(halLink.getTitle()).rel(halLink.getRel()).build());
		}
		return links;
	}

	/**
	 * Initialises {@link RepresentationFactory representation factory} for
	 * hal+json media type.
	 * 
	 * @return representation factory
	 */
	public static RepresentationFactory initRepresentationFactory() {
		return new StandardRepresentationFactory().withReader(
				RepresentationFactory.HAL_JSON, JsonRepresentationReader.class)
				.withRenderer(RepresentationFactory.HAL_JSON,
						JsonRepresentationWriter.class);
	}

	/**
	 * Clones the last child of the given {@link JSONArray json array} and
	 * returns the array added with the cloned child.
	 * 
	 * <p>
	 * Cloned child would have all the nested json arrays added but with no json
	 * object with properties.
	 * </p>
	 * 
	 * @param jsonArray
	 * @return json array
	 */
	public static JSONArray cloneLastChild(JSONArray jsonArray) {
		int cloneableObjIdx = jsonArray.length() - 1;
		if (jsonArray.optJSONObject(cloneableObjIdx) != null) {
			JSONObject cloneableObj = jsonArray.optJSONObject(cloneableObjIdx);
			jsonArray.put(cloneJsonObject(cloneableObj));
		} else if (jsonArray.optJSONArray(cloneableObjIdx) != null) {
			JSONArray cloneableArr = jsonArray.optJSONArray(cloneableObjIdx);
			jsonArray.put(cloneJsonArray(cloneableArr));
		}
		return jsonArray;
	}

	// clones the passed in json object and returns the cloned
	private static JSONObject cloneJsonObject(JSONObject jsonObject) {
		String[] propNames = JSONObject.getNames(jsonObject);
		JSONObject newObj = new JSONObject();
		if (propNames != null) {
			for (String name : propNames) {
				if (jsonObject.optJSONObject(name) != null) {
					newObj.put(name,
							cloneJsonObject(jsonObject.optJSONObject(name)));
				} else if (jsonObject.optJSONArray(name) != null) {
					newObj.put(name,
							cloneJsonArray(jsonObject.optJSONArray(name)));
				}
			}
		}
		return newObj;
	}

	// clones the passed in json array and returns thhe cloned
	private static JSONArray cloneJsonArray(JSONArray cloneableArray) {
		JSONArray newArr = new JSONArray();
		for (int index = 0; index < cloneableArray.length(); index++) {
			if (cloneableArray.optJSONObject(index) != null) {
				newArr.put(cloneJsonObject(cloneableArray.optJSONObject(index)));
			} else if (cloneableArray.optJSONArray(index) != null) {
				newArr.put(cloneJsonArray(cloneableArray.optJSONArray(index)));
			}
		}
		return newArr;
	}
}
