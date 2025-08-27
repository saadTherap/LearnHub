pipeline {
    agent any

    tools {
        gradle 'Gradle-8'
    }

    environment {
        BASE_LOG_DIR = "/home/app-rnd01/Desktop/logs"
    }

    stages {
        stage('Checkout') {
            steps {
                echo "Checking out code from ${env.BRANCH_NAME} branch..."
                git branch: 'dev', url: 'https://github.com/saadTherap/LearnHub.git', credentialsId: 'github-pat'
            }
        }

        stage('Build & Test') {
            parallel {
                stage('auth key provider') {
                    steps {
                        dir('authkeyprovider') {
                            sh 'gradle clean build'
                        }
                    }
                }
                stage('Secure File Server') {
                    steps {
                        dir('secureFileServer') {
                            sh 'gradle clean build'
                        }
                    }
                }
                stage('Learning Processor') {
                    steps {
                        dir('learningProcessor') {
                            sh 'gradle clean build'
                        }
                    }
                }
                stage('Course Configurator') {
                    steps {
                        dir('courseConfigurator') {
                            sh 'gradle clean build'
                        }
                    }
                }
                stage('Auth Server') {
                    steps {
                        dir('authserver') {
                            sh 'gradle clean build'
                        }
                    }
                }
                stage('Hazelcast Server') {
                    steps {
                        dir('hazelcast-server') {
                            sh 'gradle clean build'
                        }
                    }
                }
            }
        }

        stage('Deploy') {
            steps {
                script {
                    def services = [
                        [dir: 'service-registry', port: 8761],
                        [dir: 'authserver', port: 8090],
                        [dir: 'learningProcessor', port: 8028],
                        [dir: 'courseConfigurator', port: 8082],
                        [dir: 'secure-file-server', port: 8026]
                    ]

                    // 1. Kill old processes to prevent port conflicts
//                     for (svc in services) {
//                         sh "lsof -t -i:${svc.port} | xargs -r kill -9"
//                     }

                    echo "Deploying services with Docker Compose..."


                    // 2. Stop and remove old containers gracefully
//                     sh 'docker compose down auth-server secure-file-server course-configurator learning-processor || true'

                    // 3. Build and start new containers
                    echo "Deploying services with Docker Compose..."
                    sh 'docker compose up -d --build auth-server secure-file-server course-configurator learning-processor'

                }
            }
        }
    }
}
