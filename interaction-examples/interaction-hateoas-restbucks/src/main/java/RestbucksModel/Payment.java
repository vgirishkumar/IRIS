package RestbucksModel;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.CascadeType;
import javax.persistence.OneToMany;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.Collection;

@Entity
public class Payment {

	@Id
	@Basic(optional = false)
	private Integer Id;

	private String authorisationCode;
	@JoinColumn(name = "Id", referencedColumnName = "Id", insertable = false, updatable = false)
	@ManyToOne(optional = false)
	private Order Order;


	public Payment() {
	}
}