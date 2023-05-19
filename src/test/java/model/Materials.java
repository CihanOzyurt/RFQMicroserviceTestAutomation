package model;

import lombok.Data;

@Data
public class Materials {
    private String id;
    private String name;
    private int volume;
    private String unit;
    private String part_number;
}
