package com.renansouza.company;

import com.renansouza.config.Auditable;
import io.micronaut.core.annotation.Introspected;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.ColumnTransformer;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.Pattern;

@Data
@Entity
@Introspected
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(name="Companies", description="Companies description")
public class Company extends Auditable {

    @NonNull
    @Column(nullable = false)
    @ColumnTransformer(write = "UPPER(trim(?))")
    @Pattern(regexp = "^[\\p{Digit}]{14}$", message = "Registration must be equal to 14 digits.")
    private String registration;

    @Column(nullable = false)
    @ColumnTransformer(write = "UPPER(trim(?))")
    private String name;

    private boolean bank;
    private boolean broker;
    private boolean manager;
    private boolean listedCompany;
    private boolean administrator;

    public void canSave() {
        if (super.getId() != null) {
            throw new CompanyException("Company id must be null.");
        }

        if (StringUtils.isBlank(this.name)) {
            throw new CompanyException("Company name was not provided.");
        }

        isAllTrueOrAllFalse();
    }

    public void canUpdate() {
        if (super.getId() == null) {
            throw new CompanyException("Company id was not provided.");
        }

        if (StringUtils.isBlank(this.name)) {
            throw new CompanyException("Company name was not provided.");
        }

        isAllTrueOrAllFalse();
    }

    private void isAllTrueOrAllFalse() {
        if ((this.bank && this.broker && this.manager && this.listedCompany && this.administrator) |
                (!this.bank && !this.broker && !this.manager && !this.listedCompany && !this.administrator)) {
            throw new CompanyException("All booleans fields can't have the same value set to " + bank + ".");
        }
    }

}