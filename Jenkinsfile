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
                    echo "========================================================"
                    echo ">>> STEP 1: VERIFYING NETWORK CONNECTION (using curl)"
                    
                    try {
                        // -v: 상세 로그 출력
                        // -X POST: POST 메서드 사용
                        // -H '...': JSON 컨텐츠 타입 헤더 설정
                        // -d '...': 간단한 JSON 데이터 전송
                        // --fail: HTTP 4xx, 5xx 에러 시 실패로 처리
                        // -s -o /dev/null: 응답 본문은 출력하지 않음 (연결 성공 여부만 중요)
                        sh """
                            curl -v -X POST \\
                                -H "Content-Type: application/json" \\
                                -d '{"message": "Network test from Jenkins curl"}' \\
                                --fail http://mp_backend:3000/api/team-statistics -s -o /dev/null
                        """
                        echo ">>> RESULT: curl command executed successfully. Network connection to mp_backend:3000 seems OK."
                        
                    } catch (Exception e) {
                        echo "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"
                        echo ">>> RESULT: curl command FAILED. Network connection IS THE PROBLEM."
                        echo ">>> Error Message: ${e.getMessage()}"
                        echo "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"
                        error("Network diagnostics failed. The build agent cannot reach the backend service.")
                    }
                    echo "========================================================"

                    // --- 기존 httpRequest 로직은 일단 주석 처리 ---
                    /*
                    def payload = [ ... ]
                    def payloadJson = groovy.json.JsonOutput.toJson(payload)
                    
                    try {
                        def response = httpRequest(...)
                        ...
                    } catch (hudson.AbortException e) {
                        ...
                    }
                    */
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