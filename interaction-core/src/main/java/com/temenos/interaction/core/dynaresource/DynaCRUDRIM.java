package com.temenos.interaction.core.dynaresource;

import org.apache.wink.common.DynamicResource;
import org.odata4j.producer.ODataProducer;

import com.temenos.interaction.core.state.CRUDResourceInteractionModel;

/**
 * Define a Dynamic CRUD based Resource Interaction Model
 * CRUD (Create, Read, Update, Delete) interaction are simple, this class
 * can be used to dynamically define resources that are managed with CRUD
 * interactions
 * @author aphethean
 */
public class DynaCRUDRIM extends CRUDResourceInteractionModel implements DynamicResource {

	private String beanName;
	private String path;
	private ODataProducer producer; 
	
	public DynaCRUDRIM(String path) {
		super(path);
		this.path= path;
	}
	
	public ODataProducer getProducer() {
		return producer;
	}

	public void setProducer(ODataProducer producer) {
		this.producer = producer;
	}


	@Override
	public String getBeanName() {
		return beanName;
	}

	@Override
	public String getCollectionTitle() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getParent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPath() {
		return path;
	}

	@Override
	public String getWorkspaceTitle() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	@Override
	public void setParent(Object arg0) {
		// TODO Auto-generated method stub
		
	}

}
