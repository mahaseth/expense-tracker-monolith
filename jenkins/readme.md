# Jenkins (local)

## Prerequisites

- **Java 17** (LTS). Verify with `java -version`.

## Start Jenkins

From this directory:

```bash
java -jar jenkins.war
```

Jenkins listens on **http://localhost:8080** by default.

If port 8080 is in use, pick another port:

```bash
java -jar jenkins.war --httpPort=8085
```

Then open **http://localhost:8085** in your browser (or **http://localhost:8080** if you did not set `--httpPort`).

## First-time unlock

On first run, Jenkins asks for an **initial admin password**. You can read it from:

- The terminal output when Jenkins starts, or  
- `%USERPROFILE%\.jenkins\secrets\initialAdminPassword` on Windows (path may differ if `JENKINS_HOME` is set).

Complete the setup wizard, then create your admin user and install plugins as needed.

## Expense tracker jobs

The Spring Boot app lives under `expense-tracker/`. The pipeline expects the **Jenkins workspace to be the monorepo root** after checkout (so `expense-tracker/` and `jenkins/` sit under `${WORKSPACE}`). That is exactly what you get when Jenkins clones the repo.

### Pipeline from GitHub (clone and run)

Use the public repo: [github.com/mahaseth/expense-tracker-monolith](https://github.com/mahaseth/expense-tracker-monolith).

1. **Plugins:** Ensure **Pipeline** and **Git** are installed (**Manage Jenkins → Plugins**).
2. **Tools:** **Manage Jenkins → Tools** — add **JDK** named `JDK17` and **Maven** named `Maven2` (names must match `expense-tracker/Jenkinsfile`).
3. **New Item** → name e.g. `expense-tracker-ci` → **Pipeline** → OK.
4. Under **Pipeline**:
   - **Definition:** *Pipeline script from SCM*
   - **SCM:** *Git*
   - **Repository URL:** `https://github.com/mahaseth/expense-tracker-monolith.git`
   - **Branches to build:** `*/main` (or `main` depending on your Git plugin UI)
   - **Script Path:** `expense-tracker/Jenkinsfile`
5. **Credentials:** leave *None* for this public repository (only add credentials if you use a private fork or HTTPS with restrictions).
6. Save and click **Build Now**. Jenkins clones the repo into the job workspace, then runs the stages in the Jenkinsfile.

Optional: enable **Poll SCM** or a **GitHub webhook** under the job’s *Build Triggers* if you want builds on every push (webhook needs the [GitHub Integration](https://plugins.jenkins.io/github/) plugin and your Jenkins reachable from GitHub).

What the pipeline does: lists the project folder, runs **`mvn clean package -DskipTests`**, starts the jar in the background on **Linux/macOS** only (`Run application`), and archives `expense-tracker/target/*.jar` on success.

### Pipeline from a local clone (no GitHub)

Use **Repository URL** `file:///D:/Projects/expense-tracker-monolith` (Windows; use forward slashes) or the `file:` URL for your machine, with **Script Path** `expense-tracker/Jenkinsfile`.

### Pipeline script (paste)

1. **New Item** → **Pipeline** → OK.
2. **Pipeline** → **Definition: Pipeline script**.
3. For **full CI**, paste the contents of `expense-tracker/Jenkinsfile`. For **tests only**, paste `jenkins/jobs/expense-tracker-test.groovy`.
4. For a **local** tree without Git, point the job workspace at the monorepo root, or use a **Freestyle** job with **Execute Windows batch command**: `cd expense-tracker` then `mvnw.cmd clean verify`.

Requires **JDK 17** and the tool installations above. The built Spring Boot jar is **`tracker-0.0.1-SNAPSHOT.jar`** (Maven `artifactId`); set `JAR_NAME` in the job if you change the POM. To use a fixed path like `/Users/.../expense-tracker` instead of `${WORKSPACE}/expense-tracker`, add **`PROJECT_DIR`** in the job’s environment.
