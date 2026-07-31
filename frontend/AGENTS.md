# AGENTS.md

## Project Overview

This is a React + TypeScript frontend for a Spring Boot backend.

- Frontend: React + Vite + TypeScript
- Backend: Spring Boot REST API
- Authentication/session handled by Spring Boot
- Axios is centralized through `src/helpers/api.ts`
- Cookies and CSRF tokens are handled there.
- Do NOT create new axios instances unless specifically requested.

---

# Goal

Prioritize:

- Clean code
- Readable code
- Simple architecture
- Small files
- Easy maintenance
- Predictable folder structure

Favor boring, conventional solutions over clever ones.

---

# Code Style

- Keep functions small.
- Prefer early returns.
- Avoid nested conditionals.
- Avoid unnecessary abstractions.
- Use descriptive variable names.
- Remove dead code.
- Remove commented-out code.
- Keep components focused on one responsibility.

---

# Folder Structure

Keep the project simple.

Preferred structure:

```
src/
│
├── assets/
├── components/
├── helpers/
├── pages/
├── types/
│
├── App.tsx
├── main.tsx
```

Only create a new folder if it has a clear long-term purpose.

Avoid complex structures like:

- services/
- repositories/
- features/
- modules/
- domain/
- infrastructure/
- adapters/

unless the project genuinely grows large enough to require them.

---

# Components

Components should:

- Do one thing well.
- Stay reasonably small.
- Receive data through props.
- Avoid unnecessary state.
- Avoid deeply nested JSX.

Extract reusable UI only when it is actually reused.

Do not prematurely create generic components.

---

# API

All HTTP communication goes through:

```
src/helpers/api.ts
```

Use the existing Axios instance.

Do not create duplicate API clients.

Example:

```ts
import api from "../helpers/api";

const response = await api.get("/users");
```

Authentication, cookies, CSRF, and interceptors are already configured there.

---

# Types

Place shared interfaces inside:

```
src/types
```

Avoid inline `any`.

Prefer proper TypeScript types.

---

# State Management

Unless requested:

- Use React state.
- Use Context only when necessary.
- Do not introduce Redux, Zustand, MobX, or other libraries.

Keep state as local as possible.

---

# Styling

Reuse existing styling patterns.

Do not introduce a new CSS framework unless requested.

Avoid inline styles except for trivial cases.

---

# Imports

Prefer clean imports.

Keep import order:

1. React
2. Third-party libraries
3. Internal modules
4. Types
5. CSS

Remove unused imports.

---

# Naming

Use consistent naming.

Components:

```
LoginPage.tsx
Navbar.tsx
UserCard.tsx
```

Helpers:

```
api.ts
formatDate.ts
validateEmail.ts
```

Types:

```
User.ts
AuthResponse.ts
```

Avoid abbreviations unless commonly understood.

---

# Error Handling

Handle expected errors.

Avoid empty catch blocks.

Provide meaningful error messages.

Do not swallow exceptions silently.

---

# Performance

Do not optimize prematurely.

Only introduce memoization (`useMemo`, `useCallback`, `React.memo`) when there is measurable benefit.

Favor readability.

---

# Dependencies

Do not add new npm packages unless:

- they solve a real problem,
- the existing stack cannot solve it cleanly,
- or explicitly requested.

Prefer the standard React ecosystem.

---

# AI Agent Guidelines

When modifying code:

- Follow existing project style.
- Preserve consistency.
- Minimize unnecessary changes.
- Do not rewrite unrelated code.
- Do not rename files without reason.
- Do not introduce new architectural patterns.
- Do not over-engineer solutions.
- Keep diffs as small as possible.

If multiple approaches exist:

Choose the simplest maintainable solution.

When uncertain:

Prefer consistency with the existing codebase over theoretical best practices.