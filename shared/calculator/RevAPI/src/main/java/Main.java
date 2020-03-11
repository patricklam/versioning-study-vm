import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.revapi.*;
import org.revapi.java.JavaApiAnalyzer;
import org.revapi.simple.FileArchive;

import semverstudy.commons.Downloader;
import semverstudy.commons.Project;
import semverstudy.commons.ProjectParser;
import semverstudy.commons.ProjectVersion;

public class Main {

	private static String OUTPUT_DIR = "../../output-json/".replaceAll("/", File.separator);

	public static void main(String[] args) throws Exception {
		// Read project spec file
		File projectSpecs = new File(args[0]);
		Project[] projects = ProjectParser.parseProjects(projectSpecs);
		//
		for (Project project : projects) {
			ArrayList<Result> results = new ArrayList<>();
			ProjectVersion[] versions = project.getVersions();
			File previousVersionBinary = null;
			for (int i = 0; i < versions.length; ++i) {
				ProjectVersion version = versions[i];
				if (version.getBinary() == null || version.getBinary().equals("")) {
					break;
				}
				File currentVersionBinary = Downloader.download(version.getBinary());
				if (previousVersionBinary != null) {
					analyse(previousVersionBinary, currentVersionBinary, project.getName(), versions[i - 1], version,
							results);
				}
				previousVersionBinary = currentVersionBinary;
			}
			//
			writeOutResults(results,new File(OUTPUT_DIR + File.separator + "revapi-" + project.getName() + ".json"));
		}
	}

	private static void writeOutResults(List<Result> results, File file) throws IOException {
		PrintWriter out = new PrintWriter(new FileWriter(file));
		for(Result r : results) {
			out.println(r);
		}
		out.close();
	}

	public static void analyse(File oldFile, File newFile, String name, ProjectVersion version1, ProjectVersion version2, List<Result> results) {
		Archive oldjar = new FileArchive(oldFile);
		Archive newjar = new FileArchive(newFile);
		API oldAPI = API.of(oldjar).build();
		API newAPI = API.of(newjar).build();
		// NOTE: its frustrating to do it like this, but I don't see any other way with
		// this API.
		Reporter.results.clear();
		Revapi revapi = Revapi.builder().withAnalyzers(JavaApiAnalyzer.class).withReporters(Reporter.class).build();

		AnalysisContext.Builder builder = AnalysisContext.builder()
		    .withOldAPI(oldAPI)
		    .withNewAPI(newAPI);

		AnalysisResult result = revapi.analyze(builder.build());
		// Dump out results
		results.add(new Result(name, version1.getVersion(), version2.getVersion(), Reporter.results));
	}

	/**
	 * Represents a single result entry.
	 * @author David J. Pearce
	 *
	 */
	public static class Result {
		private final String name;
		private final String version1;
		private final String version2;
		private final List<Error> results;

		public Result(String name, String version1, String version2, List<Error> results) {
			this.name = name;
			this.version1 = version1;
			this.version2 = version2;
			this.results = new ArrayList<>(results);
		}

		@Override
		public String toString() {
			return "{\"name\": \"" + name + "\", \"version1\": \"" + version1 + "\", \"version2\": \"" + version2 + "\", \"results\": " + results + "}";
		}

		public static class Error {
			private final String tool;
			private final String key;
			private final String file;
			private final String line;
			private final String method;
			private final String direction;

			public Error(String tool, String key, String file, String line, String method, String direction) {
				this.tool = tool;
				this.key = key;
				this.file = file;
				this.line = line;
				this.method = method;
				this.direction = direction;
			}

			@Override
			public String toString() {
				return "{\"tool\": \"" + tool + "\", \"key\": \"" + key + "\", \"file\": \"" + file + "\", \"line\": \"" + line
						+ "\", \"method\": \"" + method + "\", \"direction\": \"" + direction + "\"}";
			}
		}
	}

	public static class Reporter implements org.revapi.Reporter {
		/**
		 * A global variable into which all results from the reporters are accumulated.
		 * It's somewhat unfortunately that I can't see how to get the framework to
		 * instantiate the report with anything other than an empty constructor.
		 */
		public static List<Result.Error> results = new ArrayList<>();

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
			List<Difference> differences = report.getDifferences();
			for(Difference d : differences) {
				DifferenceSeverity binary = d.classification.get(CompatibilityType.BINARY);
				DifferenceSeverity source = d.classification.get(CompatibilityType.SOURCE);
				DifferenceSeverity semantic = d.classification.get(CompatibilityType.SEMANTIC);
				DifferenceSeverity other = d.classification.get(CompatibilityType.OTHER);
				DifferenceSeverity lub = merge(binary,source,semantic,other);
				// Only consider breaking or potentially breaking changes
				if(lub == DifferenceSeverity.BREAKING || lub == DifferenceSeverity.POTENTIALLY_BREAKING) {
					String key = d.code;
					Attachments as = new Attachments(d.attachments);
					String file = as.classQualifiedName;
					String line = "?";
					String method = as.methodName;
					String direction = "?";
					results.add(new Result.Error("RevAPI",key,file,line,method,direction));
				}
			}
		}

		public String toKey(Difference d) {
			// classSimpleName
			// classQualifiedName
			// package
			// interface
			// elementKind
			// annotationType
			// oldType
			// newType
			return d.code;
		}

		private DifferenceSeverity merge(DifferenceSeverity... items) {
			for(DifferenceSeverity d : items) {
				if(d == DifferenceSeverity.BREAKING) {
					return d;
				}
			}
			for(DifferenceSeverity d : items) {
				if(d == DifferenceSeverity.POTENTIALLY_BREAKING) {
					return d;
				}
			}
			// Ignore others
			return DifferenceSeverity.NON_BREAKING;
		}

		private static class Attachments {
			final String classSimpleName;
			final String classQualifiedName;
			final String elementKind;
			final String pAckage;
			final String iNterface;
			final String methodName;
			final String annotationType;
			final String oldType;
			final String newType;

			/**
			 * The set of known attachment keys (i.e. the ones I have seen being used).
			 */
			private static String[] KNOWN_KEYS = { "classSimpleName", "classQualifiedName", "package", "interface",
					"methodName", "elementKind", "annotation", "annotationType", "attribute", "superClass", "exception",
					"oldValue", "parameterIndex", "serialVersionUID", "value", "newValue", "fieldName", "newOrdinal",
					"oldOrdinal", "newSerialVersionUID", "oldSerialVersionUID", "oldModifiers", "newModifiers",
					"oldType", "newType", "newArchive", "oldArchive", "typeParameter", "oldSuperType", "newSuperType",
					"exampleUseChainInNewApi", "exampleUseChainInOldApi" };

			public Attachments(Map<String,String> attachments) {
				classSimpleName = attachments.getOrDefault("classSimpleName", "?");
				classQualifiedName = attachments.getOrDefault("classQualifiedName", "?");
				elementKind = attachments.getOrDefault("elementKind", "?");
				pAckage = attachments.getOrDefault("package", "?");
				iNterface = attachments.getOrDefault("interface", "?");
				methodName = attachments.getOrDefault("methodName", "?");
				annotationType = attachments.getOrDefault("annotationType", "?");
				oldType = attachments.getOrDefault("oldType", "?");
				newType = attachments.getOrDefault("newType", "?");
				// Sanity check didn't miss anything
//				HashSet<String> keys = new HashSet<>(attachments.keySet());
//				keys.removeAll(Arrays.asList(KNOWN_KEYS));
//				if(keys.size() > 0) {
//					System.err.println("unknown keys: " + keys);
//				}
			}
		}
	}
}

