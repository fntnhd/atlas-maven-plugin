package org.atlas.client.maven;

import java.util.ArrayList;
import java.util.List;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.atlas.engine.ConfigurationLoader;
import org.atlas.engine.Context;
import org.atlas.engine.ModelTransformer;

/**
 * @goal generate
 */
public class GenerateSourcesMojo extends AbstractMojo {


    /**
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    public void execute() throws MojoExecutionException {
        getLog().info("Executing Atlas Engine...");
        try {
            Context.reset();
            Context.setRootFolder(project.getParent().getBasedir().getAbsolutePath());
            ConfigurationLoader.setConfigLocation(project.getBasedir().getAbsolutePath() + "/src/main/resources/atlas");
            ModelTransformer transformer = new ModelTransformer();
            transformer.transform();

            for (String path : Context.getOutputPaths()) {
                if (path.contains("test")) {
                    List<String> compileSources = new ArrayList<String>();
                    for (Object f : this.project.getTestCompileSourceRoots()) {
                        compileSources.add(((String) f).toLowerCase());
                    }
                    if (!compileSources.contains(path.toLowerCase())) {
                        getLog().info("Adding test source folder: " + path);
                        this.project.addTestCompileSourceRoot(path);
                    }
                }
                else {
                    List<String> compileSources = new ArrayList<String>();
                    for (Object f : this.project.getCompileSourceRoots()) {
                        compileSources.add(((String) f).toLowerCase());
                    }
                    if (!compileSources.contains(path.toLowerCase())) {
                        getLog().info("Adding source folder: " + path);
                        this.project.addCompileSourceRoot(path);
                    }
                }
            }
            
            for (Object f : this.project.getCompileSourceRoots()) {
                getLog().info((String) f);
            }


        } catch (Exception e) {
            getLog().error(e);
            throw new MojoExecutionException("Atlas Engine failed.", e);
        }
    }
}

