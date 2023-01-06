java11, maven 3.6.3

mvn -Pdataflow-runner compile exec:java \
-Dexec.mainClass=org.loony.dataflow.WriteFirestoreMap \
-Dexec.args="--project=phrasal-client-372213 \
--runner=DataflowRunner \
--region=us-central1 \
--tempLocation=gs://my-first-dataflow-bucket/temp/ \
--numWorkers=1 \
--maxNumWorkers=2" 