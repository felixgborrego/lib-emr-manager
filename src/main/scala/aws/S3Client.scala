package aws

import java.io.File

import aws.model._
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.regions.RegionUtils
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.PutObjectRequest

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * Deploy jar files to s3.
 * Before be able to run any Hadoop task in Amazon EMR,
 * the cluster we need to have access to our custom jar in the s3.
 *
 * By default it deploy the jar in the 'eu-west-1' region onther the path:
 *
 * s3://(bucketName)/jars/tmp/(file.jar)
 *
 */
object S3Client {

  def apply(bucketName: String, client: AmazonS3Client) = new S3Client(bucketName, client)

  def apply(bucketName: String, accessKey: String, secretKey: String): S3Client = {
    val awsCreds = new BasicAWSCredentials(accessKey, secretKey)
    S3Client(bucketName, awsCreds)
  }

  def apply(bucketName: String, credentials: BasicAWSCredentials): S3Client = {
    val client = new AmazonS3Client(credentials)
    new S3Client(bucketName, client)
  }
}

class S3Client(bucketName: String, client: AmazonS3Client) {
  client.setRegion(RegionUtils.getRegion(Regions.EU_WEST_1))

  def deploy(file: File) = Future {
    val keyName = s"jars/tmp/${file.getName}"
    client.putObject(new PutObjectRequest(bucketName, keyName, file))
    "s3://" + bucketName + "/" + keyName
  }
}

