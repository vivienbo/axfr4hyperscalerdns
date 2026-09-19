# AGENTS.md - axfr4hyperscalerdns Guide

## Scope & Context
- These instructions apply throughout the repository. More specific `AGENTS.md` files take precedence.
- **Java 21**: Use modern features (records, pattern matching, switch expressions) where they improve clarity. Prefer established repository patterns over speculative abstractions.
- Before modifying code: inspect implementation, tests, documentation, and existing patterns. Make the smallest change that solves the problem.
- For lifecycle/concurrency: use openHAB schedulers, ensure proper cleanup in `dispose()`, make repetitive paths idempotent, avoid unnecessary repeated work, and never hold locks while calling framework/external code.

## Error Handling & External Data
- Validate external input; rely on framework guarantees where appropriate. Use structured exceptions rather than human-readable messages for control flow. Preserve original cause when wrapping. Don't cache failures as success. Invalidate cached data on auth/config/connection/lifecycle changes. Close resources reliably (try-with-resources). Distinguish expected failures from defects.

## Security & Nullability
- **Security**: Treat credentials/tokens/keys as sensitive. Never expose in logs or exceptions. Use established authentication/security APIs. Validate external input crossing trust boundaries.
- **Nullability**: Annotate with `@NonNullByDefault`; use `@Nullable` intentionally. Fix rather than suppress null warnings.

## Logging
- Use parameterized SLF4J logging. Reserve `warn/error` for unexpected conditions. Update Thing status for expected failures.

## Documentation & Code Comments
- **Javadoc**: Use where appropriate; keep accurate when behavior changes.
- **Author tags**: Add for new files/substantial contributions using real human names. Preserve existing tags.
- **Code comments**: Explain non-obvious intent/constraints. Remove obvious comments that restate code.

## Formatting & Code Style
- Run `mvn spotless:apply` for formatting. Keep POM sections sorted. Use provided formatter configs.

## Testing & Validation
- Cover changed behavior with relevant tests (boundary, failure, malformed input, recovery).
- Integration tests in `itests/` use bndrun configurations. For async changes, wait for activation to complete before modifying providers.
- Bound blocking operations with timeouts to prevent hangs.

**Commands**:
```bash
mvn spotless:apply           # Format
mvn clean install             # Full build
mvn clean install -DwithResolver  # With dependencies
```

## Validation & Pull Requests
- Validate narrowly for your change (formatting, tests, module-specific builds). Run full build when warranted.
- Follow `.github/PULL_REQUEST_TEMPLATE.md`. Convert HTML-commented template sections to visible Markdown. Verify PR matches diff/scope after follow-up changes.


## Lifecycle, Concurrency, and State
- Use openHAB-provided schedulers rather than creating threads. Release resources in `dispose()`.
- Make repetitive paths idempotent (registrations, listeners, channels). Handle lifecycle transitions gracefully.
- Don't hold locks while calling framework/external code. Validate before mutable operations.
- Consider performance implications of polling/discovery/retry loops. Close connections reliably.
When code performs asynchronous, scheduled, or lifecycle-sensitive work:

- Use openHAB-provided schedulers rather than creating threads where possible.
- Ensure tasks, futures, listeners, connections, and other resources are released during `dispose()` or deactivation.
- Consider callbacks that may execute while or after the owning object is being disposed, as well as initialization, shutdown, failure, reconnection, and repeated lifecycle transitions.
- Ensure already-running work cannot publish stale state, restore an obsolete status, or reschedule itself after the state that authorized that work has become invalid.
- When validation and subsequent use depend on mutable shared state, validate and use the same captured state or provide appropriate synchronization.
- Keep distinct states separate when conflating them could break recovery, for example session validity, connection state, configuration state, and Thing status.
- Shut down dependent tasks, contexts, listeners, or consumers before closing shared resources they still depend on.
- Ensure reconnect and retry behavior cannot leave stale tasks or resources.
- When correctness depends on the order of asynchronous stages, explicitly compose or wait for their completion; submitting work to an executor does not establish that it completed before dependent work begins.
- When configuration or metadata can change at runtime, apply changes to active state and resources rather than consuming them only during initial activation.
- Initialization, reconciliation, registry-change, and reconnect paths that may run repeatedly must be idempotent and must not create duplicate registrations, listeners, channels, or protocol state.
- Consider material performance and resource impact for polling, discovery, event callbacks, registry scans, retry loops, and large collections; avoid unnecessary repeated work, unbounded state, and blocking I/O on shared scheduler or event threads.
- Avoid holding locks while calling framework or external code, and review synchronization carefully for potential races and deadlocks.

Framework lifecycle methods should return promptly unless their API explicitly allows blocking operations.
When changing authentication, connection, polling, retry, or lifecycle behavior, verify both the failure transition and the path back to a working state.

