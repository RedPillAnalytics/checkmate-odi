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
   File projectDir, buildDir, buildFile, resourcesDir

   @Shared
   String taskName, odiPassword, odiUrl

   @Shared
   def result

   @Shared
   AntBuilder ant = new AntBuilder()

   def setup() {

      projectDir = new File("${System.getProperty("projectDir")}/project-test")
      buildDir = new File(projectDir, 'build')
      buildFile = new File(projectDir, 'build.gradle')
      odiPassword = System.getProperty("odiPassword")
      odiUrl = System.getProperty("odiUrl")

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
            |   masterUrl = '$odiUrl'
            |   masterPassword = '$odiPassword'
            |   odiPassword = '$odiPassword'
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
      result = executeSingleTask(taskName, ['--source-file=PROJECT-TEST.xml', '-Si'])

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
      result = executeSingleTask(taskName, ['--source-dir=OTHER_FOLDER', '-Si'])

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

   def "Execute :import task with defaults"() {
      given:
      taskName = 'import'
      result = executeSingleTask(taskName, ['-Si'])

      expect:
      result.task(":${taskName}").outcome.name() != 'FAILED'
   }
}
