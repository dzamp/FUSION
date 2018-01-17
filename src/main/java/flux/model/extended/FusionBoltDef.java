package flux.model.extended;

import flux.model.BoltDef;

public class FusionBoltDef extends BoltDef {
    public String[] fields = null;

    public String[] getFields() {
        return fields;
    }

    public void setFields(String[] fields) {
        this.fields = fields;
    }
}
