// buildImage.groovy
// Builds a Docker image with a given name and tag

def call(Map config) {
    def imageName = config.imageName
    def imageTag = config.imageTag
    
    echo "[INFO] Building Docker image: ${imageName}:${imageTag}"
    
    sh "docker build -t ${imageName}:${imageTag} ."
    
    echo "[SUCCESS] Docker image built: ${imageName}:${imageTag}"
}
