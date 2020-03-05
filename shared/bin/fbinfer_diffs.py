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

def retrieve_output_fns(projects_file, proj):
    with open(projects_file, 'r') as pf:
        for p in json.loads(pf.read()):
            if (p['name'] == proj):
                return (path.join('output', 'fbinfer-'+p['versions'][0]['dir']),
                        path.join('output', 'fbinfer-'+p['versions'][1]['dir']))
    return None

def process(projects_file, proj):
    (base1, base2) = retrieve_bases(projects_file, proj)
    (output1, output2) = retrieve_output_fns(projects_file, proj)
    if not path.exists(output1):
        print ("FB infer output {} missing".format(output1))
        return
    if not path.exists(output2):
        print ("FB infer output {} missing".format(output2))
        return

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
            rf_bugs2_matched = set()
            for (_, target, bug_type) in rf_bugs1:
                matching_line = compute_matching_line(f1, f2, target)
                if matching_line is None:
                    print ('bug {} file {} line1 {} missing from version 2'.format(bug_type, reported_file, target))
                else:
                    print ('bug {} file {} line1 {} line2 {}'.format(bug_type, reported_file, target, matching_line))
                rf_bugs2_matched.add((reported_file, matching_line, bug_type))
            for rb2 in rf_bugs2:
                if rb2 not in rf_bugs2_matched:
                    (reported_file, target, bug_type) = rb2
                    matching_line = compute_matching_line(f2, f1, target)
                    if matching_line is None:
                        print ('bug {} file {} line1 {} missing from version 1'.format(bug_type, reported_file, target))
                    else:
                        print ('bug {} file {} line1 {} line2 {}'.format(bug_type, reported_file, target, matching_line))
        except FileNotFoundError:
            if not path.exists(path.join(base1, reported_file)):
                print ('file {} missing from version 1: {}, v2 has {} bugs'.format(reported_file, base1, len(rf_bugs2)))
            if not path.exists(path.join(base2, reported_file)):
                print ('file {} missing from version 2: {}, v1 has {} bugs'.format(reported_file, base2, len(rf_bugs1)))

def main(argv):
    if len(argv) < 2:
        print ("Usage: {} ../shared/projects.json [benchmark]".format(argv[0]))
        sys.exit(1)
    projects_file = argv[1]
    if len(argv) == 2:
        projs = []
        with open(projects_file, 'r') as pf:
            for p in json.loads(pf.read()):
                projs.append(p['name'])
        for proj in projs:
            print ('processing project {}'.format(proj))
            process(projects_file, proj)
    else:
        proj = argv[2]
        process(projects_file, proj)

if __name__ == "__main__":
    main(sys.argv)
