import groovy.util.logging.Slf4j
import org.gradle.testkit.runner.GradleRunner
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise
import spock.lang.Title

@Slf4j
@Stepwise
@Title("Execute ODI export tasks")
class ExportTest extends Specification {

   @Shared
   File projectDir, buildDir, buildFile, resourcesDir, settingsFile

   @Shared
   String taskName, odiPassword, masterUrl, masterPassword

   @Shared
   def result

   @Shared
   AntBuilder ant = new AntBuilder()

   def setup() {

      projectDir = new File("${System.getProperty("projectDir")}/project-test")
      buildDir = new File(projectDir, 'build')
      buildFile = new File(projectDir, 'build.gradle')
      odiPassword = System.getProperty("odiPassword")
      masterPassword = System.getProperty("masterPassword")
      masterUrl = System.getProperty("masterUrl")

      resourcesDir = new File('src/test/resources')
      settingsFile = new File(projectDir, 'settings.gradle')

      ant.delete(dir: projectDir)
      ant.mkdir(dir: projectDir)

      settingsFile.write("""rootProject.name = 'project-test'""")


      buildFile.write("""
            |plugins {
            |    id 'com.redpillanalytics.checkmate.odi'
            |}
            |
            |odi {
            |   masterUrl = '$masterUrl'
            |   masterPassword = '$masterPassword'
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

   def "Execute :exportProjectFile task with defaults"() {
      given:
      taskName = 'exportProjectFile'
      result = executeSingleTask(taskName, ['-Si'])

      expect:
      result.task(":${taskName}").outcome.name() != 'FAILED'
   }

   def "Execute :exportProjectFile task with --source-file option"() {
      given:
      taskName = 'exportProjectFile'
      result = executeSingleTask(taskName, ['--source-file=NEW-FILE.xml', '-Si'])

      expect:
      result.task(":${taskName}").outcome.name() != 'FAILED'
   }

   def "Execute :exportProjectDir task with defaults"() {
      given:
      taskName = 'exportProjectDir'
      result = executeSingleTask(taskName, ['-Si'])

      expect:
      result.task(":${taskName}").outcome.name() != 'FAILED'
   }

   def "Execute :exportProjectDir task with --folder-name option"() {
      given:
      taskName = 'exportProjectDir'
      result = executeSingleTask(taskName, ['--folder-name=OTHER_FOLDER', '-Si'])

      expect:
      result.task(":${taskName}").outcome.name() != 'FAILED'
   }

   def "Execute :exportModelDir task defaults"() {
      given:
      taskName = 'exportModelDir'
      result = executeSingleTask(taskName, ['-Si'])

      expect:
      result.task(":${taskName}").outcome.name() != 'FAILED'
   }

   def "Execute :exportModelDir task with --model-code option"() {
      given:
      taskName = 'exportModelDir'
      result = executeSingleTask(taskName, ['--model-code=STAGE_AREA', '-Si'])

      expect:
      result.task(":${taskName}").outcome.name() != 'FAILED'
   }

   def "Execute :export task with defaults"() {
      given:
      taskName = 'export'
      result = executeSingleTask(taskName, ['-Si'])

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

//   def "Execute :deleteModels task with --model-code option."() {
//      given:
//      taskName = 'deleteModels'
//      result = executeSingleTask(taskName, ['--model-code=STAGE_AREA','-Si'])
//
//      expect:
//      result.task(":${taskName}").outcome.name() != 'FAILED'
//   }
//
//   def "Execute :deleteModels task with defaults."() {
//      given:
//      taskName = 'deleteModels'
//      result = executeSingleTask(taskName, ['-Si'])
//
//      expect:
//      result.task(":${taskName}").outcome.name() != 'FAILED'
//   }
}
