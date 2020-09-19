package com.renansouza.companies;

import lombok.Getter;

@Getter
public enum Listining {
    NM("Novo Mercado"),
    N1("Nível 1"),
    N2("Nível 2"),
    BM("Bovespa Mais"),
    BMN2("Bovespa Mais Nível 2"),
    DR1("BDR Nível 1"),
    DR2("BDR Nível 2"),
    DR3("BDR Nível 3");

    private String description;

    Listining(String description) {
        this.description = description;
    }

}