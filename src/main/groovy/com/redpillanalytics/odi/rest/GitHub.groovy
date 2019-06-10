package com.redpillanalytics.odi.rest

import com.mashape.unirest.http.HttpResponse
import com.mashape.unirest.http.Unirest
import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j

@Slf4j
class GitHub {

   /**
    * The base REST endpoint for the GitHub API.
    */
   String baseUrl = 'https://api.github.com/repos'
   /**
    * The owner of the GitHub repo.
    */
   String owner
   /**
    * The GitHub repository to use.
    */
   String repo

   /**
    * Get the GitHub repository URL
    *
    * @return GitHub repository URL
    */
   String getRepoUrl() {
      return "${baseUrl}/${owner}/${repo}"
   }

   /**
    * Get a JSON payload describing the first matching asset returned from the latest release.
    *
    * @param pattern A regex to use for matching assets.
    *
    * @param version The release version to get. Defaults to 'latest'.
    *
    * @return JSON payload describing the first matching asset returned from the latest release.
    */
   def getAsset(String pattern = /.+/, String version = 'latest') {
      String url = "${repoUrl}/releases/$version"
      log.debug "url: $url"
      HttpResponse<String> response = Unirest.get(url)
              .asString()
      log.debug "response: ${response.dump()}"

      def body = new JsonSlurper().parseText(response.body)
      log.debug "body: $body"

      def asset = body.assets.find { it.name =~ pattern }
      log.debug "asset: $asset"

      return asset
   }

   /**
    * Get a download URL for the first matching asset returned from the release.
    *
    * @param pattern A regex to use for matching assets.
    *
    * @param version The release version to get. Defaults to 'latest'.
    *
    * @return Download URL for the first matching asset returned from the release.
    */
   def getAssetUrl(String pattern = /.+/, String version = 'latest') {

      return getAsset(pattern, version).browser_download_url
   }
}
