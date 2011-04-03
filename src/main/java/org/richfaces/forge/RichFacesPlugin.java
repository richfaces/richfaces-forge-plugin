package org.richfaces.forge;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.webapp.FacesServlet;
import javax.inject.Inject;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.model.Model;
import org.jboss.seam.forge.project.Project;
import org.jboss.seam.forge.project.dependencies.Dependency;
import org.jboss.seam.forge.project.dependencies.DependencyBuilder;
import org.jboss.seam.forge.project.dependencies.MavenDependencyAdapter;
import org.jboss.seam.forge.project.dependencies.ScopeType;
import org.jboss.seam.forge.project.facets.DependencyFacet;
import org.jboss.seam.forge.project.facets.MavenCoreFacet;
import org.jboss.seam.forge.project.facets.WebResourceFacet;
import org.jboss.seam.forge.project.facets.builtin.MavenDependencyFacet;
import org.jboss.seam.forge.resources.DirectoryResource;
import org.jboss.seam.forge.resources.FileResource;
import org.jboss.seam.forge.shell.ShellColor;
import org.jboss.seam.forge.shell.plugins.Alias;
import org.jboss.seam.forge.shell.plugins.Command;
import org.jboss.seam.forge.shell.plugins.DefaultCommand;
import org.jboss.seam.forge.shell.plugins.Option;
import org.jboss.seam.forge.shell.plugins.PipeOut;
import org.jboss.seam.forge.shell.plugins.Plugin;
import org.jboss.seam.forge.shell.plugins.RequiresFacet;
import org.jboss.seam.forge.shell.project.ProjectScoped;
import org.jboss.seam.forge.spec.servlet.ServletFacet;
import org.jboss.shrinkwrap.descriptor.api.spec.servlet.web.WebAppDescriptor;

@Alias("richfaces")
@RequiresFacet({ MavenDependencyFacet.class, ServletFacet.class, WebResourceFacet.class })
public class RichFacesPlugin implements Plugin {
    private static final String RICHFACES_VERSION = "4.0.0.Final";
    private static final String SUCCESS_MSG_FMT = "***SUCCESS*** %s %s has been installed.";
    private static final String ALREADY_INSTALLED_MSG_FMT = "***INFO*** %s %s is already present.";
    
    @Inject
    @ProjectScoped Project project;
    
    @DefaultCommand
    public void exampleDefaultCommand(@Option String opt, PipeOut pipeOut) {
        pipeOut.println(ShellColor.BLUE, "Use the install command to add a Seam Faces dependency.");
    }
    
    @Command("install")
    public void installCommand(PipeOut pipeOut) {
        installDependencies(pipeOut);
        ServletFacet servlet = project.getFacet(ServletFacet.class);
        WebAppDescriptor descriptor = servlet.getConfig();
        if (descriptor.getContextParam("javax.faces.PROJECT_STAGE") == null) {
            descriptor.contextParam("javax.faces.PROJECT_STAGE", "Development");
            pipeOut.println(ShellColor.GREEN, String.format(SUCCESS_MSG_FMT, "javax.faces.PROJECT_STAGE", "context-param"));
        } else {
            pipeOut.println(ShellColor.YELLOW, String.format(ALREADY_INSTALLED_MSG_FMT, "javax.faces.PROJECT_STAGE", "context-param"));
        }
        if (descriptor.getContextParam("javax.faces.SKIP_COMMENTS") == null) {
            descriptor.contextParam("javax.faces.SKIP_COMMENTS", "true");
            pipeOut.println(ShellColor.GREEN, String.format(SUCCESS_MSG_FMT, "javax.faces.SKIP_COMMENTS", "context-param"));
        } else {
            pipeOut.println(ShellColor.YELLOW, String.format(ALREADY_INSTALLED_MSG_FMT, "javax.faces.SKIP_COMMENTS", "context-param"));
        }
        installFacesServlet(descriptor, pipeOut);
        descriptor.sessionTimeout(30);
//        TODO: 
//        <mime-mapping>
//            <extension>ecss</extension>
//            <mime-type>text/css</mime-type>
//        </mime-mapping>
        descriptor.welcomeFile("faces/index.xhtml");
        servlet.saveConfig(descriptor);
        createFaceletFiles(pipeOut);
    }
    
    private void createFaceletFiles(PipeOut pipeOut) {
        DirectoryResource webRoot = project.getFacet(WebResourceFacet.class).getWebRootDirectory();
        DirectoryResource templateDirectory = webRoot.getOrCreateChildDirectory("templates");
        FileResource<?> templatePage = (FileResource<?>) templateDirectory.getChild("template.xhtml");
        InputStream stream = RichFacesPlugin.class.getResourceAsStream("/org/richfaces/forge/template.xhtml");
        templatePage.setContents(stream);
        pipeOut.println(ShellColor.YELLOW, String.format(SUCCESS_MSG_FMT, "template.xhtml", "file"));
        
        FileResource<?> indexPage = (FileResource<?>) webRoot.getChild("index.xhtml");
        stream = RichFacesPlugin.class.getResourceAsStream("/org/richfaces/forge/index.xhtml");
        indexPage.setContents(stream);
        pipeOut.println(ShellColor.YELLOW, String.format(SUCCESS_MSG_FMT, "index.xhtml", "file"));
        
        FileResource<?> forgeIndexPage = (FileResource<?>) webRoot.getChild("index.html");
        String contents;
//        TODO: if (contents.contains("Welcome to Seam Forge")) {
        forgeIndexPage.delete();
    }
    
