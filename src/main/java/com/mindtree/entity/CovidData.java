package com.mindtree.entity;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "covid_data")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class CovidData {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue
    private int id;

    @Column(name = "recovered")
    private String recovered;

    @Column(name = "confirmed")
    private String confirmed;

    @Column(name = "tested")
    private String tested;

    @Column(name = "district")
    private String district;

    @Column(name = "state")
    private String state;

    @Column(name = "date")
    private Date date;
}

