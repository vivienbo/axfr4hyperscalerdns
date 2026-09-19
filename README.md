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