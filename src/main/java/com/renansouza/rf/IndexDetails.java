package com.renansouza.rf;

import com.renansouza.base.Auditable;
import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Data
@Entity
@Introspected
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(name="IndexDetails", description="Index history details")
public class IndexDetails extends Auditable {

    private LocalDate start;
    private LocalDate end = LocalDate.now();
    private BigDecimal value = BigDecimal.ZERO;

    public IndexDetails(@NotNull String start, @NotNull String end, BigDecimal value) {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        this.start = LocalDate.parse(start, formatter);
        this.end = LocalDate.parse(end, formatter);
        this.value = value;
    }
}