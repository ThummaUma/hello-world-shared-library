// vars/owaspScan.groovy
// Runs OWASP Dependency-Check to scan for known vulnerable dependencies
def call(Map config) {
    dependencyCheck additionalArguments: '''
        -o "./"
        -s "./"
        -f "ALL"
        --project "hello-world-app"
    ''', odcInstallation: 'owasp-dependency-check'

    dependencyCheckPublisher pattern: 'dependency-check-report.xml'
}
