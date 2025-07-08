package com.first_player_games.ModelResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LudoWinners {

    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("winner")
    @Expose
    private List<Winner> winner;
    @SerializedName("code")
    @Expose
    private Integer code;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Winner> getWinner() {
        return winner;
    }

    public void setWinner(List<Winner> winner) {
        this.winner = winner;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public class Winner {

        @SerializedName("room_code")
        @Expose
        private String roomCode;
        @SerializedName("amount")
        @Expose
        private String amount;
        @SerializedName("winner")
        @Expose
        private String winner;
        @SerializedName("lost")
        @Expose
        private String lost;
        @SerializedName("winner_id")
        @Expose
        private String winner_id;

        public String getRoomCode() {
            return roomCode;
        }

        public void setRoomCode(String roomCode) {
            this.roomCode = roomCode;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getWinner() {
            return winner;
        }

        public void setWinner(String winner) {
            this.winner = winner;
        }

        public String getLost() {
            return lost;
        }

        public void setLost(String lost) {
            this.lost = lost;
        }

        public String getWinner_id() {
            return winner_id;
        }

        public void setWinner_id(String winner_id) {
            this.winner_id = winner_id;
        }
    }

}
