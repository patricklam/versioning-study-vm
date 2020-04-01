package contractstudy;

import com.google.common.base.Preconditions;
import org.json.JSONObject;
import java.util.stream.Collectors;

/**
 * Represents a contract element.
 * Note that contract elements have their own line numbers which may be different from the line numbers of the location (usually the method).
 * @author jens dietrich
 */
public  class ContractElement {
	
	static final String MISSING_INFO = "-"; // useful for serializing
	
	private ProgramVersion programVersion = null; 
	private Location location = null;
	private int lineNo = -1;
	private boolean methodAbstract = false;
	private ConstraintType kind = null;
	private String condition = MISSING_INFO;
	private String additionalInfo = MISSING_INFO;  // additional info, e.g. from exception message parameters
    private ConstraintedArtefact constraintedArtefact = ConstraintedArtefact.METHOD;


    public ConstraintedArtefact getConstraintedArtefact() {
        return constraintedArtefact;
    }

    public void setConstraintedArtefact(ConstraintedArtefact constraintedArtefact) {
        this.constraintedArtefact = constraintedArtefact;
    }

    public ConstraintType getKind() {
		return kind;
	}
	public void setKind(ConstraintType kind) {
		this.kind = kind;
	}

	public ProgramVersion getProgramVersion() {
		return programVersion;
	}
	public void setProgramVersion(ProgramVersion programVersion) {
		Preconditions.checkArgument(programVersion!=null);
		this.programVersion = programVersion;
	}

	public boolean isMethodAbstract() {
		return methodAbstract;
	}

	public void setMethodAbstract(boolean methodAbstract) {
		this.methodAbstract = methodAbstract;
	}

	public String getCondition() {
		return condition;
	}
	public void setCondition(String condition) {
		this.condition = condition==null?MISSING_INFO:condition;
	}


	public String getAdditionalInfo() {
		return additionalInfo;
	}
	public void setAdditionalInfo(String additionalInfo) {
		this.additionalInfo = additionalInfo==null?MISSING_INFO:additionalInfo;
	}


    public ConstraintClassification getClassification () {
        return this.getKind().getClassification(this.getConstraintedArtefact());
    }

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public int getLineNo() {
		return lineNo;
	}

	public void setLineNo(int lineNo) {
		this.lineNo = lineNo;
	}

	/**
	 * Export this object to a JSON Object.
	 * @return
	 */
	public JSONObject toJSON () {

		Preconditions.checkState(getProgramVersion().getName() != null);
		Preconditions.checkState(getProgramVersion().getVersion() != null);
		Preconditions.checkState(getLocation().getLineNo() > -1);
		Preconditions.checkState(getLocation().getCuName() != null);
		Preconditions.checkState(getLocation().getMethodName() != null);
		Preconditions.checkState(getLocation().getMethodParameterNames() != null);
		Preconditions.checkState(getCondition() != null);
		Preconditions.checkState(getKind() != null);
		Preconditions.checkState(getAdditionalInfo() != null);
		Preconditions.checkState(getConstraintedArtefact()!=null);

		return new JSONObject()
				.put("name", getProgramVersion().getName())
				.put("version", getProgramVersion().getVersion())
				.put("cu", getLocation().getCuName())
				.put("line", getLineNo())
				.put("method", getLocation().getMethodName()+'('+getLocation().getMethodParameterNames().stream().collect(Collectors.joining(","))+')')
				.put("condition", getCondition())
				.put("type", getKind())
				.put("additional_info", getAdditionalInfo())
				.put("artefact_type",getConstraintedArtefact())
				.put("abstract_method", isMethodAbstract())
				;
	}

}
