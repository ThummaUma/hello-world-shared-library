// vars/sonarScan.groovy
// Runs SonarQube static analysis on the checked-out code

def call(Map config) {
    def scannerHome = tool 'sonarqube-scanner'

    withSonarQubeEnv('sonarqube') {
        sh """
            ${scannerHome}/bin/sonar-scanner \
              -Dsonar.projectKey=${config.projectKey} \
              -Dsonar.sources=. \
              -Dsonar.exclusions=**/dependency-check-*,**/*.html,**/*.xml \
              -Dsonar.host.url=${env.SONAR_HOST_URL} \
              -Dsonar.token=${env.SONAR_AUTH_TOKEN}
        """
    }
}
