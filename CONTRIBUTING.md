# Contributing to Cinder ♨️

Thanks for wanting to contribute. This file is a concise guide to get you productive quickly – project structure, conventions, and the minimal process to open issues and PRs.

> [!NOTE]
> ### for eager ⚡ ones
> example workflow is at the [very end of this page](#example-flow)


## Overview
- Purpose: zero-knowledge file transfer (server-side minimal crypto responsibility).
- Modes: `Light` (quiz-based, client-side decryption) and `Advanced` (OPAQUE server verification).
- Tech
  + Server – Java 21 + Spring
  + Client – TypeScript + Svelte

## Repository Layout
- `core/` – Pure domain: entities, value objects, ports (interfaces). No frameworks, no implementations.
- `spring/infra/` – Infrastructure adapters: DB, file storage, crypto adapters, session managers; implements `core` ports.
- `spring/rest/` – Spring Boot application and HTTP controllers; bundles `ui/dist`.
- `ui/` – Svelte + TypeScript frontend, builds into `ui/dist`.

## Core Principles & Conventions
- **Hexagonal architecture**: `core` defines ports & entities; `infra` and `rest` are adapters.
- Keep `core` free of frameworks and external deps. Only pure Java 21 types, records, and interfaces.
- Entities and value objects must validate invariants (not null, not empty, length checks) at construction time.
- No DTOs or repositories in `core`. Only small immutable types and ports.
- Use `record` and immutable data whenever practical.
- Constructor (or factory) validation: fail-fast on bad inputs.

## Build & Test (minimal commands)
> [!NOTE]
> Project uses Maven (`mvn`).

- Build entire repo: `mvn clean install` (from repo root)
- Build core module only: `cd core && mvn clean install`
- Run backend app (dev): `cd spring/rest && mvn spring-boot:run`
- Run tests: `mvn test` (or per-module: `cd core && mvn test`)

<a name="format-commit-msg"></a>
## Commit Message Format
```
Type: #n Title
```
- Start with a type and issue ref:
  - `Feat: #123 Short description`
  - `Bugfix: #234 Correct validation (infra)`
  - `Hotfix: #456 Remove sensitive data logging (rest)`
- Keep commit headline concise and uniformly structured

## Code Quality & Review
### Naming Conventions
| Type | Convention | Example |
|------|-----------|---------|
| **Use Cases** | [Verb][Noun]UseCase | `UploadFileUseCase`, `CreateLinkUseCase` |
| **Ports (Interfaces)** | [Noun]Port | `FileStorePort`, `CryptoPort`, `RepositoryPort` |
| **Adapters** | [Technology][Noun]Adapter | `LocalFileStoreAdapter` |
| **Services** | [Noun]Service | `PepperService` |
| **Controllers** | [Noun]Controller | `UploadController`, `DownloadController` |
| **DTOs** | [Noun][Request/Response] | `UploadRequest`, `DownloadResponse` |
| **Constants (static final)** | SNAKE_CASE | `PREFIX_FILE`, `MAX_ATTEMPTS` |
| **Protected/Private Methods/Properties** | starting with underscore | `_privateMethod`, `_PROTECTED_CONSTANT` |

### Code Formatting
- **Indentation:** 2 spaces (no tabs)
- **Line Length:** 120 characters max
- **Braces:** Java standard (opening brace on same line)

### Public API
- JavaDoc for public methods, classes
- package-info.java with JavaDoc for packages

### AVOID ❌
- Inline code comments
- Breaking changes to domain model

## Security & Secrets
- Never commit secrets: peppers, HMAC keys, private keys, passphrases, quiz answers, or raw file contents.
- Development secret location: external to repo (e.g., `~/.config/spring-boot/credentials.properties`).
- If a PR accidentally exposes secrets, contact maintainers and rotate affected secrets immediately.

## Issue/PR Labels Quick Reference
- `Task` – planned work
- `Proposal` – preliminary idea/RFC
- `Bug` – user-facing error
- `Problem` – analysis/report

## License
This project is AGPL-3.0 licensed (see `README.md` badge). Contributions are assumed to be compatible with that license.

---

<a name="example-flow"></a>
## Example Flow

### Inspect the Issues
> [!IMPORTANT]
> Contributions focused on bugfixes and scheduled tasks are especially welcome and prioritized.
> If you draft a new feature or open a PR without first discussing your proposition with maintainers, please note there is a high chance it may be rejected – this is to ensure the project’s direction remains clear and consistent.

See project's issues to find any unassigned bugs, problems, or identify new ones.

> [!TIP]
> Use issue filtering to find desired labels (e.g. `Bug`, `Problem`, `help wanted`)

Once chosen, notify in the comments below your intent to resolve that issue.

### Prepare Solution
Follow naming conventions.
Write unit tests for new features.

### Test & Format
Ensure the solution is ready to be presented.

### Open PR
 > [!IMPORTANT]
 > - Squash your commits before submitting.
 > - Follow [commit message format](#format-commit-msg)

---

Thank you – concise contributions keep the project fast to review and secure.
