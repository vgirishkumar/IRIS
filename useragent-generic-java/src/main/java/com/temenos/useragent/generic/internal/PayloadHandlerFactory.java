package com.temenos.useragent.generic.internal;

/*
 * #%L
 * useragent-generic-java
 * %%
 * Copyright (C) 2012 - 2016 Temenos Holdings N.V.
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

import com.temenos.useragent.generic.PayloadHandler;

public class PayloadHandlerFactory<T extends PayloadHandler> {
	private final Class<? extends PayloadHandler> handlerClass;

	private PayloadHandlerFactory(
			final Class<? extends PayloadHandler> handlerType) {
		this.handlerClass = handlerType;
	}

	public static <T extends PayloadHandler> PayloadHandlerFactory<T> createFactory(
			final Class<? extends PayloadHandler> handlerClass) {
		if (handlerClass == null) {
			throw new IllegalArgumentException(
					"Illegal payload handler class 'null'");
		}
		return new PayloadHandlerFactory<T>(handlerClass);
	}

	public PayloadHandler createHandler(String payload) {
		try {
			PayloadHandler handler = handlerClass.newInstance();
			handler.setPayload(payload);
			return handler;
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		}
	}
}
