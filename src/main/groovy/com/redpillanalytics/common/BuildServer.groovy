package com.redpillanalytics.common

/**
 * Created by stewartbryson on 11/18/16.
 */
class BuildServer {

    public static gitUtils = new GitUtils()


    static public getBuildParameter(String varName) {

        return System.getenv(varName) ?: System.getenv('bamboo_' + varName)

    }

    static public getBuildNumber() {

        return System.getenv('SOURCE_BUILD_NUMBER') ?: System.getenv('bamboo_buildNumber') ?: getTimestamp()

    }

    static public getBuildNumExt() {

        return '.' + getBuildNumber()

    }

    static public getBuildTag() {

        return System.getenv('BUILD_TAG') ?: System.getenv('bamboo_buildResultKey') ?: getTimestamp()

    }

    static public getBuildTagExt() {

        '-' + getBuildTag()

    }

    static public getBuildUrl() {

        return System.getenv('BUILD_URL') ?: System.getenv('bamboo_resultsUrl')

    }

    static public isJenkinsCI() {

        if (getCIServer() == 'jenkins')

            return true
        else

            return false
    }

    static public isBambooCI() {

        if (getCIServer() == 'bamboo')

            return true
        else

            return false
    }

    static public getCIServer() {

        if (System.getenv('JENKINS_HOME')) {

            return 'jenkins'
        } else if (System.getenv('bamboo_planKey')) {

            return 'bamboo'
        } else {

            return 'other'
        }
    }

    static public getRepositoryUrl() {

        return System.getenv('GIT_TAG') ?: System.getenv('bamboo_planRepository_repositoryUrl') ?: gitUtils.remoteUrl ?: ""
    }

    static public getGitHubOrg() {

        getRepositoryUrl().find(/(\/|:)(.+)(\/)([^.]+)/) { all, firstSlash, org, secondSlash, repo ->

            return org.toString()
        }
    }

    static public getGitHubRepo() {

        getRepositoryUrl().find(/(\/|:)(.+)(\/)([^.]+)/) { all, firstSlash, org, secondSlash, repo ->

            return repo.toString()
        }
    }

    static public getBranch() {

        return System.getenv('GIT_LOCAL_BRANCH') ?: System.getenv('bamboo_planRepository_branchName') ?: gitUtils.initialBranch
    }

    static public getCommitEmail() {

        return gitUtils.emailAddress
    }

    static public getCommitHash() {

        return gitUtils.getCommitHash()
    }

    static public generateBuildId() {

        return UUID.randomUUID().toString()
    }

    static public getTimestamp() {

        return new Date().format('yyyy-MM-dd-HHmmssSS')
    }
}
