package com.redpillanalytics.odi.gradle.tasks

import com.redpillanalytics.odi.Instance
import groovy.util.logging.Slf4j
import oracle.odi.domain.model.OdiModel
import oracle.odi.domain.model.OdiModelFolder
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction


@Slf4j
class GetModelsTask extends DefaultTask {

    @Internal
    Instance instance

    @TaskAction
    def getModels() {

        instance.connect()

        def modelFolders = instance.findAllModelFolders()
        def models = instance.findAllModels()

        instance.beginTxn()

        log.warn 'Model Folders:'
        modelFolders.each { OdiModelFolder modelFolder ->
            //Action to do to the model folders retrieved
            log.warn modelFolder.name
        }

        log.warn 'Models:'
        models.each { OdiModel model ->
            //Action to do to the models retrieved
            log.warn model.name
        }

        instance.endTxn()

    }
}