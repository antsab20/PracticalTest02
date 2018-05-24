package com.example.sabin.colocviu2_test;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    private EditText wordClient = null;
    private EditText serverPort = null;
    private EditText wordDef = null;
    private Button serverConnect = null;
    private Button clientConnect = null;
    private EditText clientServerAddress = null;
    private EditText clientServerPort = null;
    private ServerThread serverThread = null;
    private ClientThread clientThread = null;

    private ServerConnectButtonClickListener connectButtonClickListener = new ServerConnectButtonClickListener();
    private class ServerConnectButtonClickListener implements Button.OnClickListener {

        @Override
        public void onClick(View v) {
            Log.d(TAG,"serverConnect onClick event fired!");

            if (!serverPort.getText().toString().equals("")) {
                Log.d(TAG,"Server port: " + serverPort.getText().toString());
            } else {
                Log.d(TAG,"Server port should be numeric type: " + serverPort.getText().toString());
                return;
            }
            String port = serverPort.getText().toString();
            serverThread = new ServerThread(Integer.parseInt(port));
            if (serverThread.getServerSocket() == null) {
                Log.e(TAG, "[MAIN ACTIVITY] Could not create server thread!");
                return;
            }
            serverThread.start();
        }
    }

    ClientConnectButtonClickListener clientConnectButtonClickListener = new ClientConnectButtonClickListener();
    private class ClientConnectButtonClickListener implements Button.OnClickListener {

        @Override
        public void onClick(View v) {
            Log.d(TAG,"clientConnect onClick event fired!");

            String clientAddress = clientServerAddress.getText().toString();
            String clientPort = clientServerPort.getText().toString();
            String clientWord = wordClient.getText().toString();
            if (clientAddress == null || clientAddress.isEmpty()
                    || clientPort == null || clientPort.isEmpty()) {
                Log.e(TAG,"clientConnect empty fields");
                return;
            }
            if (serverThread == null || !serverThread.isAlive()) {
                Log.e(TAG,"server is dead!");
                return;
            }
            clientThread = new ClientThread(clientAddress, Integer.parseInt(clientPort), clientWord, wordDef);
            clientThread.start();
        }
    }

    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG,"onCreate method was invoked!");

        serverPort = (EditText) findViewById(R.id.server_port_edit_text);
        serverConnect = (Button) findViewById(R.id.connect_button);
        serverConnect.setOnClickListener(connectButtonClickListener);
        clientServerAddress = (EditText) findViewById(R.id.client_address);
        clientServerPort = (EditText) findViewById(R.id.client_port);
        clientConnect = (Button) findViewById(R.id.get_weather_button);
        wordClient = (EditText) findViewById(R.id.client_city);
        wordDef = (EditText) findViewById(R.id.word_def);
        clientConnect.setOnClickListener(clientConnectButtonClickListener);
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "[MAIN ACTIVITY] onDestroy() callback method has been invoked");
        if (serverThread != null) {
            serverThread.stopThread();
        }
        super.onDestroy();
    }
}
