package semverstudy.meta;

import com.google.common.base.Preconditions;
import org.apache.log4j.Logger;
import org.w3c.dom.*;
import semverstudy.commons.*;

import javax.annotation.Nullable;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.*;
import java.io.File;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

/**
 * Metadata extractor for pom.xml files, using the structure described http://maven.apache.org/pom.html.
 * @author jens dietrich
 */
public class POMMetaDataExtractor implements MetaDataExtractor {

    public static final Logger LOGGER = Logging.getLogger("pom-metadata-extraction");

    private static XPathExpression XPATH_DEPENDENCY = null;
    private static XPathExpression XPATH_LICENSE = null;

    // precompiled xpath
    static {
        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();
        try {
            XPATH_DEPENDENCY = xpath.compile("//dependency");
            XPATH_LICENSE = xpath.compile("//license");
        } catch (XPathExpressionException e) {
            LOGGER.error(e);
        }
    }

    // for testing only !
    public static void main (String[] args) throws Exception {
        File projectSpecs = new File(args[0]);
        Project[] projects = ProjectParser.parseProjects(projectSpecs);
        ProjectVersion someVersion = projects[0].getVersions()[0];
        new POMMetaDataExtractor().extractMetaData(projects[0],someVersion);
    }

    @Override
    @Nullable
    public MetaData extractMetaData(Project project,ProjectVersion projectVersion) throws Exception {
        LOGGER.info("extracting metadata from " + project.getName() + "-" + projectVersion.getVersion());
        URL srcURL = projectVersion.getSource();
        File src = Downloader.download(srcURL);
        assert src.isDirectory();
        LOGGER.info("using downloaded sources in " + src.getAbsolutePath());

        File pom = findPom(src);
        if (pom.exists()) {
            LOGGER.info("Extracting from pom " + pom.getAbsolutePath());
        }
        else {
            LOGGER.warn("Cannot find pom to parse " + pom.getAbsolutePath());
            // null return can be used to build chain of responsibility, e.g. to look for gradle build scripts
            return null;
        }

        // find relative path for location
        Path srcPath = src.toPath();
        Path pomPath = pom.toPath();
        String relPath = srcPath.relativize(pomPath).toString().toString();

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(pom);

        NodeList result = (NodeList)XPATH_DEPENDENCY.evaluate(doc, XPathConstants.NODESET);
        Set<Dependency> dependencies = new HashSet<>();
        for (int i=0;i<result.getLength();i++) {
            Element eDependency = (Element)result.item(i);
            Dependency dependency = new Dependency();
            dependency.setGroupId(getChildValue(eDependency,"groupId"));
            dependency.setArtifactId(getChildValue(eDependency,"artifactId"));
            dependency.setVersion(getChildValue(eDependency,"version"));
            dependencies.add(dependency);
        }

        result = (NodeList)XPATH_LICENSE.evaluate(doc, XPathConstants.NODESET);
        Set<License> licenses = new HashSet<>();
        for (int i=0;i<result.getLength();i++) {
            Element eLicense = (Element)result.item(i);
            License license = new License();
            license.setName(getChildValue(eLicense,"name"));
            String urlAsString = getChildValue(eLicense,"url");
            if (urlAsString!=null) {
                license.setUrl(new URL(urlAsString));
            }
            licenses.add(license);
        }

        MetaData metaData = new MetaData();
        metaData.setKind(MetaDataKind.POM);
        metaData.setLocation(relPath);
        metaData.setDependencies(dependencies);
        metaData.setLicenses(licenses);

        return metaData;
    }

    private String getChildValue(Element e, String childName) {

        NodeList nodeList = e.getElementsByTagName(childName);
        if (nodeList.getLength()==0) {
            return null;
        }
        assert nodeList.getLength()==1 ;

        Element child = (Element)nodeList.item(0);
        Node contentNode = child.getFirstChild();
        if ((contentNode != null) && (contentNode instanceof Text)) {
            Text txt = (Text)contentNode;
            return txt.getWholeText();
        }
        else {
            return null;
        }
    }

    private File findPom(File src) {
        Preconditions.checkArgument(src.isDirectory());

        File pom = new File(src,"pom.xml");
        if (pom.exists()) return pom;

        // look in folders -- only extract first !!
        for (File dir:src.listFiles()) {
            if (dir.isDirectory()) {
                pom = new File(dir,"pom.xml");
                if (pom.exists()) return pom;
            }
        }

        return null;

    }


}
