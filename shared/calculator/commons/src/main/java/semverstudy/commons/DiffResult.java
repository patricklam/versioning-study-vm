package semverstudy.commons;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The result of diffing two representations of a library.
 * @author jens dietrich
 */
public class DiffResult {

    private String name = null;
    private String version1 = null;
    private String version2 = null;
    private List<DiffIssue> issues = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DiffResult that = (DiffResult) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(version1, that.version1) &&
                Objects.equals(version2, that.version2) &&
                Objects.equals(issues, that.issues);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, version1, version2, issues);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion1() {
        return version1;
    }

    public void setVersion1(String version1) {
        this.version1 = version1;
    }

    public String getVersion2() {
        return version2;
    }

    public void setVersion2(String version2) {
        this.version2 = version2;
    }

    public List<DiffIssue> getIssues() {
        return issues;
    }

    public void setIssues(List<DiffIssue> issues) {
        this.issues = issues;
    }
}
