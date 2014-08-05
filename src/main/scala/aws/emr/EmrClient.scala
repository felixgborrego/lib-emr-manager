package aws.emr

import aws.emr.impl._
import com.amazonaws.auth.{ AWSCredentials, BasicAWSCredentials }
import com.amazonaws.regions.RegionUtils
import com.amazonaws.services.elasticmapreduce.AmazonElasticMapReduceAsyncClient
import com.amazonaws.services.elasticmapreduce.model._
import aws.model._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.collection.JavaConversions._

object EmrClient {

  def apply(bucketName: String, accessKey: String, secretKey: String): EmrClient = {
    val awsCreds = new BasicAWSCredentials(accessKey, secretKey)
    EmrClient(bucketName, awsCreds)
  }

  def apply(bucketName: String, credentials: AWSCredentials) = {
    val awsClient = new AmazonElasticMapReduceAsyncClient(credentials)
    new EmrClient(bucketName, awsClient)
  }

}

case class EmrClient(val bucketName: String, val awsClient: AmazonElasticMapReduceAsyncClient) {

  awsClient.setRegion(RegionUtils.getRegion(Regions.EU_WEST_1))

  def createCluster(clusterType: ClusterType, jobFlowName: String = "testJob", aimVersion: String = AmiVersion.AMI_WITH_HADOOP1): Future[ClusterRunning] = Future {

    val config = configCluster(clusterType)

    val jobRequest = new RunJobFlowRequest(jobFlowName, config)
    jobRequest.setAmiVersion(aimVersion)
    jobRequest.setLogUri(s"s3://$bucketName/jobs/$jobFlowName/log")

    val result = awsClient.runJobFlow(jobRequest)

    new EmrClient(bucketName, awsClient) with ClusterRunning {
      val clusterId = result.getJobFlowId
    }
  }

  private def configCluster(clusterType: ClusterType) = {
    val config = new JobFlowInstancesConfig()
    //the jobflow will not shutting down once the steps have completed.
    config.setKeepJobFlowAliveWhenNoSteps(true)

    config.setInstanceGroups(clusterType.instanceGroups)
    // TODO move Ec2 key name to an external file
    // config.setEc2KeyName("FelixPersonalGilt")
    config
  }

}
