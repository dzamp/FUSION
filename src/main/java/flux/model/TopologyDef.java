/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package flux.model;


import flux.model.extended.FusionBoltDef;
import flux.model.extended.KafkaSpoutConfigDef;
import flux.model.extended.MqttSpoutConfigDef;
import flux.model.extended.MqttSpoutDef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Bean represenation of a topology.
 *
 * It consists of the following:
 *   1. The topology name
 *   2. A `java.util.Map` representing the `org.apache.storm.config` for the topology
 *   3. A list of spout definitions
 *   4. A list of bolt definitions
 *   5. A list of stream definitions that define the flow between spouts and bolts.
 *
 */
//@JacksonXmlRootElement(localName = "topology")
public class TopologyDef {
    private static Logger LOG = LoggerFactory.getLogger(TopologyDef.class);

    private String name;
    private Map<String,Object> configuration  =new HashMap<>();

    private Map<String, BeanDef> componentMap = new LinkedHashMap<String, BeanDef>(); // not required
    private List<IncludeDef> includes; // not required
    private TopologySourceDef topologySource;

    private Map<String, BoltDef> boltMap = new LinkedHashMap<String, BoltDef>();
    private Map<String, SpoutDef> spoutMap = new LinkedHashMap<String, SpoutDef>();
    private Map<String, KafkaSpoutConfigDef> kafkaConfig = new LinkedHashMap<>();
    private Map<String, MqttSpoutConfigDef> mqttConfig = new LinkedHashMap<>();
    private Map<String, FusionBoltDef> fusionBolts = new LinkedHashMap<>();
    private List<StreamDef> streams = new ArrayList<StreamDef>();


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setName(String name, boolean override){
        if(this.name == null || override){
            this.name = name;
        } else {
            LOG.warn("Ignoring attempt to set property 'name' with override == false.");
        }
    }

    public List<SpoutDef> getSpouts() {
        ArrayList<SpoutDef> retval = new ArrayList<SpoutDef>();
//        consolidateSpouts();
        retval.addAll(this.spoutMap.values());
//        retval.addAll((Collection<? extends SpoutDef>) this.mqttSpouts);
        return retval;
    }

    public void setSpouts(List<SpoutDef> spouts) {
        this.spoutMap = new LinkedHashMap<String, SpoutDef>();
        for(SpoutDef spout : spouts){
            this.spoutMap.put(spout.getId(), spout);
        }
    }

    public List<FusionBoltDef> getFusionbolts() {
        ArrayList<FusionBoltDef> retval = new ArrayList<>();
        retval.addAll(this.fusionBolts.values());
        return retval;
    }

    public void setFusionbolts(List<FusionBoltDef> fusionbolts) {
        this.fusionBolts = new LinkedHashMap<String, FusionBoltDef>();
        for(FusionBoltDef bolt : fusionbolts){
            this.fusionBolts.put(bolt.getId(), bolt);
        }
    }

    public List<KafkaSpoutConfigDef> getKafkaconfig() {
        ArrayList<KafkaSpoutConfigDef> retval = new ArrayList<>();
        retval.addAll(this.kafkaConfig.values());
        return retval;
    }

    public void setKafkaconfig(List<KafkaSpoutConfigDef> fusionbolts) {
        this.kafkaConfig = new LinkedHashMap<String, KafkaSpoutConfigDef>();
        for(KafkaSpoutConfigDef spout : fusionbolts){
            this.kafkaConfig.put(spout.getId(), spout);
        }
    }



    public List<MqttSpoutConfigDef> getMqttconfig() {
        ArrayList<MqttSpoutConfigDef> retval = new ArrayList<>();
        retval.addAll(this.mqttConfig.values());
        return retval;
    }

    public void setMqttconfig(List<MqttSpoutConfigDef> spouts) {
        this.mqttConfig = new LinkedHashMap<>();
        for(MqttSpoutConfigDef spout : spouts){
            this.mqttConfig.put(spout.getId(), spout);
        }
    }
//    public void consolidateSpouts(){
//        this.spoutMap.putAll();
//    }

    public void consolidateBolts(){
        this.boltMap.putAll(fusionBolts);
    }



    public List<BoltDef> getBolts() {
        consolidateBolts();
        ArrayList<BoltDef> retval = new ArrayList<BoltDef>();
        retval.addAll(this.boltMap.values());
        return retval;
    }

    public void setBolts(List<BoltDef> bolts) {
        this.boltMap = new LinkedHashMap<String, BoltDef>();
        for(BoltDef bolt : bolts){
            this.boltMap.put(bolt.getId(), bolt);
        }
    }

    public List<StreamDef> getStreams() {
        return streams;
    }

    public void setStreams(List<StreamDef> streams) {
        this.streams = streams;
    }

