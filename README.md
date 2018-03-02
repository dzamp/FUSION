```
███████╗██╗   ██╗███████╗██╗ ██████╗ ███╗   ██╗
██╔════╝██║   ██║██╔════╝██║██╔═══██╗████╗  ██║
█████╗  ██║   ██║███████╗██║██║   ██║██╔██╗ ██║
██╔══╝  ██║   ██║╚════██║██║██║   ██║██║╚██╗██║
██║     ╚██████╔╝███████║██║╚██████╔╝██║ ╚████║
╚═╝      ╚═════╝ ╚══════╝╚═╝ ╚═════╝ ╚═╝  ╚═══╝
+-         Apache Storm        -+
+-  data FLow User eXperience  -+
+-        based on Flux        -+
```


# FUSION

#### Clean install without tests:   (seldomly used)
```
mvn clean install -DskipTests=true
```

#### Run on vm, not really used
This run configuration runs the jar that the remote run has constructed in the production environment. Note the extra --remote that is essentially input for Flux.
```
sudo /opt/storm/bin/storm jar fusion-1.0-SNAPSHOT.jar flux.fusion.Fusion --remote simpleTopology.yaml -c 'nimbus.seeds=["localhost"]'
```

#### Package using local-run
Everytime there are code changes we have to repackage in order to run the new code.
Before running any topology we need to package and possibly avoid tests.
A jar is being constructed to run locally using the local-run profile.
This profile should be used most of the time since the other profile(remote-run) constructs another jar without storm dependencies to be run in production mode. It has been tested only once or twice.
```
mvn clean package -Plocal-run -DskipTests=true
```

#### Maven exec plugin
```
mvn exec:java -Dexec.mainClass=flux.fusion.Fusion -Dexec.args="--local src/test/resources/simpleTopology.xml"
```
Currently working with:
```
mvn exec:java -Dexec.mainClass=flux.Flux "-Dexec.args=--local src/test/resources/topology.yaml" -DskipTests=true
```
This will use the topology.yaml file in order to construct the topology. This is a simple test topology that will
perform word counting using two classes: RandomWordSpout.java & WordCounter.java
Note:  add this as an inteliJ maven profile in order to run it.

Existing configuration examples for running selected yaml topologies:
```
mvn exec:java -Dexec.mainClass=flux.Flux "-Dexec.args=--local src/test/resources/kafka-consumer/kafka-fusion-spout-helloworld.yaml" -DskipTests=true -Plocal-run
```
Runs the yaml topology located in the /src/test/resources/kafka-consumer/kafka-fusion-spout-helloworld.yaml
Note that in any of the runs the main class that is being run is the flux main class. This is always the main method that has to be called in order to read the yaml and construct the topology.


#### Jar exec, not really used
```
java -jar target/fusion-1.0-SNAPSHOT-jar-with-dependencies.jar --local src/test/resources/simpleTopology.xml
```

####Additional Info
There are three main branches to visit: 

   simple_tuple -> The simple tuple approach that we try to keep the storm preferred way of sending tuples
   
   fusion_tuple -> The fusion tuple approach that contains extra code to support sending more complex objects inside the tuple. 
   
   master -> Contains both but that should be the approach.Most of the code for the fusion_tuple approach is located in the tuple.abstraction package. Significant refactoring has been done in the other branches
   

Some tests are old, meaning that they might fail, please forgive me 







