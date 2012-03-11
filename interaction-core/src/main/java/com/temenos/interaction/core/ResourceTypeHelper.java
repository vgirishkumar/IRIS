package com.temenos.interaction.core;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

public class ResourceTypeHelper {
	public static boolean isType(Class<?> type, Type genericType, Type expectedType) {
		return isType(type, genericType, expectedType, null);
	}
	
	public static boolean isType(Class<?> type, Type genericType, Type expectedType, Class<?> expectedTypeParameter) {
		if(type.equals(expectedType)) {
			return true;
		}
		else if(genericType instanceof ParameterizedType) {
			//This is a ParameterizedType such as EntityResource<OEntity>
			ParameterizedType parameterizedType = (ParameterizedType) genericType;
			if(parameterizedType.getRawType().equals(expectedType)) {
				//Check the type e.g. EntityResource
				if(expectedTypeParameter == null) {
					return true;
				}
				else {
					//Check the type parameter, e.g. OEntity 
					Type[] actualTypeArgs = parameterizedType.getActualTypeArguments();
					if(actualTypeArgs.length == 1) {
						Type actualType = actualTypeArgs[0];
						if(actualType instanceof TypeVariable) {
							return ((TypeVariable<?>) actualType).getName().equals(expectedTypeParameter.getSimpleName());
						}
						else {
							return actualType.equals(expectedTypeParameter);
						}
					}
				}
			}
		}
		return false;
	}
}
