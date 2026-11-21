#!/usr/bin/env python3
"""Write the ProteinData table proteins.tsv.gz of matheclipse-chem.

The table holds the reviewed human UniProtKB entries annotated with the Gene
Ontology term "actin binding" (GO:0003779) or one of its descendants. It is
built from two downloads of the UniProt REST API (CC BY 4.0), both for the
query

    (organism_id:9606) AND (reviewed:true) AND (go:0003779)

1. the entries as TSV, with GO annotations and sequences:

   https://rest.uniprot.org/uniprotkb/stream?query=(organism_id:9606)+AND+(reviewed:true)+AND+(go:0003779)&fields=accession,gene_primary,protein_name,length,sequence,go_f,go_p,go_c&format=tsv

2. the same entries as JSON, for the recommended protein names and HGNC ids:

   https://rest.uniprot.org/uniprotkb/stream?query=(organism_id:9606)+AND+(reviewed:true)+AND+(go:0003779)&fields=accession,gene_primary,protein_name,xref_hgnc&format=json

Optionally a third file, the HGNC complete set (hgnc_complete_set.txt, CC0),
supplies the approved gene names, which are then used as the protein names in
place of UniProt's recommended names.

Usage:

    python3 -I gen_protein_data.py ENTRIES.tsv NAMES.json OUT.tsv.gz \
        --release 2026_04 [--hgnc hgnc_complete_set.txt]

Output (gzip, UTF-8, tab separated, one header line):

    T <GO id> <aspect F|P|C> <class name> <term name>
    P <symbol> <name> <length> <F terms> <P terms> <C terms> <sequence>

The term lines come first; a protein's term lists are comma separated 0-based
indexes of the term lines. The class name of a term is its name split at every
character that is not a letter or a digit, each part capitalized and the parts
joined: "actin filament binding" is "ActinFilamentBinding", "mRNA binding" is
"MRNABinding".
"""

import argparse
import csv
import gzip
import io
import json
import re
import sys

GO_ENTRY = re.compile(r"(.+?) \[GO:(\d{7})\](?:; |$)")
NON_WORD = re.compile(r"[^0-9A-Za-z]+")
ASPECTS = ("F", "P", "C")


def class_name(term):
    parts = [p for p in NON_WORD.split(term) if p]
    return "".join(p[0].upper() + p[1:] for p in parts)


def go_terms(column):
    """The (GO id, name) pairs of one UniProt GO column."""
    result = []
    position = 0
    for match in GO_ENTRY.finditer(column):
        if match.start() != position:
            raise ValueError("cannot parse GO column near: " + column[position:position + 60])
        result.append(("GO:" + match.group(2), match.group(1)))
        position = match.end()
    if position != len(column):
        raise ValueError("cannot parse GO column tail: " + column[position:])
    return result


def read_names(path):
    """Accession -> (recommended name, HGNC id or None)."""
    with open(path, encoding="utf-8") as f:
        results = json.load(f)["results"]
    names = {}
    for entry in results:
        description = entry.get("proteinDescription", {})
        recommended = description.get("recommendedName", {}).get("fullName", {}).get("value")
        if recommended is None:
            submitted = description.get("submissionNames") or [{}]
            recommended = submitted[0].get("fullName", {}).get("value", "")
        hgnc = None
        for ref in entry.get("uniProtKBCrossReferences", []):
            if ref.get("database") == "HGNC":
                hgnc = ref.get("id")
                break
        names[entry["primaryAccession"]] = (recommended, hgnc)
    return names


def read_hgnc(path):
    """HGNC id -> approved name."""
    approved = {}
    with open(path, encoding="utf-8", newline="") as f:
        for row in csv.DictReader(f, delimiter="\t"):
            approved[row["hgnc_id"]] = row["name"]
    return approved


def main(argv):
    parser = argparse.ArgumentParser(description="Write the ProteinData table.")
    parser.add_argument("entries")
    parser.add_argument("names")
    parser.add_argument("out")
    parser.add_argument("--release", required=True, help="UniProt release, e.g. 2026_04")
    parser.add_argument("--hgnc", help="HGNC complete set for the approved names")
    args = parser.parse_args(argv)

    names = read_names(args.names)
    approved = read_hgnc(args.hgnc) if args.hgnc else {}

    terms = {}  # GO id -> (aspect, name)
    proteins = {}  # symbol -> (name, sequence, {aspect: [GO ids]})
    with open(args.entries, encoding="utf-8", newline="") as f:
        rows = csv.reader(f, delimiter="\t", quoting=csv.QUOTE_NONE)
        header = next(rows)
        column = {name: i for i, name in enumerate(header)}
        go_columns = {
            "F": column["Gene Ontology (molecular function)"],
            "P": column["Gene Ontology (biological process)"],
            "C": column["Gene Ontology (cellular component)"],
        }
        for row in rows:
            accession = row[column["Entry"]]
            symbol = row[column["Gene Names (primary)"]].strip()
            sequence = row[column["Sequence"]]
            if not symbol or not re.fullmatch(r"[A-Z]+", sequence):
                print("skipped", accession, symbol, file=sys.stderr)
                continue
            if int(row[column["Length"]]) != len(sequence):
                raise ValueError("length mismatch for " + accession)
            if symbol in proteins:
                raise ValueError("duplicate symbol " + symbol)
            recommended, hgnc = names.get(accession, ("", None))
            name = approved.get(hgnc, recommended) if hgnc else recommended
            annotations = {}
            for aspect, index in go_columns.items():
                ids = []
                for go_id, term in go_terms(row[index]):
                    known = terms.setdefault(go_id, (aspect, term))
                    if known != (aspect, term):
                        raise ValueError("inconsistent term " + go_id)
                    ids.append(go_id)
                annotations[aspect] = ids
            proteins[symbol] = (name, sequence, annotations)

    by_class = {}
    for go_id, (aspect, term) in terms.items():
        by_class.setdefault(class_name(term), []).append(go_id)
    for cls, ids in by_class.items():
        if len(ids) > 1:
            print("class", cls, "shared by", ids, file=sys.stderr)

    order = sorted(terms, key=lambda g: (class_name(terms[g][1]).lower(), class_name(terms[g][1]), g))
    index = {go_id: i for i, go_id in enumerate(order)}

    text = io.StringIO()
    text.write("# ProteinData: reviewed human UniProtKB entries with GO:0003779 actin binding,"
               " UniProt release %s, CC BY 4.0\n" % args.release)
    for go_id in order:
        aspect, term = terms[go_id]
        text.write("\t".join(("T", go_id, aspect, class_name(term), term)) + "\n")
    for symbol in sorted(proteins):
        name, sequence, annotations = proteins[symbol]
        lists = [",".join(str(n) for n in sorted(index[g] for g in annotations[a]))
                 for a in ASPECTS]
        text.write("\t".join(["P", symbol, name, str(len(sequence))] + lists + [sequence]) + "\n")

    with open(args.out, "wb") as raw:
        with gzip.GzipFile(filename="", mode="wb", fileobj=raw, compresslevel=9, mtime=0) as gz:
            gz.write(text.getvalue().encode("utf-8"))
    print("%d proteins, %d terms, %d classes" % (len(proteins), len(terms), len(by_class)),
          file=sys.stderr)


if __name__ == "__main__":
    main(sys.argv[1:])
