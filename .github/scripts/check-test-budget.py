#!/usr/bin/env python3
"""Keep the fast test tier fast.

The pull-request build runs only the fast tier (every test tagged neither "slow" nor
"corpus"). Nothing stops that tier from growing back over its budget one test at a time,
so this script enforces the rule that goes with the tiers:

    if a single test method takes more than a second, tag it @Tag(TestTags.SLOW)

It reads the per-testcase timings surefire already writes and fails the build when a test
is too slow to belong in the fast tier, naming the test and what to do about it.

Run it from the repository root, after the Maven build:

    python3 .github/scripts/check-test-budget.py

Surefire reports accumulate across runs. Locally, run `mvn clean test` first - otherwise
this reads a mixture of tiers left over from an earlier `-Pslow-tests` or `-Prubi-corpus`
run and reports nonsense. On CI the checkout is clean, so this cannot happen there.
"""

import glob
import os
import sys
import xml.etree.ElementTree as ET

# Both limits are wall-clock seconds ON A GITHUB RUNNER, which is slower than a developer
# machine - do not copy local timings here.
#
# TODO: these are deliberately loose first guesses, because the runner is slower than the
# machine they were derived on by an unmeasured factor. Read the "fast tier:" line that this
# script prints on the first green pull-request run and tighten both to roughly twice what
# it reports. Reference numbers on an Apple M5, `mvn clean test`, 2026-09-08:
# 5940 tests in 81.4s, slowest single test 5.2s
# (io archunit BiojavaDependencyTest#noBiojavaOutsideBioModule, a whole-classpath scan -
# it is legitimately the slowest thing in the fast tier, so the per-method limit has to
# clear it with room to spare).
#
# MAX_METHOD_SECONDS is the one-second rule with room for runner noise: a test near the
# limit locally must not fail the build for someone else. It still catches the tests that
# actually matter - the ones tagged @Tag(TestTags.SLOW) run 20-90s here.
MAX_METHOD_SECONDS = 15.0

# MAX_TOTAL_SECONDS is the backstop against death by a thousand fast-but-not-free tests.
MAX_TOTAL_SECONDS = 600.0

REPORTS = "symja_android_library/*/target/surefire-reports/TEST-*.xml"


def main() -> int:
    files = glob.glob(REPORTS)
    if not files:
        print(f"::warning::no surefire reports matched {REPORTS} - nothing to check")
        return 0

    total = 0.0
    count = 0
    offenders = []
    for path in files:
        try:
            root = ET.parse(path).getroot()
        except ET.ParseError as exc:
            print(f"::warning::could not parse {path}: {exc}")
            continue
        suite = root.get("name") or os.path.basename(path)
        for case in root.iter("testcase"):
            seconds = float(case.get("time") or 0.0)
            total += seconds
            count += 1
            if seconds > MAX_METHOD_SECONDS:
                offenders.append((seconds, suite, case.get("name")))

    print(f"fast tier: {count} tests in {total:.1f}s "
          f"across {len(files)} classes (budget {MAX_TOTAL_SECONDS:.0f}s)")

    failed = False

    if offenders:
        failed = True
        offenders.sort(reverse=True)
        print(f"::error::{len(offenders)} test(s) took longer than "
              f"{MAX_METHOD_SECONDS:.0f}s and do not belong in the fast tier:")
        for seconds, suite, name in offenders:
            print(f"::error::  {seconds:7.2f}s  {suite}#{name}")
        print("::error::Add @Tag(TestTags.SLOW) to each of them, or make them faster. "
              "The slow tier runs with: mvn verify -Pall-tests")

    if total > MAX_TOTAL_SECONDS:
        failed = True
        print(f"::error::total fast-tier test time {total:.1f}s exceeds the "
              f"{MAX_TOTAL_SECONDS:.0f}s budget.")
        print("::error::Tag the slowest tests @Tag(TestTags.SLOW), or raise "
              "MAX_TOTAL_SECONDS here if the budget is genuinely too tight.")

    if not failed:
        print("test-time budget OK")
    return 1 if failed else 0


if __name__ == "__main__":
    sys.exit(main())
