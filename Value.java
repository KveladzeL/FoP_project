public abstract class Value {
    
}

class IntValue extends Value {
    int value;

    public IntValue(int value){
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}

class StringValue extends Value{
    String value;

    public StringValue(String value){
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}