package InteractionNoteModel;

import java.util.Collection;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class Person {

	@Id
	/* Added this GeneratedValue annotation to auto create ids, otherwise this class in generated from the EDMX */
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	private Integer id;
	
	private String name;
	
	/* Support the navigation properties */
	@OneToMany(mappedBy="Persons")
	private Collection<Note> notes;

	public Person() {}
}