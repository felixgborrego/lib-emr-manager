import java.io.File

import aws.S3Client
import com.amazonaws.AmazonClientException
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.{ PutObjectRequest, PutObjectResult }
import org.mockito.Matchers._
import org.mockito.Mockito._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.mock.MockitoSugar
import org.scalatest.{ FlatSpec, Matchers }

class S3ClientSpec extends FlatSpec with MockitoSugar with ScalaFutures with Matchers {

  "S3Client" should "return an exception when the deploy can't be done" in {
    val amazon = mock[AmazonS3Client]
    val exception = new AmazonClientException("test")
    when(amazon.putObject(anyObject[PutObjectRequest]())).thenThrow(exception)

    val client = S3Client("bucketName", amazon)
    val response = client.deploy(new File("test.jar"))

    whenReady(response.failed) { ex =>
      ex should be === exception
    }
  }

  "S3Client" should "return the path in s3 if the operation is performed" in {
    val amazon = mock[AmazonS3Client]
    val exception = new AmazonClientException("test")
    when(amazon.putObject(anyObject[PutObjectRequest]())).thenReturn(new PutObjectResult)

    val client = S3Client("bucketName", amazon)
    val response = client.deploy(new File("test.jar"))

    whenReady(response) { path =>
      path should be === "s3://bucketName/jars/tmp/test.jar"
    }
  }

}
