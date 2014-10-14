package org.richfaces.forge;

import java.util.List;

import org.jboss.forge.addon.projects.ProjectFacet;

public interface RichFacesFacet extends ProjectFacet {

    static final String SUCCESS_MSG_FMT = "***SUCCESS*** %s %s has been installed.";
    static final String ALREADY_INSTALLED_MSG_FMT = "***INFO*** %s %s is already present.";

    public static final String RICH_VERSION_PROP_NAME = "version.richfaces";
    public static final String RICH_VERSION_PROP = "${" + RICH_VERSION_PROP_NAME + "}";

    public String getDefaultVersion();

    public List<String> getAvailableVersions();

    public void setVersion(String version);

    public String getVersion();
}
