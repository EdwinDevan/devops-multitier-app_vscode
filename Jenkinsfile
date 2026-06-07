pipeline {
    agent any
    tools {
        maven 'M3_INSTANCE'
    }
    stages {
        stage('Compile & Package') {
            steps {
                sh 'mvn clean package dependency:copy-dependencies'
            }
        }
        
        stage('Deploy to Production') {
            // CRITICAL: This stage ONLY runs if the code is on the main branch!
            when {
                branch 'main'
            }
            steps {
                sh 'sudo fuser -k 8081/tcp || true'
                sh '''
                    export JENKINS_NODE_COOKIE=dontKillMe
                    nohup java -cp "target/classes:target/dependency/*:target/devops-multitier-app-1.0-SNAPSHOT.jar" com.visualpath.MultiTierApp > multitier.log 2>&1 &
                '''
                echo 'Production deployment finalized.'
            }
        }
    }
}
