import com.redpillanalytics.odi.rest.GitHub
import groovy.util.logging.Slf4j
import spock.lang.Shared
import spock.lang.Specification

@Slf4j
class GitHubTest extends Specification{

   @Shared
   def gitHub = new GitHub(owner: 'RedPillAnalytics', repo: 'odi-api')

   def "Latest release of 'odi-api' returned"() {

      when:
      def result = gitHub.getAsset(/(odi-api)(.+)(\.zip)/)

      then:
      log.warn "asset: ${result.toString()}"
      result
   }

   def "Get download URL of latest release of 'odi-api'"() {

      when:
      def url = gitHub.getLatestAssetUrl(/(odi-api)(.+)(\.zip)/)

      then:
      log.warn "url: ${url}"
      url
   }
}
