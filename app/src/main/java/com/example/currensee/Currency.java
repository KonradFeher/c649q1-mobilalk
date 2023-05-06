package com.example.currensee;

import java.util.Date;

public class Currency {
    private String fullName;
    private String abbreviation;
    private String symbol;
    // value in EUR
    private float relativeValue;
    private Date updatedAt;

    public Currency(String fullName, String abbreviation, String symbol, float relativeValue, Date updatedAt) {
        this.fullName = fullName;
        this.abbreviation = abbreviation;
        this.symbol = symbol;
        this.relativeValue = relativeValue;
        this.updatedAt = updatedAt;
    }

    public Currency(String fullName, String abbreviation, String symbol, float relativeValue) {
        this.fullName = fullName;
        this.abbreviation = abbreviation;
        this.symbol = symbol;
        this.relativeValue = relativeValue;
        this.updatedAt = new Date(System.currentTimeMillis());
    }

    public Currency() {
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
    public float getRelativeValue() {
        return relativeValue;
    }
    public Date getUpdatedAt() {
        return updatedAt;
    }
}
