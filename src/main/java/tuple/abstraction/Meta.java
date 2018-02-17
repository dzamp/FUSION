package tuple.abstraction;


import org.apache.commons.lang3.tuple.Pair;

public class Meta {
    private static final long serialVersionUID = 43214321L;
    String fieldName;
    int position;
    String className;

    public Meta() {
    }

    public Meta(String fieldName, int position, String className) {
        this.fieldName = fieldName;
        this.position = position;
        this.className = className;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) return false;
        if(obj == this) return true;
        if (!(obj instanceof Meta))return false;
        Meta meta = (Meta)obj;
        if(this.position == meta.position && this.className.equals(meta.className) && this.fieldName.equals(meta.fieldName))
            return true;
        return false;
    }

    @Override
    public String toString() {
        return "[ " + fieldName + ", " + position + ", " + className + " ]\n";
    }
}