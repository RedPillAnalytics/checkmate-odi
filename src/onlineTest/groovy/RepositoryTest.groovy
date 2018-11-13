import groovy.util.logging.Slf4j
import org.gradle.testkit.runner.GradleRunner
import org.junit.ClassRule
import org.junit.Rule
import org.spockframework.runtime.extension.builtin.ClassRuleExtension
import org.testcontainers.Testcontainers
import org.testcontainers.containers.OracleContainer
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Title

@Slf4j
@Title("Execute :tasks")
class RepositoryTest extends Specification {

   @ClassRule
   @Shared
   OracleContainer oracle = new OracleContainer()

   @Shared
   File projectDir, buildDir, buildFile, resourcesDir

   @Shared
   String taskName

   @Shared
   def result

   def setupSpec() {

      projectDir = new File("${System.getProperty("projectDir")}/project-test")
      buildDir = new File(projectDir, 'build')
      buildFile = new File(projectDir, 'build.gradle')

      resourcesDir = new File('src/test/resources')

      buildFile.write("""
            plugins {
                id 'com.redpillanalytics.checkmate.odi'
            }
            
            odi {
               masterUrl = '${oracle.getJdbcUrl()}'
               masterPassword = 'Admin123'
               odiPassword = 'Admin123'
               dbaUser = 'system'
               dbaPassword = 'oracle'
            }
        """)
   }

   def setup() {

      projectDir.delete()

      new AntBuilder().copy(todir: projectDir) {
         fileset(dir: resourcesDir)
      }
   }

   // helper method
   def executeSingleTask(String taskName, List otherArgs, Boolean logOutput = true) {

      otherArgs.add(0, taskName)

      log.warn "runner arguments: ${otherArgs.toString()}"

      // execute the Gradle test build
      result = GradleRunner.create()
              .withProjectDir(projectDir)
              .withArguments(otherArgs)
              .withPluginClasspath()
              .build()

      // log the results
      if (logOutput) log.warn result.getOutput()

      return result

   }

   def "Execute :createRepository"() {

      given:
      taskName = 'createRepository'
      result = executeSingleTask(taskName, ['clean', '-Si'])

      expect:
      result.task(":${taskName}").outcome.name() != 'FAILED'

   }

}
