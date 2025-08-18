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
                stage('Learning Processor') {
                    steps {
                        dir('learningProcessor') {
                            sh 'gradle bootRun'
                        }
                    }
                }
                stage('Course Configurator') {
                    steps {
                        dir('courseConfigurator') {
                            sh 'gradle bootRun'
                        }
                    }
                }
                stage('Auth Server') {
                    steps {
                        dir('authserver') {
                            sh 'gradle bootRun'
                        }
                    }
                }
                stage('Hazelcast Server') {
                    steps {
                        dir('hazelcast-server') {
                            sh 'gradle bootRun'
                        }
                    }
                }
            }
        }
    }
}