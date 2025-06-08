environment {
    AWS_DEFAULT_REGION = 'us-east-1'
}

stages {
    stage('Checkout') {
        steps {
            checkout scm
        }
    }
    stage('Build') {
        steps {
             sh './mvnw clean package -DskipTests'
        }
    }
    stage('Deploy to Elastic Beanstalk') {
        steps {
          sh './deploy.sh'
        }
    }
}

post {
    success {
        echo 'Deployment concluído com sucesso.'
    }
    failure {
        echo 'Falha no deployment.'
    }
}