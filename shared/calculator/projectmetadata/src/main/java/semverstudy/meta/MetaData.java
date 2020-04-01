package semverstudy.meta;

import java.net.URI;
import java.util.Set;

/**
 * Representation of Maven meta data.
 * @author jens dietrich
 */
public interface MetaData {

    String getLicenseName ();

    URI getLicenseURI();

    Set<Dependency> getDependencies();

}
