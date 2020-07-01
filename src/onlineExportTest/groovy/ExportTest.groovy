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

      projectDir = new File("${System.getProperty("projectDir")}/JUMP")
      buildDir = new File(projectDir, 'build')
      odiPassword = System.getProperty("odiPassword")
      masterPassword = System.getProperty("masterPassword")
      masterUrl = System.getProperty("masterUrl")

      resourcesDir = new File('src/test/resources')

      ant.delete(dir: projectDir)
      ant.mkdir(dir: projectDir)

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

      def args = [taskName] + otherArgs

      log.warn "runner arguments: ${args.toString()}"

      // execute the Gradle test build
      result = GradleRunner.create()
              .withProjectDir(projectDir)
              .withArguments(args)
              .withPluginClasspath()
              .build()

      // log the results
      if (logOutput) log.warn result.getOutput()

      return result
   }

   def "Execute :export task with defaults"() {
      given:
      taskName = 'export'
      result = executeSingleTask(taskName, ['-Si'])

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

   def "Execute :exportProjectDir task for EDW Loads folder"() {
      given:
      taskName = 'exportProjectDir'
      result = executeSingleTask(taskName, ['--folder-name=EDW Loads', '-Si'])

      expect:
      result.task(":${taskName}").outcome.name() != 'FAILED'
   }

   def "Execute :exportProjectDir task for only procedures and packages"() {
      given:
      taskName = 'exportProjectDir'
      result = executeSingleTask(taskName, ['--object-type=package', '--object-type=procedure', '-Si'])

      expect:
      result.task(":${taskName}").outcome.name() != 'FAILED'
   }

   def "Execute :exportProjectDir task for EDW Loads folder and only procedures and packages"() {
      given:
      taskName = 'exportProjectDir'
      result = executeSingleTask(taskName, ['--folder-name=EDW Loads', '--object-type=package', '--object-type=procedure', '-Si'])

      expect:
      result.task(":${taskName}").outcome.name() != 'FAILED'
   }

   def "Execute :exportProjectDir task for objects Load ACCOUNT_ACTIVITY_F and Load COMPANY_D"() {
      given:
      taskName = 'exportProjectDir'
      result = executeSingleTask(taskName, ['--object-name=Load ACCOUNT_ACTIVITY_F', '--object-name=Load COMPANY_D', '-Si'])

      expect:
      result.task(":${taskName}").outcome.name() != 'FAILED'
   }

   def "Execute :exportModelDir task with defaults"() {
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

   def "Execute :exportLoadPlanDir task with defaults"() {
      given:
      taskName = 'exportLoadPlanDir'
      result = executeSingleTask(taskName, ['-Si'])

      expect:
      result.task(":${taskName}").outcome.name() != 'FAILED'
   }

   def "Execute :exportLoadPlanDir task with --load-plan option"() {
      given:
      taskName = 'exportLoadPlanDir'
      result = executeSingleTask(taskName, ['-Si', '--load-plan=LOAD_EDW'])

      expect:
      result.task(":${taskName}").outcome.name() != 'FAILED'
   }

   def "Execute :exportScenarioDir task with defaults"() {
      given:
      taskName = 'exportScenarioDir'
      result = executeSingleTask(taskName, ['-Si'])

      expect:
      result.task(":${taskName}").outcome.name() != 'FAILED'
   }

   def "Execute :exportScenarioDir task with --scenario-name option"() {
      given:
      taskName = 'exportScenarioDir'
      result = executeSingleTask(taskName, ['-Si', '--scenario-name=LOAD_F'])

      expect:
      result.task(":${taskName}").outcome.name() != 'FAILED'
   }

   def "Execute :exportScenarioDir task with --scenario-name and --scenario-folder option"() {
      given:
      taskName = 'exportScenarioDir'
      result = executeSingleTask(taskName, ['-Si','--scenario-folder=EDW', '--scenario-name=LOAD_D'])

      expect:
      result.task(":${taskName}").outcome.name() != 'FAILED'
   }

   def "Execute :exportScenarioDir task with --scenario-name and --scenario-version option"() {
      given:
      taskName = 'exportScenarioDir'
      result = executeSingleTask(taskName, ['-Si','--scenario-name=LOAD_D', '--scenario-version=001'])

      expect:
      result.task(":${taskName}").outcome.name() != 'FAILED'
   }

   def "Execute :exportScenarioDir task with --scenario-folder, --scenario-name and --scenario-version option"() {
      given:
      taskName = 'exportScenarioDir'
      result = executeSingleTask(taskName, ['-Si','--scenario-folder=EDW', '--scenario-name=LOAD_D', '--scenario-version=001'])

      expect:
      result.task(":${taskName}").outcome.name() != 'FAILED'
   }


   def "Execute :exportGlobalDir task with defaults"() {
      given:
      taskName = 'exportGlobalDir'
      result = executeSingleTask(taskName, ['-Si'])

      expect:
      result.task(":${taskName}").outcome.name() != 'FAILED'
   }

   def "Execute :exportTopologyDir task with defaults"() {
      given:
      taskName = 'exportTopologyDir'
      result = executeSingleTask(taskName, ['-Si'])

      expect:
      result.task(":${taskName}").outcome.name() != 'FAILED'
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
      result = executeSingleTask(taskName, ['--source-file=JUMP', '-Si'])

      expect:
      result.task(":${taskName}").outcome.name() != 'FAILED'
   }

}
