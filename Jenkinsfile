pipeline {
    agent any

    tools {
        gradle 'Gradle-8'
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
                        echo "Restarting ${svc} systemd service..."
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
