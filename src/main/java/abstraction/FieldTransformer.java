package abstraction;

public interface FieldTransformer {
    //applies a transformation to the incoming fields returning the new fields

    public String[] transformFields(String[] incomingFields);

}
