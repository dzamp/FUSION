package algorithms.util;

public enum Operator  {
    GREATER_THAN,
    LESS_THAN,
    EQUAL,
    NOT_EQUAL;


    public static Operator select(String value){
        switch (value){
            case "gt":
                return Operator.GREATER_THAN;
            case "lt":
                return Operator.LESS_THAN;
            case "eq":
                return Operator.EQUAL;
            case "neq":
                return Operator.NOT_EQUAL;
        }
        return null;
    }



}
