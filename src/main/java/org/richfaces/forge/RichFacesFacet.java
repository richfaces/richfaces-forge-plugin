package org.richfaces.forge;

import com.google.inject.Inject;
import java.util.Arrays;
import javax.faces.webapp.FacesServlet;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.model.Model;
import org.jboss.seam.forge.project.Project;
import org.jboss.seam.forge.project.dependencies.Dependency;
import org.jboss.seam.forge.project.dependencies.DependencyBuilder;
import org.jboss.seam.forge.project.dependencies.MavenDependencyAdapter;
import org.jboss.seam.forge.project.dependencies.ScopeType;
import org.jboss.seam.forge.project.facets.BaseFacet;
import org.jboss.seam.forge.project.facets.DependencyFacet;
import org.jboss.seam.forge.project.facets.MavenCoreFacet;
import org.jboss.seam.forge.project.facets.WebResourceFacet;
import org.jboss.seam.forge.project.facets.builtin.MavenDependencyFacet;
import org.jboss.seam.forge.shell.ShellColor;
import org.jboss.seam.forge.shell.ShellPrintWriter;
import org.jboss.seam.forge.shell.ShellPrompt;
import org.jboss.seam.forge.shell.plugins.Alias;
import org.jboss.seam.forge.shell.plugins.RequiresFacet;
import org.jboss.seam.forge.spec.servlet.ServletFacet;
import org.jboss.shrinkwrap.descriptor.api.spec.servlet.web.WebAppDescriptor;

/**
 *
 * @author bleathem
 */
@Alias("org.richfaces")
@RequiresFacet({MavenDependencyFacet.class, ServletFacet.class, WebResourceFacet.class})
public class RichFacesFacet extends BaseFacet {

    @Deprecated
    static final String RICHFACES_VERSION = "4.0.0.Final";
    static final String SUCCESS_MSG_FMT = "***SUCCESS*** %s %s has been installed.";
    static final String ALREADY_INSTALLED_MSG_FMT = "***INFO*** %s %s is already present.";

    @Override
    public boolean install() {
        installDependencies();
        installDescriptor();
        return true;
    }

