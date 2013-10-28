package RestbucksModel;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="rb_order")
public class Order {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	private Integer Id;

	private String location;
	private String name;
	private String milk;
	private Integer quantity;
	private String size;
	@ManyToOne(cascade = CascadeType.ALL , fetch = FetchType.LAZY, optional = true)
	private Payment Payment;


	public Order() {
	}
}