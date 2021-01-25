package com.renansouza.user;

import com.renansouza.base.Auditable;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.data.annotation.MappedEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@MappedEntity(value = "user")
@Table(name = "user", uniqueConstraints =  @UniqueConstraint(columnNames={"username"}))
@Introspected
@MappedSuperclass
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(name="Companies", description="Companies description")
public class User extends Auditable {

    @NotNull
    @Column(name = "username", unique = true, nullable = false, updatable = false)
    private String username;
    @NotNull
    @Column(name = "password", nullable = false)
    private String password;

    private String role;

    private boolean changePassword;

}