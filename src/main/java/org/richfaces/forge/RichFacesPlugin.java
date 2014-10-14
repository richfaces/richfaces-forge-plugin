package org.richfaces.forge;


import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.inject.Inject;

import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.facets.MetadataFacet;
import org.jboss.forge.addon.projects.facets.WebResourcesFacet;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.ui.annotation.Command;
import org.jboss.forge.addon.ui.annotation.Option;
import org.jboss.forge.addon.ui.output.UIOutput;
import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.source.JavaSource;

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
            //ShellMessages.success(out, "RichFacesFacet is configured.");
        }
    }

    private void assertInstalled() {
        if (!project.hasFacet(RichFacesFacet.class)) {
            throw new RuntimeException("RichFacesFacet is not installed. Use 'richfaces setup' to get started.");
        }
    }

    @Command("help")
    public void exampleDefaultCommand(@Option("opt") final String opt, final UIOutput UIOutput) {
        UIOutput.out().println(/*ShellColor.BLUE,*/ "Use the install commands to install:");
        UIOutput.out().println(/*ShellColor.BLUE,*/ "  install-example-facelet: a sample RichFaces enabled facelet file");
    }

    @Command("install-example-facelet")
    public void installExampleFacelets(final UIOutput UIOutput) throws FileNotFoundException {
        assertInstalled();
        createFaceletFiles(UIOutput);
        createRichBean(UIOutput);
    }

    /**
     * Create a simple template file, and a RichFaces enabled index file that uses the template
     *
     * @param UIOutput
     */
    private void createFaceletFiles(final UIOutput UIOutput) {
        DirectoryResource webRoot = project.getFacet(WebResourcesFacet.class).getWebRootDirectory();
        DirectoryResource templateDirectory = webRoot.getOrCreateChildDirectory("templates");
        FileResource<?> templatePage = (FileResource<?>) templateDirectory.getChild("template.xhtml");
        InputStream stream = RichFacesPlugin.class.getResourceAsStream("/org/richfaces/forge/template.xhtml");
        templatePage.setContents(stream);
        UIOutput.out().println(/*ShellColor.YELLOW, */String.format(RichFacesFacet.SUCCESS_MSG_FMT, "template.xhtml", "file"));

        FileResource<?> indexPage = (FileResource<?>) webRoot.getChild("index.xhtml");
        stream = RichFacesPlugin.class.getResourceAsStream("/org/richfaces/forge/index.xhtml");
        indexPage.setContents(stream);
        UIOutput.out().println(/*ShellColor.YELLOW, */String.format(RichFacesFacet.SUCCESS_MSG_FMT, "index.xhtml", "file"));

        FileResource<?> forgeIndexPage = (FileResource<?>) webRoot.getChild("index.html");
        String contents;
        // TODO: if (contents.contains("Welcome to Seam Forge")) {
        forgeIndexPage.delete();
    }

    /**
     * Create a simple JSF managed bean to back the RichFaces input in the  example facelet file
     *
     * @param UIOutput
     */
    private void createRichBean(final UIOutput UIOutput) throws FileNotFoundException {
        JavaSourceFacet source = project.getFacet(JavaSourceFacet.class);
        DirectoryResource sourceRoot = source.getBasePackageDirectory();
        FileResource<?> indexPage = (FileResource<?>) sourceRoot.getChild("RichBean.java");
        InputStream stream = RichFacesPlugin.class.getResourceAsStream("/org/richfaces/forge/RichBean.java.txt");
        JavaSource javaSource = Roaster.parse(JavaSource.class, stream);
            //JavaParser.parse(stream);
        String pacakgename = project.getFacet(MetadataFacet.class).getProjectGroupName();
        javaSource.setPackage(pacakgename);
        source.saveJavaSource(javaSource);
        UIOutput.out().println(/*ShellColor.YELLOW, */String.format(RichFacesFacet.SUCCESS_MSG_FMT, "RichBean", "class"));
    }
}