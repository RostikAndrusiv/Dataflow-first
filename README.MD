java11, maven 3.6.3

to run PipelineWithSelectById place idss.txt to your bucket. Verify getters in JobOptions  `getIdBucket` `getIdFilename` `getProjectName`

commandline args:

mvn -Pdataflow-runner compile exec:java \
-Dexec.mainClass=org.loony.dataflow.<Pipeline> \
-Dexec.args="--<ProjectName> \
--runner=DataflowRunner \
--region=us-central1 \
--tempLocation=gs://<Your Bucket>/temp/ \
--numWorkers=1 \
--maxNumWorkers=2" 