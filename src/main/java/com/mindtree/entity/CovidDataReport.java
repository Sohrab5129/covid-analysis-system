package com.mindtree.entity;

import lombok.Data;

@Data
public class CovidDataReport {
    private String  fState;
    private String fConfirmed;
    private String  sState;
    private String sConfirmed;
    private String date;
    private String  state;
    private Integer count;
}
