package aws

package object model {

  object Regions {
    val EU_WEST_1 = "eu-west-1"
  }

  import com.amazonaws.services.elasticmapreduce.model.InstanceGroupConfig

  object MicroCluster extends ClusterType("m1.small") {
    override def instanceGroups = {
      val master = new InstanceGroupConfig(InstanceRole.Master, awsInstanceType, 1)
      val core = new InstanceGroupConfig(InstanceRole.CORE, awsInstanceType, 1)
      master :: core :: Nil
    }
  }

  object HightPerformanceCluster extends ClusterType("c3.xlarge") {
    override def instanceGroups = {
      val master = new InstanceGroupConfig(InstanceRole.Master, awsInstanceType, 1)
      val core = new InstanceGroupConfig(InstanceRole.CORE, awsInstanceType, 10)
      master :: core :: Nil
    }
  }

  object NormalCluster extends ClusterType("c3.large") {
    override def instanceGroups = {
      val master = new InstanceGroupConfig(InstanceRole.Master, awsInstanceType, 1)
      val core = new InstanceGroupConfig(InstanceRole.CORE, awsInstanceType, 4)
      master :: core :: Nil
    }
  }

  object InstanceRole {
    val Master = "MASTER"
    val CORE = "CORE"
  }

  object AmiVersion {
    val AMI_WITH_HADOOP2_4 = "3.1.0"
    val AMI_WITH_HADOOP1 = "2.4.6"
  }

  /**
   * Predefined Amazon instances.
   * Modeled after: http://aws.amazon.com/ec2/instance-types/
   */
  sealed abstract class ClusterType(val awsInstanceType: String) {
    def instanceGroups: List[InstanceGroupConfig]
  }

}

