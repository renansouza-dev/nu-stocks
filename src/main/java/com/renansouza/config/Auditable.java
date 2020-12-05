package com.renansouza.config;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.data.annotation.DateCreated;
import io.micronaut.data.annotation.DateUpdated;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDateTime;

@Data
@Entity
@Introspected
@NoArgsConstructor
@AllArgsConstructor
@Schema(name="Companies", description="Companies description")
public class Auditable {

    @Id
    @GeneratedValue
    private Long id;

    private boolean isDeleted;

    @DateCreated
    @PastOrPresent
    @Getter(AccessLevel.NONE)
    private LocalDateTime dateCreated;

    @DateUpdated
    @PastOrPresent
    @Getter(AccessLevel.NONE)
    private LocalDateTime lastUpdated;

}
