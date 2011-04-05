package org.richfaces.forge;

import java.util.Arrays;
import java.util.List;
import org.jboss.seam.forge.project.dependencies.Dependency;
import org.jboss.seam.forge.project.dependencies.DependencyBuilder;
import org.jboss.seam.forge.project.dependencies.ScopeType;

/**
 *
 * @author bleathem
 */
public enum RichFacesVersion {

    RICHFACES_4_0_0("RichFaces 4.0.0.Final", Arrays.asList(
        DependencyBuilder.create("org.richfaces.ui:richfaces-components-ui:4.0.0.Final"),
        DependencyBuilder.create("org.richfaces.ui:richfaces-components-ui:4.0.0.Final"),
        DependencyBuilder.create("org.richfaces.core:richfaces-core-impl:4.0.0.Final"),
        DependencyBuilder.create("javax.servlet:servlet-api").setScopeType(ScopeType.PROVIDED),
        DependencyBuilder.create("javax.servlet.jsp:jsp-api").setScopeType(ScopeType.PROVIDED),
        DependencyBuilder.create("javax.servlet:jstl").setScopeType(ScopeType.PROVIDED),
        DependencyBuilder.create("net.sf.ehcache:ehcache")));
    private List<? extends Dependency> dependencies;
    private String name;

    private RichFacesVersion(String name, List<? extends Dependency> deps) {
        this.name = name;
        this.dependencies = deps;
    }

    public List<? extends Dependency> getDependencies() {
        return dependencies;
    }

    @Override
    public String toString() {
        return name;
    }
}
