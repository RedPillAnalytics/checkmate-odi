package com.redpillanalytics.odi.rest

import com.mashape.unirest.http.HttpResponse
import com.mashape.unirest.http.Unirest
import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j

import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

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
    * @return JSON payload describing the first matching asset returned from the latest release.
    */
   def getLatestAsset(String pattern = /.+/) {
      String url = "${repoUrl}/releases/latest"
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
    * Get a download URL for the first matching asset returned from the latest release.
    *
    * @param pattern A regex to use for matching assets.
    *
    * @return Download URL for the first matching asset returned from the latest release.
    */
   def getLatestAssetUrl(String pattern = /.+/) {

      return getLatestAsset(/(odi-api)(.+)(\.zip)/).browser_download_url
   }
}
