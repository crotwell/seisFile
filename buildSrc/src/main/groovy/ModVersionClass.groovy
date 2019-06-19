
import org.gradle.api.Project
import org.gradle.api.Plugin
import org.gradle.api.plugins.JavaPlugin
import java.util.Date;

class ModVersionClass extends VersionClass  {
        
        ModVersionClass() {
            super()
        }

        def String getVersionString(Project project) {
            return super.getVersionString(project)+"_localmod"
        }
        
        def getGenSrc() {
            return 'generated-src/modVersion'
        }
        
        def String taskName() {
            return 'modVersionClass'
        }
        
        def void addTaskDependency(Project project) {
//            project.getTasks().getByName('wrapper') {
//               dependsOn taskName()
//            }
        }
}
