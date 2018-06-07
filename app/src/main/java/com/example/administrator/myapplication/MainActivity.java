package com.example.administrator.myapplication;

import android.content.Intent;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.os.Handler;

public class MainActivity extends AppCompatActivity {
    private Button disconnect, connect, button;
    private EditText ip, port;
    private EditText[] pressure = new EditText[6];
    boolean run = false;
    private Datas mDatas;

    BufferedWriter out = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDatas = new Datas();
        ip = (EditText) findViewById(R.id.IP);//IP text
        port = (EditText) findViewById(R.id.port);//port text
        pressure[0] = (EditText) findViewById(R.id.pressure1);//压力text
        pressure[1] = (EditText) findViewById(R.id.pressure2);
        pressure[2] = (EditText) findViewById(R.id.pressure3);
        pressure[3] = (EditText) findViewById(R.id.pressure4);
        pressure[4] = (EditText) findViewById(R.id.pressure5);
        pressure[5] = (EditText) findViewById(R.id.pressure6);

        connect = (Button) findViewById(R.id.connect);
        disconnect = (Button) findViewById(R.id.disconnect);
        button = (Button) findViewById(R.id.button);
        disconnectClick();

        connect.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                run = true;
                connectClick();
                new Thread(net).start();
            }
        });
        disconnect.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        println("SENDOFF");
                        try {
                            socket.close();
                        } catch (IOException e) {
                        }
                    }
                }).start();
                run = false;
                disconnectClick();
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("datas",mDatas);
                intent.putExtra("dataBundle",bundle);
                startActivity(intent);
                //随机生成25组模拟数据  实测时请删除该线程
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Random rand = new Random();
//                        for (int i=0;i<25;i++){
//                            try {
//                                Thread.sleep(1000);
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//                            Intent intent = new Intent("ACTION_DATA_RECEIVED");
//                            DecimalFormat df = new DecimalFormat("#.00");
//                            double [] datas = { Double.valueOf(df.format(rand.nextDouble()*12)),
//                                    Double.valueOf(df.format(rand.nextDouble()*12)),
//                                    Double.valueOf(df.format(rand.nextDouble()*12)),
//                                    Double.valueOf(df.format(rand.nextDouble()*12)),
//                                    Double.valueOf(df.format(rand.nextDouble()*12)),
//                                    Double.valueOf(df.format(rand.nextDouble()*12))};
//                            intent.putExtra("data",datas);
//                            sendBroadcast(intent);}
//                    }
//                }).start();
            }
        });
    }

    void disconnectClick() {
        ip.setEnabled(true);
        connect.setEnabled(true);
        port.setEnabled(true);
        disconnect.setEnabled(false);
    }

    void connectClick() {
        ip.setEnabled(false);
        connect.setEnabled(false);
        port.setEnabled(false);
        disconnect.setEnabled(true);
    }

    public void handleException(Exception e, String prefix) {
        //e.printStackTrace();
        toastText(prefix);
    }

    public void toastText(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void println(String msg) {
        if (out != null)
            try {
                out.write(msg + "\n");
                out.flush();
            } catch (IOException e) {
            }
    }

    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 0) //如果消息是刚才发送的标识
            {
                Bundle bundle = msg.getData();
                String[] datas = bundle.getString("data").split(",");
                double[] ddatas = new double[6];
                if (datas.length == 6) {
                    //pressure[0].setText(data);
                    for (int i = 0; i < 6; i++) {
                        pressure[i].setText(datas[i]);
                        mDatas.addvalue(i+1,Double.valueOf(datas[i].trim()));
                        ddatas[i] = Double.parseDouble(datas[i]);
//                        Intent intent = new Intent("ACTION_DATA_RECEIVED");
//                        intent.putExtra("data",ddatas);
//                        sendBroadcast(intent);
                    }
                    Intent intent = new Intent("ACTION_DATA_RECEIVED");
                    intent.putExtra("data",ddatas);
                    sendBroadcast(intent);
                }

            } else if (msg.what == 1) {
                toastText("连接关闭！");
                disconnectClick();
            } else if (msg.what == 2) {
                toastText("连接不成功！");
                disconnectClick();
            }
        }

        ;
    };
    Socket socket = null;
    Runnable net = new Runnable() {
        @Override
        public void run() {
            try {
                socket = new Socket();
                SocketAddress socAddress = new InetSocketAddress(ip.getText().toString(), Integer.parseInt(port.getText().toString()));
                socket.connect(socAddress, 3000);//超时3秒
                out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                out.write("SENDON");
                out.flush();
                InputStream is = socket.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
//                char chars[] = new char[64];
//                int len;
//                StringBuilder sb = new StringBuilder();
//                while ((len=br.read(chars)) != -1) {
//                    sb.append(new String(chars, 0, len));
//                }

                try {
                    String data = null;
                    while ((data = br.readLine()) != null) {
                        //socket.sendUrgentData(0);
                        System.out.println(data);
                        Bundle bundle = new Bundle();
                        Log.d("log", "run: "+data);
                        bundle.putString("data", data);
                        Message msg = new Message();
                        msg.what = 0;
                        msg.setData(bundle);
                        handler.sendMessage(msg);
                    }
                } catch (IOException e) {

                } finally {
                    if (!socket.isClosed())
                        socket.close();
                }

            } catch (Exception e) {
                handler.sendEmptyMessage(2);
            }
        }
    };


}
