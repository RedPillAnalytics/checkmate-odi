import groovy.util.logging.Slf4j
import org.gradle.testkit.runner.GradleRunner
import org.junit.ClassRule
import org.testcontainers.containers.OracleContainer
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Title

@Slf4j
@Title("Execute :tasks")
class ProjectTest extends Specification {

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
            |plugins {
            |    id 'com.redpillanalytics.checkmate.odi'
            |}
            |
            |odi {
            |   masterUrl = '${oracle.getJdbcUrl()}'
            |   masterPassword = 'oracle'
            |   odiPassword = 'oracle'
            |}
            |""".stripMargin()
      )
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

   def "Execute :createProject task"() {

      given:
      taskName = 'createProject'
      result = executeSingleTask(taskName, ['clean', '-Si'])

      expect:
      result.task(":${taskName}").outcome.name() != 'FAILED'

   }

   def "Execute :exportProject task"() {

      given:
      taskName = 'exportProject'
      result = executeSingleTask(taskName, ['-Si'])

      expect:
      result.task(":${taskName}").outcome.name() != 'FAILED'

   }

   def "Execute :exportProjectObjects task"() {

      given:
      taskName = 'exportProjectObjects'
      result = executeSingleTask(taskName, ['-Si'])

      expect:
      result.task(":${taskName}").outcome.name() != 'FAILED'

   }

   def "Execute :exportAllProjects task"() {

      given:
      taskName = 'exportAllProjects'
      result = executeSingleTask(taskName, ['-Si'])

      expect:
      result.task(":${taskName}").outcome.name() != 'FAILED'

   }

   def "Execute :exportProjectFolder task"() {

      given:
      taskName = 'exportProjectFolder'
      result = executeSingleTask(taskName, ['--project-code=JUMP', '--folder-name=Source Loads', '-Si'])

      expect:
      result.task(":${taskName}").outcome.name() != 'FAILED'

   }

   def "Execute :importObject task with --import-path value"() {

      given:
      taskName = 'importObject'
      result = executeSingleTask(taskName, ['--import-path=src/main/odi/MOD_FF_HR.xml', '-Si'])

      expect:
      result.task(":${taskName}").outcome.name() != 'FAILED'

   }

   def "Execute :importAllXml task"() {

      given:
      taskName = 'importAllXML'
      result = executeSingleTask(taskName, ['-Si'])

      expect:
      result.task(":${taskName}").outcome.name() != 'FAILED'

   }

   def "Execute :importProject task with default values"() {

      given:
      taskName = 'importProject'
      result = executeSingleTask(taskName, ['-Si'])

      expect:
      result.task(":${taskName}").outcome.name() != 'FAILED'

   }

   def "Execute :importProject task with --import-path value"() {

      given:
      taskName = 'importProject'
      result = executeSingleTask(taskName, ['--import-path=src/main/odi/project-test.xml', '-Si'])

      expect:
      result.task(":${taskName}").outcome.name() != 'FAILED'

   }

   def "Execute :deleteProject task"() {

      given:
      taskName = 'deleteProject'
      result = executeSingleTask(taskName, ['-Si'])

      expect:
      result.task(":${taskName}").outcome.name() != 'FAILED'

   }

   def "Execute :getProjects task"() {

      given:
      taskName = 'getProjects'
      result = executeSingleTask(taskName, ['-Si'])

      expect:
      result.task(":${taskName}").outcome.name() != 'FAILED'

   }

   def "Execute :getModels task"() {

      given:
      taskName = 'getModels'
      result = executeSingleTask(taskName, ['-Si'])

      expect:
      result.task(":${taskName}").outcome.name() != 'FAILED'

   }

   def "Execute :exportModelFolder task"() {

      given:
      taskName = 'exportModelFolder'
      result = executeSingleTask(taskName, ['--folder-name=FlatFilesHR', '-Si'])

      expect:
      result.task(":${taskName}").outcome.name() != 'FAILED'

   }

   def "Execute :exportModel task with --model-code value"() {

      given:
      taskName = 'exportModel'
      result = executeSingleTask(taskName, ['--model-code=FF_HR', '-Si'])

      expect:
      result.task(":${taskName}").outcome.name() != 'FAILED'

   }

}
