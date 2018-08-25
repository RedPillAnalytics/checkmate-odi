package com.redpillanalytics.common

import groovy.util.logging.Slf4j
import org.gradle.api.Project

/**
 * Created by stewartbryson on 11/30/16.
 */
@Slf4j
class GradleUtils {

    static public getParameter(Project project, String name, String extension, String defaultValue = null) {

        // define the value we get along the way
        def value

        def extName = "${extension}.${name}"
        if (project.ext.has(extName)) {

            // give precedence to Gradle properties passed with a dot (.) notation
            // For instance: checkmate.buildId instead of just buildId
            // this will hopefully allow me to gracefully remove the non-dot-notation properties over time

            value = project.ext.get(extName)

        } else if (project.ext.has(name)) {

            // next in order is a non dot-notation parameter
            // I would like to eventually phase this out

            value = project.ext.get(name)

        } else if (BuildServer.getBuildParameter(name)) {

            // next are non dot notation environment variables
            // note: we support the Bamboo weird way of doing variables
            value = BuildServer.getBuildParameter(name)
        } else {

            // next we return values from the custom extension
            value = project.extensions.getByName(extension).properties.get(name, defaultValue)

        }

        // we want to update the value in the extension in these cases
        // this way, listeners, other plugins, etc. can all use them
        if (project.extensions."$extension".hasProperty(name)) {

            project.extensions.getByName(extension)."$name" = value
        }

        // finally return it
        return value
    }

}
