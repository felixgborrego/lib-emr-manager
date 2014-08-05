Prototype Scala client for Amazon Elastic Map Reduce
====================================================

## Description

Work in progress ...

Scala library to manager an Amazon Elastic Map Reduce cluster.
It allows you to create clusters and launch Hadoop jobs on demand.

## Usage

The following code will create a new cluster with 2 EC2 micro instances and execute the Hadoop task inside emr-job.jar 

```scala
// Create new Amazon EMR client
val emrClient = EmrClient("test", access_key_id, secret_access_key)

// Create a new cluster
val cluster:Future[ClusterRunning] = emrClient.createCluster(MicroCluster)

// Execute a new task  in the cluster
val job:Future[TaskRunning] = cluster.flatMap {
      _.addJob("s3://felix-buckettest1/emr/jar/emr-job.jar", "dummydata.ActionJob")
}
```

## Roadmap

this project is still in an early stage, there are many things that should be improved:

- [ ] Get cluster status
- [ ] Stop cluster
- [ ] Schedule task and group task
- [ ] Allow different regions
- [ ] ...


 