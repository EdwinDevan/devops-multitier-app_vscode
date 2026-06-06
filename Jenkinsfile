pipeline {
    agent any
    tools {
        maven 'M3_INSTANCE'
    }
    stages {
        stage('Compile & Package') {
            steps {
                // compile the app and copy dependencies to target/dependency/
                sh 'mvn clean package dependency:copy-dependencies'
            }
        }
        stage('Deploy Sandbox Instance') {
            steps {
                sh 'sudo fuser -k 8081/tcp || true'
                sh '''
                    export JENKINS_NODE_COOKIE=dontKillMe
                    
                    # Include target/dependency/* in the classpath so the MySQL driver is loaded!
                    nohup java -cp "target/classes:target/dependency/*:target/devops-multitier-app-1.0-SNAPSHOT.jar" com.visualpath.MultiTierApp > multitier.log 2>&1 &
                '''
                echo 'Multi-tier app infrastructure is starting up...'
                sleep 3
                echo 'Depeloyment successful! Access your live application at http://192.168.1.14:8081'
            }
        }
    }
}
