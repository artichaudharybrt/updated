package com.gamegards.gaming27.Details;

import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gamegards.gaming27.BaseActivity;
import com.gamegards.gaming27.Details.Adapter.GameDetailsAdapter;
import com.gamegards.gaming27.Details.Menu.DialogBetCommHistory;
import com.gamegards.gaming27.Details.Menu.DialogDepositBonusHistory;
import com.gamegards.gaming27.Details.Menu.DialogDepositHistory;
import com.gamegards.gaming27.Details.Menu.DialogRebateHistory;
import com.gamegards.gaming27.Details.Menu.DialogRedeemHistory;
import com.gamegards.gaming27.Details.Menu.DialogReferralLevel;
import com.gamegards.gaming27.Details.Menu.DialogSalaryHistory;
import com.gamegards.gaming27.Details.Menu.DialogTransactionHistory;
import com.gamegards.gaming27.Interface.OnItemClickListener;
import com.gamegards.gaming27.R;

import java.util.ArrayList;
import java.util.List;

public class GameDetails_A extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_details);

        initGameDetailsList();

    }

    private void initGameDetailsList() {

        RecyclerView recyclerView = findViewById(R.id.recDetailsList);
        recyclerView.setLayoutManager(new GridLayoutManager(this,3));
        GameDetailsAdapter detailsAdapter = new GameDetailsAdapter(this);

        detailsAdapter.onItemSelectListener(new OnItemClickListener() {
            @Override
            public void Response(View v, int position, Object object) {
     //           DialogGameHistory dialogGameHistory = new DialogGameHistory(GameDetails_A.this);
                DialogRedeemHistory dialogRedeemHistory = new DialogRedeemHistory(GameDetails_A.this);
                DialogTransactionHistory dialogTransactionHistory = new DialogTransactionHistory(GameDetails_A.this);
//                DialogReferralUser dialogReferralUser =  new DialogReferralUser(GameDetails_A.this);
//                DialogReferralUserPurch dialogReferralUserPurch =  new DialogReferralUserPurch(GameDetails_A.this);
                DialogReferralLevel dialogReferralLevel =  new DialogReferralLevel(GameDetails_A.this);
                DialogDepositBonusHistory dialogDepositBonusHistory =  new DialogDepositBonusHistory(GameDetails_A.this);
                DialogSalaryHistory dialogSalaryHistory =  new DialogSalaryHistory(GameDetails_A.this);
                DialogBetCommHistory dialogBetCommHistory =  new DialogBetCommHistory(GameDetails_A.this);
                DialogRebateHistory dialogRebateHistory =  new DialogRebateHistory(GameDetails_A.this);
                DialogDepositHistory dialogAmountHistory =  new DialogDepositHistory(GameDetails_A.this);

              /*   if(position == 0)
                {
                    dialogGameHistory.show();
                }
                else */

                if(position == 0)
                {
                    dialogRedeemHistory.show();
                }
                else if(position == 1)
                {
                    dialogTransactionHistory.show();
                }
//                else if(position == 3)
//                {
//                    dialogReferralUser.show();
//                }
//                else if(position == 4)
//                {
//                    dialogReferralUserPurch.show();
//                }
                else if(position == 2)
                {
                    dialogReferralLevel.show();
                }else if(position == 3)
                {
                    dialogDepositBonusHistory.show();
                }else if(position == 4)
                {
                    dialogSalaryHistory.show();
                }else if(position == 5)
                {
                    dialogBetCommHistory.show();
                }else if(position == 6)
                {
                    dialogRebateHistory.show();
                }else if(position == 7)
                {
                    dialogAmountHistory.show();
                }
            }
        });

        List<GameDetailsModel> gameDetailsModels = new ArrayList<>();

     //   gameDetailsModels.add(new GameDetailsModel("1","Games History",R.drawable.ic_game_console));
        gameDetailsModels.add(new GameDetailsModel("2","Redeems",R.drawable.ic_game_redeem));
        gameDetailsModels.add(new GameDetailsModel("3","Transactions",R.drawable.ic_game_transaction));
//        gameDetailsModels.add(new GameDetailsModel("4","Referral",R.drawable.ic_game_transaction));
//        gameDetailsModels.add(new GameDetailsModel("5","Referral Purchase",R.drawable.ic_game_transaction));
        gameDetailsModels.add(new GameDetailsModel("4","Level Wise Refer",R.drawable.ic_game_transaction));
        gameDetailsModels.add(new GameDetailsModel("5","Deposit Bonus",R.drawable.ic_game_transaction));
        gameDetailsModels.add(new GameDetailsModel("6","Salary Income",R.drawable.ic_game_transaction));
        gameDetailsModels.add(new GameDetailsModel("7","Bet Commission",R.drawable.ic_game_transaction));
        gameDetailsModels.add(new GameDetailsModel("8","Rebate History",R.drawable.ic_game_transaction));
        gameDetailsModels.add(new GameDetailsModel("9","Deposit History",R.drawable.ic_game_transaction));

        detailsAdapter.setArrayList(gameDetailsModels);
        recyclerView.setAdapter(detailsAdapter);

    }

    public void onBack(View view) {
        onBackPressed();
    }

}