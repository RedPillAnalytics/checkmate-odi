import groovy.util.logging.Slf4j
import org.gradle.testkit.runner.GradleRunner
import org.junit.ClassRule
import org.junit.rules.TemporaryFolder
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Title
import spock.lang.Unroll

@Slf4j
@Title("Execute :tasks")
class ExportProjectFolderTest extends Specification {

   @Shared
   File projectDir, buildDir, buildFile

   @Shared
   def result, taskList

   def setupSpec() {

      projectDir = new File("${System.getProperty("projectDir")}/export-folder")
      projectDir.mkdirs()
      buildDir = new File(projectDir, 'build')
      buildFile = new File(projectDir, 'build.gradle')
      taskList = ['exportProjectFolder']

//      resourcesDir = new File('src/test/resources')
//
//      new AntBuilder().copy(todir: projectDir) {
//         fileset(dir: resourcesDir)
//      }

      buildFile.write("""
            plugins {
                id 'com.redpillanalytics.checkmate.odi'
            }
            
            odi {
               masterUrl = "jdbc:oracle:thin:@odi-repo.csagf46svk9g.us-east-2.rds.amazonaws.com:1521/ORCL"
               masterPassword = 'Welcome1'
               odiPassword = 'Welcome1'
            }
        """)
   }

   def "Executing :tasks contains :#task"() {

      given:

      result = GradleRunner.create()
              .withProjectDir(projectDir)
              .withArguments('-Si', 'exportProjectFolder')
              .withPluginClasspath()
              .build()

      log.warn result.getOutput()

      expect:
      ['SUCCESS', 'UP_TO_DATE', 'SKIPPED'].contains(result.task(":exportProjectFolder").outcome.toString())
   }

}
