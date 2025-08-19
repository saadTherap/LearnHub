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
                stage('Service Registry') {
                    steps {
                        dir('service-registry') {
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
                        [dir: 'auth-key-provider', port: 8091]
                    ]
                    for (svc in services) {
                        sh """
                            bash -c '
                            PID=\$(lsof -t -i:${svc.port} || true)
                            if [ -n "\$PID" ]; then
                                kill -9 \$PID
                                echo "Killed process \$PID on port ${svc.port}"
                            else
                                echo "No process on port ${svc.port}"
                            fi
                            '
                        """
                    }
                    echo "Deploying services with Docker Compose..."

                    sh 'docker compose down auth-key-provider auth-server course-configurator learning-processor service-registry || true'

                    sh '''
                      sh 'docker compose up -d --build auth-key-provider auth-server course-configurator learning-processor service-registry'
                    '''
                }
            }
        }

//         stage('Deploy') {
//             steps {
//                 script {
//                     // Ensure log directory exists
//                     sh "mkdir -p ${BASE_LOG_DIR}"
//
//                     def services = [
//                         [dir: 'service-registry', port: 8761, jar: 'service-registry-0.0.1-SNAPSHOT.jar'],
//                         [dir: 'authserver', port: 8090, jar: 'authserver-0.1-SNAPSHOT.jar'],
//                         [dir: 'learningProcessor', port: 8028, jar: 'learningProcessor-0.0.1-SNAPSHOT.jar'],
//                         [dir: 'courseConfigurator', port: 8082, jar: 'courseConfigurator-0.0.1-SNAPSHOT.jar'],
//                         [dir: 'hazelcast-server', port: 5701, jar: 'hazelcast-server-0.0.1-SNAPSHOT.jar']
//                     ]
//
//                     for (svc in services) {
//                         dir(svc.dir) {
//                             // Kill existing process on port
//                             sh """
//                             PID=\$(lsof -t -i:${svc.port}) || true
//                             if [ ! -z "\$PID" ]; then
//                                 kill -9 \$PID
//                             fi
//                             """
//
//                             // Start service detached using 'at now' (fire-and-forget)
//                             sh """
//                             echo "java -jar build/libs/${svc.jar} > ${BASE_LOG_DIR}/${svc.dir}.log 2>&1 &" | at now
//                             """
//                         }
//                     }
//                 }
//             }
//         }
    }
}
