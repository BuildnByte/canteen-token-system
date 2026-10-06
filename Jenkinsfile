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
                bat 'mvn clean compile'
            }
        }
        stage('Test') {
            steps {
                bat 'mvn test'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                    archiveArtifacts artifacts: 'target/selenium-screenshots/**', allowEmptyArchive: true
                }
            }
        }
        stage('Package') {
            steps {
                bat 'mvn package -DskipTests'
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }
        stage('Docker Build') {
            steps {
                bat 'docker build -t canteen-token-system:%BUILD_NUMBER% -t canteen-token-system:latest .'
            }
        }
        stage('Docker Deploy') {
            steps {
                bat """
                    docker stop canteen-app || echo "No container to stop"
                    docker rm canteen-app || echo "No container to remove"
                    docker run -d -p %DEPLOY_PORT%:8082 -e SPRING_DATASOURCE_URL=jdbc:mysql://host.docker.internal:3306/canteen_db --name canteen-app canteen-token-system:%BUILD_NUMBER%
                """
            }
        }
    }
}