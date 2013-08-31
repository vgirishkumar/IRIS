package com.temenos.interaction.core.command;

import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

import javax.ws.rs.core.GenericEntity;

import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import com.temenos.interaction.core.resource.EntityResource;


public class CommandHelper {
	
	/**
	 * Create a new entity resource.
	 * @param entity entity
	 * @return entity resource
	 */
	public static<E>  EntityResource<E> createEntityResource(E entity) {
		return createEntityResource(null, entity);
	}

	/**
	 * Create a new entity resource
	 * @param entityName entity name
	 * @param entity entity
	 * @return entity resource
	 */
	public static<E>  EntityResource<E> createEntityResource(String entityName, final E entity) {
		return new EntityResource<E>(entityName, entity) {
			@Override
			public GenericEntity<EntityResource<E>> getGenericEntity() {
				//Override the generic type to be the type of the entity rather than 'E' 
				return new GenericEntity<EntityResource<E>>(this, getEffectiveGenericType(this.getClass().getGenericSuperclass(), entity));
			}
		};
	}
	
	/*
	 * Returns the type of the specified entity.
	 * @param genericType generic type
	 * @param entity entity
	 * @return type
	 */
	@SuppressWarnings("rawtypes")
	private static<E> Type getEffectiveGenericType(final Type superClassType, final E entity) {
		Type newGenericType;
		if(superClassType instanceof ParameterizedType) {
			ParameterizedType parametrizedType = (ParameterizedType) superClassType;
			Type[] types = parametrizedType.getActualTypeArguments();
			Type[] newActualTypeArguments = new Type[types.length];
			for(int i=0; i < types.length; i++) {
				Type type = types[i];
				if(type instanceof TypeVariable) {
					final TypeVariable<?> typeVar = (TypeVariable<?>) type;
					newActualTypeArguments[i] = new TypeVariable() {
						@Override
						public Type[] getBounds() {
							return typeVar.getBounds();
						}

						@Override
						public GenericDeclaration getGenericDeclaration() {
							return typeVar.getGenericDeclaration();
						}

						@Override
						public String getName() {
							return entity.getClass().getSimpleName();
						}
						
					};
				}
			}
			newGenericType = ParameterizedTypeImpl.make((Class<?>) parametrizedType.getRawType(), newActualTypeArguments, parametrizedType.getOwnerType());
		}
		else {
			newGenericType = superClassType;
		}
		return newGenericType;
	}
}
