package org.richfaces.forge;

import java.util.ArrayList;
import java.util.List;

import org.jboss.forge.addon.dependencies.Dependency;
import org.jboss.forge.addon.facets.AbstractFacet;
import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.facets.constraints.FacetConstraints;
import org.jboss.forge.addon.javaee.servlet.ServletFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFacet;
import org.jboss.forge.addon.projects.facets.DependencyFacet;
import org.jboss.forge.addon.projects.facets.MetadataFacet;
import org.jboss.forge.addon.projects.facets.ResourcesFacet;
import org.jboss.forge.addon.projects.facets.WebResourcesFacet;
import org.jboss.shrinkwrap.descriptor.api.javaee7.ParamValueType;
import org.jboss.shrinkwrap.descriptor.api.webapp31.WebAppDescriptor;

/**
 * @author bleathem
 */
@FacetConstraints({ @FacetConstraint(DependencyFacet.class), @FacetConstraint(MetadataFacet.class),
        @FacetConstraint(ProjectFacet.class), @FacetConstraint(ResourcesFacet.class) })
public class RichFacesFacetImpl extends AbstractFacet<Project> implements RichFacesFacet {
    static final String FACES_SERVLET_CLASS = "javax.faces.webapp.FacesServlet";

    @Override
    public boolean install() {
        installDependencies(version);
        installDescriptor(version);
        return true;
    }

    @Override
    public boolean isInstalled() {
        DependencyFacet deps = getFaceted().getFacet(DependencyFacet.class);
        if (getFaceted().hasAllFacets(DependencyFacet.class, WebResourcesFacet.class, ServletFacet.class)) {
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
    private void installDescriptor(final RichFacesVersion version) {
        @SuppressWarnings("unchecked")
        ServletFacet<WebAppDescriptor> servlet = getFaceted().getFacet(ServletFacet.class);
        WebAppDescriptor descriptor = (WebAppDescriptor) servlet.getConfig();
        boolean found = false;
        for (ParamValueType<WebAppDescriptor> contextParam : descriptor.getAllContextParam()) {
            if (contextParam.getParamName().equals("javax.faces.DATETIMECONVERTER_DEFAULT_TIMEZONE_IS_SYSTEM_TIMEZONE")) {
                found = true;
            }
        }
        if (!found) {
            descriptor.createContextParam().paramName("javax.faces.DATETIMECONVERTER_DEFAULT_TIMEZONE_IS_SYSTEM_TIMEZONE")
                .paramValue("true");
        }

        descriptor.getOrCreateWelcomeFileList().getAllWelcomeFile().add("faces/index.xhtml");
        servlet.saveConfig(descriptor);
    }

    /**
     * A helper method to determine if the Faces Servlet is defined in the web.xml
     *
     * @param descriptor
     * @return true if the Faces Servlet is defined, false otherwise
     */
    private boolean isFacesServletDefined(final WebAppDescriptor descriptor) {
        return descriptor.exportAsString().contains(FACES_SERVLET_CLASS);
    }

    /**
     * Install the maven dependencies required for RichFaces
     *
     * @param version
     */
    private void installDependencies(final RichFacesVersion version) {
        installDependencyManagement(version);

        DependencyFacet deps = getFaceted().getFacet(DependencyFacet.class);
        for (Dependency dependency : version.getDependencies()) {
            deps.addDirectDependency(dependency);
        }

    }

    /**
     * Install the richfaces-bom in the pom's dependency management
     *
     * @param version
     */
    private void installDependencyManagement(final RichFacesVersion version)
   {
      if (version == RichFacesVersion.RICHFACES_4_5_0) {
          return; // no dep management for 4.5
      }
      DependencyFacet deps = getFaceted().getFacet(DependencyFacet.class);
      for (Dependency dependency : version.getDependencyManagement()) {
         deps.addManagedDependency(dependency);
      }
   }

    public String getDefaultVersion() {
        return RichFacesVersion.RICHFACES_4_5_0.name();
    }

    public List<String> getAvailableVersions() {
        List<String> list = new ArrayList<String>();
        for (RichFacesVersion v : RichFacesVersion.values()) {
            list.add(v.name());
        }
        return list;
    }

    private RichFacesVersion version;

    public void setVersion(String version) {
        this.version = RichFacesVersion.valueOf(version);
    }

    public String getVersion() {
        return version.name();
    }
}
