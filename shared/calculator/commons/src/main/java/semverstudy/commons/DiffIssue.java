package semverstudy.commons;

import java.util.Objects;

/**
 * An issue discovered while diffing two versions of a library.
 * @author jens dietrich
 */
public class DiffIssue {
    private String key = null;
    private String file = null;
    private String line = null;
    private String method = null;
    private String direction = null;
    private String tool = null;
    private String details = null;

    public String getTool() {
        return tool;
    }

    public void setTool(String tool) {
        this.tool = tool;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DiffIssue diffIssue = (DiffIssue) o;
        return Objects.equals(key, diffIssue.key) &&
                Objects.equals(file, diffIssue.file) &&
                Objects.equals(line, diffIssue.line) &&
                Objects.equals(method, diffIssue.method) &&
                Objects.equals(direction, diffIssue.direction) &&
                Objects.equals(tool, diffIssue.tool) &&
                Objects.equals(details, diffIssue.details);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, file, line, method, direction, tool, details);
    }
}
