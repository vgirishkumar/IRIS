package RestbucksModel;

/*
 * #%L
 * interaction-example-hateoas-restbucks
 * %%
 * Copyright (C) 2012 - 2014 Temenos Holdings N.V.
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


import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name="rb_order")
@SuppressWarnings("unused")
public class Order {

	@Id
	@Basic(optional = false)
	private String Id;

	private String location;
	private String name;
	private String email;
	private String milk;
	private Integer quantity;
	private String size;

	// an Order can have one Payments
	@OneToOne(cascade=CascadeType.ALL, mappedBy="order")
    private Payment payment;
	
	public Order() {
	}
}