pipeline {
  agent any

environment {
    PATH = "/home/fabio/.local/bin:/snap/bin:${env.PATH}"
    AWS_DEFAULT_REGION = 'us-east-1'
}

parameters {
    choice(name: 'DEPLOY_ENV', choices: ['staging', 'production'], description: 'Selecione o ambiente de deploy')
}

stages {
        stage('Verify Tools') {
        steps {
            // Verifica se AWS EB CLI está instalado e executável
            sh "which eb || (echo 'EB CLI não encontrado. Instale o AWS Elastic Beanstalk CLI no agente Jenkins.' && exit 1)"
            // Ajusta permissão de execução no EB CLI (pipx/snap)
            sh "chmod +x $(which eb) || true"
            // Verifica se zip está instalado e executável
            sh "which zip || (echo 'zip não encontrado. Instale zip no agente Jenkins.' && exit 1)"
        }
    }
    stage('Checkout') {
        steps {
            checkout scm
            //sh 'apt-get update && apt-get install -y zip'
            sh 'chmod +x ./mvnw'
            sh 'chmod +x ./deploy.sh'
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
        echo 'Deploy deu bom.'
    }
    failure {
        echo 'Falha no deploy.'
    }
}
}