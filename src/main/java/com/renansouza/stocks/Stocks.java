package com.renansouza.stocks;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Stocks {

    @Id
    @GeneratedValue
    private Long id;

    private String ticker;
    private String name;

    private float freeFloat;

    private String sector;
    private String subSector;
    private String segment;
    private Listining listining;

}