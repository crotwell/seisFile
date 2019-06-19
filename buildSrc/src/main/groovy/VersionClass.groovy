
import org.gradle.api.Project
import org.gradle.api.Plugin
import org.gradle.api.plugins.JavaPlugin
import java.util.Date;
import java.text.SimpleDateFormat;

class VersionClass implements Plugin<Project>  {
        
        VersionClass() {
        }

        def String getVersionString(Project project) {
            if (project.version.endsWith("-SNAPSHOT")) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HHmmss")
                return project.version+"_"+sdf.format(new Date())
            }
            return project.version
        }

        def getGenSrc() {
            return 'generated-src/version'
        }

        def getGenSrcDir(Project project) {
            return new File(project.buildDir, getGenSrc())
        }

        def String taskName() {
            return 'makeVersionClass'
        }
        
        def void apply(Project project) {
            project.getPlugins().apply( JavaPlugin.class )
            def generatedSrcDir = getGenSrcDir(project)
                
            def makeVersionClassTask = project.task(taskName()) {
              doLast {
                def now = new Date()
                def outFilename = "java/"+project.group.replace('.','/')+"/"+project.name.replace('-','/')+"/BuildVersion.java"
                def outFile = new File(generatedSrcDir, outFilename)
                outFile.getParentFile().mkdirs()
                def f = new FileWriter(outFile)
                f.write('package  '+project.group+"."+project.name.replace('-','.')+';\n')
                f.write("""
/**
 * Simple class for storing the version derived from the gradle build.gradle file.
 * 
 */ 
public class BuildVersion {

    private static final String version = \""""+getVersionString(project)+"""\";
    private static final String name = \""""+project.name+"""\";
    private static final String group = \""""+project.group+"""\";
    private static final String date = \""""+now+"""\";

    /** returns the version of the project from the gradle build.gradle file. */
    public static String getVersion() {
        return version;
    }
    /** returns the name of the project from the gradle build.gradle file. */
    public static String getName() {
        return name;
    }
    /** returns the group of the project from the gradle build.gradle file. */
    public static String getGroup() {
        return group;
    }
    /** returns the date this file was generated, usually the last date that the project was modified. */
    public static String getDate() {
        return date;
    }
    public static String getDetailedVersion() {
        return getGroup()+":"+getName()+":"+getVersion()+" "+getDate();
    }
""")
                        
                f.write("}\n") 
                f.close()
            }
            project.sourceSets {
                version {
                    java {
                        srcDir project.buildDir.name+'/'+getGenSrc()+'/java'
                    }
                }
            }
          }
            makeVersionClassTask.ext.generatedSrcDir = generatedSrcDir
            makeVersionClassTask.getInputs().files(project.sourceSets.main.getAllSource() )
            makeVersionClassTask.getInputs().property("project version", { project.version })
            makeVersionClassTask.getOutputs().files(generatedSrcDir)
            if (project.getBuildFile() != null && project.getBuildFile().exists()) {
                makeVersionClassTask.getInputs().files(project.getBuildFile())
            }
            addTaskDependency(project)
        }

        def void addTaskDependency(Project project) {
            project.getTasks().getByName('compileJava') {
               dependsOn taskName()
               source += project.fileTree(dir:new File(project.buildDir, getGenSrc()))
            }
        }
}
