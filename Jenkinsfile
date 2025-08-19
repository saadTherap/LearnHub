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

        stage('Kill Existing Processes') {
            steps {
                script {
                    def ports = [
                        [name: 'service-registry', port: 8761],
                        [name: 'authserver', port: 8090],
                        [name: 'learningProcessor', port: 8028],
                        [name: 'courseConfigurator', port: 8082]
                    ]

                    for (svc in ports) {
                        echo "Checking for existing process on port ${svc.port} (${svc.name})..."
                        sh """
                        PID=\$(lsof -t -i:${svc.port}) || true
                        if [ ! -z "\$PID" ]; then
                            echo "Killing process \$PID on port ${svc.port}"
                            kill -9 \$PID
                        else
                            echo "No process running on port ${svc.port}"
                        fi
                        """
                    }
                }
            }
        }

        stage('Deploy Services via systemd') {
            steps {
                script {
                    def systemdServices = [
                        'service-registry',
                        'authserver',
                        'learningProcessor',
                        'courseConfigurator'
                    ]

                    for (svc in systemdServices) {
                        echo "Starting/restarting ${svc} systemd service..."
                        sh """
                        sudo systemctl daemon-reload
                        sudo systemctl enable ${svc}
                        sudo systemctl restart ${svc}
                        sudo systemctl status ${svc} --no-pager
                        """
                    }
                }
            }
        }
    }
}
