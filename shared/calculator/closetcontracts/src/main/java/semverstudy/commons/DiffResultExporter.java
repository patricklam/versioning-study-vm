package semverstudy.commons;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.util.List;

/**
 * Utility to export diff results. The JSON is pretty-printed.
 * @author jens dietrich
 */
public class DiffResultExporter {

    public static void writeResults(List<DiffResult> results, File out) throws Exception {
        new ObjectMapper().writerWithDefaultPrettyPrinter().writeValue(out,results);
    }
}
