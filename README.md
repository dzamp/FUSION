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

#### Clean install without tests:
```
mvn clean install -DskipTests=true
```


#### Package
```
mvn package -DskipTests=true
```

#### Run on vm
```
sudo /opt/storm/bin/storm jar fusion-1.0-SNAPSHOT.jar flux.fusion.Fusion --remote simpleTopology.xml -c 'nimbus.seeds=["localhost"]'
```

#### Package using local-run
```
mvn clean package -Plocal-run -DskipTests=true
```

#### Maven exec plugin
```
mvn exec:java -Dexec.mainClass=flux.fusion.Fusion -Dexec.args="--local src/test/resources/simpleTopology.xml"
```
Currently working with:
```
exec:java -Dexec.mainClass=flux.Flux "-Dexec.args=--local src/test/resources/topology.yaml" -DskipTests=true
```
This will use the topology.yaml file in order to construct the topology. This is a simple test topology that will
perform word counting using two classes: RandomWordSpout.java & WordCounter.java
Note:  add this as an inteliJ maven profile in order to run it.

#### Jar exec
```
java -jar target/fusion-1.0-SNAPSHOT-jar-with-dependencies.jar --local src/test/resources/simpleTopology.xml
```



