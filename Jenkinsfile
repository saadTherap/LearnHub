pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                echo "Checking out code from ${env.BRANCH_NAME} branch..."
                git branch: env.BRANCH_NAME, url: 'https://github.com/saadTherap/LearnHub.git', credentialsId: 'github-pat'
            }
        }

        stage('Build & Test') {
            parallel {
                stage('Learning Processor') {
                    steps {
                        dir('learningProcessor') {
                            sh './gradlew clean build'
                        }
                    }
                }
                stage('Course Configurator') {
                    steps {
                        dir('courseConfigurator') {
                            sh './gradlew clean build'
                        }
                    }
                }
                stage('Auth Server') {
                    steps {
                        dir('authserver') {
                            sh './gradlew clean build'
                        }
                    }
                }
                stage('Hazelcast Server') {
                    steps {
                        dir('hazelcast-server') {
                            sh './gradlew clean build'
                        }
                    }
                }
            }
        }

        stage('Merge & Push to Dev') {
            when {
                expression { env.BRANCH_NAME.startsWith('feature/') }
            }
            steps {
                script {
                    echo "Merging changes from ${env.BRANCH_NAME} into dev..."
                    // Check out main and pull latest changes
                    sh 'git checkout dev'
                    sh 'git pull origin dev'
                    // Merge feature branch into main
                    sh 'git merge --no-ff --no-commit ${env.BRANCH_NAME}'
                    // Handle merge conflicts (this is the key part)
                    sh 'git add .' // Add all changes
                    sh 'git commit -m "Merged changes from ${env.BRANCH_NAME}"'
                    // Push to remote main
                    sh 'git push origin dev'
                }
            }
        }

        stage('Deploy to Remote') {
            steps {
                script {
                    echo 'Deploying to remote machine via SSH...'

                    def remoteUser = 'app-rnd01'
                    def remoteIp = '192.168.0.215'
                    def remoteDir = '/home/app-rnd01/Desktop/Dev/LearnHub'
                    def repoUrl = 'https://github.com/saadTherap/LearnHub.git'
                    def deployBranch = 'dev'

                    sh """
                        ssh -o StrictHostKeyChecking=no ${remoteUser}@${remoteIp} "
                            # Ensure the Dev directory exists
                            mkdir -p ${remoteDir}

                            # Change to the Dev directory
                            cd ${remoteDir}

                            # Check if the repository already exists
                            if [ -d ".git" ]; then
                                # If it exists, pull the latest changes
                                git checkout ${deployBranch}
                                git pull origin ${deployBranch}
                            else
                                # If it doesn't exist, clone the repository
                                git clone ${repoUrl} .
                                git checkout ${deployBranch}
                            fi

                            # Navigate to the correct directory to run docker-compose
                            cd ${remoteDir}

                            echo 'Deployment complete.'
                        "
                    """
                }
            }
        }
    }
}