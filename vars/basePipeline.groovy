// vars/basePipeline.groovy
// Lives in hello-world-shared-library repo
// Usage in Jenkinsfile: basePipeline(config) { deploymentPipeline([...]) }

def call(Map config, Closure body) {

    pipeline {

        agent any

        parameters {

            choice(
                name: 'DEPLOYMENT_ACTION',
                choices: ['Release', 'Hotfix'],
                description: 'Release = normal deploy, Hotfix = urgent, skips optional checks'
            )

            choice(
                name: 'BUILD_TYPE',
                choices: ['python', 'none'],
                description: 'python = pip install and build image, none = skip build, only update manifest'
            )

            gitParameter(
                name: 'REF_NAME',
                type: 'PT_BRANCH_TAG',
                useRepository: config.repo,
                branchFilter: '.*',
                tagFilter: '.*',
                sortMode: 'DESCENDING_SMART',
                selectedValue: 'TOP',
                quickFilterEnabled: true
            )

            string(
                name: 'IMAGE_TAG',
                defaultValue: '',
                description: 'Leave blank to auto-generate tag from build number and short commit hash'
            )

            booleanParam(
                name: 'SKIP_DOCKER_BUILD',
                defaultValue: false,
                description: 'Skip Docker build and push, only update values.yaml'
            )

            booleanParam(
                name: 'UPDATE_VALUES_YAML',
                defaultValue: true,
                description: 'Update values.yaml in hello-world-manifests, ArgoCD auto-syncs after this'
            )
        }

        stages {

            stage('Prepare Inputs') {
                steps {
                    script {
                        env.REF_NAME = params.REF_NAME.replaceAll("origin/|refs/heads/|refs/tags/", "")

                        echo "RUN SUMMARY"
                        echo "Action: ${params.DEPLOYMENT_ACTION}"
                        echo "Build type: ${params.SKIP_DOCKER_BUILD ? 'none (skipped)' : params.BUILD_TYPE}"
                        echo "Ref: ${env.REF_NAME}"
                        echo "Image tag: ${params.IMAGE_TAG ?: '(auto)'}"
                        echo "Update values: ${params.UPDATE_VALUES_YAML}"
                    }
                }
            }

            stage('Deploy') {
                steps {
                    script {
                        body()
                    }
                }
            }
        }
    }
}
