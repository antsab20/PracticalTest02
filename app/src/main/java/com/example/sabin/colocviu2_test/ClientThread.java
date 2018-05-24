package com.example.sabin.colocviu2_test;

import android.provider.SyncStateContract;
import android.util.Log;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientThread extends Thread {
    private String address;
    private String word;
    private EditText wordDef;
    private int port;
    private Socket socket;

    public ClientThread(String address, int port, String word, EditText wordDef) {
        this.address = address;
        this.port = port;
        this.word = word;
        this.wordDef = wordDef;
    }

    @Override
    public void run() {
        Log.d("ClientThread", "Client with " + word + " spawned!");
        try {
            socket = new Socket(address, port);
            if (socket == null) {
                Log.e("ClientThread", "[CLIENT THREAD] Could not create socket!");
                return;
            }

            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);

            printWriter.println(word);
            printWriter.flush();

            String defs;
            while ((defs = bufferedReader.readLine()) != null) {
                Log.d("ClientThread", "The definition is: " + defs);
                final String finalizedDef = defs;
                wordDef.post(new Runnable() {
                    @Override
                    public void run() {
                        wordDef.setText(finalizedDef);
                    }
                });
                break;
            }
        }catch (IOException ioException) {
            Log.e("ClientThread", "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ioException) {
                    Log.e("ClientThread", "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
                }
            }
        }
    }
}
