package org.richfaces.forge;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.DispatcherType;

import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.facets.BaseFacet;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.project.facets.WebResourceFacet;
import org.jboss.forge.shell.ShellPrintWriter;
import org.jboss.forge.shell.ShellPrompt;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.RequiresFacet;
import org.jboss.forge.spec.javaee.FacesFacet;
import org.jboss.forge.spec.javaee.ServletFacet;
import org.jboss.shrinkwrap.descriptor.api.spec.servlet.web.FilterDef;
import org.jboss.shrinkwrap.descriptor.api.spec.servlet.web.ServletDef;
import org.jboss.shrinkwrap.descriptor.api.spec.servlet.web.WebAppDescriptor;

/**
 * @author bleathem
 */
@Alias("org.richfaces")
@RequiresFacet({ DependencyFacet.class, FacesFacet.class, WebResourceFacet.class })
public class RichFacesFacet extends BaseFacet
{
   static final String FACES_SERVLET_CLASS = "javax.faces.webapp.FacesServlet";

   static final String SUCCESS_MSG_FMT = "***SUCCESS*** %s %s has been installed.";
   static final String ALREADY_INSTALLED_MSG_FMT = "***INFO*** %s %s is already present.";

   @Inject
   private ShellPrompt prompt;
   @Inject
   private ShellPrintWriter writer;

   @Override
   public boolean install()
   {
      writer.println();
      RichFacesVersion version = prompt.promptChoiceTyped("Which version of RichFaces?",
               Arrays.asList(RichFacesVersion.values()), RichFacesVersion.RICHFACES_4_2_2);
      installDependencies(version);
      installDescriptor(version);
      return true;
   }

   @Override
   public boolean isInstalled()
   {
      DependencyFacet deps = getProject().getFacet(DependencyFacet.class);
      if (getProject().hasAllFacets(Arrays.asList(DependencyFacet.class, WebResourceFacet.class, ServletFacet.class))) {
         for (RichFacesVersion version : RichFacesVersion.values()) {
            boolean hasVersionDependencies = true;
            for (Dependency dependency : version.getDependencies()) {
               if (!deps.hasEffectiveDependency(dependency)) {
                  hasVersionDependencies = false;
                  break;
               }
            }
            if (hasVersionDependencies) {
               return true;
            }
         }
      }
      return false;
   }

   /**
    * Set the context-params and Servlet definition if they are not yet set.
    *
    * @param version
    */
   private void installDescriptor(final RichFacesVersion version)
   {
      ServletFacet servlet = project.getFacet(ServletFacet.class);
      WebAppDescriptor descriptor = servlet.getConfig();
      if (descriptor.getContextParam("javax.faces.SKIP_COMMENTS") == null) {
         descriptor.contextParam("javax.faces.SKIP_COMMENTS", "true");
      }

      if (!isFacesServletDefined(descriptor) & !descriptor.getVersion().startsWith("3")) {
         descriptor.facesServlet();
      }

      if (RichFacesVersion.RICHFACES_3_3_3.equals(version)) {
         List<ServletDef> servlets = descriptor.getServlets();
         String facesServletName = "FacesServlet";
         for (ServletDef servletDef : servlets) {
            if (FACES_SERVLET_CLASS.equals(servletDef.getServletClass())) {
               facesServletName = servletDef.getName();
            }
         }
         FilterDef filter = descriptor.filter("org.ajax4jsf.Filter")
                  .mapping().servletName(facesServletName)
                  .dispatchTypes(DispatcherType.REQUEST,
                           DispatcherType.FORWARD,
                           DispatcherType.INCLUDE,
                           DispatcherType.ERROR);
      }

      descriptor.sessionTimeout(30);
      // TODO:
      // <mime-mapping>
      // <extension>ecss</extension>
      // <mime-type>text/css</mime-type>
      // </mime-mapping>
      descriptor.welcomeFile("faces/index.xhtml");
      servlet.saveConfig(descriptor);
   }

   /**
    * A helper method to determine if the Faces Servlet is defined in the web.xml
    *
    * @param descriptor
    * @return true if the Faces Servlet is defined, false otherwise
    */
   private boolean isFacesServletDefined(final WebAppDescriptor descriptor)
   {
      // TODO: When WebAppDescriptor.getServlets is implemented:
      // List<ServletDef> servlets = descriptor.getServlets();
      // if (servlets != null && ! servlets.isEmpty()) {
      // for (ServletDef servlet : servlets) {
      // writer.println(ShellColor.MAGENTA, servlet.getName());
      // if (servlet.getName().equals("Faces Servlet")) {
      // writer.println(ShellColor.YELLOW, String.format(ALREADY_INSTALLED_MSG_FMT, "Faces Servlet", "mapping"));
      // return;
      // }
      // }
      // } else {
      // writer.println("servlets list is empty");
      // }
      return descriptor.exportAsString().contains(FACES_SERVLET_CLASS);
   }

   /**
    * Install the maven dependencies required for RichFaces
    *
    * @param version
    */
   private void installDependencies(final RichFacesVersion version)
   {
      installDependencyManagement(version);

      DependencyFacet deps = project.getFacet(DependencyFacet.class);
      for (Dependency dependency : version.getDependencies()) {
         deps.addDirectDependency(dependency);
      }

      // TODO: When forge has classifier support (<classifier>jdk15</classifier>)
      // dependency = DependencyBuilder.create();
      // dependency.setArtifactId("testng").setGroupId("org.testng").setVersion("5.1.0").setScopeType(ScopeType.TEST);
      // installDependency(deps, dependency);

   }

   /**
    * Install the richfaces-bom in the pom's dependency management
    *
    * @param version
    */
   private void installDependencyManagement(final RichFacesVersion version)
   {
      DependencyFacet deps = project.getFacet(DependencyFacet.class);
      for (Dependency dependency : RichFacesVersion.RICHFACES_4_2_2.getDependencyManagement()) {
         deps.addManagedDependency(dependency);
      }
   }
}
