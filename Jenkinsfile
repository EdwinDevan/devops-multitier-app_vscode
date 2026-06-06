pipeline {
    agent any
    tools {
        maven 'M3_INSTANCE'
    }
    stages {
        stage('Compile & Package') {
            steps {
                sh 'mvn clean package'
            }
        }
        stage('Deploy Sandbox Instance') {
            steps {
                // Clear out port 8081 cleanly so it never locks up
                sh 'sudo fuser -k 8081/tcp || true'
                sh '''
                    export JENKINS_NODE_COOKIE=dontKillMe
                    nohup java -cp target/classes:target/devops-multitier-app-1.0-SNAPSHOT.jar com.visualpath.MultiTierApp > multitier.log 2>&1 &
                '''
                echo 'Multi-tier app infrastructure is live at http://192.168.1.14:8081'
            }
        }
    }
}
