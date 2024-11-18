package com.example.vcardapp;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class Transacoes implements Serializable {

    long data;
    String descricao;
    String nEnviou;
    String nRecebeu;
    Double saldoAposEnvio;
    Double saldoAposReceber;
    Double valor;
    String dataParsed;

    public Transacoes() {
    }

    public Transacoes(String descricao, String nEnviou, String nRecebeu, Double saldoAposEnvio, Double saldoAposReceber, Double valor) {
        this.data = System.currentTimeMillis();
        this.descricao = descricao;
        this.nEnviou = nEnviou;
        this.nRecebeu = nRecebeu;
        this.saldoAposEnvio = saldoAposEnvio;
        this.saldoAposReceber = saldoAposReceber;
        this.valor = valor;
    }

    public long getData() {
        return data;
    }

    public void setData(long data) {
        this.data = data;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getnEnviou() {
        return nEnviou;
    }

    public void setnEnviou(String nEnviou) {
        this.nEnviou = nEnviou;
    }

    public String getnRecebeu() {
        return nRecebeu;
    }

    public void setnRecebeu(String nRecebeu) {
        this.nRecebeu = nRecebeu;
    }

    public Double getSaldoAposEnvio() {
        return saldoAposEnvio;
    }

    public void setSaldoAposEnvio(Double saldoAposEnvio) {
        this.saldoAposEnvio = saldoAposEnvio;
    }

    public Double getSaldoAposReceber() {
        return saldoAposReceber;
    }

    public void setSaldoAposReceber(Double saldoAposReceber) {
        this.saldoAposReceber = saldoAposReceber;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    public String getDataParsed() {
        return dataParsed;
    }

    public void setDataParsed(String dataParsed) {
        this.dataParsed = dataParsed;
    }

    // funcoes comuns historico

    public static String convertTimestampToDate(long timestamp) {
        // Convert timestamp to Date and format it as "dd-MM-yyyy"
        Date date = new Date(timestamp);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        return dateFormat.format(date);
    }

    public static Long convertDateToTimestamp(String dateString) throws ParseException {
            // Parse the date string
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            Date date = dateFormat.parse(dateString);

            // Convert the Date to timestamp
            return date.getTime();
        }

    public static ArrayList<Transacoes> sortTransactionList(ArrayList<Transacoes> transactionList) {
        // Create a custom comparator for sorting based on date
        Collections.sort(transactionList, new Comparator<Transacoes>() {
            @Override
            public int compare(Transacoes t1, Transacoes t2) {
                return Long.compare(t2.getData(), t1.getData());

            }
        });
        return transactionList; // Return the sorted list
    }

    public static long convertTimestampRoundUp(String dateString) throws ParseException {
        // split string intoday-month-year
        String[] dateSplit = dateString.split("-");
        int day = Integer.parseInt(dateSplit[0]);
        int month = Integer.parseInt(dateSplit[1]);
        int year = Integer.parseInt(dateSplit[2]);
        int hour = 23;
        int minute = 59;
        int second = 59;

        // Create a LocalDateTime instance
        LocalDateTime dateTime = LocalDateTime.of(year, month, day, hour, minute, second);

        // Convert LocalDateTime to Instant
        Instant instant = dateTime.atZone(ZoneId.systemDefault()).toInstant();

        // Get the timestamp in milliseconds
        return instant.toEpochMilli();
    }

    public static ArrayList<Transacoes> sortTransactionListByValue(ArrayList<Transacoes> transactionList) {
        // Create a custom comparator for sorting based on value
        Collections.sort(transactionList, new Comparator<Transacoes>() {
            @Override
            public int compare(Transacoes t1, Transacoes t2) {
                return Double.compare(t2.getValor(), t1.getValor());

            }
        });
        return transactionList; // Return the sorted list
    }

    public static ArrayList<Transacoes> applyFilters(ArrayList<Transacoes> transactionList, Filtros filtros, String userPhoneNumber) {
        // percorrer todas as transacoes
        // se a transacao nao estiver dentro dos filtros, remove-la da lista
        ArrayList<Transacoes> newTransactionList = new ArrayList<Transacoes>();
        newTransactionList.addAll(transactionList);

        // aumentar o intervalo 1 dia para incluir operacoes do mesmo dia mas com horas diferentes
        for (Transacoes transacao : transactionList) {

            // filtrar o tipo de transacao (credito ou debito)
            if (filtros.getTransactionType().equals("debit")) {
                if (!transacao.getnEnviou().equals(userPhoneNumber)) {
                    newTransactionList.remove(transacao);
                }
            } else if (filtros.getTransactionType().equals("credit")) {
                if (!transacao.getnRecebeu().equals(userPhoneNumber)) {
                    newTransactionList.remove(transacao);
                }
            }

            // filtrar pelo data range selecionado
            if (filtros.getDateDe() != null && filtros.getDateAte() != null) { // verificar por causa das conversoes de timstramps etc
                if (transacao.getData() < filtros.getDateDe() || transacao.getData() > filtros.getDateAte()) {
                    newTransactionList.remove(transacao);
                }
            } else if (filtros.getDateDe() != null) {
                if (transacao.getData() < filtros.getDateDe()) {
                    newTransactionList.remove(transacao);
                }
            } else if (filtros.getDateAte() != null) {
                if (transacao.getData() > filtros.getDateAte()) {
                    newTransactionList.remove(transacao);
                }
            }

        }
        return newTransactionList;
    }
}
