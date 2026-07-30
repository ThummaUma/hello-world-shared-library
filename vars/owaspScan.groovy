// vars/owaspScan.groovy
// Runs OWASP Dependency-Check to scan for known vulnerable dependencies

def call(Map config) {

    withCredentials([string(credentialsId: 'nvd-api-key', variable: 'NVD_API_KEY')]) {

        dependencyCheck additionalArguments: """
            --nvdApiKey ${NVD_API_KEY}
            -o "./"
            -s "./"
            -f "ALL"
            --project "hello-world-app"
        """,
        odcInstallation: 'owasp-dependency-check'

    }

    dependencyCheckPublisher pattern: 'dependency-check-report.xml'
}
