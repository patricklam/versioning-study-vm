#!/usr/bin/python3

# inputs: ../shared/projects.json proj
# assumes that FB Infer output has been copied to output/fbinfer-$proj-$version
# outputs:
#  [
#   {
#    "name": "jsoup",
#    "version1": "1.9.2",
#    "version2": "1.10.1",
#    "tool": "fbinfer",
#    "results": [["resourceleak", "+1"], ["nullderef", "+1"]]
#   }
# ]

from os import path
import json
import sys
from diff_calculator import retrieve_bases, compute_matching_line

def retrieve_outputs(projects_file, proj):
    with open(projects_file, 'r') as pf:
        for p in json.loads(pf.read()):
            if (p['name'] == proj):
                return (path.join('output', 'fbinfer-'+p['versions'][0]['dir']),
                        path.join('output', 'fbinfer-'+p['versions'][1]['dir']))
    return None

def main(argv):
    projects_file = argv[1]
    proj = argv[2]
    (base1, base2) = retrieve_bases(projects_file, proj)
    (output1, output2) = retrieve_outputs(projects_file, proj)
    bugs1 = []
    bugs2 = []
    with open(path.join(output1, 'report.json')) as r1h:
        for r in json.loads(r1h.read()):
            bugs1.append((r["file"], r["line"], r["bug_type"]))
    with open(path.join(output2, 'report.json')) as r2h:
        for r in json.loads(r2h.read()):
            bugs2.append((r["file"], r["line"], r["bug_type"]))

    reported_files = set(map(lambda x: x[0], bugs1 + bugs2))
    for reported_file in reported_files:
        rf_bugs1 = sorted(list(filter(lambda x: x[0] == reported_file, bugs1)))
        rf_bugs2 = sorted(list(filter(lambda x: x[0] == reported_file, bugs2)))
        try:
            with open(path.join(base1, reported_file), 'r') as f1h:
                f1 = f1h.readlines()
            with open(path.join(base2, reported_file), 'r') as f2h:
                f2 = f2h.readlines()
            for (_, target, bug_type) in rf_bugs1:
                matching_line = compute_matching_line(f1, f2, target)
                if matching_line is None:
                    print ('bug {} file {} line1 {} missing from version 2'.format(bug_type, reported_file, target))
                print ('bug {} file {} line1 {} line2 {}'.format(bug_type, reported_file, target, matching_line))
            # TODO now look for things in rf_bugs2 that aren't in rf_bugs1
        except FileNotFoundError:
            print ('file {} missing from one version'.format(reported_file))

    sys.exit(0)

    path_to_file = "src/main/java/org/apache/commons/math3/geometry/euclidean/twod/SubLine.java"
    target = 120


    print ('matching line for {} is {}'.format(target, matching_line))

if __name__ == "__main__":
    main(sys.argv)
