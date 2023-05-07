package com.example.currensee;

import java.io.Serializable;
import java.util.Date;

public class Currency implements Serializable {
    private String fullName;
    private String abbreviation;
    private String symbol;
    // value in EUR
    private float value;
    private Date updatedAt;

    public Currency(String fullName, String abbreviation, String symbol, float value, Date updatedAt) {
        this.fullName = fullName;
        this.abbreviation = abbreviation;
        this.symbol = symbol;
        this.value = value;
        this.updatedAt = updatedAt;
    }

    public Currency(String fullName, String abbreviation, String symbol, float value) {
        this.fullName = fullName;
        this.abbreviation = abbreviation;
        this.symbol = symbol;
        this.value = value;
        this.updatedAt = new Date(System.currentTimeMillis());
    }


    public Currency() {
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getFullName() {
        return fullName;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public String getSymbol() {
        return symbol;
    }

    public float getValue() {
        return value;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void updateAt() {
        this.updatedAt = new Date(System.currentTimeMillis());
    }
}
