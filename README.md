# Smart Meter SSI
- This project demonstrates a **Self-Sovereign Identity (SSI)** flow using **Decentralized Identifiers (DID)** and **Verifiable Credentials (VC)** for issuing and verifying digital credentials in **AnonCreds** format, within the **Hyperledger Aries** ecosystem.

- The solution is applied in a case study of self-sovereign identities for smart meters, exploring how SSI concepts can be used for authentication and access control to device data, ensuring identity, authenticity, and trust in communication between systems in the [InterSCity Platform](https://gitlab.com/interscity/interscity-platform).

## Technologies

### Backend
- [**Spring boot 4.0.2**](https://spring.io/projects/spring-boot) - Spring Boot makes it easy to create stand-alone, production-grade Spring based Applications that you can just run.
- [**Spring Security**](https://spring.io/projects/spring-security) - Spring Security is a powerful and highly customizable authentication and access control framework.
- [**Spring Data JPA**](https://docs.spring.io/spring-data/jpa/reference/index.html) - Spring Data JPA provides repository support for the Jakarta Persistence API (JPA).
- [**Thymeleaf**](https://www.thymeleaf.org/) - Thymeleaf is a modern server-side Java template engine for both web and standalone environments.
- [**Springdoc open api**](https://springdoc.org/) - Java library helps to automate the generation of API documentation using spring boot projects.

### SSI
- [**ACA-Py 0.12.2**](https://aca-py.org/) - ACA-Py is a production-ready, open-source self-sovereign identity (SSI) agent for building non-mobile decentralized trust services. 
- [**acapy-java-client 0.10.0**](https://github.com/hyperledger-labs/acapy-java-client) - Aries Cloud Agent Python Java Client Library.
- [**bc-wallet-mobile**](https://github.com/bcgov/bc-wallet-mobile) - BC Wallet to hold Verifiable Credentials.


### Infra
- [**Docker 29.2.1**](https://www.docker.com/) - Docker is a platform designed to help developers build, share, and run container applications.

## Setup

### `.env.sample`
- Contains the environment variables required to deploy the project.
- Copy it to `.env` and update the values if necessary:

```bash
cp .env.sample .env
```

### ACA-Py
#### Issuer
- To configure the issuer and register the required credentials on the ledger, run the scripts in `scripts/bootstrap` in the following order:

1. `01-Create-did-issuer.sh`
2. `02-Add-public-did-in-ledger.sh`
3. `03-Make-did-public.sh`
4. `04-Create-schemas.sh`
5. `05-Create-Credential-definitions.sh`
