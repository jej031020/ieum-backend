// [핵심 수정 1] 파이프라인 최상단에 변수 선언
def qualityGateResult

properties([
    parameters([
        string(name: 'SONAR_PROJECT_KEY', defaultValue: 'your-project-key-here', description: 'SonarQube Project Key'),
        string(name: 'SWV_BACKEND_URL', defaultValue: 'http://mp_backend:3000/api/team-statistics', description: 'SWV Backend Notification URL')
    ])
])

pipeline {
    agent {
        docker {
            image 'gradle:8.5.0-jdk17'
            args '-v gradle-cache:/home/gradle/.gradle --network=shared-net'
        }
    }

    environment {
        SONAR_SERVER       = 'SonarQube-Server'
        SONAR_CREDENTIALS  = 'SONAR_QUBE_TOKEN'
        SWV_CREDENTIALS    = 'SWV_BACKEND_TOKEN_ID'
    }
    
    // [참고] tools 블록은 더 이상 필요 없을 수 있습니다.
    // withSonarQubeEnv가 SonarScanner를 자동으로 관리해주는 경우가 많습니다.
    // 만약 오류가 발생하면 이 블록의 주석을 해제하십시오.
    // tools {
    //     'hudson.plugins.sonar.SonarRunnerInstallation' 'SonarScanner-Latest'
    // }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Make gradlew executable') {
            steps { 
                sh 'chmod +x ./gradlew'
            }
        }

        stage('Build Application') {
            steps {
                sh './gradlew --no-daemon clean build -x test'
            }
        }

        stage('SonarQube Analysis & Quality Gate') {
            steps {
                script {
                    withSonarQubeEnv(env.SONAR_SERVER) {
                        sh "./gradlew --no-daemon sonar -Dsonar.projectKey=${params.SONAR_PROJECT_KEY} -Dsonar.token=${SONAR_AUTH_TOKEN}"
                    }
                    
                    // [핵심 수정 2] 결과를 'env'가 아닌 파이프라인 변수에 직접 저장
                    qualityGateResult = waitForQualityGate abortPipeline: true, credentialsId: env.SONAR_CREDENTIALS
                }
            }
        }

        stage('Notify SWV Backend') {
            steps {
                script {
                    // [핵심 수정 3] 객체를 직접 사용하여 payload 생성 (JSON 변환/파싱 불필요)
                    def payload = [
                        jobName             : env.JOB_NAME,
                        buildNumber         : env.BUILD_NUMBER.toInteger(),
                        buildUrl            : env.BUILD_URL,
                        commitHash          : sh(returnStdout: true, script: 'git rev-parse HEAD').trim(),
                        
                        // 객체의 속성에 직접 접근
                        qualityGateStatus   : qualityGateResult.status,
                        sonarQubeResult     : qualityGateResult
                    ]
                    
                    // payload 객체를 JSON 문자열로 변환
                    def payloadJson = groovy.json.JsonOutput.toJson(payload)
                    
                    echo "========================================================"
                    echo ">>> Preparing to send HTTP POST to SWV Backend"
                    echo ">>> Request URL: ${params.SWV_BACKEND_URL}"
                    echo ">>> Request Body:"
                    echo groovy.json.JsonOutput.prettyPrint(payloadJson)
                    echo "========================================================"
                    try {
                        def response = httpRequest(
                            url: params.SWV_BACKEND_URL,
                            httpMode: 'POST',
                            contentType: 'APPLICATION_JSON',
                            requestBody: payloadJson,
                            // authentication: env.SWV_CREDENTIALS,
                            quiet: false 
                        )
                        echo "Notification sent successfully."
                        echo "Response Status: ${response.status}"
                        echo "Response Body: ${response.content}"

                    } catch (hudson.AbortException e) {
                        echo "Failed to send notification."
                        echo "Error: ${e.getMessage()}"
                        error("Notification to SWV Backend failed.")
                    }
                }
            }
        }
    }

    post {
        always {
            echo "Pipeline finished. Cleaning up workspace..."
            cleanWs()
        }
        success {
            echo "Pipeline successfully completed."
        }
        failure {
            echo "Pipeline failed!"
        }
    }
}