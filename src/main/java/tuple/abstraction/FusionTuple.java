package tuple.abstraction;

import org.apache.storm.tuple.Values;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FusionTuple implements Serializable {
    private static final long serialVersionUID = 12341234L;
    //        public Object lock;
    //wrapper class for tuple.
    // this map will contain all values for alla streams, of any kind, by its component name(spout/bolt) or
    // of the incoming stream
    Map<String, List<Values>> valueMap;


    //Metadata Map that contains info about the incoming tuples and its classes,
    //Not that this will contain information from multiple sources
    Map<String, List<Meta>> metaMap;


    public FusionTuple() {
        valueMap = new HashMap<>();
        metaMap = new HashMap<>();
//        lock = new Object();
    }


    //Always return NEW TYPES TO AVOID CONCURRENCY ISSUES

    public List<Values> getStreamValues(String id) {
        List<Values> valeus = new ArrayList<>();
        if (valueMap.containsKey(id)) valeus = valueMap.get(id);
        return valeus;
    }


    public Meta getFieldMetadataByName(String streamId, String fieldName) {
        Meta meta = new Meta();
        if (metaMap.containsKey(streamId)) {
            if (metaMap.get(streamId) != null && metaMap.get(streamId).size() != 0) {
                for (Meta pair : metaMap.get(streamId)) {
                    if (pair.getFieldName().equals(fieldName))
                        meta = pair;
                }
            }
        }
        return meta;
    }

    public int getPositionOfFieldInStream(String streamId, String fieldName) {
        if (metaMap.containsKey(streamId)) {
            if (metaMap.get(streamId) != null && metaMap.get(streamId).size() != 0) {
                for (Meta pair : metaMap.get(streamId)) {
                    if (pair.getFieldName().equals(fieldName))
                        return pair.getPosition();
                }
            }
        }
        return -1;
    }

    public String getClassOfFieldInStream(String streamId, String fieldName) {
        if (metaMap.containsKey(streamId)) {
            if (metaMap.get(streamId) != null && metaMap.get(streamId).size() != 0) {
                for (Meta pair : metaMap.get(streamId)) {
                    if (pair.getFieldName().equals(fieldName))
                        return pair.getClassName();
                }
            }
        }
        return null;
    }

    public List<Meta> getStreamMetadata(String streamId) {
        List<Meta> metaList = new ArrayList<>();
        if(metaMap.containsKey(streamId))
            metaList = metaMap.get(streamId);
        return metaList;
    }

    public List<String> getFieldNamesOfStream(String streamId) {
        List<String> fieldNames = new ArrayList<>();
        if(this.metaMap.containsKey(streamId)) {
            List<Meta> metadata = this.metaMap.get(streamId);
            if (metadata != null) {
                metadata.forEach(meta -> fieldNames.add(meta.getFieldName()));
            }
        }
        return fieldNames;
    }


    public void addValuestoStream(String streamId, List<Values> vals) {
        valueMap.put(streamId, vals);
    }


    public void setStreamMetadata(String streamId, List<Meta> metaList) {
        this.metaMap.put(streamId, metaList);
    }

    //    @Deprecated
    public void addNewValuesRowTostreamValues(String streamId, Values values, Meta meta) {
        this.valueMap.get(streamId).add(values);
        if (!this.metaMap.get(streamId).contains(meta))
            if (meta != null) this.metaMap.get(streamId).add(meta);
    }


    //    @Deprecated
    public void addMetadataToStream(String streamId, Meta meta) {
        if (this.metaMap.get(streamId) != null) {
            int pos = this.metaMap.get(streamId).size();
            meta.setPosition(pos);
            this.metaMap.get(streamId).add(meta);
        }
    }

    public void removeFieldAndMetadataFromStream(String streamId, String fieldName) {
        int positionOfField = this.getPositionOfFieldInStream(streamId, fieldName);

        List<Values> valuesList = getStreamValues(streamId);
        valuesList.forEach(valuesTuple -> valuesTuple.remove(positionOfField));
        List<Meta> metaList = getStreamMetadata(streamId);
        metaList.remove(positionOfField);
        for (int i = 0; i < metaList.size(); i++) {
            //update internal position referrences
            metaList.get(i).setPosition(i);
        }
        setStreamMetadata(streamId, metaList);
        addValuestoStream(streamId, valuesList);
    }

//    @Override
//    public synchronized String toString() {
//        synchronized (lock) {
//            StringBuilder builder = new StringBuilder();
//            for (String stream : valueMap.keySet()) {
//                builder.append(stream + " --> ");
//                for (Values values : valueMap.get(stream)) {
//                    builder.append("[ ");
//                    for (Object obj : values)
//                        builder.append(" ").append(obj.toString()).append(",");
//                    builder.append(" ] \n");
//                }
//                builder.append(" \n");
//            }
//
//            for (String stream : metaMap.keySet()) {
//                builder.append(stream + " --> ");
//                for (Meta meta : metaMap.get(stream)) {
//                    builder.append(meta.toString());
//                }
//                builder.append(" \n");
//            }
//            return builder.toString();
//        }
//    }
}
