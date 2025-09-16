pipeline {
    agent any
    
    tools {
        maven 'Maven'
        jdk 'JDK-21'
    }
    
    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'your-git-repo-url'
            }
        }
        
        stage('Start Grid') {
            steps {
                script {
                    bat 'docker-compose up -d'
                    sleep 10
                }
            }
        }
        
        stage('Run Tests') {
            steps {
                bat 'mvn clean test -DsuiteXmlFile=testng.xml -Dgrid.url=http://localhost:4444 -Dexecution.env=Jenkins'
            }
        }
        
        stage('Generate Report') {
            steps {
                allure([
                    includeProperties: false,
                    jdk: '',
                    properties: [],
                    reportBuildPolicy: 'ALWAYS',
                    results: [[path: 'allure-results']]
                ])
            }
        }
        
        stage('Stop Grid') {
            steps {
                bat 'docker-compose down'
            }
        }
    }
    
    post {
        always {
            bat 'docker-compose down'
        }
    }
}