package contractstudy;

/**
 * Consumes constraints found, for instance, by collecting them in a list, or by appending them to a report.
 * @author jens dietrich
 */
public interface ExtractionListener<T extends ContractElement> {
	void constraintFound(T constraint);
	void locationVisited(Location location);
	void extractionExceptionEncountered(String message,Throwable x);
}
