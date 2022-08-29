package com.example.bitcoin;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import java.io.File;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.Security;
import java.util.HashMap;

public class Create extends AppCompatActivity {

    public File file;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_create);
            //deserializeMap();


        }
    public void createWallet(View v) throws InvalidAlgorithmParameterException, CipherException, NoSuchAlgorithmException, IOException, NoSuchProviderException {
        EditText Edtname = findViewById(R.id.edtTxtName);
        String walletdir = getFilesDir()+"/"+Edtname.getText().toString(); //stworzenie ścieżki do pliku
        EditText Edtpassword = findViewById(R.id.edtTxtPassword);
        String password = Edtpassword.getText().toString();

        file=new File(walletdir); // stworzenie instancji pliku w danej ścieżce
        if (file.exists()) {
            ShowToast("Wallet is already created");
        }
        else {
            file.mkdirs();
            String walletname = WalletUtils.generateNewWalletFile(password,file); //stworzenie pliku portfela
            ShowToast("Wallet's Directory : "+file.getAbsolutePath() +"/"+ walletname); //wyświetlenie wiadomości o stworzeniu pliku
            MainActivity.wallets.put(Edtname.getText().toString(),file.getAbsolutePath() +"/"+ walletname); //dodanie do hashmapy portfeli nazwy portfela oraz ścieżki do jego pliku
            //serializeMap();
        }
    }

    public void ShowToast(String message) { //funkcja do wyświetlania wiadomości
        runOnUiThread(() -> {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        });
    }
}