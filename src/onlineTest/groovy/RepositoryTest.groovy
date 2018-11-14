import groovy.util.logging.Slf4j
import org.gradle.testkit.runner.GradleRunner
import org.junit.ClassRule
import org.testcontainers.containers.GenericContainer

import org.testcontainers.containers.wait.strategy.Wait
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Title

@Slf4j
@Title("Execute :tasks")
class RepositoryTest extends Specification {

   @ClassRule
   @Shared
   GenericContainer oracle = new GenericContainer('redpillanalytics/oracle-xe:11.2.0-1.0')
           .withExposedPorts(1521)
           .waitingFor(Wait.forLogMessage('oracle-xe available\\.\n',1))

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
            |   masterUrl = 'jdbc:oracle:thin:@${oracle.getContainerIpAddress()}:${oracle.getMappedPort(1521)}/xe'
            |   masterPassword = 'oracle'
            |   odiPassword = 'oracle'
            |   dbaUser = 'system'
            |   dbaPassword = 'oracle'
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

   def "Execute :createRepository"() {

      given:
      taskName = 'createRepository'
      result = executeSingleTask(taskName, ['clean', '-Si'])

      expect:
      result.task(":${taskName}").outcome.name() != 'FAILED'

   }

}
