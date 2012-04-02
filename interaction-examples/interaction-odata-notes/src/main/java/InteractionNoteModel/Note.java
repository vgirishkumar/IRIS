package InteractionNoteModel;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class Note {

	@Id
	@Basic(optional = false)
	private Integer Id;
	
			private String body;
			private Integer PersonId;
		
	public Note() {}
}