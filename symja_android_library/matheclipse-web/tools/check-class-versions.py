#!/usr/bin/env python3
"""Check that every class of a flat jar directory loads on a Java 17 JVM.

The notebook kernel runs on CheerpJ with cheerpjInit({version: 17}), and a Java 17 JVM refuses a
class file newer than major version 61. One such class in any of the jars of
target/notebook/jars is enough to make the page fail - and only when that class is first used, so
the failure would not show at start-up. This reads the header of every .class entry of every jar
and reports, per jar, the highest major version, flagging anything above the limit.

Two kinds of entries are listed separately rather than flagged, because a Java 17 JVM never loads
them as classes: META-INF/versions/<n>/** of a multi-release jar for n > 17 (picked only by a JVM
of that version or newer), and module-info.class (read only on the module path; CheerpJ loads
the jars on the class path).

Usage: check-class-versions.py [jar-dir] [--max 61]
Exit status 1 if a class that a Java 17 JVM would load is newer than the limit.
"""

import os
import re
import struct
import sys
import zipfile

# Java 17 = class file major version 61
DEFAULT_MAX = 61
MR_ENTRY = re.compile(r"^META-INF/versions/(\d+)/")


def class_major(data):
    """The major version of a class file, or None if the bytes are no class file."""
    if len(data) < 8 or data[:4] != b"\xca\xfe\xba\xbe":
        return None
    return struct.unpack(">H", data[6:8])[0]


def check_jar(path, limit):
    """Scan one jar.

    Returns (max major of loadable classes, count, offending entries, versioned entries summary,
    module-info majors).
    """
    loadable_max = 0
    count = 0
    offending = []
    versioned = {}
    module_info = []
    with zipfile.ZipFile(path) as jar:
        for info in jar.infolist():
            name = info.filename
            if not name.endswith(".class"):
                continue
            # only the header is needed, and some jars hold thousands of classes
            with jar.open(info) as entry:
                major = class_major(entry.read(8))
            if major is None:
                continue
            mr = MR_ENTRY.match(name)
            if mr and int(mr.group(1)) > 17:
                # used only by a newer JVM than the browser's
                versioned[mr.group(1)] = max(versioned.get(mr.group(1), 0), major)
                continue
            if name.endswith("module-info.class"):
                module_info.append(major)
                continue
            count += 1
            loadable_max = max(loadable_max, major)
            if major > limit:
                offending.append((name, major))
    return loadable_max, count, offending, versioned, module_info


def main(argv):
    """Check every jar of the directory and print a table; see the module doc."""
    limit = DEFAULT_MAX
    args = list(argv)
    if "--max" in args:
        i = args.index("--max")
        limit = int(args[i + 1])
        del args[i:i + 2]
    jar_dir = args[0] if args else os.path.join(
        os.path.dirname(os.path.abspath(__file__)), "..", "target", "notebook", "jars")
    jars = sorted(f for f in os.listdir(jar_dir) if f.endswith(".jar"))
    bad = 0
    for name in jars:
        loadable_max, count, offending, versioned, module_info = check_jar(
            os.path.join(jar_dir, name), limit)
        status = "OK " if not offending else "BAD"
        extra = ""
        if versioned:
            extra += " versions/" + ",".join(
                "%s(max %d)" % (k, v) for k, v in sorted(versioned.items(), key=lambda kv: int(kv[0])))
        if module_info:
            extra += " module-info(%d)" % max(module_info)
        print("%s max=%-3d classes=%-6d %s%s" % (status, loadable_max, count, name, extra))
        for entry, major in offending[:5]:
            print("      %d %s" % (major, entry))
        if len(offending) > 5:
            print("      ... %d more" % (len(offending) - 5))
        if offending:
            bad += 1
    print("%d jars, %d with classes above major version %d" % (len(jars), bad, limit))
    return 1 if bad else 0


if __name__ == "__main__":
    sys.exit(main(sys.argv[1:]))
