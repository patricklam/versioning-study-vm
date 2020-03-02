import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import org.revapi.*;
import org.revapi.java.JavaApiAnalyzer;
import org.revapi.simple.FileArchive;

import semverstudy.commons.Downloader;
import semverstudy.commons.Project;
import semverstudy.commons.ProjectParser;
import semverstudy.commons.ProjectVersion;

public class Main {

	public static void main(String[] args) throws Exception {
		// Read project spec file
		File projectSpecs = new File(args[0]);
        Project[] projects = ProjectParser.parseProjects(projectSpecs);
        //
		for (Project project : projects) {
			ProjectVersion[] versions = project.getVersions();
			File previousVersionFolder = null;
			for (int i = 0; i < versions.length; ++i) {
				ProjectVersion version = versions[i];
				File currentVersionFolder = Downloader.download(version.getSource());
				if(previousVersionFolder != null) {
					analyse(previousVersionFolder,currentVersionFolder);
				}
				previousVersionFolder = currentVersionFolder;
				System.out.println("GOT: " + versions[i]);
			}
		}
	}

	public static void analyse(File oldFile, File newFile) {
		Archive oldjar = new FileArchive(oldFile);
		Archive newjar = new FileArchive(newFile);
		API oldAPI = API.of(oldjar).build();
		API newAPI = API.of(newjar).build();

		//
		//Revapi revapi = Revapi.builder().withAllExtensionsFromThreadContextClassLoader().withReporters(Reporter.class).build();
		Revapi revapi = Revapi.builder().withAnalyzers(JavaApiAnalyzer.class).withReporters(Reporter.class).build();

		AnalysisContext.Builder builder = AnalysisContext.builder()
		    .withOldAPI(oldAPI)
		    .withNewAPI(newAPI);

		AnalysisResult result = revapi.analyze(builder.build());
		System.out.println("GOT:" + result.getClass().getName());
		//
		System.out.println("GOT: " + result.isSuccess());
	}

	public static class Reporter implements org.revapi.Reporter {

		@Override
		public void close() throws Exception {
		}

		@Override
		public String getExtensionId() {
			return "reporter";
		}

		@Override
		public Reader getJSONSchema() {
			return null;
		}

		@Override
		public void initialize(AnalysisContext analysisContext) {
		}

		@Override
		public void report(Report report) {
			System.out.println("OLD: " + report.getOldElement());
			System.out.println("NEW: " + report.getOldElement());
			System.out.println(report.getDifferences());
			// TODO Auto-generated method stub
			System.out.println("Reporter::report()");
		}
	}
}

