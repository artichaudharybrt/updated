package com.first_player_games.Api;

public class EndPoints {

    public static String SERVER_PATH = "ws://games.androappstech.in:1234";
    //    public static String BASE_URL = "https://demo.androappstech.in/";
//    public static final String SOCKET_IP = "https://demo-socket.androappstech.in/ludo";  //URL FOR DEMO


    public static String BASE_URL = "https://bigjackpot.club/";
    public static final String SOCKET_IP = "https://socket.bigjackpot.club/ludo_old";  //URL FOR DEVELOPMENT


  //  public static final String BASE_URL = "https://mg.multiapisoft.com/";  //URL FOR DEVELOPMENT
  //  public static final String SOCKET_IP = "https://socket.multiapisoft.com/";

    private static final String API = "api/";

    public static final String email_login = API+ "User/email_login";
    public static final String user_profile = API+ "User/profile";


    public static final String get_table_master = API+ "LudoOld/get_table_master";
    public static final String get_table = API+ "LudoOld/get_table";
    public static final String join_table = API+ "LudoOld/join_table";
    public static final String start_game = API+ "LudoOld/start_game";
    public static final String make_winner = API+ "LudoOld/make_winner";
    public static final String leave_table = API+ "LudoOld/leave_table";
    public static final String gameStatus = API+ "LudoOld/status";
    public static final String rolldice = API+ "/rolldice";
    public static final String ludo_winners = API+ "User/ludo_winners";

    public static final String token = "c7d3965d49d4a59b0da80e90646aee77548458b3377ba3c0fb43d5ff91d54ea28833080e3de6ebd4fde36e2fb7175cddaf5d8d018ac1467c3d15db21c11b6909";

}
