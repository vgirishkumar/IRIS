package com.interaction.example.odata.northwind;

import java.util.HashMap;
import java.util.Map;

import com.temenos.interaction.core.link.CollectionResourceState;
import com.temenos.interaction.core.link.ResourceState;
import com.temenos.interaction.core.link.ResourceStateMachine;

public class Behaviour {

	public ResourceState getSimpleODataInteractionModel() {
		// the service root
		ResourceState initialState = new ResourceState("ServiceDocument", "begin", "/");
		ResourceState metadata = new ResourceState("", "metadata", "/$metadata");

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
		CollectionResourceState categories = new CollectionResourceState("Categories", "categories", "/Categories");
		ResourceState pseudo = new ResourceState(categories, "Categories_pseudo_created");
		ResourceState category = new ResourceState("Categories", "category", "/Categories({id})");

		//Add state transitions
		Map<String, String> uriLinkageMap = new HashMap<String, String>();
		uriLinkageMap.put("id", "CategoryID");
		categories.addTransitionForEachItem("GET", category, uriLinkageMap);
		categories.addTransition("POST", pseudo);

		return new ResourceStateMachine(categories);
	}
	
	public ResourceStateMachine getCustomersSM() {
		CollectionResourceState customers = new CollectionResourceState("Customers", "customers", "/Customers");
		ResourceState pseudo = new ResourceState(customers, "Customers_pseudo_created");
		ResourceState category = new ResourceState("Customers", "category", "/Customers({id})");

		//Add state transitions
		Map<String, String> uriLinkageMap = new HashMap<String, String>();
		uriLinkageMap.put("id", "CustomerID");
		customers.addTransitionForEachItem("GET", category, uriLinkageMap);
		customers.addTransition("POST", pseudo);

		return new ResourceStateMachine(customers);
	}

	public ResourceStateMachine getEmployeesSM() {
		CollectionResourceState employees = new CollectionResourceState("Employees", "employees", "/Employees");
		ResourceState pseudo = new ResourceState(employees, "Employees_pseudo_created");
		ResourceState employee = new ResourceState("Employees", "employee", "/Employees({id})");

		//Add state transitions
		Map<String, String> uriLinkageMap = new HashMap<String, String>();
		uriLinkageMap.put("id", "EmployeeID");
		employees.addTransitionForEachItem("GET", employee, uriLinkageMap);
		employees.addTransition("POST", pseudo);

		return new ResourceStateMachine(employees);
	}
	
	public ResourceStateMachine getOrdersSM() {
		CollectionResourceState orders = new CollectionResourceState("Orders", "orders", "/Orders");
		ResourceState pseudo = new ResourceState(orders, "Orders_pseudo_created");
		ResourceState order = new ResourceState("Orders", "order", "/Orders({id})");

		//Add state transitions
		Map<String, String> uriLinkageMap = new HashMap<String, String>();
		uriLinkageMap.put("id", "OrderID");
		orders.addTransitionForEachItem("GET", order, uriLinkageMap);
		orders.addTransition("POST", pseudo);

		return new ResourceStateMachine(orders);
	}

	public ResourceStateMachine getOrderDetailsSM() {
		CollectionResourceState orderDetails = new CollectionResourceState("Order_Details", "orderDetails", "/Order_Details");
		ResourceState pseudo = new ResourceState(orderDetails, "OrderDetails_pseudo_created");
		ResourceState orderDetail = new ResourceState("Order_Details", "orderDetail", "/Order_Details({id})");

		//Add state transitions
		Map<String, String> uriLinkageMap = new HashMap<String, String>();
		uriLinkageMap.put("id", "OrderID");
		orderDetails.addTransitionForEachItem("GET", orderDetail, uriLinkageMap);
		orderDetails.addTransition("POST", pseudo);

		return new ResourceStateMachine(orderDetails);
	}

	public ResourceStateMachine getProductsSM() {
		CollectionResourceState products = new CollectionResourceState("Products", "products", "/Products");
		ResourceState pseudo = new ResourceState(products, "Products_pseudo_created");
		ResourceState product = new ResourceState("Products", "product", "/Products({id})");

		//Add state transitions
		Map<String, String> uriLinkageMap = new HashMap<String, String>();
		uriLinkageMap.put("id", "ProductID");
		products.addTransitionForEachItem("GET", product, uriLinkageMap);
		products.addTransition("POST", pseudo);

		return new ResourceStateMachine(products);
	}
	
	public ResourceStateMachine getSuppliersSM() {
		CollectionResourceState suppliers = new CollectionResourceState("Suppliers", "suppliers", "/Suppliers");
		ResourceState pseudo = new ResourceState(suppliers, "Suppliers_pseudo_created");
		ResourceState supplier = new ResourceState("Suppliers", "supplier", "/Suppliers({id})");

		//Add state transitions
		Map<String, String> uriLinkageMap = new HashMap<String, String>();
		uriLinkageMap.put("id", "SupplierID");
		suppliers.addTransitionForEachItem("GET", supplier, uriLinkageMap);
		suppliers.addTransition("POST", pseudo);

		return new ResourceStateMachine(suppliers);
	}

}
