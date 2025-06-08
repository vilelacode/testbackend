
#!/bin/bash
set -e

EB_APP_NAME="baseapi"
EB_ENV_NAME="Baseapi-env"

ARTIFACT="target/*.jar"

if ! ls $ARTIFACT 1> /dev/null 2>&1; then
echo "Erro: artefato não encontrado em $ARTIFACT"
exit 1
fi

if [ ! -d .elasticbeanstalk ]; then
eb init $EB_APP_NAME --platform java --region $AWS_DEFAULT_REGION
fi

eb deploy $EB_ENV_NAME --staged

echo "Deploy finalizado: $EB_ENV_NAME"

