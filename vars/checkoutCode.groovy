// checkoutCode.groovy
// Checks out code from the given GitHub repo and branch

def call(Map config) {
    echo "[INFO] Checking out branch '${config.branch}' from ${config.repoUrl}"
    
    checkout([
        $class: 'GitSCM',
        branches: [[name: "*/${config.branch}"]],
        userRemoteConfigs: [[
            url: config.repoUrl,
            credentialsId: 'github-creds'
        ]]
    ])
    
    echo "[SUCCESS] Code checked out"
}
