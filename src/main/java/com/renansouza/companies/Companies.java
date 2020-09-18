package com.renansouza.companies;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.data.annotation.DateCreated;
import io.micronaut.data.annotation.DateUpdated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.*;
import java.time.LocalDateTime;

@Data
@Entity
@Introspected
@NoArgsConstructor
@AllArgsConstructor
public class Companies {

    @Id
    @GeneratedValue
    private Long id;

    @Size(min = 14, max = 14, message = "Registration must be equal to 14 digits.")
    private String registration;
    @NotBlank(message = "Name can not be empty.")
    private String name;

    @NotBlank(message = "Sector cannot be null.")
    private String sector;
    @NotBlank(message = "Sub Sector cannot be null.")
    private String subSector;
    @NotBlank(message = "Segment cannot be null.")
    private String segment;
    @NotNull(message = "Listing Segment cannot be null.")
    private Listining listining;

    private boolean isDeleted;

    @DateCreated
    @PastOrPresent
    private LocalDateTime dateCreated;

    @DateUpdated
    @PastOrPresent
    private LocalDateTime lastUpdated;

}