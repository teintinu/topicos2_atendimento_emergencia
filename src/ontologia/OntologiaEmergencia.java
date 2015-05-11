package ontologia;

import jade.content.onto.BCReflectiveIntrospector;
import jade.content.onto.BasicOntology;
import jade.content.onto.BeanOntology;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.content.schema.ConceptSchema;
import jade.content.schema.ObjectSchema;
import jade.content.schema.PrimitiveSchema;

public class OntologiaEmergencia extends BeanOntology {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3627169520123940965L;

	public static final String NOME = "Emergencia-Medica-ontology";

	private static Ontology theInstance = new OntologiaEmergencia();

	public static Ontology getInstance() {
		return theInstance;
	}

	/**
	 * Constructor
	 */
	private OntologiaEmergencia() {
		super(NOME);
		try {
			add(Emergencia.class);
			add(Local.class);
		} catch (OntologyException oe) {
			oe.printStackTrace();
		}
	}

}
