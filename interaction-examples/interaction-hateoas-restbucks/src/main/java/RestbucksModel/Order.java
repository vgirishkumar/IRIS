package RestbucksModel;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.CascadeType;
import javax.persistence.OneToMany;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import java.util.Collection;

@Entity
@Table(name="rb_order")
public class Order {

	@Id
	@Basic(optional = false)
	private Integer Id;

	private String location;
	private String name;
	private String milk;
	private Integer quantity;
	private String size;
	@JoinColumn(name = "Id", referencedColumnName = "Id", insertable = false, updatable = false)
	@ManyToOne(optional = false)
	private Payment Payment;


	public Order() {
	}
}