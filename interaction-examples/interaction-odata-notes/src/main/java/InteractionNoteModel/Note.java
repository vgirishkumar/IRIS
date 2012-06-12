package InteractionNoteModel;

import javax.persistence.Basic;
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
	
	/* link to the person whose note this belongs to */
	@ManyToOne
	@JoinColumn(name = "PERSONID")
	private Person Persons;
	
	public Note() {}
}