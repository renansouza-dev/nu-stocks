package com.renansouza.rf;

import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

@Singleton
@Slf4j
public class FixedIncomeService {

    // TODO Find date in map -> https://stackoverflow.com/questions/7120052/java-hashmap-search-keys-for-a-date

    @Inject
    private FixedIncomeRepository repository;

    private static final Indexes indexes;
    private static final LocalDate NOW = LocalDate.now();
    private static final double DAILY_PERCENT = 0.003968254;
    private static final Set<LocalDate> holidays = new HashSet<>();
    private static final Set<DayOfWeek> weekend = EnumSet.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);

    static {
        holidays.add(LocalDate.of(2020, 1, 1));
        holidays.add(LocalDate.of(2020, 2, 24));
        holidays.add(LocalDate.of(2020, 2, 25));
        holidays.add(LocalDate.of(2020, 4, 10));
        holidays.add(LocalDate.of(2020, 4, 21));
        holidays.add(LocalDate.of(2020, 5, 1));
        holidays.add(LocalDate.of(2020, 6, 11));
        holidays.add(LocalDate.of(2020, 9, 7));
        holidays.add(LocalDate.of(2020, 10, 12));
        holidays.add(LocalDate.of(2020, 11, 2));
        holidays.add(LocalDate.of(2020, 11, 15));
        holidays.add(LocalDate.of(2020, 12, 25));


        // TODO ms para fazer webscrapping nessa pagina https://www.bcb.gov.br/controleinflacao/historicotaxasjuros
        final List<IndexDetails> indexDetails = Arrays.asList(
                new IndexDetails("18/06/2020", "05/08/2020", BigDecimal.valueOf(2.15)),
                new IndexDetails("06/08/2020", "26/11/2020", BigDecimal.valueOf(1.9))
        );

        indexes = new Indexes("CDI", "Certificado de deposito interbancrio", indexDetails);
    }

    FixedIncome save(final FixedIncome fixedIncome) {
        return repository.save(fixedIncome);
    }

    List<FixedIncome> get() {
        return (List<FixedIncome>) repository.findAll();
    }

    List<FixedIncome> calcIncome(LocalDate until) {
        final List<FixedIncome> incomes = new ArrayList<>();

        repository.findAll().forEach(income -> {
            final LocalDate localDate = until == null ? NOW : until;

            final long totalDays = income.getDate().datesUntil(localDate).count();
            income.getDate().datesUntil(localDate)
                    .filter(date -> !weekend.contains(date.getDayOfWeek()) && !holidays.contains(date))
                    .forEach(date -> calcIncome(income, date));

            income.setIof(calcIOF(income.getGrossIncome(), totalDays));
            income.setIr(calcIR(income.getGrossIncome(), totalDays));

            incomes.add(income);
        });

        return incomes;
    }

    private BigDecimal calcIOF(final BigDecimal grossIncome, final long totalDays) {
        return totalDays < 30
                ? BigDecimal.ONE
                            .subtract(BigDecimal.valueOf(100 - (totalDays * 3.33)))
                            .multiply(grossIncome)
                            .setScale(0, RoundingMode.FLOOR)
                : BigDecimal.ZERO;
    }

    private BigDecimal calcIR(final BigDecimal grossIncome, final long totalDays) {
        final BigDecimal multiplier;

        if (totalDays <= 180) {
            multiplier = BigDecimal.valueOf(22.5 / 100);
        } else if (totalDays <= 360) {
            multiplier = BigDecimal.valueOf(20.0 / 100);
        } else if (totalDays <= 720) {
            multiplier = BigDecimal.valueOf(17.5 / 100);
        } else {
            multiplier = BigDecimal.valueOf(15.0 / 100);
        }

        return grossIncome
                .multiply(multiplier, MathContext.DECIMAL64)
                .setScale(2, RoundingMode.HALF_UP);
    }

    private void calcIncome(final FixedIncome income, final LocalDate date) {
        if (date.equals(income.getDate())) {
            return;
        }

        final double indexValuePercent = (1 + (getIndexValue(date).doubleValue() / 100));
        final double dailyIndexPercent = Math.pow(indexValuePercent, DAILY_PERCENT) - 1;
        final double dailyRatePercent = dailyIndexPercent * (income.getRate().doubleValue() / 100) + 1;

        final BigDecimal multiplied = income.getAmount().add(income.getGrossIncome());
        final BigDecimal grossIncrement = multiplied.multiply(BigDecimal.valueOf(dailyRatePercent)).subtract(multiplied);

        income.setGrossIncome(grossIncrement.add(income.getGrossIncome()));
    }

    private BigDecimal getIndexValue(final LocalDate date) {
        final Optional<IndexDetails> details = indexes.getHistory().stream()
                .filter(indexDetails ->
                        (date.isEqual(indexDetails.getStart()) || date.isEqual(indexDetails.getEnd())) ||
                        (date.isAfter(indexDetails.getStart()) && date.isBefore(indexDetails.getEnd())))
                .findFirst();

        return details.isPresent() ? details.get().getValue() : BigDecimal.ZERO;
    }

}