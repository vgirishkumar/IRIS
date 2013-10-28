package RestbucksModel;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

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