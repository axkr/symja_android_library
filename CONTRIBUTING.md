# Contributing

First off thanks for your interest in improving Symja! We appreciate you taking the time to contribute to Symja and to ensure that your contribution is easy to review and process we kindly ask that you follow the guidance outlined below.

---

### Table of Contents

Read through these guidelines before you get started:

1. [Questions & Concerns](#questions-concerns)
2. [Issues & Bugs](#issues-bugs)
3. [Feature Requests](#feature-requests)
4. [Submitting Pull Requests](#submitting-pull-requests)
5. [Running the tests](#running-the-tests)

### Questions & Concerns

If you have any questions about using or developing for this project, send a question in the [Discord Symja chat][5] or directly via [email][1].

### Issues & Bugs

Submit an [issue][2] or [pull request][3] with a fix if you find any bugs in
the project. See [below](#submitting-pull-requests) for instructions on sending
in pull requests.

When submitting an issue or pull request, make sure you're as detailed as possible
and fill in all answers to questions asked in the templates. For example, an issue
that simply states "X/Y/Z isn't working!" will be ignored.

### Feature Requests

Submit an [issue][2] to request a new feature. Features fall into one of two
categories:

1. **Major**: Major changes should be discussed in the [Discord chat group][5]. We're
always open to suggestions and will get back to you as soon as we can!
2. **Minor**: A minor feature can simply be added via a [pull request][3].

### Submitting Pull Requests

Before you do anything, make sure you check the current list of [pull requests][4]
to ensure you aren't duplicating anyone's work. Then, do the following:

1. [Fork the repository][6]
3. Implement your feature or bug fix.
2. Add tests for your unimplemented feature or bug fix.
4. Run `mvn clean install` to run the tests. This runs the *fast* tier only - see
   [Running the tests](#running-the-tests) below, and run `mvn verify -Pall-tests`
   before you push.  
5. Commit your changes, and be sure to leave a detailed commit message.
6. Push the changes to your forked repo on Github 
7. [Submit a pull request][8]

### Running the tests

The test suite is split into three tiers so that an ordinary edit/test cycle stays quick.
A test belongs to a tier through a JUnit 5 `@Tag`; the tag names are constants in
`matheclipse-core/src/test/java/org/matheclipse/core/system/TestTags.java`.

| Tier | Command | What it runs |
| --- | --- | --- |
| fast | `mvn verify` | everything untagged - about a minute. This is what CI runs. |
| slow | `mvn test -Pslow-tests -pl matheclipse-core` | only `@Tag("slow")`, about five minutes |
| fast + slow | `mvn verify -Pall-tests` | **run this before pushing** |
| corpus | `mvn -pl matheclipse-io test -Prubi-corpus -Dsurefire.timeout=5400` | the Rubi scoring corpus, about half an hour |

**When you add a test: if a single test method takes more than a second, tag it
`@Tag(TestTags.SLOW)`.** This is not just a convention - the pull-request build runs
`.github/scripts/check-test-budget.py`, which fails the build over an untagged slow test
and names it. It exists so the fast tier stays fast as the suite grows.

The **corpus** tier is a scoreboard rather than a gate: its expected values come from
Rubi's reference implementation, so some failures are known gaps and the profile keeps the
build green on purpose. Compare the per-class pass counts against the previous run instead
of expecting all-green. It needs `reuseForks=false` (the profile sets it) because the test
base flips a global parser setting; sharing one JVM across those classes makes every single
test fail, which is a harness artifact and not a regression.

No GitHub Actions workflow runs the slow or corpus tiers automatically. There is a manual
"Slow tests (manual)" workflow you can start from the Actions tab, and **before cutting a
release** run `mvn verify -Pall-tests` and the corpus profile locally.

[1]: mailto:axelclk@gmail.com
[2]: https://github.com/axkr/symja_android_library/issues/new
[3]: https://github.com/axkr/symja_android_library/compare
[4]: https://github.com/axkr/symja_android_library/pulls
[5]: https://discord.gg/tYknzr2qam
[6]: https://help.github.com/articles/fork-a-repo
[7]: https://help.github.com/articles/fork-a-repo#create-branches
[8]:https://help.github.com/articles/using-pull-requests
