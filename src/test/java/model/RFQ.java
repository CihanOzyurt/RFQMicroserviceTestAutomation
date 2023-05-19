package model;

import lombok.Data;

@Data
public class RFQ {
    public String id;
    public String name;
    public String description;
    public String category;
    public String start_date;
    public String end_date;
}
