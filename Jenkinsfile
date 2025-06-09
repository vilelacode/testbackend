pipeline {
  agent any

environment {
    AWS_DEFAULT_REGION = 'us-east-1'
}

parameters {
    choice(name: 'DEPLOY_ENV', choices: ['staging', 'production'], description: 'Selecione o ambiente de deploy')
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
    stage('Deploy') {
        steps {
            script {
                def envChoice = params.DEPLOY_ENV ?: 'staging'
                def EB_ENV_NAME = "baseapi-${envChoice}-env"
                echo "Fazendo deploy no ambiente: ${EB_ENV_NAME}"
                sh "./deploy.sh ${EB_ENV_NAME}"
            }
        }
    }
}

post {
    success {
        echo 'Deploy deu bom, meu bom.'
    }
    failure {
        echo 'Falha no deploy.'
        }
    }
}