package com.example.bitcoin;

import androidx.appcompat.app.AppCompatActivity;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.http.HttpService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.Provider;
import java.security.Security;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    private Button btnCreate, btnLoad, btnConnect, btnInfo, btnMap, btnLocation;
    public static Web3j web3;
    public static Credentials credentials;
    public static String prv = "17a3737feeba9b7b43594cb0351604b24c2e9c4eaa02e7c3d6c7131d665121a5";
    public static Credentials zero_acc_credentials = Credentials.create(prv);
    public static HashMap<String, String> wallets = new HashMap<>();
    public static DatabaseReference dataref;
    public static FirebaseDatabase data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(MainActivity.this);
        setContentView(R.layout.activity_main2);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        web3 = Web3j.build(new HttpService("https://rinkeby.infura.io/v3/8dc81f85c71e4c0cbcb26e4a9754b56e"));
        data=FirebaseDatabase.getInstance("https://geocoin-9da1d-default-rtdb.europe-west1.firebasedatabase.app/");
        dataref= data.getReference("message");
        setupBouncyCastle();
        deserializeMap();
        initViews();

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Create.class);
                startActivity(intent);
            }
        });

        btnLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Load.class);
                serializeMap();
                startActivity(intent);
            }
        });

        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Connect.class);
                startActivity(intent);
            }
        });

        btnInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Info.class);
                startActivity(intent);
            }
        });

        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });

    }

    private void initViews() {
        btnCreate = findViewById(R.id.btnCreate);
        btnLoad = findViewById(R.id.btnLoad);
        btnInfo = findViewById(R.id.btnInfo);
        btnConnect = findViewById(R.id.btnConnect);
        btnMap = findViewById(R.id.btnMap);

    }

    private void setupBouncyCastle() {
        final Provider provider = Security.getProvider(BouncyCastleProvider.PROVIDER_NAME);
        if (provider == null) {
            return;
        }
        if (provider.getClass().equals(BouncyCastleProvider.class)) {
            return;
        }
        Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME);
        Security.insertProviderAt(new BouncyCastleProvider(), 1);
    }

    public void ShowToast(String message) {
        runOnUiThread(() -> {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        });
    }
    // hashmapa z sciezkami do portfeli jest serializowana do pliku wallets.ser,
    // aby nastęnie można było z tego pliku załadować je następnym razem po uruchomieniu aplikacji, czy dodaniu nowego portfela
    public void serializeMap() {
        try {
            FileOutputStream fileout = new FileOutputStream(getFilesDir() + "/" + "wallets.ser"); //instancja obiektu do zapisu danych do pliku
            ObjectOutputStream out = new ObjectOutputStream(fileout); //instancja klasy do zapisywania objektu do pliku
            out.writeObject(wallets); //zapisanie hashmapy do pliku
            out.close(); //zamknięcie strumieni
            fileout.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //deserializacja hashmapy z informacjami o ścieżkach portfeli
    public void deserializeMap() {

        try {
            FileInputStream filein = new FileInputStream(getFilesDir() + "/" + "wallets.ser"); //instancja obiektu do odczytu danych z pliku
            ObjectInputStream in = new ObjectInputStream(filein); //instancja obiektu do pobrania obiektu z pliku

            wallets = (HashMap<String, String>) in.readObject(); //zaladowanie obiektu hashmapy
            in.close(); //zamknięcie strumieni
            filein.close();
        } catch (FileNotFoundException e) {
            serializeMap(); // w przypadku braku pliku, najpierw jest on tworzony poprzez wywołanie metody serialize z obecnie istniejącej hashmapy
            deserializeMap(); // a następnie jest znów deserializowany aby działanie funkcji pomyślnie się zakończyło
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    public static BigDecimal retrieveBalance() {
        //get wallet's balance
        try {
            EthGetBalance balanceWei = web3.ethGetBalance(credentials.getAddress(), DefaultBlockParameterName.LATEST).sendAsync() //"0xf5D1D88181C77BA02463b6741a205729Ac300A8a"
                    .get();
            BigDecimal balanceEth = new BigDecimal(balanceWei.getBalance()).divide(new BigDecimal(1000000000000000000L), 18, RoundingMode.HALF_UP);
            return balanceEth;
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return BigDecimal.valueOf(-1);
    }
}


