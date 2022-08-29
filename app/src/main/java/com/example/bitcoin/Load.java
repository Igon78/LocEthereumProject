package com.example.bitcoin;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.web3j.crypto.WalletUtils;

public class Load extends AppCompatActivity {


    public static String walletname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load2);
        walletname="";
    }


    public void loadWallet(View v) {
        try {
            TextView txtaddress=findViewById(R.id.txtAddress);
            EditText Edtpassword = findViewById(R.id.edtTxtPassword);
            EditText Edtname = findViewById(R.id.edtTxtName);
            this.walletname = Edtname.getText().toString(); //pobranie podanej nazwy portfela
            String path = MainActivity.wallets.get(walletname); //pobranie ścieżki do pliku portfela z hashmapy
            MainActivity.credentials = WalletUtils.loadCredentials(Edtpassword.getText().toString(), path); //pobranie danych z pliku portfela
            Toast.makeText(this, "Your address is " + MainActivity.credentials.getAddress(), Toast.LENGTH_LONG).show(); //wyświetlenie informacji o załadowanym portfelu
            txtaddress.setText("Your address is :" + MainActivity.credentials.getAddress()); // wyświetlenie adresu portfela w textview
        } catch (Exception e) {
            ShowToast("Wallet doesn't exist or bad password");
        }
    }
    public void ShowToast(String message) {
        runOnUiThread(() -> {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        });
    }
}