## Commit Convention

This repository follows a scoped commit convention for the monorepo.

Format:

type(scope): message

### Types
- **feat** - introduces a new feature
- **fix** - fixes a bug
- **docs** - documentation only changes
- **style** - formatting code style
- **refactor** - code changes that improve structure or readability without changing behavior
- **perf** - code change that improves performance
- **test** - adds missing tests or corrects existing automated tests.
- **build** - changes that affect the build system or external dependencies
- **ci** - changes to CI configuration files or scripts
- **chore** - other changes that don't modify productions or test files, such as tooling updates or configuration adjustments.

### Scopes

- **issuer** - Smart meter issuer SSI service
- **verifier** - Smart meter verifier service 
- **scripts** - helper scripts
- **infra** - docker, infrastructure and deployment
- **repo** - repository-level changes
- **docs** - documentation updates
