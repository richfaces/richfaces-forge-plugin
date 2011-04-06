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
    ),
    RICHFACES_3_3_3 ("RichFaces 3.3.3.Final", 
        Arrays.asList (
            DependencyBuilder.create("org.richfaces.ui:richfaces-ui:3.3.3.Final"),
            DependencyBuilder.create("org.richfaces.framework:richfaces-impl:3.3.3.Final"),
            DependencyBuilder.create("org.richfaces.framework:richfaces-api:3.3.3.Final"),
            DependencyBuilder.create("javax.servlet:servlet-api:2.4").setScopeType(ScopeType.PROVIDED),
            DependencyBuilder.create("javax.servlet:jsp-api:2.0").setScopeType(ScopeType.PROVIDED),
            DependencyBuilder.create("javax.servlet:jstl:1.1.2").setScopeType(ScopeType.PROVIDED),
            DependencyBuilder.create("javax.servlet.jsp:jsp-api:2.1").setScopeType(ScopeType.PROVIDED),
            DependencyBuilder.create("javax.faces:jsf-api:1.2_12").setScopeType(ScopeType.PROVIDED),
            DependencyBuilder.create("javax.faces:jsf-impl:1.2_12").setScopeType(ScopeType.PROVIDED),
            DependencyBuilder.create("javax.el:el-api:1.0").setScopeType(ScopeType.PROVIDED),
            DependencyBuilder.create("el-impl:el-api:1.0").setScopeType(ScopeType.PROVIDED),
            DependencyBuilder.create("javax.annotation:jsr250-api:1.0").setScopeType(ScopeType.PROVIDED)
        ),
        Collections.EMPTY_LIST
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
