package org.richfaces.forge;

import java.io.InputStream;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.project.facets.WebResourceFacet;
import org.jboss.forge.project.facets.events.InstallFacets;
import org.jboss.forge.resources.DirectoryResource;
import org.jboss.forge.resources.FileResource;
import org.jboss.forge.shell.ShellColor;
import org.jboss.forge.shell.ShellMessages;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.Command;
import org.jboss.forge.shell.plugins.DefaultCommand;
import org.jboss.forge.shell.plugins.Option;
import org.jboss.forge.shell.plugins.PipeOut;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.plugins.RequiresProject;

@Alias("richfaces")
@RequiresProject
public class RichFacesPlugin implements Plugin {

    private final Project project;
    private final Event<InstallFacets> installFacets;

    @Inject
    public RichFacesPlugin(final Project project, final Event<InstallFacets> event) {
        this.project = project;
        this.installFacets = event;
    }

    @DefaultCommand
    public void status(final PipeOut out) {
        if (project.hasFacet(RichFacesFacet.class)) {
            out.println("RichFaces is installed.");
        } else {
            out.println("RichFaces is not installed. Use 'richfaces setup' to get started.");
        }
    }

    // confirmed working
    @Command("setup")
    public void setup(final PipeOut out) {
        if (!project.hasFacet(RichFacesFacet.class)) {
            installFacets.fire(new InstallFacets(RichFacesFacet.class));
        }
        if (project.hasFacet(RichFacesFacet.class)) {
            ShellMessages.success(out, "RichFacesFacet is configured.");
        }
    }

    private void assertInstalled() {
        if (!project.hasFacet(RichFacesFacet.class)) {
            throw new RuntimeException("RichFacesFacet is not installed. Use 'richfaces setup' to get started.");
        }
    }

    @Command("help")
    public void exampleDefaultCommand(@Option final String opt, final PipeOut pipeOut) {
        pipeOut.println(ShellColor.BLUE, "Use the install commands to install:");
        pipeOut.println(ShellColor.BLUE, "  install-example-facelet: a sample RichFaces enabled facelet file");
    }

    @Command("install-example-facelet")
    public void installExampleFacelets(final PipeOut pipeOut) {
        assertInstalled();
        createFaceletFiles(pipeOut);
        createRichBean(pipeOut);
    }

    /**
     * Create a simple template file, and a RichFaces enabled index file that uses the template
     *
     * @param pipeOut
     */
    private void createFaceletFiles(final PipeOut pipeOut) {
        DirectoryResource webRoot = project.getFacet(WebResourceFacet.class).getWebRootDirectory();
        DirectoryResource templateDirectory = webRoot.getOrCreateChildDirectory("templates");
        FileResource<?> templatePage = (FileResource<?>) templateDirectory.getChild("template.xhtml");
        InputStream stream = RichFacesPlugin.class.getResourceAsStream("/org/richfaces/forge/template.xhtml");
        templatePage.setContents(stream);
        pipeOut.println(ShellColor.YELLOW, String.format(RichFacesFacet.SUCCESS_MSG_FMT, "template.xhtml", "file"));

        FileResource<?> indexPage = (FileResource<?>) webRoot.getChild("index.xhtml");
        stream = RichFacesPlugin.class.getResourceAsStream("/org/richfaces/forge/index.xhtml");
        indexPage.setContents(stream);
        pipeOut.println(ShellColor.YELLOW, String.format(RichFacesFacet.SUCCESS_MSG_FMT, "index.xhtml", "file"));

        FileResource<?> forgeIndexPage = (FileResource<?>) webRoot.getChild("index.html");
        String contents;
        // TODO: if (contents.contains("Welcome to Seam Forge")) {
        forgeIndexPage.delete();
    }

    /**
     * Create a simple JSF managed bean to back the RichFaces input in the example facelet file
     *
     * @param pipeOut
     */
    private void createRichBean(final PipeOut pipeOut) {
        JavaSourceFacet source = project.getFacet(JavaSourceFacet.class);
        DirectoryResource sourceRoot = source.getBasePackageResource();
        FileResource<?> indexPage = (FileResource<?>) sourceRoot.getChild("RichBean.java");
        InputStream stream = RichFacesPlugin.class.getResourceAsStream("/org/richfaces/forge/RichBean.java.txt");
        indexPage.setContents(stream);
        pipeOut.println(ShellColor.YELLOW, String.format(RichFacesFacet.SUCCESS_MSG_FMT, "RichBean", "class"));
    }
}