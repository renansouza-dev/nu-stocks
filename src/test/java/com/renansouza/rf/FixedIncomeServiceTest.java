package com.renansouza.rf;

import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@MicronautTest
class FixedIncomeServiceTest {

    @Inject
    private FixedIncomeRepository repository;

    @Inject
    private FixedIncomeService service;

    private FixedIncome fixedIncome = null;

    @BeforeEach
    void setUp() {
        fixedIncome = new FixedIncome();
        fixedIncome.setId(1L);
        fixedIncome.setAmount(BigDecimal.TEN);
        fixedIncome.setRate(BigDecimal.valueOf(125));
        fixedIncome.setDate(LocalDate.of(2020, 8, 28));
        fixedIncome.setDueDate(fixedIncome.getDate().plusYears(3));
    }

    @Test
    @DisplayName("Test saving a fixed income.")
    void saveFixedIncome() {
        final FixedIncome income = new FixedIncome();
        income.setIndex("CDI");
        income.setIssuer("Issuer");
        income.setAmount(BigDecimal.TEN);
        income.setDueDate(LocalDate.now());
        income.setType(FixedIncomeType.DAILY);
        income.setRate(BigDecimal.valueOf(125));

        final FixedIncome save = service.save(income);

//        assertEquals(1, save.getId());
    }

    @Test
    @DisplayName("Test for a fixed income but get none.")
    void getNoneFixedIncome() {
        when(repository.findAll()).thenReturn(Collections.EMPTY_LIST);
        final List<FixedIncome> fixedIncomes = service.findAll();

        assertEquals(0, fixedIncomes.size());
    }

    @Test
    @DisplayName("Test for a fixed income.")
    void getFixedIncome() {
        when(repository.findAll()).then(invocation -> Collections.singletonList(fixedIncome));
        final List<FixedIncome> fixedIncomes = service.findAll();

        assertEquals(1, fixedIncomes.size());
        assertEquals(1, fixedIncomes.get(0).getId());
        assertEquals(BigDecimal.TEN, fixedIncomes.get(0).getAmount());
    }

    @Test
    @DisplayName("Test for a fixed income calculation setting a until date.")
    void calcIncome() {
        when(repository.findAll()).then(invocation -> Collections.singletonList(fixedIncome));
        final List<FixedIncome> fixedIncomes = service.calcIncome(LocalDate.of(2020, 11, 24));

        assertEquals(1, fixedIncomes.size());
        assertEquals(BigDecimal.ZERO, fixedIncomes.get(0).getIof());
        assertEquals(0, BigDecimal.valueOf(0.01).compareTo(fixedIncomes.get(0).getIr()));
        assertEquals(0, BigDecimal.valueOf(0.05).compareTo(fixedIncomes.get(0).getGrossIncome().setScale(2, RoundingMode.HALF_UP)));
    }

    @Test
    @DisplayName("Test a fixed income on same day investment")
    void skipCalcIncome() {
        when(repository.findAll()).then(invocation -> Collections.singletonList(fixedIncome));
        final List<FixedIncome> fixedIncomes = service.calcIncome(LocalDate.of(2020, 8, 28));

        assertEquals(1, fixedIncomes.size());
        assertEquals(BigDecimal.ZERO, fixedIncomes.get(0).getIof());
        assertEquals(0, BigDecimal.valueOf(0.00).compareTo(fixedIncomes.get(0).getIr()));
        assertEquals(0, BigDecimal.valueOf(0.00).compareTo(fixedIncomes.get(0).getGrossIncome().setScale(2, RoundingMode.HALF_UP)));
    }

    @MockBean(FixedIncomeRepository.class)
    FixedIncomeRepository repository() {
        return mock(FixedIncomeRepository.class);
    }

}