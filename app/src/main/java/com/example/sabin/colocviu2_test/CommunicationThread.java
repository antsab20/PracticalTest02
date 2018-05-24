package com.example.sabin.colocviu2_test;

import android.provider.SyncStateContract;
import android.util.Log;

import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.ResponseHandler;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.BasicResponseHandler;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;
import cz.msebera.android.httpclient.protocol.HTTP;

public class CommunicationThread extends Thread {

    private ServerThread serverThread;
    private Socket socket;

    public CommunicationThread(ServerThread serverThread, Socket socket) {
        this.serverThread = serverThread;
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
        Log.d("CommunicationThread", "[CommunicationThread] started");
        BufferedReader bufferedReader = Utilities.getReader(socket);
        PrintWriter printWriter = Utilities.getWriter(socket);
        String word = bufferedReader.readLine();
        Log.d("CommunicationThread", "The word has arrived " + word);
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost("http://services.aonaware.com/dictservice/dictservice.asmx/Define");
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("word", word));
        UrlEncodedFormEntity urlEncodedFormEntity = null;

            urlEncodedFormEntity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
            httpPost.setEntity(urlEncodedFormEntity);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            String pageSourceCode = httpClient.execute(httpPost, responseHandler);

            if (pageSourceCode == null) {
                Log.e("CommunicationThread", "[COMMUNICATION THREAD] Error getting the information from the webservice!");
                return;
            }
            pageSourceCode = pageSourceCode.substring(164);
            int x = pageSourceCode.indexOf("WordDefinition");
            pageSourceCode = pageSourceCode.substring(x+"WordDefinition>".length());
            int y = pageSourceCode.indexOf("</WordDefinition");
            pageSourceCode = pageSourceCode.substring(0,y);
            printWriter.println(pageSourceCode);
            printWriter.flush();
        } catch (Exception e) {
            Log.e("CommunicationThread", e.getMessage());
        }


    }
}
