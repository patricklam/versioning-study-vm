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
			File previousVersionBinary = null;
			for (int i = 0; i < versions.length; ++i) {
				ProjectVersion version = versions[i];
				File currentVersionBinary = Downloader.download(version.getBinary());
				if(previousVersionBinary != null) {
					analyse(previousVersionBinary,currentVersionBinary);
				}
				previousVersionBinary = currentVersionBinary;
			}
		}
	}

	public static void analyse(File oldFile, File newFile) {
		Archive oldjar = new FileArchive(oldFile);
		Archive newjar = new FileArchive(newFile);
		API oldAPI = API.of(oldjar).build();
		API newAPI = API.of(newjar).build();
		// NOTE: its frustrating to do it like this, but I don't see any other way with
		// this API.
		Reporter.errorCount = 0;
		Revapi revapi = Revapi.builder().withAnalyzers(JavaApiAnalyzer.class).withReporters(Reporter.class).build();

		AnalysisContext.Builder builder = AnalysisContext.builder()
		    .withOldAPI(oldAPI)
		    .withNewAPI(newAPI);

		AnalysisResult result = revapi.analyze(builder.build());
		System.out.println("BEFORE: " + oldFile);
		System.out.println(" AFTER: " + newFile);
		System.out.println(" DELTA: " + Reporter.errorCount);
	}

	public static class Reporter implements org.revapi.Reporter {
		private static int errorCount;

		public int getErrorCount() {
			return errorCount;
		}

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
			errorCount += report.getDifferences().size();
		}
	}
}

