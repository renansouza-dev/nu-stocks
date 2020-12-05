package com.renansouza.companies;

import com.renansouza.config.Auditable;
import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Entity
@Introspected
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(name="Companies", description="Companies description")
public class Companies extends Auditable {

    @Size(min = 14, max = 14, message = "Registration must be equal to 14 digits.")
    private String registration;
    @NotBlank(message = "Name can not be empty.")
    private String name;

    @Lob
    private byte[] logo;

    @NotBlank(message = "Sector cannot be null.")
    private String sector;
    @NotBlank(message = "Sub Sector cannot be null.")
    private String subSector;
    @NotBlank(message = "Segment cannot be null.")
    private String segment;
    @NotNull(message = "Listing Segment cannot be null.")
    private Listining listining;

}