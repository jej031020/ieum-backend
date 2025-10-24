// 파이프라인 수준의 전역 변수 및 설정 정의
properties([
    // 파이프라인 실행 시 사용자로부터 값을 입력받음
    parameters([
        string(name: 'SONAR_PROJECT_KEY', defaultValue: 'your-project-key-here', description: 'SonarQube Project Key'),
        string(name: 'SWV_BACKEND_URL', defaultValue: 'https://metaverseacademy', description: 'SWV Backend Notification URL'), // 쉼표 추가
        string(name: 'NOTIFY_EMAIL', defaultValue: 'jaehokim1005@g.hongik.ac.kr', description: 'Email to send notification') // 파라미터 이름 통일
    ])
])

pipeline {
    // 1. 개선: 특정 Docker 이미지를 사용하여 일관되고 격리된 빌드 환경 보장
    agent {
        docker {
            image 'gradle:8.5.0-jdk17' // 1. Gradle 이미지로 변경 (프로젝트 JDK 버전에 맞게 선택)
            args '-v gradle-cache:/home/gradle/.gradle'
        }
    }

    // 2. 개선: 환경 변수를 중앙에서 관리하여 가독성 및 유지보수성 향상
    environment {
        SONAR_SCANNER_TOOL = 'SonarScanner-Latest'
        SONAR_SERVER       = 'SonarQube-Server'
        SONAR_CREDENTIALS  = 'SONAR_QUBE_TOKEN'
        SWV_CREDENTIALS    = 'SWV_BACKEND_TOKEN_ID'
    }

    tools {
        'hudson.plugins.sonar.SonarRunnerInstallation' "${env.SONAR_SCANNER_TOOL}"
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Make gradlew executable') { // 3. gradlew 실행 권한 부여 단계 추가
            steps {
                sh 'chmod +x ./gradlew'
            }
        }

        stage('Build Application') {
            steps {
                sh './gradlew --no-daemon clean build -x test' // 4. Gradle 빌드 명령어로 변경
            }
        }

        stage('SonarQube Analysis & Quality Gate') {
            steps {
                withSonarQubeEnv(env.SONAR_SERVER) {
                    // Jenkins 파라미터를 Gradle 명령어에 시스템 속성으로 전달합니다.
                    sh "./gradlew --no-daemon sonar -Dsonar.projectKey=${params.SONAR_PROJECT_KEY} -Dsonar.token=${SONAR_AUTH_TOKEN}"
                }
                
                timeout(time: 10, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true, credentialsId: env.SONAR_CREDENTIALS
                }
            }
        }

        stage('Notify SWV Backend') {
            steps {
                script {
                    // 4. 개선: Groovy Map과 JsonOutput을 사용하여 안전하게 JSON 생성
                    def payload = [
                        jobName     : env.JOB_NAME,
                        buildNumber : env.BUILD_NUMBER.toInteger(), // 타입을 명확히 함
                        buildUrl    : env.BUILD_URL,
                        status      : "SONARQUBE_PASSED",
                        commitHash  : sh(returnStdout: true, script: 'git rev-parse HEAD').trim() // 추가 정보
                    ]
                    
                    def payloadJson = groovy.json.JsonOutput.toJson(payload)
                    echo "Sending notification to SWV Backend..."
                    echo groovy.json.JsonOutput.prettyPrint(payloadJson)

                    httpRequest(
                        url: params.SWV_BACKEND_URL, // 파라미터로 주입된 URL 사용
                        httpMode: 'POST',
                        contentType: 'APPLICATION_JSON',
                        requestBody: payloadJson,
                        authentication: env.SWV_CREDENTIALS,
                        quiet: true
                    )
                }
            }
        }
    }

    // 5. 개선: post 블록을 통해 파이프라인의 각 상태에 대한 후속 조치 자동화
    post {
        always {
            // 성공/실패 여부와 관계없이 항상 실행 (예: 작업 공간 정리)
            echo "Pipeline finished. Cleaning up workspace..."
            cleanWs()
        }
        success {
            // 파이프라인 성공 시 실행
            echo "Pipeline successfully completed."
            // 성공 알림 이메일 전송
            mail to: params.NOTIFY_EMAIL, // 올바른 파라미터 이름 참조
                subject: "Jenkins Pipeline Success: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                body: "The pipeline '${env.JOB_NAME}' (build #${env.BUILD_NUMBER}) has completed successfully.\n\nBuild URL: ${env.BUILD_URL}"
        }
        failure {
            // 파이프라인 실패 시 실행
            echo "Pipeline failed!"
            mail to: params.NOTIFY_EMAIL, // 올바른 파라미터 이름 참조
                subject: "Jenkins Pipeline Failure: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                body: "The pipeline '${env.JOB_NAME}' (build #${env.BUILD_NUMBER}) has failed.\n\nBuild URL: ${env.BUILD_URL}"
        }
    }
}