package com.renansouza.base;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.data.annotation.DateCreated;
import io.micronaut.data.annotation.DateUpdated;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.ColumnTransformer;

import javax.persistence.*;
import javax.validation.constraints.PastOrPresent;
import java.lang.reflect.Constructor;
import java.time.LocalDateTime;
import java.util.Locale;

@Data
@Entity
@Introspected
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy= InheritanceType.JOINED)
@Schema(name="Companies", description="Companies description")
public class Auditable {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "name", nullable = false)
    @ColumnTransformer(write = "UPPER(trim(?))")
    private String name;

    private boolean active = true;

    @DateCreated
    @PastOrPresent
    @Getter(AccessLevel.MODULE)
    @Setter(AccessLevel.NONE)
    @Column(name = "date_created", nullable = false)
    private LocalDateTime dateCreated = LocalDateTime.now();

    @DateUpdated
    @PastOrPresent
    @Getter(AccessLevel.MODULE)
    @Column(name = "last_updated", nullable = false)
    private LocalDateTime lastUpdated = LocalDateTime.now();

    public <T extends RuntimeException> void canSave(Class<T> exceptionClazz) throws T {
        try {
            final Constructor<T> c = exceptionClazz.getConstructor(String.class);
            final String simpleName = exceptionClazz.getSimpleName().replace("Exception", "").toLowerCase(Locale.ROOT);

            if (this.getId() != null) {
                throw c.newInstance(simpleName + " id must be null.");
            }

            if (StringUtils.isBlank(this.getName())) {
                throw c.newInstance(simpleName + " name was not provided.");
            }
        } catch (ReflectiveOperationException e) {
            // ignore
        }
    }

    public <T extends RuntimeException> void canUpdate(Class<T> exceptionClazz) throws T {
        try {
            final Constructor<T> c = exceptionClazz.getConstructor(String.class);
            final String simpleName = exceptionClazz.getSimpleName().replace("Exception", "").toLowerCase(Locale.ROOT);

            if (this.getId() == null) {
                throw c.newInstance(simpleName + " id must not be null.");
            }

            if (StringUtils.isBlank(this.getName())) {
                throw c.newInstance(simpleName + " name was not provided.");
            }
        } catch (ReflectiveOperationException e) {
            // ignore
        }
    }

}