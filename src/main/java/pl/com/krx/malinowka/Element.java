package pl.com.krx.malinowka;

public class Element {

    private Double value;
    private String sampledAt;

    public Element(Double value, String sampledAt) {
        this.value = value;
        this.sampledAt = sampledAt;
    }

    public Element() {
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public String getSampledAt() {
        return sampledAt;
    }

    public void setSampledAt(String sampledAt) {
        this.sampledAt = sampledAt;
    }

    @Override
    public String toString() {
        return "Element{" +
                "value=" + value +
                ", sampledAt=" + sampledAt +
                '}';
    }
}
