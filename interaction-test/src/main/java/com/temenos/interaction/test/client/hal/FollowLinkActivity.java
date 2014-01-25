package com.temenos.interaction.test.client.hal;

/*
 * #%L
 * interaction-test
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


import java.util.HashMap;
import java.util.Map;

import com.temenos.interaction.test.client.Actions;
import com.temenos.interaction.test.client.Activity;
import com.theoryinpractise.halbuilder.spi.Representation;

/**
 * This class implements an Activity that will follow a series of links 
 * to a goal for the HAL media type.
 * @author aphethean
 */
public class FollowLinkActivity extends Activity {

	private Map<LinkEvent, Actions> eventActions = new HashMap<LinkEvent, Actions>();
	
	public FollowLinkActivity() {};
	
	/**
	 * Add a new link event.  When a {@link LinkEvent}s is seen, the 
	 * associated {@link Actions} will be taken.
	 * @param event
	 * @param actions
	 */
	public void addLinkEvent(LinkEvent event, Actions actions) {
		eventActions.put(event, actions);
	}
	
	/**
	 * GET the supplied URI and process any {@link LinkEvent}s
	 */
	public Representation go(String startUri) {
		
		return null;
	}
}
