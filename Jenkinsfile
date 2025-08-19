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

        stage('Deploy') {
            parallel {
                stage('Service Registry') {
                    steps {
                        dir('service-registry') {
                            sh '''
                            # Kill any process running on 8761
                            PID=$(lsof -t -i:8761)
                            if [ ! -z "$PID" ]; then
                            kill -9 $PID
                            fi

                            nohup gradle bootRun &
                            '''
                        }
                    }
                }
                stage('Auth Server') {
                    steps {
                        dir('authserver') {
                            sh '''
                            # Kill any process running on 8090
                            PID=$(lsof -t -i:8090)
                            if [ ! -z "$PID" ]; then
                            kill -9 $PID
                            fi

                            nohup gradle bootRun &
                            '''
                        }
                    }
                }
                stage('Learning Processor') {
                    steps {
                        dir('learningProcessor') {
                            sh '''
                            # Kill any process running on 8028
                            PID=$(lsof -t -i:8028)
                            if [ ! -z "$PID" ]; then
                            kill -9 $PID
                            fi

                            nohup gradle bootRun &
                            '''
                        }
                    }
                }
                stage('Course Configurator') {
                    steps {
                        dir('courseConfigurator') {
                            sh '''
                            # Kill any process running on 8082
                            PID=$(lsof -t -i:8082)
                            if [ ! -z "$PID" ]; then
                            kill -9 $PID
                            fi

                            nohup gradle bootRun &
                            '''
                        }
                    }
                }
            }
        }
    }
}