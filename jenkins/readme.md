# Jenkins (local)

## Prerequisites

- **Java 17** (LTS). Verify with `java -version`.

## Start Jenkins

From this directory:

```bash
java -jar jenkins.war
```

Jenkins listens on **[http://localhost:8080](http://localhost:8080)** by default.

If port 8080 is in use, pick another port:

```bash
java -jar jenkins.war --httpPort=8085
```

Then open **[http://localhost:8085](http://localhost:8085)** in your browser (or **[http://localhost:8080](http://localhost:8080)** if you did not set `--httpPort`).

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

### Build on every push (GitHub webhook + ngrok)

GitHub cannot reach **localhost**, so for a Jenkins instance on your machine you expose it with a tunnel (e.g. [ngrok](https://ngrok.com/download)), then register that public URL in GitHub.

**1. Jenkins URL (optional but useful)**  
**Manage Jenkins → System** → **Jenkins URL** — set it to your ngrok HTTPS URL while you use the tunnel (so links in Jenkins and some plugins stay correct). Example: `https://abc123.ngrok-free.app/` (trailing slash is fine).

**2. Start ngrok** (use the same port Jenkins uses, default **8080**):

```bash
ngrok http 8080
```

Copy the **HTTPS** forwarding URL from the ngrok output (e.g. `https://abc123.ngrok-free.app`). Free ngrok URLs change when you restart ngrok unless you use a [reserved domain](https://ngrok.com/docs/guides/how-to-set-up-a-custom-domain/).

**3. Jenkins job — build trigger**  
Install the **[GitHub](https://plugins.jenkins.io/github/)** plugin if needed. Open your Pipeline job → **Configure** → **Build Triggers** → enable **GitHub hook trigger for GITScm polling** (this listens for GitHub’s webhook POSTs, not slow SCM polling). Save.

**4. GitHub — webhook**  
In the repo [mahaseth/expense-tracker-monolith](https://github.com/mahaseth/expense-tracker-monolith): **Settings → Webhooks → Add webhook**


| Field             | Value                                                                                                  |
| ----------------- | ------------------------------------------------------------------------------------------------------ |
| **Payload URL**   | `https://<your-ngrok-host>/github-webhook/` — example: `https://abc123.ngrok-free.app/github-webhook/` |
| **Content type**  | `application/json`                                                                                     |
| **Secret**        | Optional; if you set one, configure the same under Jenkins credentials / GitHub plugin settings        |
| **Which events?** | **Just the push event** (or “Let me select individual events” → enable **Pushes**)                     |


Save the webhook. Use **Recent Deliveries** on GitHub to confirm **200** responses after a push.

**5. Test**  
Push a commit to `main`. The job should start without clicking **Build Now**. Keep ngrok (and Jenkins) running while you rely on the tunnel.

**Note:** If you prefer not to expose Jenkins, use **Poll SCM** in the job (e.g. `H/5 * * * *` every five minutes) instead — no ngrok, but builds are delayed until the next poll.

Pipeline stages: **Checkout** (`checkout scm` — job must use *Pipeline script from SCM*), **Build** (`mvn clean package -DskipTests`), **Deploy** (runs `java -jar` in the background; Linux/macOS stops a previous instance via `pkill`, Windows uses `start /B`). Archives `expense-tracker/target/*.jar` on success.

### Pipeline from a local clone (no GitHub)

Use **Repository URL** `file:///D:/Projects/expense-tracker-monolith` (Windows; use forward slashes) or the `file:` URL for your machine, with **Script Path** `expense-tracker/Jenkinsfile`.

### Pipeline script (paste

1. **New Item** → **Pipeline** → OK.
2. **Pipeline** → **Definition: Pipeline script**.
3. For **full CI**, paste the contents of `expense-tracker/Jenkinsfile`. For **tests only**, paste `jenkins/jobs/expense-tracker-test.groovy`.
4. For a **local** tree without Git, point the job workspace at the monorepo root, or use a **Freestyle** job with **Execute Windows batch command**: `cd expense-tracker` then `mvnw.cmd clean verify`.

Requires **JDK 17** and the tool installations above. The built Spring Boot jar is `**tracker-0.0.1-SNAPSHOT.jar`** (Maven `artifactId`); set `JAR_NAME` in the job if you change the POM. To use a fixed path like `/Users/.../expense-tracker` instead of `${WORKSPACE}/expense-tracker`, add `**PROJECT_DIR`** in the job’s environment.