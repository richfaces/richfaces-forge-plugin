package org.richfaces.forge;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.jboss.seam.forge.project.dependencies.Dependency;
import org.jboss.seam.forge.project.dependencies.DependencyBuilder;
import org.jboss.seam.forge.project.dependencies.ScopeType;

/**
 *
 * @author bleathem
 */
public enum RichFacesVersion {

    RICHFACES_4_0_0 ("RichFaces 4.0.0.Final",
        Arrays.asList (
            DependencyBuilder.create("org.richfaces.ui:richfaces-components-ui:4.0.0.Final"),
            DependencyBuilder.create("org.richfaces.core:richfaces-core-impl:4.0.0.Final"),
            DependencyBuilder.create("javax.servlet:servlet-api").setScopeType(ScopeType.PROVIDED),
            DependencyBuilder.create("javax.servlet.jsp:jsp-api").setScopeType(ScopeType.PROVIDED),
            DependencyBuilder.create("javax.servlet:jstl").setScopeType(ScopeType.PROVIDED),
            DependencyBuilder.create("net.sf.ehcache:ehcache")
        ),
        Arrays.asList (
            DependencyBuilder.create("org.richfaces:richfaces-bom:4.0.0.Final").setScopeType(ScopeType.IMPORT).setPackagingType("pom")
        )
    );
    
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
