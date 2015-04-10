Note: Work in progress...

## Description

Scala library to manage an cluster in Amazon Elastic Map Reduce.
It allows you to create a Hadoop cluster in EC2, launch your task in the cluster, get the results and shutdown the cluster on demand from your Scala code.

## Experimental
This lib is in an early stage. Do not use this with any expectation of stability, what is here is just an experiment over the weekend, and would need more work to reach a point where it could be used as a base for anything else.

## Usage

The following code will create a new cluster with 2 EC2 micro instances, wait until the cluster is ready, execute the Hadoop task inside emr-job.jar and shutdown the cluster. 

```scala
// Create new Amazon EMR client
val emrClient = EmrClient("test", access_key_id, secret_access_key)

// Create a new cluster and get a future cluster running
val cluster:Future[ClusterRunning] = emrClient.createCluster(MicroCluster)

// Execute a new task  in the cluster
val job:Future[TaskRunning] = cluster.flatMap {
      _.addJob("s3://felix-buckettest1/emr/jar/emr-job.jar", "dummydata.ActionJob")
}
```

## TODO

This project is still in an early stage, there are many things that should be improved:

- [ ] Get cluster status
- [ ] Stop cluster
- [ ] Schedule task and group task
- [ ] Allow different regions
- [ ] ...


 
