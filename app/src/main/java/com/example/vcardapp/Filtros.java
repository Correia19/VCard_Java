package com.example.vcardapp;

public class Filtros {
    private String transactionType;
    private String ascOUdesc;
    private String ordenationType;
    private Long dateDe;
    private Long dateAte;

    public Filtros(String transactionType, String ascOUdesc, String ordenationType, Long dateDe, Long dateAte) {
        this.transactionType = transactionType;
        this.ascOUdesc = ascOUdesc;
        this.ordenationType = ordenationType;
        this.dateDe = dateDe;
        this.dateAte = dateAte;
    }
    public Filtros() {

    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getAscOUdesc() {
        return ascOUdesc;
    }

    public void setAscOUdesc(String ascOUdesc) {
        this.ascOUdesc = ascOUdesc;
    }

    public String getOrdenationType() {
        return ordenationType;
    }

    public void setOrdenationType(String ordenationType) {
        this.ordenationType = ordenationType;
    }

    public Long getDateDe() {
        return dateDe;
    }

    public void setDateDe(Long dateDe) {
        this.dateDe = dateDe;
    }

    public Long getDateAte() {
        return dateAte;
    }

    public void setDateAte(Long dateAte) {
        this.dateAte = dateAte;
    }
}