### Error Handling and External Data

For code interacting with devices, services, files, sockets, or external APIs:

- Validate external input where malformed, incomplete, missing, or unexpected values can realistically occur.
- Rely on existing framework, library, parser, or API guarantees where appropriate instead of adding redundant validation.
- Follow the surrounding implementation's established validation and error-handling patterns.
- When a protocol provides structured data such as JSON or XML, use an appropriate parser and inspect structured fields rather than relying on formatting-sensitive substring matching.
- Do not use human-readable exception messages, log messages, or similar text for program control flow when a dedicated exception type, error code, enum, or other structured state can represent the condition.
- Catch or wrap exceptions only when they can be handled, translated at an abstraction boundary, or given useful context; preserve the original cause when wrapping.
- Do not map missing or unknown external values to valid values such as zero unless the protocol explicitly defines that mapping.
- Do not treat absence from a partial or transient response as proof that a capability is unsupported, and do not make baseline discovery or operation depend on optional enrichment data unless the protocol requires it.
- Preserve supported legacy protocol and API representations when introducing newer alternatives unless compatibility is intentionally dropped; gate version- or capability-specific behavior on the actual supported feature rather than assuming related features were introduced together.
- When acknowledgement controls redelivery, acknowledge external messages or events only after successful processing or durable retention.
- Preserve protocol-defined units, encodings, charsets, identifiers, and semantics.
- Close resources reliably, preferably using try-with-resources.
- Handle interruption correctly and do not silently consume interrupt-related exceptions.
- Distinguish expected communication failures from actual software defects.

When caching external or computed data:

- Do not cache transient failure or sentinel results as successful data unless that behavior is intentional.
- Consider whether authentication, configuration, connection, or lifecycle changes require related cached data to be invalidated.
- Ensure cache keys and invalidation account for every input that can change the derived result, including transitive dependencies where relevant.

### Security and Sensitive Data

- Treat credentials, API keys, access tokens, refresh tokens, webhook secrets, private keys, and similar values as sensitive data.
- Do not expose sensitive data through logs, exceptions, Thing properties, discovery results, `toString()` implementations, or other user-visible diagnostics.
- Reuse established project authentication, networking, and security facilities where applicable, and do not disable TLS certificate or hostname verification or weaken authentication by default.
- Treat externally controlled URLs, paths, headers, identifiers, and other values crossing trust boundaries as untrusted and validate or constrain them where they can affect security-sensitive behavior.

### Nullability and Mutable State

Follow the following null-annotation conventions:

- Annotate non-DTO classes with `@NonNullByDefault`.
- Use `@Nullable` only when `null` is an intentional state.
- Do not suppress null warnings instead of addressing the underlying issue.
- When mutable fields may change concurrently, use a local copy where needed so a null check and subsequent access operate on the same value.

### Logging

- Use parameterized SLF4J logging instead of string concatenation.
- Treat expected device, network, and communication failures as normal runtime conditions; update Thing status where appropriate rather than routinely logging them at `warn` or `error`.
- Reserve `warn`, `error`, and stack traces for unexpected conditions that require attention or may indicate a software defect.

### Documentation and Comments

#### Javadoc

- Use Javadoc where appropriate for API, class, and method documentation.
- Keep Javadoc accurate when behavior changes.

#### Author Attribution

- Add an `@author` tag when creating a new Java source file or making a substantial contribution to an existing Java source file.
- Do not add an `@author` tag for trivial changes such as formatting, typo fixes, dependency updates, or small mechanical modifications.
- For a new Java source file, use `@author Real Name - Initial contribution`.
- For a substantial contribution to an existing Java source file, add a new tag using `@author Real Name - Description of contribution`.
- Keep the contribution description brief but meaningful so it identifies the main contribution without becoming a detailed change log.
- Append new `@author` tags after existing tags so contributions remain chronological, with the oldest at the top and newest at the bottom.
- Preserve existing `@author` tags rather than modifying or replacing them to describe later contributions.
- Use the real human contributor name; do not use AI tools, AI assistant names, GitHub usernames, aliases, placeholder names, or invented identities.
- Do not guess a contributor's real name; determine it from reliable repository information, Git configuration, commit metadata, or information provided by the user.

#### Code Comments

- Explain non-obvious intent, constraints, invariants, protocol or API behavior, compatibility requirements, lifecycle or concurrency assumptions, and intentional deviations from otherwise obvious implementations.
- Do not add comments that merely restate code, narrate straightforward control flow, repeat names in prose, or provide tutorial-style implementation walkthroughs.
- Prefer improving structure and naming when code can be made self-explanatory.
- Preserve useful comments describing genuinely non-obvious behavior.
- When behavior or implementation changes, update or remove stale comments and keep related documentation consistent with the implementation.

