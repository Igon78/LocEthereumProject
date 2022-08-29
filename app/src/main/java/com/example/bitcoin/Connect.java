package com.example.bitcoin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.kenai.jffi.Main;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;

import java.math.BigDecimal;


public class Connect extends AppCompatActivity {

    private Button btnConnectANode, btnBalance, btnFree;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK); //ŻEBY WRÓCIĆ NA EKRAN GŁÓWNY
        startActivity(intent);
    }
    public void balanceGet(View v){
        TextView balance = findViewById(R.id.txtBalance);
        try {
            balance.setText(MainActivity.retrieveBalance().toString());
        } catch (Exception e) {
            ShowToast("You must load your wallet first");
        }

    }
    public void connectToEthNetwork(View v) {

        MainActivity.web3 = Web3j.build(new HttpService("https://rinkeby.infura.io/v3/8dc81f85c71e4c0cbcb26e4a9754b56e"));
        try {
            //if the client version has an error the user will not gain access if successful the user will get connnected
            Web3ClientVersion clientVersion = MainActivity.web3.web3ClientVersion().sendAsync().get();
            if (!clientVersion.hasError()) {
                ShowToast("Connected!");
            } else {
                ShowToast(clientVersion.getError().getMessage());
            }
        } catch (Exception e) {
            ShowToast(e.getMessage());
        }
    }
    public void ShowToast(String message) {
        runOnUiThread(() -> {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        });
    }
    public void gettingfreeeth(View v){
        try{

            TransactionReceipt receipt = Transfer.sendFunds(MainActivity.web3, MainActivity.zero_acc_credentials,MainActivity.credentials.getAddress(), BigDecimal.valueOf(100000), Convert.Unit.GWEI).send();
            Toast.makeText(this, "Transaction successful: " +receipt.getTransactionHash(), Toast.LENGTH_LONG).show();
        }
        catch(Exception e){
            ShowToast("low balance");

        }
    }
}