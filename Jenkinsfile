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
            when {
                expression { 
                    // This allows execution if we are on the main branch OR running inside a standard single pipeline job where BRANCH_NAME is null
                    return env.BRANCH_NAME == 'main' || env.BRANCH_NAME == null 
                }
            }
            steps {
                // Safely stop any old application running on port 8081
                sh 'sudo fuser -k 8085/tcp || true'
                
                // Spin up the new application process cleanly in the background
                sh '''
                    export JENKINS_NODE_COOKIE=dontKillMe
                    nohup java -cp "target/classes:target/dependency/*" com.visualpath.MultiTierApp > app.log 2>&1 &
                '''
                echo 'Production deployment initialized successfully.'
            }
        }
    }
}
