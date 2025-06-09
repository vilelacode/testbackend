environment {
    AWS_DEFAULT_REGION = 'us-east-1'
}

def EB_ENV_NAME = "baseapi-${params.DEPLOY_ENV}-env"

stage('Checkout') {
    checkout scm
}

stage('Build') {
    sh './mvnw clean package -DskipTests'
}

stage('Deploy') {
    echo "Fazendo deploy no ambiente: ${EB_ENV_NAME}"
    sh "./deploy.sh ${EB_ENV_NAME}"
}

post {
    success {
        echo 'Deploy deu bom.'
    }
    failure {
        echo 'Falha no deploy.'
    }
}