package com.gamegards.bigjackpot.Details.Menu;

public class DepositHistoryModel {
    
    private String userId;
    private String transactionId;
    private String utr;
    private String phone;
    private String money;
    private String type;
    private String status;
    private String time;

    public DepositHistoryModel() {
    }

    public DepositHistoryModel(String userId, String transactionId, String utr, String phone, String money, String type, String status, String time) {
        this.userId = userId;
        this.transactionId = transactionId;
        this.utr = utr;
        this.phone = phone;
        this.money = money;
        this.type = type;
        this.status = status;
        this.time = time;
    }

    // Getters
    public String getUserId() {
        return userId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getUtr() {
        return utr;
    }

    public String getPhone() {
        return phone;
    }

    public String getMoney() {
        return money;
    }

    public String getType() {
        return type;
    }

    public String getStatus() {
        return status;
    }

    public String getTime() {
        return time;
    }

    // Setters
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public void setUtr(String utr) {
        this.utr = utr;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "DepositHistoryModel{" +
                "userId='" + userId + '\'' +
                ", transactionId='" + transactionId + '\'' +
                ", utr='" + utr + '\'' +
                ", phone='" + phone + '\'' +
                ", money='" + money + '\'' +
                ", type='" + type + '\'' +
                ", status='" + status + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
