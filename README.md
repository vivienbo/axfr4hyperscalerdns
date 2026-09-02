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
* DNS over TLS (DoT) isn't supported yet (as Windows DNS Server does not support it)5533