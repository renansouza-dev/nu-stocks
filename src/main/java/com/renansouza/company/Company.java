package com.renansouza.company;

import com.renansouza.base.Auditable;
import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.hibernate.annotations.ColumnTransformer;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Pattern;

@Data
@Table(name = "companies", uniqueConstraints =  @UniqueConstraint(columnNames={"name", "registration"}))
@Introspected
@MappedSuperclass
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(name="Companies", description="Companies description")
public class Company extends Auditable {

    @NonNull
    @Column(nullable = false, unique = true)
    @ColumnTransformer(write = "UPPER(trim(?))")
    @Pattern(regexp = "^[\\p{Digit}]{14}$", message = "Registration must be equal to 14 digits.")
    private String registration;

    private boolean bank;
    private boolean broker;
    private boolean manager;
    private boolean listedCompany;
    private boolean administrator;

    void isAllTrueOrAllFalse() {
        if ((this.bank && this.broker && this.manager && this.listedCompany && this.administrator) |
                (!this.bank && !this.broker && !this.manager && !this.listedCompany && !this.administrator)) {
            throw new CompanyException("All booleans fields can't have the same value set to " + bank + ".");
        }
    }

}