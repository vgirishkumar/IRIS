package com.interaction.example.odata.northwind;

/*
 * #%L
 * interaction-example-odata-northwind
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

import com.temenos.interaction.core.hypermedia.Action;
import com.temenos.interaction.core.hypermedia.CollectionResourceState;
import com.temenos.interaction.core.hypermedia.ResourceState;
import com.temenos.interaction.core.hypermedia.ResourceStateMachine;

public class Behaviour {

	public ResourceState getSimpleODataInteractionModel() {
		// the service root
		ResourceState initialState = new ResourceState("ServiceDocument", "begin", createActionList(new Action("GETServiceDocument", Action.TYPE.VIEW), null), "/");
		ResourceState metadata = new ResourceState("Metadata", "metadata", createActionList(new Action("GETMetadata", Action.TYPE.VIEW), null), "/$metadata");

		ResourceStateMachine categories = getCategoriesSM();
		ResourceStateMachine customers = getCustomersSM();
		ResourceStateMachine employees = getEmployeesSM();
		ResourceStateMachine orders = getOrdersSM();
		ResourceStateMachine orderDetails = getOrderDetailsSM();
		ResourceStateMachine products = getProductsSM();
		ResourceStateMachine suppliers = getSuppliersSM();

		//Add transitions between RSMs
		initialState.addTransition("GET", metadata);
		initialState.addTransition("GET", categories);
		initialState.addTransition("GET", customers);
		initialState.addTransition("GET", employees);
		initialState.addTransition("GET", orders);
		initialState.addTransition("GET", orderDetails);
		initialState.addTransition("GET", products);
		initialState.addTransition("GET", suppliers);
		
		return initialState;
	}

	public ResourceStateMachine getCategoriesSM() {
		CollectionResourceState categories = new CollectionResourceState("Categories", "Categories", createActionList(new Action("GETEntities", Action.TYPE.VIEW), null), "/Categories");
		ResourceState pseudo = new ResourceState(categories, "Categories_pseudo_created", createActionList(null, new Action("CreateEntity", Action.TYPE.ENTRY)));
		ResourceState category = new ResourceState("Categories", "category", createActionList(new Action("GETEntity", Action.TYPE.VIEW), null), "/Categories({id})");

		//Add state transitions
		Map<String, String> uriLinkageMap = new HashMap<String, String>();
		uriLinkageMap.put("id", "CategoryID");
		categories.addTransitionForEachItem("GET", category, uriLinkageMap);
		categories.addTransition("POST", pseudo);

		return new ResourceStateMachine(categories);
	}
	
	public ResourceStateMachine getCustomersSM() {
		CollectionResourceState customers = new CollectionResourceState("Customers", "Customers", createActionList(new Action("GETEntities", Action.TYPE.VIEW), null), "/Customers");
		ResourceState pseudo = new ResourceState(customers, "Customers_pseudo_created", createActionList(null, new Action("CreateEntity", Action.TYPE.ENTRY)));
		ResourceState category = new ResourceState("Customers", "category", createActionList(new Action("GETEntity", Action.TYPE.VIEW), null), "/Customers({id})");

		//Add state transitions
		Map<String, String> uriLinkageMap = new HashMap<String, String>();
		uriLinkageMap.put("id", "CustomerID");
		customers.addTransitionForEachItem("GET", category, uriLinkageMap);
		customers.addTransition("POST", pseudo);

		return new ResourceStateMachine(customers);
	}

	public ResourceStateMachine getEmployeesSM() {
		CollectionResourceState employees = new CollectionResourceState("Employees", "Employees", createActionList(new Action("GETEntities", Action.TYPE.VIEW), null), "/Employees");
		ResourceState pseudo = new ResourceState(employees, "Employees_pseudo_created", createActionList(null, new Action("CreateEntity", Action.TYPE.ENTRY)));
		ResourceState employee = new ResourceState("Employees", "employee", createActionList(new Action("GETEntity", Action.TYPE.VIEW), null), "/Employees({id})");

		//Add state transitions
		Map<String, String> uriLinkageMap = new HashMap<String, String>();
		uriLinkageMap.put("id", "EmployeeID");
		employees.addTransitionForEachItem("GET", employee, uriLinkageMap);
		employees.addTransition("POST", pseudo);

		return new ResourceStateMachine(employees);
	}
	
	public ResourceStateMachine getOrdersSM() {
		CollectionResourceState orders = new CollectionResourceState("Orders", "Orders", createActionList(new Action("GETEntities", Action.TYPE.VIEW), null), "/Orders");
		ResourceState pseudo = new ResourceState(orders, "Orders_pseudo_created", createActionList(null, new Action("CreateEntity", Action.TYPE.ENTRY)));
		ResourceState order = new ResourceState("Orders", "order", createActionList(new Action("GETEntity", Action.TYPE.VIEW), null), "/Orders({id})");

		//Add state transitions
		Map<String, String> uriLinkageMap = new HashMap<String, String>();
		uriLinkageMap.put("id", "OrderID");
		orders.addTransitionForEachItem("GET", order, uriLinkageMap);
		orders.addTransition("POST", pseudo);

		return new ResourceStateMachine(orders);
	}

	public ResourceStateMachine getOrderDetailsSM() {
		CollectionResourceState orderDetails = new CollectionResourceState("Order_Details", "OrderDetails", createActionList(new Action("GETEntities", Action.TYPE.VIEW), null), "/Order_Details");
		ResourceState pseudo = new ResourceState(orderDetails, "OrderDetails_pseudo_created", createActionList(null, new Action("CreateEntity", Action.TYPE.ENTRY)));
		ResourceState orderDetail = new ResourceState("Order_Details", "orderDetail", createActionList(new Action("GETEntity", Action.TYPE.VIEW), null), "/Order_Details({id})");

		//Add state transitions
		Map<String, String> uriLinkageMap = new HashMap<String, String>();
		uriLinkageMap.put("id", "OrderID");
		orderDetails.addTransitionForEachItem("GET", orderDetail, uriLinkageMap);
		orderDetails.addTransition("POST", pseudo);

		return new ResourceStateMachine(orderDetails);
	}

	public ResourceStateMachine getProductsSM() {
		CollectionResourceState products = new CollectionResourceState("Products", "Products", createActionList(new Action("GETEntities", Action.TYPE.VIEW), null), "/Products");
		ResourceState pseudo = new ResourceState(products, "Products_pseudo_created", createActionList(null, new Action("CreateEntity", Action.TYPE.ENTRY)));
		ResourceState product = new ResourceState("Products", "product", createActionList(new Action("GETEntity", Action.TYPE.VIEW), null), "/Products({id})");

		//Add state transitions
		Map<String, String> uriLinkageMap = new HashMap<String, String>();
		uriLinkageMap.put("id", "ProductID");
		products.addTransitionForEachItem("GET", product, uriLinkageMap);
		products.addTransition("POST", pseudo);

		return new ResourceStateMachine(products);
	}
	
	public ResourceStateMachine getSuppliersSM() {
		CollectionResourceState suppliers = new CollectionResourceState("Suppliers", "Suppliers", createActionList(new Action("GETEntities", Action.TYPE.VIEW), null), "/Suppliers");
		ResourceState pseudo = new ResourceState(suppliers, "Suppliers_pseudo_created", createActionList(null, new Action("CreateEntity", Action.TYPE.ENTRY)));
		ResourceState supplier = new ResourceState("Suppliers", "supplier", createActionList(new Action("GETEntity", Action.TYPE.VIEW), null), "/Suppliers({id})");

		//Add state transitions
		Map<String, String> uriLinkageMap = new HashMap<String, String>();
		uriLinkageMap.put("id", "SupplierID");
		suppliers.addTransitionForEachItem("GET", supplier, uriLinkageMap);
		suppliers.addTransition("POST", pseudo);

		return new ResourceStateMachine(suppliers);
	}

	private List<Action> createActionList(Action view, Action entry) {
		List<Action> actions = new ArrayList<Action>();
		if (view != null)
			actions.add(view);
		if (entry != null)
			actions.add(entry);
		return actions;
	}

}