### Code Style and Formatting

#### Formatting

- Run `mvn spotless:apply` for the affected project to apply repository formatting, including import organization.
- Keep POM sections sorted.
- When a file is generated from another source, update the source or generator and regenerate the output rather than hand-editing generated content, unless the repository workflow explicitly requires direct edits.
- Avoid formatting or generation changes outside the intended scope.

## File-Specific Guidelines

### pom.xml Files

- Prefer versions and configuration inherited from repository `dependencyManagement` and `pluginManagement`; override or duplicate them only when the module intentionally differs.

When adding or changing Maven dependencies:

- Prefer existing JDK, openHAB, or already-used library functionality over adding a new dependency when it provides an appropriate solution.
- Check whether corresponding `features.xml` and `*.bndrun` changes are required, including runtime dependencies and hardcoded versions.
- Run Spotless after the changes.
- Validate with `-DwithResolver` when dependency resolution may be affected.

### *.bndrun Files

- These files configure integration tests using bndtools.
- Reference documentation: <https://bnd.bndtools.org/chapters/825-instructions-ref.html>
- When Maven dependencies change, check whether integration-test run bundles also need updating.
- Use the resolver when appropriate rather than manually maintaining stale dependency lists.

## Testing and Validation

Behavioral changes should normally be accompanied by tests that demonstrate the intended behavior and protect against regression.

Tests should:

- Cover the behavior changed by the implementation.
- Include relevant boundary, failure, malformed-input, and recovery cases.
- Verify externally observable behavior rather than implementation details.
- Remain deterministic and avoid unnecessary timing dependencies.
- Avoid timing-sensitive tests that depend on fixed delays; synchronize on observable events or conditions using latches, barriers, futures, locks or conditions, or bounded polling where appropriate.
- Tests must tolerate slow or congested build runners. If a fixed sleep cannot reasonably be avoided, use a conservative duration with sufficient margin for CI load and keep the delay bounded.
- When testing blocking, shutdown, retry, or deadlock-prone behavior, bound the operation itself with a timeout so regressions fail the test instead of hanging the build, and ensure helper threads cannot keep the JVM alive after the timeout.

When modifying existing behavior, inspect existing tests first and extend them rather than creating redundant coverage.

### Integration Tests

- Integration tests are located in the `itests/` directory.
- Use bndrun configurations to define realistic OSGi runtime environments.
- For lifecycle-sensitive changes, consider activation, deactivation, reactivation, service arrival/removal, and different installation orders where relevant.
- In bnd-based integration tests, do not infer a service-availability race merely from a direct `getService(...)` call during test setup. Services supplied by the resolved runtime are normally available before the tests execute; use polling only when the specific service can actually arrive asynchronously after test execution has started.
- When an integration test modifies a provider tracked by an `AbstractRegistry`-based registry, ensure asynchronous registry activation has completed before modifying the provider. Use `waitForCompletedAsyncActivationTasks()` when available, before adding, updating, or removing provider elements.

### Build Validation

Run the narrowest validation that provides meaningful coverage for the change.

- For Markdown-only changes, run Markdown linting for the changed files.
- For Java changes, run Spotless and the relevant tests or Maven build for the affected module.
- For dependency or resolver changes, also validate with `-DwithResolver` where relevant.
- Run the full repository build when the scope or impact of the change warrants it.

Useful commands:

```bash
# Format the affected project
mvn spotless:apply

# Full build
mvn clean install

# Full build with dependency resolution
mvn clean install -DwithResolver
```

Before considering a change complete:

1. Review the complete diff, including files changed indirectly by formatting or generation.
1. Remove accidental, unrelated, or unnecessary changes.
1. Re-read newly added comments and remove comments that merely explain obvious code.
1. Run the applicable formatting, linting, tests, and Maven build based on the scope of the change.
1. Review relevant compiler and static-analysis findings produced by the build.
1. Fix static-analysis findings introduced or materially worsened by the change.
   Do not perform unrelated cleanup solely to remove pre-existing findings.
1. Verify lifecycle, concurrency, state transitions, failure handling, recovery, and cleanup for affected code.
1. Verify that documentation and configuration metadata still match the implemented behavior.
1. Do not claim that a test, build, formatter, linter, or analysis passed unless it was actually run successfully.

## Pull Requests

When creating a pull request, read and follow `.github/PULL_REQUEST_TEMPLATE.md` for the repository's current PR requirements.

- Treat HTML-commented template text as instructions and write visible Markdown that addresses the applicable requirements; do not copy the comment delimiters or leave the PR description hidden inside an HTML comment.
- Before creating or updating a pull request, verify that its title and description still match the current diff and scope, especially after follow-up changes.
- Treat the template as the source of truth for the remaining pull-request requirements rather than duplicating them here.