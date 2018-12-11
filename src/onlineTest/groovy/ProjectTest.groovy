import groovy.util.logging.Slf4j
import org.gradle.testkit.runner.GradleRunner
import spock.lang.Ignore
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise
import spock.lang.Title

@Slf4j
@Stepwise
@Title("Execute ODI export and import tasks")
class ProjectTest extends Specification {

   @Shared
   File projectDir, buildDir, buildFile, resourcesDir

   @Shared
   String taskName

   @Shared
   def result

   @Shared
   AntBuilder ant = new AntBuilder()

   def setup() {

      projectDir = new File("${System.getProperty("projectDir")}/project-test")
      buildDir = new File(projectDir, 'build')
      buildFile = new File(projectDir, 'build.gradle')

      resourcesDir = new File('src/test/resources')

      ant.delete(dir: projectDir)

      ant.copy(todir: projectDir) {
         fileset(dir: resourcesDir)
      }

      buildFile.write("""
            |plugins {
            |    id 'com.redpillanalytics.checkmate.odi'
            |}
            |
            |odi {
            |   masterUrl = "jdbc:oracle:thin:@odi-repo.csagf46svk9g.us-east-2.rds.amazonaws.com:1521/ORCL"
            |   masterPassword = 'Welcome1'
            |   odiPassword = 'Welcome1'
            |}
        |""".stripMargin())
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

   def "Execute :smartImportFile task with defaults"() {
      given:
      taskName = 'smartImportFile'
      result = executeSingleTask(taskName, ['-Si'])

      expect:
      result.task(":${taskName}").outcome.name() != 'FAILED'
   }

   def "Execute :smartImportFile task with --source-file option"() {
      given:
      taskName = 'smartImportFile'
      result = executeSingleTask(taskName, ['--source-file=src/main/odi/PROJECT-TEST.xml', '-Si'])

      expect:
      result.task(":${taskName}").outcome.name() != 'FAILED'
   }

   def "Execute :smartImportDir task with defaults"() {
      given:
      taskName = 'smartImportDir'
      result = executeSingleTask(taskName, ['-Si'])

      expect:
      result.task(":${taskName}").outcome.name() != 'FAILED'
   }

//   def "Execute :importObject task with --source-path value"() {
//
//      given:
//      taskName = 'importObject'
//      result = executeSingleTask(taskName, ['--source-path=src/main/odi/MAP_TEST_MAPPING.xml', '-Si'])
//
//      expect:
//      result.task(":${taskName}").outcome.name() != 'FAILED'
//
//   }
//
//   def "Execute :importAllObjectsXML task"() {
//
//      given:
//      taskName = 'importAllObjectsXML'
//      result = executeSingleTask(taskName, ['-Si'])
//
//      expect:
//      result.task(":${taskName}").outcome.name() != 'FAILED'
//
//   }
//
//   def "Execute :importObjectXML task with default values"() {
//
//      given:
//      taskName = 'importObjectXML'
//      result = executeSingleTask(taskName, ['-Si'])
//
//      expect:
//      result.task(":${taskName}").outcome.name() != 'FAILED'
//
//   }
//
//   def "Execute :importObjectXML task with --import-path value"() {
//
//      given:
//      taskName = 'importObjectXML'
//      result = executeSingleTask(taskName, ['--import-path=src/main/odi/project-test.xml', '-Si'])
//
//      expect:
//      result.task(":${taskName}").outcome.name() != 'FAILED'
//
//   }

//   def "Execute :exportProject task"() {
//
//      given:
//      taskName = 'exportProject'
//      result = executeSingleTask(taskName, ['-Si'])
//
//      expect:
//      result.task(":${taskName}").outcome.name() != 'FAILED'
//
//   }
//
//   def "Execute :exportProjectObjects task"() {
//
//      given:
//      taskName = 'exportProjectObjects'
//      result = executeSingleTask(taskName, ['-Si'])
//
//      expect:
//      result.task(":${taskName}").outcome.name() != 'FAILED'
//
//   }
//
//   @Ignore
//   def "Execute :exportAllProjects task"() {
//
//      given:
//      taskName = 'exportAllProjects'
//      result = executeSingleTask(taskName, ['-Si'])
//
//      expect:
//      result.task(":${taskName}").outcome.name() != 'FAILED'
//
//   }
//
//   def "Execute :exportProjectFolder task"() {
//
//      given:
//      taskName = 'exportProjectFolder'
//      result = executeSingleTask(taskName, ['--project-code=JUMP', '--folder-name=Source Loads', '-Si'])
//
//      expect:
//      result.task(":${taskName}").outcome.name() != 'FAILED'
//
//   }
//
//   def "Execute :deleteProject task"() {
//
//      given:
//      taskName = 'deleteProject'
//      result = executeSingleTask(taskName, ['-Si'])
//
//      expect:
//      result.task(":${taskName}").outcome.name() != 'FAILED'
//
//   }
//
//   def "Execute :getProjects task"() {
//
//      given:
//      taskName = 'getProjects'
//      result = executeSingleTask(taskName, ['-Si'])
//
//      expect:
//      result.task(":${taskName}").outcome.name() != 'FAILED'
//
//   }
//
//   def "Execute :getModels task"() {
//
//      given:
//      taskName = 'getModels'
//      result = executeSingleTask(taskName, ['-Si'])
//
//      expect:
//      result.task(":${taskName}").outcome.name() != 'FAILED'
//
//   }
//
//   def "Execute :exportModelFolder task"() {
//
//      given:
//      taskName = 'exportModelFolder'
//      result = executeSingleTask(taskName, ['--folder-name=FlatFilesHR', '-Si'])
//
//      expect:
//      result.task(":${taskName}").outcome.name() != 'FAILED'
//
//   }
//
//   def "Execute :exportModel task with --model-code value"() {
//
//      given:
//      taskName = 'exportModel'
//      result = executeSingleTask(taskName, ['--model-code=FF_HR', '-Si'])
//
//      expect:
//      result.task(":${taskName}").outcome.name() != 'FAILED'
//
//   }
//
//   def "Execute :getLoadPlansAndScenarios task"() {
//
//      given:
//      taskName = 'getLoadPlansAndScenarios'
//      result = executeSingleTask(taskName, ['-Si'])
//
//      expect:
//      result.task(":${taskName}").outcome.name() != 'FAILED'
//
//   }
//
//   def "Execute :exportLoadPlansAndScenarios task"() {
//
//      given:
//      taskName = 'exportLoadPlansAndScenarios'
//      result = executeSingleTask(taskName, ['--folder-name=TEST_FOLDER', '-Si'])
//
//      expect:
//      result.task(":${taskName}").outcome.name() != 'FAILED'
//
//   }
}
