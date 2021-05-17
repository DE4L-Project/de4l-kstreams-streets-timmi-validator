IMAGE_TAG = "devdocker.wifa.uni-leipzig.de:5000/de4l/de4l-timmi-validator"

BUILD = BRANCH_NAME == 'master' ? 'latest' : BRANCH_NAME

node('master') {
    checkout scm
    echo "Build: ${BUILD}"

    withCredentials([usernamePassword(credentialsId: 'docker-registry-devdocker', passwordVariable: 'DOCKER_PASSWORD', usernameVariable: 'DOCKER_USER')]) {
        sh "sudo docker login -u ${DOCKER_USER} -p ${DOCKER_PASSWORD} devdocker.wifa.uni-leipzig.de:5000"
    }

    stage('Build Container with latest Build') {
        sh "sudo docker build -t ${IMAGE_TAG}:${BUILD} ."
        sh "sudo docker push ${IMAGE_TAG}:${BUILD}"
    }
}

