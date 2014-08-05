package aws.emr

import com.amazonaws.event.{ ProgressEvent, ProgressListener }
import com.amazonaws.services.elasticmapreduce.AmazonElasticMapReduceAsyncClient
import com.amazonaws.services.elasticmapreduce.model.{ AddJobFlowStepsRequest, HadoopJarStepConfig, StepConfig }

import scala.collection.JavaConversions._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

package object impl {

  trait ClusterRunning {
    this: EmrClient =>
    def awsClient: AmazonElasticMapReduceAsyncClient
    def clusterId: String

    def addJob(jar: String, mainClass: String): Future[TaskRunning] = Future {
      println(s"adding step to cluster: $clusterId")
      // A custom step
      val config = new HadoopJarStepConfig()
        .withJar(jar)
        .withMainClass("com.twitter.scalding.Tool")
        .withArgs(mainClass, "--hdfs")

      val customStep = new StepConfig(s"Step-$mainClass", config)
      customStep.setActionOnFailure("CONTINUE")

      val result = awsClient.addJobFlowSteps(new AddJobFlowStepsRequest()
        .withJobFlowId(clusterId)
        .withSteps(customStep).withGeneralProgressListener(listener))

      val newStepId = result.getStepIds.head
      println(s"Get stepID: $newStepId")

      val id = clusterId
      new EmrClient(bucketName, awsClient) with TaskRunning with ClusterRunning {
        val clusterId = id
        val stepId = newStepId
      }

    }

    private def listener = new ProgressListener {
      override def progressChanged(progressEvent: ProgressEvent): Unit = {
        // TODO log events
        // println(s"\t  event: ${progressEvent.getEventType}")
      }
    }
  }

  trait TaskRunning {
    val stepId: String
  }

}
