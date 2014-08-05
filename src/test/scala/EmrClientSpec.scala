import aws.emr.EmrClient
import aws.model.MicroCluster
import com.amazonaws.AmazonClientException
import com.amazonaws.services.elasticmapreduce.AmazonElasticMapReduceAsyncClient
import com.amazonaws.services.elasticmapreduce.model.{ AddJobFlowStepsResult, AddJobFlowStepsRequest, RunJobFlowRequest, RunJobFlowResult }
import org.mockito.Matchers._
import org.mockito.Mockito._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.mock.MockitoSugar
import org.scalatest.{ FlatSpec, Matchers }
import aws.emr.impl.ClusterRunning
import scala.util.{ Failure, Success }
import scala.concurrent.ExecutionContext.Implicits.global

class EmrClientSpec extends FlatSpec with MockitoSugar with ScalaFutures with Matchers {

  "EmrClient" should "return an exception when the cluster can't be created" in {
    val amazon = mock[AmazonElasticMapReduceAsyncClient]
    val exception = new AmazonClientException("test")
    when(amazon.runJobFlow(anyObject[RunJobFlowRequest]())).thenThrow(exception)

    val client = EmrClient("bucketName", amazon)
    val response = client.createCluster(MicroCluster)

    whenReady(response.failed) { ex =>
      ex should be === exception
    }
  }

  "EmrClient" should "return a new cluster starting" in {
    val amazon = mock[AmazonElasticMapReduceAsyncClient]
    when(amazon.runJobFlow(anyObject[RunJobFlowRequest]())).thenReturn(new RunJobFlowResult().withJobFlowId("clusterId"))

    val client = EmrClient("bucketName", amazon)
    val response = client.createCluster(MicroCluster)

    whenReady(response) { cluster =>
      cluster.clusterId should be === "clusterId"
    }
  }

  "EmrClient" should "return a new task starting" in {
    val amazon = mock[AmazonElasticMapReduceAsyncClient]
    when(amazon.addJobFlowSteps(anyObject[AddJobFlowStepsRequest]())).thenReturn(new AddJobFlowStepsResult().withStepIds("stepId"))
    when(amazon.runJobFlow(anyObject[RunJobFlowRequest]())).thenReturn(new RunJobFlowResult().withJobFlowId("jobId"))

    val client = EmrClient("bucketName", amazon)
    val response = client.createCluster(MicroCluster).flatMap(_.addJob("test.jar", "mainClass"))

    whenReady(response) { job =>
      job.stepId should be === "stepId"
    }
  }
}
