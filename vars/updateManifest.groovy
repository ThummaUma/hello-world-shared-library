// updateManifest.groovy
// Updates the image tag in the Helm values.yaml inside the manifests repo,
// then commits and pushes so Argo CD can detect the change

def call(Map config) {
    def manifestsRepo = config.manifestsRepo
    def valuesFile = config.valuesFile
    def imageTag = config.imageTag
    def gitUser = config.gitUser
    def gitToken = config.gitToken
    
    echo "[INFO] Updating ${valuesFile} with new tag: ${imageTag}"
    
    dir('manifests-repo') {
        checkout([
            $class: 'GitSCM',
            branches: [[name: '*/main']],
            userRemoteConfigs: [[
                url: manifestsRepo,
                credentialsId: 'github-creds'
            ]]
        ])
        
        def repoWithoutProtocol = manifestsRepo.replace("https://", "")
        
        sh """
            git config user.name "${gitUser}"
            git config user.email "${gitUser}@users.noreply.github.com"
            git remote set-url origin https://${gitUser}:${gitToken}@${repoWithoutProtocol}
            sed -i 's/tag:.*/tag: "${imageTag}"/' ${valuesFile}
            git add ${valuesFile}
            git commit -m "Update image tag to ${imageTag}" || echo "No changes to commit"
            git push origin main
        """
    }
    
    echo "[SUCCESS] Manifest updated with tag: ${imageTag}"
}
