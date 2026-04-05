# SonarQube + Jenkins

## SonarQube server

1. Download from [SonarQube downloads](https://www.sonarsource.com/products/sonarqube/downloads/) or use Docker, start the server (default UI: `http://localhost:9000`).
2. Sign in, create a project (or let the first analysis create it). The key in `expense-tracker/sonar-project.properties` is `expense-tracker` — use the same in SonarQube or change both to match.

## Jenkins plugins

**Manage Jenkins → Plugins** — install:

- **SonarQube Scanner** (provides `withSonarQubeEnv` in Pipeline)

## Jenkins → SonarQube connection

**Manage Jenkins → Configure System → SonarQube servers**

| Field | Value |
|--------|--------|
| **Name** | `SonarQube` (must match `withSonarQubeEnv('SonarQube')` in `expense-tracker/Jenkinsfile`) |
| **Server URL** | e.g. `http://your-sonar-host:9000` |
| **Server authentication token** | SonarQube user token (Secret text credential) |

## Pipeline

`expense-tracker/Jenkinsfile` runs:

1. **Build & Test** — `mvn clean verify` (tests, JaCoCo report, JaCoCo check from `pom.xml`).
2. **SonarQube** — `mvn sonar:sonar` inside `withSonarQubeEnv`, using `sonar-project.properties` for paths and JaCoCo XML import.

## Local analysis (without Jenkins)

From `expense-tracker/`:

```bash
mvn clean verify
mvn sonar:sonar -Dsonar.host.url=http://localhost:9000 -Dsonar.login=YOUR_TOKEN
```

On Windows use `mvnw.cmd` if Maven is not on `PATH`.

## Security

Do **not** commit Sonar tokens. If a token was ever committed, revoke it in SonarQube and create a new one.

## Quality Gate (optional)

In SonarQube, configure a Quality Gate. The pipeline uses `waitForQualityGate abortPipeline: true` — **do not** pass `serverUrl` or `credentialsId` on that step (the SonarQube Scanner plugin does not support them; it caused a Groovy compile error).

Requires a **webhook** from SonarQube to Jenkins: `https://<jenkins-host>/sonarqube-webhook/` (use your ngrok URL if Jenkins is not public). See [SonarQube Jenkins integration](https://docs.sonarsource.com/sonarqube/latest/analyzing-source-code/ci-integration/jenkins-integration/).
