package ontologia;

import jade.content.onto.BCReflectiveIntrospector;
import jade.content.onto.BeanOntology;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.content.schema.ConceptSchema;

public class OntologiaEmergenciaMedica extends BeanOntology {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3627169520123940965L;

	public static final String NOME = "Emergencia-Medica-ontology";

	private static Ontology theInstance = new OntologiaEmergenciaMedica();

	public static Ontology getInstance() {
		return theInstance;
	}

	/**
	 * Constructor
	 */
	private OntologiaEmergenciaMedica() {
		super(NOME, OntologiaEmergencia.getInstance());
		try {
			add(EmergenciaMedica.class);
		} catch (OntologyException oe) {
			oe.printStackTrace();
		}
	}

}
