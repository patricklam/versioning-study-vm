package test.semver.meta;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import semverstudy.commons.Project;
import semverstudy.commons.ProjectVersion;
import semverstudy.meta.*;

import java.net.URL;
import java.util.Objects;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class Test1 {

    private Project project = null;
    private ProjectVersion version = null;
    private MetaDataExtractor extractor = null;

    @Before
    public void setUp() throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();

        version = Mockito.mock(ProjectVersion.class);
        when(version.getSource()).thenReturn(classLoader.getResource("project1.zip"));

        project = Mockito.mock(Project.class);
        when(project.getName()).thenReturn("project1");
        when(project.getVersions()).thenReturn(new ProjectVersion[]{version});

        extractor = new POMMetaDataExtractor();
    }

    @Test
    public void testLicense() throws Exception {
        MetaData metaData = extractor.extractMetaData(project,version);
        assertEquals(1,metaData.getLicenses().size());
        License license = metaData.getLicenses().iterator().next();
        assertEquals("Apache 2",license.getName());
        assertEquals(new URL("http://www.apache.org/licenses/LICENSE-2.0"),license.getUrl());
    }

    private boolean containsDependency(Set<Dependency> dependencies, String groupId, String artifactId, String version) {
        return dependencies.stream()
            .filter(dep -> Objects.equals(dep.getGroupId(),groupId))
            .filter(dep -> Objects.equals(dep.getArtifactId(),artifactId))
            .filter(dep -> Objects.equals(dep.getVersion(),version))
            .findAny().isPresent();

    }

    @Test
    public void testDependencies() throws Exception {
        MetaData metaData = extractor.extractMetaData(project,version);
        assertEquals(21,metaData.getDependencies().size());

        assertTrue(containsDependency(metaData.getDependencies(),"com.google.code.findbugs","jsr305","3.0.2"));
        assertTrue(containsDependency(metaData.getDependencies(),"org.slf4j","slf4j-api",null));
    }




}
