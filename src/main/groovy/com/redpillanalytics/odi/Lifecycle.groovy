package com.redpillanalytics.odi

import groovy.util.logging.Slf4j
import oracle.odi.domain.project.OdiProject

@Slf4j
class Lifecycle {

   Instance instance

   Lifecycle(String url, String driver, String masterRepo, String workRepo, String masterPassword, String odiUser, String odiPassword) {

      this.instance = new Instance(url, driver, masterRepo, workRepo, masterPassword, odiUser, odiPassword)

   }

   def createProject(String name, String code) {

      instance.beginTxn()

      instance.odi.getTransactionalEntityManager().persist(new OdiProject(name, code))

      instance.endTxn()
   }
}
