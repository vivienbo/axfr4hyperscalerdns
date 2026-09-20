# AXFR4HyperscalerDNS

This project aims at:
* Providing a **secondary DNS** Server that accepts Zone Transfers (AXFR, IXFR) and updates an Hyperscaler DNS Service,
* Providing a **primary DNS** server that detects changes in Hyperscaler DNS Services Zones, and transmits Zone Transfers (AXFR, IXFR) to a secondary DNS-compatible server.

Intended supported DNS Servers are:
* Windows DNS Server,
* bind9,
* unbind

Intended supported Hyperscaler DNS Services starts with:
* [Azure DNS](https://learn.microsoft.com/en-us/azure/dns/)

and intends to support:
* [AWS Route 53](https://aws.amazon.com/route53/),
* [OVH Cloud DNS](https://docs.ovhcloud.com/en/guides/web-cloud/domains/api-domain-dns)
* and [GCP Cloud DNS](https://cloud.google.com/dns)

The application is:
* Written in Java 21 (LTS),
* Uses Maven for build and configuration
* Leverages the [DNSJava](https://mvnrepository.com/artifact/dnsjava/dnsjava) library

# Not expected to be supported for now
* DNS over HTTPS (DoH) isn't necessary as users of this DNS server shouldn't be end users
* DNS over TLS (DoT) isn't supported yet (as Windows DNS Server does not support it)

# Step 1 of the development

* Support as Secondary DNS replicating data to Azure DNS.
* Supports multiple domains
* Supports replication of subdomains or topdomains
    * For instance the DNS domain may receive AXFR/IXFR for domain example.com and the Azure DNS domain could be ad.example.com (hence only subdomains to ad.example.com get replicated to Azure)
    * Fpr instance the DNS domain may receive AXFR/IXFR for domain ad.example.com and the Azure DNS domain could be example.com
* Support for multiple Azure subscriptions / Entra ID Application Secrets, with the secrets stored using java compliant keystores

## Build & Testing

### Prerequisites
- JDK 21+
- Maven 3.6+

### Building the Project

```bash
# Clean build with tests (includes code coverage)
mvn clean verify

# Production build with dependencies resolved
mvn clean install -DwithResolver

# Format code using Spotless
mvn spotless:apply
```

### Code Coverage

Code coverage reports are generated after running tests:
- HTML Report: `target/site/jacoco/index.html`
- Test Results: `target/surefire-reports/`

Coverage is tracked in CI and uploaded as artifacts for each pull request.

## Development Workflow

### Running Tests Locally

```bash
# Run all unit tests
mvn test

# Run with coverage
mvn clean verify

# View coverage report (opens in browser)
open target/site/jacoco/index.html
```

### CI/CD on GitHub

This project uses GitHub Actions for automated testing:

#### Pull Requests
- Every PR triggers validation on `main` and `original-cicd` branches
- Code is checked out, built, and tested automatically
- Coverage reports are uploaded as artifacts
- Build must pass (all tests green) before merging

#### Release Builds
- Pushing a tag matching pattern `v*` (e.g., `v1.0.0`) triggers release build
- Full build with all dependencies is performed
- JAR artifacts (with resolver dependencies) are published to GitHub Releases

See `.github/workflows/` for workflow details.

## Project Structure

```
axfr4hyperscalerdns/
├── .github/workflows/       # CI/CD workflows
│   ├── pr-validation.yml    # PR & push validation
│   └── release-build.yml    # Tag-based release builds
├── src/main/java/           # Main source code
│   └── net/boistuaud/axfr4hyperscalerdns/
├── src/test/java/           # Unit tests (JUnit 5)
├── target/site/jacoco/      # Coverage reports
└── pom.xml                  # Maven configuration
```

## Testing Best Practices

### Writing Tests
- Use `*Test.java` naming convention for test classes
- Test each DTO and Config class separately
- Use JUnit 5 assertions with AssertJ for readability
- Place tests in package-level or category-based subdirectories

### Example Test Structure

```java
package net.boistuaud.axfr4hyperscalerdns.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ZoneTransferConfigTest {
    @Test
    void testConstructor() {
        // Test implementation
    }
}
```