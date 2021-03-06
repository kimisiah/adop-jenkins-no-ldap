import hudson.*
import hudson.model.*
import hudson.security.*
import jenkins.*
import jenkins.model.*
import java.util.*
import com.michelin.cio.hudson.plugins.rolestrategy.*
import java.lang.reflect.*

def env = System.getenv()

// Roles
def globalRoleRead = "read"
def globalRoleAdmin = "admin"
def globalRoleDeploymentAdmin = "deployment-admin"
def globalRoleDeployment = "deployment"

def adminUser = env['INITIAL_ADMIN_USER']
def adminPassword = env['INITIAL_ADMIN_PASSWORD']

def jenkinsInstance = Jenkins.getInstance()
def currentAuthenticationStrategy = Hudson.instance.getAuthorizationStrategy()

Thread.start {
    sleep 15000
    if (currentAuthenticationStrategy instanceof RoleBasedAuthorizationStrategy) {
      println "Role based authorisation already enabled."
      println "Exiting script..."
      return
    } else {
      println "Enabling role based authorisation strategy..."
    }

    // Set new authentication strategy
    RoleBasedAuthorizationStrategy roleBasedAuthenticationStrategy = new RoleBasedAuthorizationStrategy()
    jenkinsInstance.setAuthorizationStrategy(roleBasedAuthenticationStrategy)

    // Create default Admin
    println "--> creating local user 'admin'"

    def hudsonRealm = new HudsonPrivateSecurityRealm(false)
    hudsonRealm.createAccount(adminUser,adminPassword)
    jenkinsInstance.setSecurityRealm(hudsonRealm)

    Constructor[] constrs = Role.class.getConstructors();
    for (Constructor<?> c : constrs) {
      c.setAccessible(true);
    }

    // Make the method assignRole accessible
    Method assignRoleMethod = RoleBasedAuthorizationStrategy.class.getDeclaredMethod("assignRole", String.class, Role.class, String.class);
    assignRoleMethod.setAccessible(true);

    // Create admin set of permissions
    Set<Permission> adminPermissions = new HashSet<Permission>();
    adminPermissions.add(Permission.fromId("hudson.model.View.Delete"));
    adminPermissions.add(Permission.fromId("hudson.model.Computer.Connect"));
    adminPermissions.add(Permission.fromId("hudson.model.Run.Delete"));
    adminPermissions.add(Permission.fromId("hudson.model.Hudson.UploadPlugins"));
    adminPermissions.add(Permission.fromId("com.cloudbees.plugins.credentials.CredentialsProvider.ManageDomains"));
    adminPermissions.add(Permission.fromId("hudson.model.Computer.Create"));
    adminPermissions.add(Permission.fromId("hudson.model.View.Configure"));
    adminPermissions.add(Permission.fromId("com.sonyericsson.hudson.plugins.gerrit.trigger.PluginImpl.Retrigger"));
    adminPermissions.add(Permission.fromId("hudson.model.Hudson.ConfigureUpdateCenter"));
    adminPermissions.add(Permission.fromId("hudson.model.Computer.Build"));
    adminPermissions.add(Permission.fromId("hudson.model.Item.Configure"));
    adminPermissions.add(Permission.fromId("hudson.model.Hudson.Administer"));
    adminPermissions.add(Permission.fromId("hudson.model.Item.Cancel"));
    adminPermissions.add(Permission.fromId("hudson.model.Item.Read"));
    adminPermissions.add(Permission.fromId("com.cloudbees.plugins.credentials.CredentialsProvider.View"));
    adminPermissions.add(Permission.fromId("hudson.model.Computer.Delete"));
    adminPermissions.add(Permission.fromId("hudson.model.Item.Build"));
    adminPermissions.add(Permission.fromId("hudson.scm.SCM.Tag"));
    adminPermissions.add(Permission.fromId("hudson.model.Item.Discover"));
    adminPermissions.add(Permission.fromId("hudson.model.Hudson.Read"));
    adminPermissions.add(Permission.fromId("com.cloudbees.plugins.credentials.CredentialsProvider.Update"));
    adminPermissions.add(Permission.fromId("hudson.model.Item.Create"));
    adminPermissions.add(Permission.fromId("hudson.model.Item.Move"));
    adminPermissions.add(Permission.fromId("hudson.model.Item.Workspace"));
    adminPermissions.add(Permission.fromId("com.cloudbees.plugins.credentials.CredentialsProvider.Delete"));
    adminPermissions.add(Permission.fromId("hudson.model.View.Read"));
    adminPermissions.add(Permission.fromId("hudson.model.Hudson.RunScripts"));
    adminPermissions.add(Permission.fromId("hudson.model.View.Create"));
    adminPermissions.add(Permission.fromId("hudson.model.Item.Delete"));
    adminPermissions.add(Permission.fromId("com.sonyericsson.hudson.plugins.gerrit.trigger.PluginImpl.ManualTrigger"));
    adminPermissions.add(Permission.fromId("hudson.model.Computer.Configure"));
    adminPermissions.add(Permission.fromId("com.cloudbees.plugins.credentials.CredentialsProvider.Create"));
    adminPermissions.add(Permission.fromId("hudson.model.Computer.Disconnect"));
    adminPermissions.add(Permission.fromId("hudson.model.Run.Update"));

    // Create deployment-admin permission
    Set<Permission> deploymentAdminPermissions = new HashSet<Permission>();
    deploymentAdminPermissions.add(Permission.fromId("hudson.model.View.Delete"));
    deploymentAdminPermissions.add(Permission.fromId("hudson.model.Computer.Connect"));
    deploymentAdminPermissions.add(Permission.fromId("hudson.model.Run.Delete"));
    deploymentAdminPermissions.add(Permission.fromId("hudson.model.Computer.Create"));
    deploymentAdminPermissions.add(Permission.fromId("hudson.model.Computer.Build"));
    deploymentAdminPermissions.add(Permission.fromId("hudson.model.Item.Configure"));
    deploymentAdminPermissions.add(Permission.fromId("hudson.model.Item.Cancel"));
    deploymentAdminPermissions.add(Permission.fromId("hudson.model.Item.Read"));
    deploymentAdminPermissions.add(Permission.fromId("com.cloudbees.plugins.credentials.CredentialsProvider.View"));
    deploymentAdminPermissions.add(Permission.fromId("hudson.model.Computer.Delete"));
    deploymentAdminPermissions.add(Permission.fromId("hudson.model.Item.Build"));
    deploymentAdminPermissions.add(Permission.fromId("hudson.scm.SCM.Tag"));
    deploymentAdminPermissions.add(Permission.fromId("hudson.model.Item.Discover"));
    deploymentAdminPermissions.add(Permission.fromId("com.cloudbees.plugins.credentials.CredentialsProvider.Update"));
    deploymentAdminPermissions.add(Permission.fromId("hudson.model.Item.Create"));
    deploymentAdminPermissions.add(Permission.fromId("hudson.model.Item.Move"));
    deploymentAdminPermissions.add(Permission.fromId("hudson.model.Item.Workspace"));
    deploymentAdminPermissions.add(Permission.fromId("com.cloudbees.plugins.credentials.CredentialsProvider.Delete"));
    deploymentAdminPermissions.add(Permission.fromId("hudson.model.View.Read"));
    deploymentAdminPermissions.add(Permission.fromId("hudson.model.View.Create"));
    deploymentAdminPermissions.add(Permission.fromId("hudson.model.View.Configure"));
    deploymentAdminPermissions.add(Permission.fromId("hudson.model.Item.Delete"));
    deploymentAdminPermissions.add(Permission.fromId("com.cloudbees.plugins.credentials.CredentialsProvider.Create"));
    deploymentAdminPermissions.add(Permission.fromId("hudson.model.Run.Update"));

    // Create deployment set of permissions
    Set<Permission> deploymentPermissions = new HashSet<Permission>();
    deploymentPermissions.add(Permission.fromId("hudson.model.Item.Build"));

    // Create the admin Role
    Role adminRole = new Role(globalRoleAdmin, adminPermissions);
    roleBasedAuthenticationStrategy.addRole(RoleBasedAuthorizationStrategy.GLOBAL, adminRole);

    // Create the deployment-admin Role
    Role deploymentAdminRole = new Role(globalRoleDeploymentAdmin, deploymentAdminPermissions);
    roleBasedAuthenticationStrategy.addRole(RoleBasedAuthorizationStrategy.GLOBAL, deploymentAdminRole);

    // Create the deployment Role
    Role deploymentRole = new Role(globalRoleDeployment, deploymentPermissions);
    roleBasedAuthenticationStrategy.addRole(RoleBasedAuthorizationStrategy.GLOBAL, deploymentRole);

    // Assign the role
    roleBasedAuthenticationStrategy.assignRole(RoleBasedAuthorizationStrategy.GLOBAL, adminRole, adminUser);
    println "Admin role created...OK"

    roleBasedAuthenticationStrategy.assignRole(RoleBasedAuthorizationStrategy.GLOBAL, deploymentAdminRole, adminUser);
    println "Deployment Admin role created...OK"

    roleBasedAuthenticationStrategy.assignRole(RoleBasedAuthorizationStrategy.GLOBAL, deploymentRole, adminUser);
    println "Deployment role created...OK"

    // Create permissions
    Set<Permission> authenticatedPermissions = new HashSet<Permission>();
    authenticatedPermissions.add(Permission.fromId("hudson.model.Hudson.Read"));
    authenticatedPermissions.add(Permission.fromId("hudson.model.View.Read"));

    Role authenticatedRole = new Role(globalRoleRead, authenticatedPermissions);
    roleBasedAuthenticationStrategy.addRole(RoleBasedAuthorizationStrategy.GLOBAL, authenticatedRole);

    // Assign the role
    roleBasedAuthenticationStrategy.assignRole(RoleBasedAuthorizationStrategy.GLOBAL, authenticatedRole, 'authenticated');
    println "Read role created...OK"

    // Save the state
    println "Saving changes."
    jenkinsInstance.save()
}