    public Map<String,Object> getConfig() {
        return configuration;
    }

    public void setConfig(Map<String,Object> config) {
        this.configuration = config;
    }

    public List<BeanDef> getComponents() {
        ArrayList<BeanDef> retval = new ArrayList<BeanDef>();
        retval.addAll(this.componentMap.values());
        return retval;
    }

    public void setComponents(List<BeanDef> components) {
        this.componentMap = new LinkedHashMap<String, BeanDef>();
        for(BeanDef component : components){
            this.componentMap.put(component.getId(), component);
        }
    }

    public List<IncludeDef> getIncludes() {
        return includes;
    }

    public void setIncludes(List<IncludeDef> includes) {
        this.includes = includes;
    }

    // utility methods
    public int parallelismForBolt(String boltId){
        return this.boltMap.get(boltId).getParallelism();
    }

    public BoltDef getBoltDef(String id){
        return this.boltMap.get(id);
    }
    public FusionBoltDef getFusionBoltDef(String id){
        return this.fusionBolts.get(id);
    }

    public SpoutDef getSpoutDef(String id){
        return this.spoutMap.get(id);
    }

    public MqttSpoutConfigDef getMqttSpoutConfigDef(String id){
        return this.mqttConfig.get(id);
    }


    public BeanDef getComponent(String id){
        return this.componentMap.get(id);
    }

    // used by includes implementation
    public void addAllBolts(List<BoltDef> bolts, boolean override){
        for(BoltDef bolt : bolts){
            String id = bolt.getId();
            if(this.boltMap.get(id) == null || override) {
                this.boltMap.put(bolt.getId(), bolt);
            } else {
                LOG.warn("Ignoring attempt to create bolt '{}' with override == false.", id);
            }
        }
    }

    public void addAllMqttConfigs(List<MqttSpoutConfigDef> mqttSpoutConfigDefs, boolean override){
        for(MqttSpoutConfigDef mqttCfg : mqttSpoutConfigDefs){
            String id = mqttCfg.getId();
            if(this.mqttConfig.get(id) == null || override) {
                this.mqttConfig.put(mqttCfg.getId(), mqttCfg);
            } else {
                LOG.warn("Ignoring attempt to create mqttConfig '{}' with override == false.", id);
            }
        }
    }

    public void addAllKafkaConfigs(List<KafkaSpoutConfigDef> kafkaSpoutConfigDefs, boolean override){
        for(KafkaSpoutConfigDef kafkaCfg : kafkaSpoutConfigDefs){
            String id = kafkaCfg.getId();
            if(this.kafkaConfig.get(id) == null || override) {
                this.kafkaConfig.put(kafkaCfg.getId(), kafkaCfg);
            } else {
                LOG.warn("Ignoring attempt to create mqttConfig '{}' with override == false.", id);
            }
        }
    }


    public void addAllSpouts(List<SpoutDef> spouts, boolean override){
        for(SpoutDef spout : spouts){
            String id = spout.getId();
            if(this.spoutMap.get(id) == null || override) {
                this.spoutMap.put(spout.getId(), spout);
            } else {
                LOG.warn("Ignoring attempt to create spout '{}' with override == false.", id);
            }
        }
    }

    public void addAllComponents(List<BeanDef> components, boolean override) {
        for(BeanDef bean : components){
            String id = bean.getId();
            if(this.componentMap.get(id) == null || override) {
                this.componentMap.put(bean.getId(), bean);
            } else {
                LOG.warn("Ignoring attempt to create component '{}' with override == false.", id);
            }
        }
    }

    public void addAllStreams(List<StreamDef> streams, boolean override) {
        //TODO figure out how we want to deal with overrides. Users may want to add streams even when overriding other
        // properties. For now we just add them blindly which could lead to a potentially invalid topology.
        this.streams.addAll(streams);
    }

    public TopologySourceDef getTopologySource() {
        return topologySource;
    }

    public void setTopologySource(TopologySourceDef topologySource) {
        this.topologySource = topologySource;
    }

    public boolean isDslTopology(){
        return this.topologySource == null;
    }


    public boolean validate(){
        boolean hasSpouts = this.spoutMap != null && this.spoutMap.size() > 0;
        boolean hasBolts = this.boltMap != null && this.boltMap.size() > 0;
        boolean hasStreams = this.streams != null && this.streams.size() > 0;
        boolean hasSpoutsBoltsStreams = hasStreams && hasBolts && hasSpouts;
        // you cant define a topologySource and a DSL topology at the same time...
        if (!isDslTopology() && ((hasSpouts || hasBolts || hasStreams))) {
            return false;
        }
        if(isDslTopology() && (hasSpouts && hasBolts && hasStreams)) {
            return true;
        }
        return true;
    }
}
