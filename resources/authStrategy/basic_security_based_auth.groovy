import jenkins.model.*
import hudson.security.*
import hudson.model.*

def env = System.getenv()

def instance = Jenkins.getInstance()
def adminUser = env['INITIAL_ADMIN_USER']
def adminPassword = env['INITIAL_ADMIN_PASSWORD']

println "--> creating local user 'admin'"

def hudsonRealm = new HudsonPrivateSecurityRealm(false)
hudsonRealm.createAccount(adminUser,adminPassword)
instance.setSecurityRealm(hudsonRealm)

def strategy = new hudson.security.GlobalMatrixAuthorizationStrategy()
strategy.add(Jenkins.ADMINISTER,adminUser)
strategy.add(Jenkins.ADMINISTER,'authenticated')
instance.setAuthorizationStrategy(strategy)

instance.save()
