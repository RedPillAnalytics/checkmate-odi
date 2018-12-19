

import groovy.util.logging.Slf4j
import org.gradle.testkit.runner.GradleRunner
import org.junit.ClassRule
import org.junit.rules.TemporaryFolder
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Title
import spock.lang.Unroll

@Slf4j
@Title("Execute tasks using --dry-run")
class DryRunTest extends Specification {

   @ClassRule
   @Shared
   TemporaryFolder testProjectDir = new TemporaryFolder()

   @Shared buildFile
   @Shared result
   @Shared indexedResultOutput

   // run the Gradle build
   // return regular output
   def setupSpec() {

      buildFile = testProjectDir.newFile('build.gradle')
      buildFile.write("""
            |plugins {
            |    id 'com.redpillanalytics.checkmate.odi'
            |}
            |
            |odi {
            |   projectName = 'test-project'
            |}
            |""".stripMargin())

      result = GradleRunner.create()
              .withProjectDir(testProjectDir.root)
              .withArguments('-Sim', 'build')
              .withPluginClasspath()
              .build()

      indexedResultOutput = result.output.readLines()

      log.warn result.output
   }

   @Unroll
   def "a dry run configuration contains :#task"() {

      given: "a dry run task"

      expect:
      result.output.contains(":$task")

      where:
      task << ['build']
   }

}
