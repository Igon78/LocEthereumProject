package com.example.bitcoin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;

public class Info extends AppCompatActivity {
    Credentials creds = MainActivity.credentials;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        TextView name=findViewById(R.id.edtName);
        TextView address=findViewById(R.id.edtAddress);
        TextView balance=findViewById(R.id.balance);
        try {
            name.setText(Load.walletname);
            address.setText(creds.getAddress());
            balance.setText(MainActivity.retrieveBalance().toString());
        } catch (Exception e) {
            ShowToast("No wallet has been loaded");
        }


    }
        @Override
        public void onBackPressed() {
            super.onBackPressed();
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK); //ŻEBY WRÓCIĆ NA EKRAN GŁÓWNY
            startActivity(intent);
        }

    public void loadPrv(View v){
        TextView balance=findViewById(R.id.edtKeyPriv);
        balance.setText(this.creds.getEcKeyPair().getPrivateKey().toString());
    }

    @Override
    public void onActivityReenter(int resultCode, Intent data) {
        super.onActivityReenter(resultCode, data);
    }
    public void ShowToast(String message) {
        runOnUiThread(() -> {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        });
    }
}
