package org.richfaces.forge;

import javax.inject.Inject;
import org.jboss.seam.forge.project.Project;
import org.jboss.seam.forge.project.dependencies.DependencyBuilder;
import org.jboss.seam.forge.project.facets.DependencyFacet;
import org.jboss.seam.forge.project.facets.builtin.MavenDependencyFacet;
import org.jboss.seam.forge.shell.ShellColor;
import org.jboss.seam.forge.shell.plugins.Alias;
import org.jboss.seam.forge.shell.plugins.Command;
import org.jboss.seam.forge.shell.plugins.DefaultCommand;
import org.jboss.seam.forge.shell.plugins.Option;
import org.jboss.seam.forge.shell.plugins.PipeOut;
import org.jboss.seam.forge.shell.plugins.Plugin;
import org.jboss.seam.forge.shell.plugins.RequiresFacet;
import org.jboss.seam.forge.shell.project.ProjectScoped;

@Alias("richfaces")
@RequiresFacet(MavenDependencyFacet.class)
public class RichFacesPlugin implements Plugin {
    private static final String RICHFACES_VERSION = "3.0.0.Final";
    private static final String SUCCESS_MSG_FMT = "***SUCCESS*** %s dependency has been installed.";
    private static final String ALREADY_INSTALLED_MSG_FMT = "***INFO***%s dependency is already present.";
    
    @Inject
    @ProjectScoped Project project;
    
    @DefaultCommand
    public void exampleDefaultCommand(@Option String opt, PipeOut pipeOut) {
        pipeOut.println(ShellColor.BLUE, "Use the install command to add a Seam Faces dependency.");
    }
    
    @Command("install")
    public void installCommand(PipeOut pipeOut) {
        DependencyBuilder uiDependency = DependencyBuilder.create();
        uiDependency.setArtifactId("richfaces-components-ui");
        uiDependency.setGroupId("org.richfaces.ui");
        uiDependency.setVersion(RICHFACES_VERSION);
        DependencyBuilder coreDependency = DependencyBuilder.create();
        coreDependency.setArtifactId("richfaces-core-impl");
        coreDependency.setGroupId("org.richfaces.core");
        coreDependency.setVersion(RICHFACES_VERSION);
        DependencyFacet deps = project.getFacet(DependencyFacet.class);
        if (deps.hasDependency(uiDependency)) {
            pipeOut.println(ShellColor.YELLOW, String.format(ALREADY_INSTALLED_MSG_FMT, "RichFaces Components UI"));
        } else {
            deps.addDependency(uiDependency);
            pipeOut.println(ShellColor.GREEN, String.format(SUCCESS_MSG_FMT, "RichFaces Components UI"));
        }
        if (deps.hasDependency(coreDependency)) {
            pipeOut.println(ShellColor.YELLOW, String.format(ALREADY_INSTALLED_MSG_FMT, "RichFaces Core"));
        } else {
            deps.addDependency(coreDependency);
            pipeOut.println(ShellColor.GREEN, String.format(SUCCESS_MSG_FMT, "RichFaces core"));
        }
    }
}

