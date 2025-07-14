package com.gamegards.bigjackpot._LuckJackpot;

public class BetHistoryModel {
    private String betType;
    private String amount;
    private String gameId;
    private String status;
    private long timestamp;

    public BetHistoryModel(String betType, String amount, String gameId, String status) {
        this.betType = betType;
        this.amount = amount;
        this.gameId = gameId;
        this.status = status;
        this.timestamp = System.currentTimeMillis();
    }

    // Getters
    public String getBetType() { return betType; }
    public String getAmount() { return amount; }
    public String getGameId() { return gameId; }
    public String getStatus() { return status; }
    public long getTimestamp() { return timestamp; }

    // Setters
    public void setBetType(String betType) { this.betType = betType; }
    public void setAmount(String amount) { this.amount = amount; }
    public void setGameId(String gameId) { this.gameId = gameId; }
    public void setStatus(String status) { this.status = status; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
