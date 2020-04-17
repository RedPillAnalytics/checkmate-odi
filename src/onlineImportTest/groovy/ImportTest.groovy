import groovy.util.logging.Slf4j
import org.gradle.testkit.runner.GradleRunner
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise
import spock.lang.Title

@Slf4j
@Stepwise
@Title("Execute ODI import tasks")
class ImportTest extends Specification {

   @Shared
   File projectDir, buildDir, buildFile, resourcesDir, settingsFile

   @Shared
   String taskName, odiPassword, masterUrl, masterPassword

   @Shared
   def result

   @Shared
   AntBuilder ant = new AntBuilder()

   def setup() {

      projectDir = new File("${System.getProperty("projectDir")}/JUMP")
      buildDir = new File(projectDir, 'build')
      odiPassword = System.getProperty("odiPassword")
      masterPassword = System.getProperty("masterPassword")
      masterUrl = System.getProperty("masterUrl")

      resourcesDir = new File('src/test/resources')

      ant.delete(dir: projectDir)

      ant.copy(todir: projectDir) {
         fileset(dir: resourcesDir)
      }

      settingsFile = new File(projectDir, 'settings.gradle').write("""rootProject.name = 'JUMP'""")

      buildFile = new File(projectDir, 'build.gradle').write("""
            |plugins {
            |    id 'com.redpillanalytics.checkmate.odi'
            |}
            |
            |odi {
            |   masterUrl = '$masterUrl'
            |   masterPassword = '$masterPassword'
            |   odiPassword = '$odiPassword'
            |   projectName = 'JUMP'
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

   def "Execute :import task with defaults"() {
      given:
      taskName = 'import'
      result = executeSingleTask(taskName, ['-Si'])

      expect:
      result.task(":${taskName}").outcome.name() != 'FAILED'
   }

   def "Execute :importProjectDir task with defaults"() {
      given:
      taskName = 'importProjectDir'
      result = executeSingleTask(taskName, ['-Si'])

      expect:
      result.task(":${taskName}").outcome.name() != 'FAILED'
   }

   def "Execute :importProjectDir task with --source-dir option"() {
      given:
      taskName = 'importProjectDir'
      result = executeSingleTask(taskName, ['--source-dir=EDW Loads', '-Si'])

      expect:
      result.task(":${taskName}").outcome.name() != 'FAILED'
   }

   def "Execute :importModelDir task with defaults"() {
      given:
      taskName = 'importModelDir'
      result = executeSingleTask(taskName, ['-Si'])

      expect:
      result.task(":${taskName}").outcome.name() != 'FAILED'
   }

   def "Execute :importLoadPlanDir task with defaults"() {
      given:
      taskName = 'importLoadPlanDir'
      result = executeSingleTask(taskName, ['-Si'])

      expect:
      result.task(":${taskName}").outcome.name() != 'FAILED'
   }

   def "Execute :importScenarioDir task with defaults"() {
      given:
      taskName = 'importScenarioDir'
      result = executeSingleTask(taskName, ['-Si'])

      expect:
      result.task(":${taskName}").outcome.name() != 'FAILED'
   }

   def "Execute :importGlobalDir task with defaults"() {
      given:
      taskName = 'importGlobalDir'
      result = executeSingleTask(taskName, ['-Si'])

      expect:
      result.task(":${taskName}").outcome.name() != 'FAILED'
   }

   def "Execute :importTopologyDir task with defaults"() {
      given:
      taskName = 'importTopologyDir'
      result = executeSingleTask(taskName, ['-Si'])

      expect:
      result.task(":${taskName}").outcome.name() != 'FAILED'
   }

   def "Execute :importProjectFile task with defaults"() {
      given:
      taskName = 'importProjectFile'
      result = executeSingleTask(taskName, ['-Si'])

      expect:
      result.task(":${taskName}").outcome.name() != 'FAILED'
   }

   def "Execute :importProjectFile task with --source-file option"() {
      given:
      taskName = 'importProjectFile'
      result = executeSingleTask(taskName, ['--source-file=src/main/file/FILE_JUMP.xml', '-Si'])

      expect:
      result.task(":${taskName}").outcome.name() != 'FAILED'
   }

}
