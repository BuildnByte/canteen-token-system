pipeline {
    agent any

    tools {
        maven 'Maven-3.9.16'
        jdk 'JDK-21'
    }

    parameters {
        string(name: 'DEPLOY_PORT', defaultValue: '8082', description: 'Port the app will run on after deploy')
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'development', url: 'https://github.com/BuildnByte/canteen-token-system.git'
            }
        }
        stage('Build') {
            steps {
                bat 'mvn clean package -DskipTests'
            }
        }
        stage('Package') {
            steps {
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }
        stage('Deploy') {
            steps {
                bat """
                    for /f "tokens=5" %%p in ('netstat -aon ^| findstr :%DEPLOY_PORT%') do (
                    taskkill /F /PID %%p 2>nul || exit 0
                    )
                    start /B java -jar target\\canteen-token-system-0.0.1-SNAPSHOT.jar --server.port=%DEPLOY_PORT%
                """
            }
        }
    }
}