/**
 * Pipeline script job: unit tests only (faster than full verify).
 * Expects Jenkins workspace = monorepo root (parent of expense-tracker/).
 */
pipeline {
    agent any

    options {
        timestamps()
    }

    stages {
        stage('Test') {
            steps {
                dir('expense-tracker') {
                    script {
                        if (isUnix()) {
                            sh 'chmod +x mvnw 2>/dev/null || true'
                            sh './mvnw test -B --no-transfer-progress'
                        } else {
                            bat 'mvnw.cmd test -B --no-transfer-progress'
                        }
                    }
                }
            }
        }
    }
}