    private void installFacesServlet(WebAppDescriptor descriptor, PipeOut pipeOut) {
//        TODO: When WebAppDescriptor.getServlets is implemented:
//        List<ServletDef> servlets = descriptor.getServlets();
//        if (servlets != null && ! servlets.isEmpty()) {
//            for (ServletDef servlet : servlets) {
//                pipeOut.println(ShellColor.MAGENTA, servlet.getName());
//                if (servlet.getName().equals("Faces Servlet")) {
//                    pipeOut.println(ShellColor.YELLOW, String.format(ALREADY_INSTALLED_MSG_FMT, "Faces Servlet", "mapping"));
//                    return;
//                }
//            }
//        } else {
//            pipeOut.println("servlets list is empty");
//        }
        if (descriptor.exportAsString().contains(FacesServlet.class.getName())) {
            pipeOut.println(ShellColor.YELLOW, String.format(ALREADY_INSTALLED_MSG_FMT, "Faces Servlet", "mapping"));
            return;
        }
        descriptor.servlet("Faces Servlet", FacesServlet.class, new String[] {"*.jsf", "/faces/*"});
        pipeOut.println(ShellColor.GREEN, String.format(SUCCESS_MSG_FMT, "Faces Servlet", "mapping"));
    }
    
    private void installDependencies(PipeOut pipeOut) {
        installDependencyManagement(project, pipeOut);
        
        DependencyFacet deps = project.getFacet(DependencyFacet.class);
        DependencyBuilder dependency;
        
        dependency = DependencyBuilder.create();
        dependency.setArtifactId("richfaces-components-ui").setGroupId("org.richfaces.ui").setVersion(RICHFACES_VERSION);
        installDependency(deps, dependency, pipeOut);
        
        dependency = DependencyBuilder.create();
        dependency.setArtifactId("richfaces-core-impl").setGroupId("org.richfaces.core").setVersion(RICHFACES_VERSION);
        installDependency(deps, dependency, pipeOut);
        
        dependency = DependencyBuilder.create();
        dependency.setArtifactId("servlet-api").setGroupId("javax.servlet").setScopeType(ScopeType.PROVIDED);
        installDependency(deps, dependency, pipeOut);
        
        dependency = DependencyBuilder.create();
        dependency.setArtifactId("jsp-api").setGroupId("javax.servlet.jsp").setScopeType(ScopeType.PROVIDED);
        installDependency(deps, dependency, pipeOut);
        
        dependency = DependencyBuilder.create();
        dependency.setArtifactId("jstl").setGroupId("javax.servlet").setScopeType(ScopeType.PROVIDED);
        installDependency(deps, dependency, pipeOut);
        
        dependency = DependencyBuilder.create();
        dependency.setArtifactId("ehcache").setGroupId("net.sf.ehcache");
        installDependency(deps, dependency, pipeOut);
        
//        TODO: When forge has classifier support (<classifier>jdk15</classifier>)
//        dependency = DependencyBuilder.create();
//        dependency.setArtifactId("testng").setGroupId("org.testng").setVersion("5.1.0").setScopeType(ScopeType.TEST);
//        installDependency(deps, dependency, pipeOut);
        
    }
    
    private void installDependencyManagement(Project project, PipeOut pipeOut) {
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
                pipeOut.println(ShellColor.YELLOW, String.format(ALREADY_INSTALLED_MSG_FMT, "RichFaces BOM", "dependency"));
                return;
            }
        }
        dependencyManagement.addDependency(new MavenDependencyAdapter(bomDependency));
        pom.setDependencyManagement(dependencyManagement);
        maven.setPOM(pom);
        
        pipeOut.println(ShellColor.GREEN, String.format(SUCCESS_MSG_FMT, "RichFaces BOM", "dependency"));
    }
    
    private void installDependency(DependencyFacet deps, Dependency dependency, PipeOut pipeOut) {
        String name = dependency.toCoordinates();
        if (deps.hasDependency(dependency)) {
            pipeOut.println(ShellColor.YELLOW, String.format(ALREADY_INSTALLED_MSG_FMT, name, "dependency"));
        } else {
            deps.addDependency(dependency);
            pipeOut.println(ShellColor.GREEN, String.format(SUCCESS_MSG_FMT, name, "dependency"));
        }
    }
    
    private void installrichBean() {
    
    }
    
}

