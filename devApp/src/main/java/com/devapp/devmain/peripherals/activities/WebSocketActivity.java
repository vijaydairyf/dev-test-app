package com.devapp.devmain.peripherals.activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.devApp.R;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class WebSocketActivity extends Activity {
    private static final int NORMAL_CLOSURE_STATUS = 1000;
    private static final String TAG = "webSocket";
    private TextView textView;
    private OkHttpClient client;
    private WebSocket mWebSocket;
    private Button createButton;
    private int ct = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_socket);
        textView = (TextView) findViewById(R.id.tv_data);
        createButton = (Button) findViewById(R.id.btn_createConnection);
        client = new OkHttpClient();
    }

    public void createConnection(View view) {
        Request request = new Request.Builder().url("ws://echo.websocket.org").build();
        EchoWebSocketListener listener = new EchoWebSocketListener();
        WebSocket ws = client.newWebSocket(request, listener);
        client.dispatcher().executorService().shutdown();
        createButton.setVisibility(View.GONE);
    }

    public void sendData(View v) {
        if (mWebSocket != null) {
            Log.v(TAG, "sending:" + mWebSocket.send("Sending data: " + ct));
            ct++;
        } else
            Log.v(TAG, "web socket is null");
    }

    public void closeConnection(View v) {
        if (mWebSocket != null) {
            Log.v(TAG, "closing:" + mWebSocket.close(NORMAL_CLOSURE_STATUS, "Goodbye !"));
        } else
            Log.v(TAG, "web socket is null");

    }

    private void output(final String txt) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textView.setText(textView.getText().toString() + "\n" + txt);
            }
        });
    }

    private final class EchoWebSocketListener extends WebSocketListener {

        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            mWebSocket = webSocket;
            webSocket.send("Socket connection is read");
        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
            output("Receiving : " + text);
        }

        @Override
        public void onMessage(WebSocket webSocket, ByteString bytes) {
            output("Receiving bytes : " + bytes.hex());
        }

        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
            webSocket.close(NORMAL_CLOSURE_STATUS, null);
            output("Closing connection: " + reason);
        }

        @Override
        public void onClosed(WebSocket webSocket, int code, String reason) {
            output("Connection closed");
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
            output("Error : " + t.toString());
        }
    }

}
