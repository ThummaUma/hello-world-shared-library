// pushImage.groovy
// Logs into Docker Hub and pushes the built image

def call(Map config) {
    def imageName = config.imageName
    def imageTag = config.imageTag
    def username = config.username
    def password = config.password
    
    echo "[INFO] Logging into Docker Hub as ${username}"
    sh "echo ${password} | docker login -u ${username} --password-stdin"
    
    echo "[INFO] Pushing image: ${imageName}:${imageTag}"
    sh "docker push ${imageName}:${imageTag}"
    
    sh "docker tag ${imageName}:${imageTag} ${imageName}:latest"
    sh "docker push ${imageName}:latest"
    
    echo "[SUCCESS] Image pushed: ${imageName}:${imageTag}"
}
