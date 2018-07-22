pipeline {
    agent {
        label 'freebsd&&kotlin'
    }

    environment {
        NEXUS = "https://gizmo.kruemel.home/nexus/"
        REPOSITORY = "repository/webtools/nmapservice/"
    }

    tools {
        maven 'Maven 3.5.4'
    }

    options {
        ansiColor('xterm')
        buildDiscarder logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: '', numToKeepStr: '5')
    }

    stages {
        stage('clean') {
            steps {
                sh 'mvn -B clean'
            }
        }

        stage('build') {
            steps {
                sh 'mvn -B compile'
            }
        }

        stage('test') {
            steps {
                sh 'mvn -B test'
            }
        }

        stage('deploy') {
            when {
                branch 'master'
            }
            steps {
                configFileProvider([configFile(fileId: '96a603cc-e1a4-4d5b-a7e9-ae1aa566cdfc', variable: 'MAVEN_SETTINGS_XML')]) {
                    sh 'mvn -Dmaven.wagon.http.ssl.insecure=true -B -s "$MAVEN_SETTINGS_XML" -DskipTests deploy'
                }
            }
        }

//        stage('poke rundeck') {
//            when {
//                branch 'master'
//            }
//
//            steps {
//                script {
//                    step([$class: "RundeckNotifier",
//                        includeRundeckLogs: true,
//                        jobId: "8c822ea8-ef03-419d-95cd-5a2ca7106071",
//                        rundeckInstance: "gizmo",
//                        shouldFailTheBuild: true,
//                        shouldWaitForRundeckJob: true,
//                        tailLog: true])
//                }
//            }
//        }
    }

    post {
        always {
            mail to: "rafi@guengel.ch",
                    subject: "${JOB_NAME} (${BRANCH_NAME};${env.BUILD_DISPLAY_NAME}) -- ${currentBuild.currentResult}",
                    body: "Refer to ${currentBuild.absoluteUrl}"
        }
    }
}
