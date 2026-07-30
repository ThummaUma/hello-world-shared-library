// vars/deploymentPipeline.groovy
// Orchestrates: checkoutCode -> sonarScan -> owaspScan -> buildImage -> pushImage -> updateManifest
// ArgoCD (already running, auto-sync enabled) deploys after updateManifest pushes.
def call(Map config) {
    def imageTag = params.IMAGE_TAG?.trim() ? params.IMAGE_TAG.trim()
                   : "${env.BUILD_NUMBER}-${env.REF_NAME.replaceAll('[^a-zA-Z0-9]', '-')}"
    checkoutCode([
        repoUrl: config.codeRepoUrl,
        branch : env.REF_NAME
    ])
    sonarScan([
        projectKey: 'hello-world-app'
    ])
    owaspScan([
        projectKey: 'hello-world-app'
    ])
    if (!params.SKIP_DOCKER_BUILD) {
        buildImage([
            imageName: config.dockerHubRepo,
            imageTag : imageTag
        ])
        withCredentials([usernamePassword(
            credentialsId: 'dockerhub-creds',
            usernameVariable: 'DOCKER_USER',
            passwordVariable: 'DOCKER_PASS'
        )]) {
            pushImage([
                imageName: config.dockerHubRepo,
                imageTag : imageTag,
                username : env.DOCKER_USER,
                password : env.DOCKER_PASS
            ])
        }
    } else {
        echo "Skipping Docker build and push (SKIP_DOCKER_BUILD = true)"
    }
    if (params.UPDATE_VALUES_YAML) {
        withCredentials([usernamePassword(
            credentialsId: 'github-creds',
            usernameVariable: 'GIT_USER',
            passwordVariable: 'GIT_TOKEN'
        )]) {
            updateManifest([
                manifestsRepo: config.manifestRepoUrl,
                valuesFile   : config.valuesFile,
                imageTag     : imageTag,
                gitUser      : env.GIT_USER,
                gitToken     : env.GIT_TOKEN
            ])
        }
    } else {
        echo "Skipping values.yaml update (UPDATE_VALUES_YAML = false)"
    }
    echo """
Pipeline complete.
Image pushed: ${config.dockerHubRepo}:${imageTag}
ArgoCD will auto-sync hello-world-manifests into Minikube shortly (if UPDATE_VALUES_YAML was true).
"""
}
