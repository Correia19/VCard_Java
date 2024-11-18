package com.example.vcardapp;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class Notificacoes implements Serializable {

    long data;
    String descricao;
    String nEnviou;
    String nRecebeu;
    Double valor;
    Boolean lida;
    String dataParsed;

    public Notificacoes(String descricao, String nEnviou, String nRecebeu, Double valor, Boolean lida) {
        this.data = System.currentTimeMillis();
        this.descricao = descricao;
        this.nEnviou = nEnviou;
        this.nRecebeu = nRecebeu;
        this.valor = valor;
        this.lida = lida;
    }

    public Notificacoes() {
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

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    public Boolean getLida() {
        return lida;
    }

    public void setLida(Boolean lida) {
        this.lida = lida;
    }
    public String getDataParsed() {
        return dataParsed;
    }

    public void setDataParsed(String dataParsed) {
        this.dataParsed = dataParsed;
    }

    // Funcoes comuns
    public static String convertTimestampToDate(long timestamp) {
        // Convert timestamp to Date and format it as "dd-MM-yyyy"
        Date date = new Date(timestamp);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        return dateFormat.format(date);
    }

    public static ArrayList<Notificacoes> sortNotificationsList(ArrayList<Notificacoes> notificationsList) {
        // Create a custom comparator for sorting based on date
        Collections.sort(notificationsList, new Comparator<Notificacoes>() {
            @Override
            public int compare(Notificacoes t1, Notificacoes t2) {
                return Long.compare(t2.getData(), t1.getData());

            }
        });
        return notificationsList; // Return the sorted list
    }
}
