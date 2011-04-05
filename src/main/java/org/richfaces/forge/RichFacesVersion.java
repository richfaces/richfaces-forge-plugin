package org.richfaces.forge;

import org.jboss.seam.forge.project.dependencies.Dependency;
import org.jboss.seam.forge.project.dependencies.DependencyBuilder;

/**
 *
 * @author bleathem
 */
public enum RichFacesVersion {

    RICHFACES_4_0_0("RichFaces 4.0.0", DependencyBuilder.create("org.richfaces.ui:richfaces-components-ui:4.0.0.Final"));
    
    private Dependency dependency;
    private String name;

    private RichFacesVersion(String name, Dependency dep) {
        this.name = name;
        this.dependency = dep;
    }

    public Dependency getDependency() {
        return dependency;
    }

    @Override
    public String toString() {
        return name;
    }
}
