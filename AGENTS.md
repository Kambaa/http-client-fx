# AGENTS.md

## Project overview

This is a JavaFX desktop application, not a React or web frontend project.
This project's aim is making an http client just like Postman, user select http method, url, request
headers and body, and when presses SEND button, it makes an HTTP requests, gets the response code,
headers and body and shows it to the user.

## Stack

- Java
- JavaFX
- Maven

## Working rules

- Do not suggest React, Vite, Next.js, JSX, or browser-based optimizations.
- Prefer JavaFX patterns: controllers, FXML, scene/stage lifecycle, bindings, observable properties.
- Preserve desktop-app behavior and existing UI flow.
- Keep changes compatible with the current build tool and Java version.

## Build/Run Commands

Project has maven wrapper, prefer that. SceneBuilder is located at C:\Users\YGPC\AppData\Local\SceneBuilder\SceneBuilder.exe, you can open the fxml file via that(For example `C:\Users\YGPC\AppData\Local\SceneBuilder\SceneBuilder.exe C:\Users\YGPC\IdeaProjects\http-client-fx\src\main\resources\tr\com\yusufgunduz\httpclientfx/hello-view.fxml`).

### Maven

- `mvnw clean`: Clean build artifacts
- `mvnw compile`: Compile source code
- `mvnw javafx:run`: Run javafx application
- 

### Naming Conventions

- Classes: PascalCase (e.g., `HomeController`)
- Variables: camelCase (e.g., `userInput`)
- Constants: ALL_CAPS (e.g., `MAX_RETRIES`)
- FXML files: lowercase (e.g., `login.fxml`)

### Formatting

- 2-space indents
- 100-column line limit
- Braces on new lines for control structures
- No trailing spaces

### Types

- Prefer primitive types over wrappers
- Use `Optional<T>` for nullable values
- Favor `java.time` over `java.util.Date`

### Error Handling

- Prefer checked exceptions for recoverable issues
- Use `try-with-resources` for auto-closing
- Log errors with `java.util.logging` or SLF4J

### JavaFX Best Practices

- Use `FXMLLoader#load()` for UI loading
- Bind properties with `Binding` or `DoubleBinding`
- Use `Platform.runLater()` for UI updates
- Avoid direct UI manipulation in controllers

### Imports

- Use fully qualified names in imports
- Group imports by package
- No star imports (`import java.util.*`)

### Directory Structure

- `src/main/java`: Application code
- `src/main/resources`: FXML, properties, config
- `src/test/java`: Tests
- `src/test/resources`: Test data

### Commit Messages

- Follow Conventional Commits
- Use imperative mood (e.g., "Fix bug in login flow")
- Prefix with type (feat, fix, docs, style, refactor, test)

## Additional Rules

### Security

- Never log sensitive information
- Use `java.security.MessageDigest` for hashing
- Validate all user inputs

### Performance

- Use `java.util.concurrent` for parallelism
- Avoid heavy computations in UI threads
- Use `@Cacheable` for expensive operations

### Dependencies

- Keep dependencies up to date
- Use `mvn dependencyUpgrade` for upgrades
- Avoid version conflicts with `<dependencyManagement>`

### Documentation

- Update Javadoc with API changes
- Keep README.md up to date
- Add migration notes for breaking changes

### Tools

- Use IntelliJ IDEA for development
- Use Git for version control
- Use Maven for dependency management
- Use Java 17+ for compilation

## Contributing

## Appendix

### Common Issues

- "Cannot find symbol" errors: Check imports and package declarations
- "Method does not exist" errors: Check method signatures
- "Missing resource" errors: Check resource paths

### FAQ

- "How to run a single test?": Use `mvn -Dtest=MyTestClass test`
- "How to debug JavaFX?": Use `javafx.fxml.LoadException` and `Platform.runLater()`
- "How to add a new dependency?": Update pom.xml and run `mvn dependencyUpgrade`