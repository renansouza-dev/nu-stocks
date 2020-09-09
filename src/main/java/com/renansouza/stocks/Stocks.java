package com.renansouza.stocks;

import io.micronaut.core.annotation.Introspected;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Entity
@Introspected
@NoArgsConstructor
@AllArgsConstructor
public class Stocks {

    @Id
    @GeneratedValue
    private Long id;

    @Size(min = 5)
    @NotEmpty(message = "Ticker can not be empty.")
    private String ticker;
    @NotEmpty(message = "Name can not be empty.")
    private String name;

    private String sector;
    private String subSector;
    private String segment;
    private Listining listining;

}