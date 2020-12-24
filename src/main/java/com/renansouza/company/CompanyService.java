package com.renansouza.company;

import com.renansouza.base.SortingAndOrderArguments;
import io.micronaut.transaction.annotation.ReadOnly;
import io.micronaut.transaction.annotation.TransactionalAdvice;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Singleton
public class CompanyService {

    @Inject
    CompanyRepository repository;

    private final static List<String> VALID_PROPERTY_NAMES = Arrays.asList("id", "name", "registration");
    private final EntityManager entityManager;

    public CompanyService(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @ReadOnly
    List<Company> findAll(@NotNull SortingAndOrderArguments args) {
        String qlString = "SELECT c FROM Company as c";
        if (args.getOrder().isPresent() && args.getSort().isPresent() && VALID_PROPERTY_NAMES.contains(args.getSort().get())) {
            qlString += " ORDER BY c." + args.getSort().get() + " " + args.getOrder().get().toLowerCase();
        }

        TypedQuery<Company> query = entityManager.createQuery(qlString, Company.class);
        args.getOffset().ifPresent(query::setFirstResult);
        args.getMax().ifPresent(query::setMaxResults);

        final List<Company> companies = query.getResultList();

        if (companies.isEmpty()) {
            throw new CompanyNotFoundException("None company found.");
        }

        return companies;
    }

    @ReadOnly
    Company findById(final long id) {
        final Optional<Company> company = repository.findById(id);
        if (company.isEmpty()) {
            throw new CompanyNotFoundException("Company not found with id " + id + ".");
        }

        return company.get();
    }

    @TransactionalAdvice
    Company save(final Company company) {
        company.canSave();

        if (repository.findByNameIgnoreCase(company.getName()).isPresent() ||
            repository.findByRegistration(company.getRegistration()).isPresent()) {
            throw new  CompanyException("Company already saved.");
        }

        return repository.save(company);
    }

    @TransactionalAdvice
    void update(final Company company) {
        company.canUpdate();

        final var companyToUpdate = repository.findById(company.getId());
        if (companyToUpdate.isEmpty()) {
            throw new CompanyNotFoundException("Company not found with id " + company.getId() + ".");
        }

        companyToUpdate.ifPresent(
            cia -> {
                cia.setName(company.getName());
                cia.setRegistration(company.getRegistration());
                cia.setDeleted(company.isDeleted());
                cia.setBank(company.isBank());
                cia.setBroker(company.isBroker());
                cia.setManager(company.isManager());
                cia.setListedCompany(company.isListedCompany());
                cia.setAdministrator(company.isAdministrator());

                repository.update(cia);
        });
    }

    @TransactionalAdvice
    void delete(final long id) {
        final Optional<Company> companyToUpdate = repository.findById(id);
        if (companyToUpdate.isEmpty()) {
            throw new CompanyNotFoundException("Company not found with id " + id + ".");
        }

        companyToUpdate.ifPresent(
            company -> {
                company.setDeleted(true);
                repository.update(company);
        });
    }

}