package com.example.sabin.colocviu2_test;

import android.provider.SyncStateContract;
import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

import cz.msebera.android.httpclient.client.ClientProtocolException;

public class ServerThread extends Thread {
    private Integer port;
    private ServerSocket serverSocket = null;

    public ServerThread(int port) {
        this.port = port;
        try {
            this.serverSocket = new ServerSocket(port);
        } catch (IOException ioException) {
            Log.e("ServerThread", "An exception has occurred: " + ioException.getMessage());
        }
    }


    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Log.i("ServerThread", "[SERVER THREAD] Waiting for a client invocation...");
                Socket socket = serverSocket.accept();
                Log.i("ServerThread", "[SERVER THREAD] A connection request was received from " + socket.getInetAddress() + ":" + socket.getLocalPort());
                CommunicationThread communicationThread = new CommunicationThread(this, socket);
                communicationThread.start();
            }
        } catch (ClientProtocolException clientProtocolException) {
            Log.e("ServerThread", "[SERVER THREAD] An exception has occurred: " + clientProtocolException.getMessage());
        } catch (IOException ioException) {
            Log.e("ServerThread", "[SERVER THREAD] An exception has occurred: " + ioException.getMessage());
        }
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public void stopThread() {
        interrupt();
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException ioException) {
                Log.e("ServerThread", "[SERVER THREAD] An exception has occurred: " + ioException.getMessage());
            }
        }
    }
}
