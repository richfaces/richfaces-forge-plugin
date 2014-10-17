package org.richfaces.forge;

import java.io.FileNotFoundException;

import javax.inject.Inject;

import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.ui.annotation.Command;
import org.jboss.forge.addon.ui.annotation.Option;
import org.jboss.forge.addon.ui.output.UIOutput;

public class RichFacesPlugin {

    private final Project project;

    @Inject
    public RichFacesPlugin(final Project project) {
        this.project = project;
    }

    @Command
    public void status(final UIOutput out) {
        if (project.hasFacet(RichFacesFacet.class)) {
            out.out().println("RichFaces is installed.");
        } else {
            out.out().println("RichFaces is not installed. Use 'richfaces setup' to get started.");
        }
    }

    // confirmed working
    @Command("setup")
    public void setup(final UIOutput out) {
        if (!project.hasFacet(RichFacesFacet.class)) {
            // installFacets.fire(new InstallFacets(RichFacesFacet.class));
        }
        if (project.hasFacet(RichFacesFacet.class)) {
            // ShellMessages.success(out, "RichFacesFacet is configured.");
        }
    }

    @Command("help")
    public void exampleDefaultCommand(@Option("opt") final String opt, final UIOutput UIOutput) {
        UIOutput.out().println(/* ShellColor.BLUE, */"Use the install commands to install:");
        UIOutput.out().println(/* ShellColor.BLUE, */"  install-example-facelet: a sample RichFaces enabled facelet file");
    }

    @Command("install-example-facelet")
    public void installExampleFacelets(final UIOutput UIOutput) throws FileNotFoundException {
    }
}