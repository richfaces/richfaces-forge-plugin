package org.richfaces.forge;

import java.util.Arrays;
import java.util.List;

import org.jboss.forge.addon.dependencies.Dependency;
import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;

/**
 * @author bleathem
 */
public enum RichFacesVersion {

    RICHFACES_4_5_0("RichFaces 4.5.0.CR1", Arrays.asList(DependencyBuilder.create("org.richfaces:richfaces:4.5.0.CR1")), Arrays
        .asList(DependencyBuilder.create("org.richfaces:richfaces:4.5.0.CR1"))

    ),

    RICHFACES_4_3_2("RichFaces 4.3.2.Final", Arrays.asList(
        DependencyBuilder.create("org.richfaces.ui:richfaces-components-ui:4.3.2.Final"),
        DependencyBuilder.create("org.richfaces.core:richfaces-core-impl:4.3.2.Final")), Arrays.asList(DependencyBuilder
        .create("org.richfaces:richfaces-bom:4.3.2.Final").setScopeType("import").setPackaging("pom"))),
    RICHFACES_4_2_2("RichFaces 4.2.2.Final", Arrays.asList(
        DependencyBuilder.create("org.richfaces.ui:richfaces-components-ui:4.2.2.Final"),
        DependencyBuilder.create("org.richfaces.core:richfaces-core-impl:4.2.2.Final")), Arrays.asList(DependencyBuilder
        .create("org.richfaces:richfaces-bom:4.2.2.Final").setScopeType("import").setPackaging("pom")));

    private List<? extends Dependency> dependencies;
    private List<? extends Dependency> dependencyManagement;
    private String name;

    private RichFacesVersion(String name, List<? extends Dependency> deps, List<? extends Dependency> depManagement) {
        this.name = name;
        this.dependencies = deps;
        this.dependencyManagement = depManagement;
    }

    public List<? extends Dependency> getDependencies() {
        return dependencies;
    }

    public List<? extends Dependency> getDependencyManagement() {
        return dependencyManagement;
    }

    @Override
    public String toString() {
        return name;
    }
}