    @Override
    public boolean isInstalled() {
        DependencyFacet deps = getProject().getFacet(DependencyFacet.class);
        if (getProject().hasAllFacets(Arrays.asList(MavenDependencyFacet.class, WebResourceFacet.class, ServletFacet.class))) {
            for (RichFacesVersion version : RichFacesVersion.values()) {
                if (deps.hasDependency(version.getDependency())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Set the context-params and Servlet definition if they are not yet set.
     *
     * @param writer
     */
    private void installDescriptor() {
        ServletFacet servlet = project.getFacet(ServletFacet.class);
        WebAppDescriptor descriptor = servlet.getConfig();
        if (descriptor.getContextParam("javax.faces.PROJECT_STAGE") == null) {
            descriptor.contextParam("javax.faces.PROJECT_STAGE", "Development");
        } else {
        }
        if (descriptor.getContextParam("javax.faces.SKIP_COMMENTS") == null) {
            descriptor.contextParam("javax.faces.SKIP_COMMENTS", "true");
        } else {
        }

        if (isFacesServletDefined(descriptor)) {
        } else {
            descriptor.servlet("Faces Servlet", FacesServlet.class, new String[]{"*.jsf", "/faces/*"});
        }

        descriptor.sessionTimeout(30);
//        TODO:
//        <mime-mapping>
//            <extension>ecss</extension>
//            <mime-type>text/css</mime-type>
//        </mime-mapping>
        descriptor.welcomeFile("faces/index.xhtml");
        servlet.saveConfig(descriptor);
    }

    /**
     * A helper method to determine if the Faces Servlet is defined in the web.xml
     *
     * @param descriptor
     * @return true if the Faces Servlet is defined, false otherwise
     */
    private boolean isFacesServletDefined(WebAppDescriptor descriptor) {
//        TODO: When WebAppDescriptor.getServlets is implemented:
//        List<ServletDef> servlets = descriptor.getServlets();
//        if (servlets != null && ! servlets.isEmpty()) {
//            for (ServletDef servlet : servlets) {
//                writer.println(ShellColor.MAGENTA, servlet.getName());
//                if (servlet.getName().equals("Faces Servlet")) {
//                    writer.println(ShellColor.YELLOW, String.format(ALREADY_INSTALLED_MSG_FMT, "Faces Servlet", "mapping"));
//                    return;
//                }
//            }
//        } else {
//            writer.println("servlets list is empty");
//        }
        return descriptor.exportAsString().contains(FacesServlet.class.getName());
    }

    /**
     * Install the maven dependencies required for RichFaces
     *
     * @param writer
     */
    private void installDependencies() {
        installDependencyManagement(project);

        DependencyFacet deps = project.getFacet(DependencyFacet.class);
        DependencyBuilder dependency;

        dependency = DependencyBuilder.create();
        dependency.setArtifactId("richfaces-components-ui").setGroupId("org.richfaces.ui").setVersion(RICHFACES_VERSION);
        installDependency(deps, dependency);

        dependency = DependencyBuilder.create();
        dependency.setArtifactId("richfaces-core-impl").setGroupId("org.richfaces.core").setVersion(RICHFACES_VERSION);
        installDependency(deps, dependency);

        dependency = DependencyBuilder.create();
        dependency.setArtifactId("servlet-api").setGroupId("javax.servlet").setScopeType(ScopeType.PROVIDED);
        installDependency(deps, dependency);

        dependency = DependencyBuilder.create();
        dependency.setArtifactId("jsp-api").setGroupId("javax.servlet.jsp").setScopeType(ScopeType.PROVIDED);
        installDependency(deps, dependency);

        dependency = DependencyBuilder.create();
        dependency.setArtifactId("jstl").setGroupId("javax.servlet").setScopeType(ScopeType.PROVIDED);
        installDependency(deps, dependency);

        dependency = DependencyBuilder.create();
        dependency.setArtifactId("ehcache").setGroupId("net.sf.ehcache");
        installDependency(deps, dependency);

//        TODO: When forge has classifier support (<classifier>jdk15</classifier>)
//        dependency = DependencyBuilder.create();
//        dependency.setArtifactId("testng").setGroupId("org.testng").setVersion("5.1.0").setScopeType(ScopeType.TEST);
//        installDependency(deps, dependency);

    }

    /**
     * Install the richfaces-bom in the pom's dependency management
     *
     * @param project
     * @param writer
     */
    private void installDependencyManagement(Project project) {
        MavenCoreFacet maven = project.getFacet(MavenCoreFacet.class);
        Model pom = maven.getPOM();
        DependencyManagement dependencyManagement = pom.getDependencyManagement();
        if (dependencyManagement == null) {
            dependencyManagement = new DependencyManagement();
        }
        DependencyBuilder bomDependency = DependencyBuilder.create();
        bomDependency.setArtifactId("richfaces-bom").setGroupId("org.richfaces").setVersion("4.0.0.Final").setScopeType(ScopeType.IMPORT).setPackagingType("pom");
        for (org.apache.maven.model.Dependency dependency : dependencyManagement.getDependencies()) {
            Dependency localDependency = new MavenDependencyAdapter(dependency);
            if (bomDependency.getArtifactId().equals(localDependency.getArtifactId())
                    && bomDependency.getGroupId().equals(localDependency.getGroupId())) {
                return;
            }
        }
        dependencyManagement.addDependency(new MavenDependencyAdapter(bomDependency));
        pom.setDependencyManagement(dependencyManagement);
        maven.setPOM(pom);

    }

    /**
     * A helper method to install a dependency, and log the result
     *
     * @param deps
     * @param dependency
     * @param writer
     */
    private void installDependency(DependencyFacet deps, Dependency dependency) {
        if (! deps.hasDependency(dependency)) {
            deps.addDependency(dependency);
        }
    }
}
