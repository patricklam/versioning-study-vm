package contractstudy;

import org.apache.log4j.Logger;
import semverstudy.commons.Logging;

import java.util.*;

/**
 * Collect constraints.
 * @author jens dietrich
 */
public class ConstraintCollector implements ExtractionListener<ContractElement> {
	static Logger LOGGER = Logging.getLogger(ConstraintCollector.class);
	private List<ContractElement> contractElements = new ArrayList<>();
	private Set<Location> locationsVisited = new HashSet<>();

	@Override
	public void constraintFound(ContractElement contractElement) {
		this.contractElements.add(contractElement);
	}

	@Override
	public void locationVisited(Location location) {
		this.locationsVisited.add(location);
	}

	public List<ContractElement> getContractElements() {
		return contractElements;
	}

	public Set<Location> getVisitedLocations() {
		return locationsVisited;
	}

	@Override
	public void extractionExceptionEncountered(String message, Throwable x) {
		LOGGER.debug(message, x);
        // x.printStackTrace();
	}
}
