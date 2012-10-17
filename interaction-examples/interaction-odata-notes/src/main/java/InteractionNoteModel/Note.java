package InteractionNoteModel;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@SuppressWarnings("unused")
public class Note {

	@Id
	/* Added this GeneratedValue annotation to auto create ids, otherwise this class in generated from the EDMX */
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	private Integer id;
	
	private String body;
	
	/* used in link to the person state */
	@Column(name = "PERSONID", insertable = false, updatable = false)
	private Integer personId;
	
	@ManyToOne
	@JoinColumn(name = "PERSONID")
	private Person NotePerson;
	
	public Note() {}
}