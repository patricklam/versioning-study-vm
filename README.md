This repo contains artifacts for the semantic versioning study, as follows:

* Vagrantfile/bootstrap.sh/shared: contains a VM description for the artifact; is supposed to install an environment where all tools are available and can run. Currently the bootstrap.sh script downloads all of the benchmarks and runs FB Infer, but I'll probably split that into three scripts: one to provision the VM with tools; one to download the benchmarks; and one to run the analyses.

* shared/projects.json: contains the list of benchmarks to analyze; the above scripts should eventually use this file as the source of truth for where to get the benchmarks, but I haven't implemented that yet.

* shared/output: the driver script should dump csv files for each of the tools in this directory.

* example-jsoup-fbinfer.json: an example json file of the form to be output by a tool.

For instance, jsoup 1.10.1 gains two FB Infer warnings, one resource leak and one null deref. I need to understand why these are reported for 1.10.1 and not 1.9.2 (the flagged line is the same, but maybe the callee changed.)

* shared/bin/clean.sh: runs mvn clean/gradlew clean on all projects/selected project.

* shared/bin/download.sh: downloads source code as specified by projects.json for all projects/selected project.

* shared/bin/run-infer.sh: runs FB Infer on all projects/selected project and puts output in benchmarks/output

* shared/bin/fbinfer_diffs.py: diffs FB Infer output in benchmarks/output and outputs fbinfer-benchmark.json diffs.
