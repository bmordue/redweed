package me.bmordue.redweed.model.dto;

public class EdgeDTO {
    private final String from;
    private final String to;
    private final String label;

    public EdgeDTO(String from, String to, String label) {
        this.from = from;
        this.to = to;
        this.label = label;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getLabel() {
        return label;
    }
}
