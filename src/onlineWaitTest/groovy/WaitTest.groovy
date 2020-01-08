import groovy.util.logging.Slf4j
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise
import spock.lang.Title

@Slf4j
@Stepwise
@Title("Execute Agent wait task")
class WaitTest extends Specification {

   @Shared
   File projectDir, buildFile, settingsFile

   @Shared
   String projectName = 'wait-test'

   @Shared
   FileTreeBuilder projectTree

   @Shared
   BuildResult result

   @Shared
   String taskName, odiPassword, masterUrl, masterPassword

   def setupSpec() {

      odiPassword = System.getProperty("odiPassword")
      masterPassword = System.getProperty("masterPassword")
      masterUrl = System.getProperty("masterUrl")

      projectTree = new FileTreeBuilder(new File(System.getProperty("projectBase")))
      projectDir = projectTree.dir(projectName)
      projectDir.deleteDir()
      projectTree.dir(projectName) {
         file('build.gradle', """
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
         file('settings.gradle', """rootProject.name = '$projectName'""")
      }
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

   def "Execute :waitForAgent task with defaults"() {
      given:
      taskName = 'waitForAgent'
      result = executeSingleTask(taskName, ['-Si'])

      expect:
      !result.tasks.collect { it.outcome }.contains('FAILURE')
   }
}