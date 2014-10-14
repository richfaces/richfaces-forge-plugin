package org.richfaces.forge;

import java.util.concurrent.Callable;

import javax.inject.Inject;

import org.jboss.forge.addon.facets.FacetFactory;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.dependencies.DependencyInstaller;
import org.jboss.forge.addon.projects.ui.AbstractProjectCommand;
import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Metadata;

public class RichFacesCommand extends AbstractProjectCommand implements UICommand {

    @Inject
    public DependencyInstaller installer;

    @Inject
    private ProjectFactory projectFactory;

    @Inject
    private FacetFactory facetFactory;

    @Inject
    private RichFacesFacet richFacesFacet;

    @Inject
    @WithAttributes(shortName = 'v', label = "RichFaces version", type = InputType.DROPDOWN)
    private UISelectOne<String> richfacesVersion;

    @Override
    public void initializeUI(UIBuilder builder) throws Exception {
        builder.add(richfacesVersion);

        richfacesVersion.setDefaultValue(new Callable<String>() {
           @Override
           public String call() throws Exception {
              return richFacesFacet.getDefaultVersion();
           }
        });
        richfacesVersion.setValueChoices(new Callable<Iterable<String>>() {
           @Override
           public Iterable<String> call() throws Exception {
              return richFacesFacet.getAvailableVersions();
           }
        });
    }

    @Override
    public Result execute(UIExecutionContext context) throws Exception {
        richFacesFacet.setVersion(richfacesVersion.getValue());
        facetFactory.install(getSelectedProject(context), richFacesFacet);
        return Results.success("Installed RichFaces " + richfacesVersion.getValue());
    }

    @Override
    public UICommandMetadata getMetadata(UIContext context) {
        return Metadata.forCommand(getClass()).name("RichFaces: Install");
    }

    @Override
    protected boolean isProjectRequired() {
        return true;
    }

    @Override
    protected ProjectFactory getProjectFactory() {
        return projectFactory;
    }

}
