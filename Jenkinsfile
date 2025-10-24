// 파이프라인 수준의 전역 변수 및 설정 정의
properties([
    // 파이프라인 실행 시 사용자로부터 값을 입력받음
    parameters([
        string(name: 'SONAR_PROJECT_KEY', defaultValue: 'your-project-key-here', description: 'SonarQube Project Key'),
        string(name: 'SWV_BACKEND_URL', defaultValue: 'https://metaverseacademy', description: 'SWV Backend Notification URL')
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
                script { // [개선 1] script 블록으로 감싸서 변수를 선언할 수 있도록 함
                    withSonarQubeEnv(env.SONAR_SERVER) {
                        sh "./gradlew --no-daemon sonar -Dsonar.projectKey=${params.SONAR_PROJECT_KEY} -Dsonar.token=${SONAR_AUTH_TOKEN}"
                    }
                    
                    // [개선 2] waitForQualityGate의 결과를 'qualityGateStatus' 변수에 저장
                    def qualityGateStatus = waitForQualityGate abortPipeline: true, credentialsId: env.SONAR_CREDENTIALS
                    
                    // [개선 3] 다음 스테이지에서 사용할 수 있도록 결과를 전역 변수에 저장 (선택사항이지만 유용)
                    env.SONARQUBE_STATUS = qualityGateStatus.status
                    // 예시: Quality Gate의 모든 조건을 JSON 문자열로 저장
                    env.SONARQUBE_CONDITIONS = groovy.json.JsonOutput.toJson(qualityGateStatus.conditions)
                }
            }
        }

        stage('Notify SWV Backend') {
            steps {
                script {
                    // [개선 4] 이전 단계에서 저장된 변수를 사용하여 동적으로 Payload 생성
                    def payload = [
                        jobName             : env.JOB_NAME,
                        buildNumber         : env.BUILD_NUMBER.toInteger(),
                        buildUrl            : env.BUILD_URL,
                        commitHash          : sh(returnStdout: true, script: 'git rev-parse HEAD').trim(),
                        
                        // SonarQube로부터 받은 실제 데이터 주입
                        sonarQubeTaskStatus : "SUCCESS", // 이 단계에 도달했다면 분석 자체는 성공
                        qualityGateStatus   : env.SONARQUBE_STATUS, // 'OK' 또는 'ERROR'
                        
                        // SonarQube의 상세 조건들을 포함 (JSON 문자열을 다시 파싱하여 객체로 변환)
                        qualityGateConditions: new groovy.json.JsonSlurper().parseText(env.SONARQUBE_CONDITIONS)
                    ]
                    
                    def payloadJson = groovy.json.JsonOutput.toJson(payload)
                    echo "Sending enriched notification to SWV Backend..."
                    echo groovy.json.JsonOutput.prettyPrint(payloadJson)

                    httpRequest(
                        url: params.SWV_BACKEND_URL,
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
        }
        failure {
            // 파이프라인 실패 시 실행
            echo "Pipeline failed!"
        }
    }
}