package com.odairtns.trackyourexpenses.Models;

public class TripRecord  {
    int id, tripID, amountType;
    String date, currency, details, expType, paymentMethod;
    Double amount, amountStdCurrency, storedExgAmount;
    public TripRecord() {
    }


    public TripRecord(int id, int tripID, String date, String currency, String details,
                      String expType, Double amount, int amountType) {
        this.id = id;
        this.tripID = tripID;
        this.date = date;
        this.currency = currency;
        this.details = details;
        this.expType = expType;
        this.amount = amount;
        this.amountType = amountType;

    }


    public TripRecord(int id, int tripID, String date, String currency, String details,
                      String expType, Double amount, Double amountStdCurrency, int amountType) {
        this.id = id;
        this.tripID = tripID;
        this.date = date;
        this.currency = currency;
        this.details = details;
        this.expType = expType;
        this.amount = amount;
        this.amountStdCurrency = amountStdCurrency;
        this.amountType = amountType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTripID() {
        return tripID;
    }

    public void setTripID(int tripID) {
        this.tripID = tripID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getExpType() {
        return expType;
    }

    public void setExpType(String expType) {
        this.expType = expType;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getAmountStdCurrency() {
        return amountStdCurrency;
    }

    public void setAmountStdCurrency(Double amountStdCurrency) {
        this.amountStdCurrency = amountStdCurrency;
    }

    public Double getStoredExgAmount() {
        return storedExgAmount;
    }

    public void setStoredExgAmount(Double storedExgAmount) {
        this.storedExgAmount = storedExgAmount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public int getAmountType() {
        return amountType;
    }

    public void setAmountType(int amountType) {
        this.amountType = amountType;
    }

}
