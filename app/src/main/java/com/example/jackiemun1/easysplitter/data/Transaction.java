package com.example.jackiemun1.easysplitter.data;

public class Transaction {

    private double price;
    private String description;
    private String buyer;
    private String uId;

    public Transaction() {
    }

    public Transaction(double price, String description, String buyer, String uId) {
        this.price = price;
        this.description = description;
        this.buyer = buyer;
        this.uId = uId;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBuyer() {
        return buyer;
    }

    public void setBuyer(String buyer) {
        this.buyer = buyer;
    }

    public String getUid() {
        return uId;
    }

    public void setUid(String uId) {
        this.uId = uId;
    }
}
