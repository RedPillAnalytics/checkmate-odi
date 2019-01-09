package com.redpillanalytics.odi.gradle.tasks

import com.redpillanalytics.odi.Instance
import groovy.util.logging.Slf4j
import oracle.odi.domain.runtime.loadplan.OdiLoadPlan
import oracle.odi.domain.runtime.scenario.OdiScenario
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

@Slf4j
class GetLoadPlansAndScenariosTask extends DefaultTask {

    @Internal
    Instance instance

    @TaskAction
    def taskAction() {

        instance.connect()

        def scenarios = instance.findAllScenarios()
        def loadPlans = instance.findAllLoadPlans()

        instance.beginTxn()

        if(scenarios.isEmpty())
            log.warn 'Does not exist Scenarios on the ODI Repo ...'
        else {
            log.warn 'List of Scenarios:'
            scenarios.each { OdiScenario scenario ->
                //Action to do to the scenarios retrieved
                log.warn scenario.name
            }
        }

        if(loadPlans.isEmpty())
            log.warn 'Does not exist Load Plans on the ODI Repo ...'
        else {
            log.warn 'List of Load Plans:'
            loadPlans.each { OdiLoadPlan loadPlan ->
                //Action to do to the Load Plans retrieved
                log.warn loadPlan.name
            }
        }

        instance.endTxn()

    }
}