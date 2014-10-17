package org.richfaces.forge;

import java.io.FileNotFoundException;
import java.util.List;

import org.jboss.forge.addon.facets.AbstractFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFacet;
import org.jboss.forge.addon.ui.output.UIOutput;

public abstract class RichFacesFacet extends AbstractFacet<Project> implements ProjectFacet {

    static final String SUCCESS_MSG_FMT = "%s %s has been installed.";
    static final String ALREADY_INSTALLED_MSG_FMT = "%s %s is already present.";

    public static final String RICH_VERSION_PROP_NAME = "version.richfaces";
    public static final String RICH_VERSION_PROP = "${" + RICH_VERSION_PROP_NAME + "}";

    public abstract String getDefaultVersion();

    public abstract List<String> getAvailableVersions();

    public abstract void setVersion(String version);

    public abstract String getVersion();

    public abstract void createFaceletFiles(Project project, UIOutput uiOutput);

    public abstract void createRichBean(Project project, UIOutput uiOutput) throws FileNotFoundException;
}
