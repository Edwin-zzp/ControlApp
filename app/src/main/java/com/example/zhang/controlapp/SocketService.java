package com.example.zhang.controlapp;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.example.zhang.controlapp.common.EventMsg;
import com.example.zhang.controlapp.models.DeviceModel;


import org.greenrobot.eventbus.EventBus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.NoRouteToHostException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Timer;
import java.util.TimerTask;

public class SocketService extends Service {

    private DeviceModel mDevice = null;
    private static final String TAG = "sockersv";
    private static final String encoding ="GB2312";

    /**
     * 接收服务器消息 变量
     */
    // 输入流对象
    private InputStream is;

    // 输入流读取器对象
    private InputStreamReader isr ;
    private BufferedReader br ;

    // 接收服务器发送过来的消息
    private String response=null;






    /*socket*/
    private Socket socket;
    /*连接线程*/
    private Thread connectThread,receiveThread,sendThread;

    private Timer timer = new Timer();
    private OutputStream outputStream;

    private SocketBinder sockerBinder = new SocketBinder();
    private String ip;
    private String port;
    private TimerTask task;

    /*默认重连*/
    private boolean isReConnect = true;

    private Handler handler = new Handler(Looper.getMainLooper());
    //final StringBuilder buffer = new StringBuilder();


    @Override
    public IBinder onBind(Intent intent) {
        return sockerBinder;
    }


    public class SocketBinder extends Binder {

        /*返回SocketService 在需要的地方可以通过ServiceConnection获取到SocketService  */
        public SocketService getService() {
            return SocketService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();


    }

    public void reconnect(){
        releaseSocket();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        /*拿到传递过来的ip和端口号*/
        //ip = intent.getStringExtra(Constants.INTENT_IP);
       // port = intent.getStringExtra(Constants.INTENT_PORT);

        ip = ((DeviceModel)intent.getSerializableExtra("deviceObject")).getHost();
        port = ((DeviceModel)intent.getSerializableExtra("deviceObject")).getPort().toString();


        /*初始化socket*/
        initSocket();

        return super.onStartCommand(intent, flags, startId);
    }


    /*初始化socket*/
    private void initSocket() {
        if (socket == null && connectThread == null) {
            connectThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    socket = new Socket();
                    try {
                        /*超时时间为2秒*/
                        socket.connect(new InetSocketAddress(ip, Integer.valueOf(port)), 1500);
                        /*连接成功的话  发送心跳包*/
                        if (socket.isConnected()) {


                            /*因为Toast是要运行在主线程的  这里是子线程  所以需要到主线程哪里去显示toast*/
                            toastMsg("socket已连接");

                            /*发送连接成功的消息*/
                            EventMsg msg = new EventMsg();
                            msg.setTag("connectSucccess");
                            EventBus.getDefault().post(msg);
                            /*发送心跳数据*/

                            sendBeatData();

                            receive();


                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                        if (e instanceof SocketTimeoutException) {
                            toastMsg("连接超时，请检查");
                            /*发送连接失败的消息*/
                            EventMsg msg = new EventMsg();
                            msg.setTag("connectfaile");
                            EventBus.getDefault().post(msg);

                            //releaseSocket();
                            stopSelf();

                        } else if (e instanceof NoRouteToHostException) {
                            toastMsg("该地址不存在，请检查");
                            EventMsg msg = new EventMsg();
                            msg.setTag("connectfaile");
                            EventBus.getDefault().post(msg);
                            stopSelf();

                        } else if (e instanceof ConnectException) {
                            toastMsg("连接异常或被拒绝，请检查");
                            EventMsg msg = new EventMsg();
                            msg.setTag("connectfaile");
                            EventBus.getDefault().post(msg);
                            stopSelf();

                        }


                    }

                }
            });

            /*启动连接线程*/
            connectThread.start();

        }


    }

    /*因为Toast是要运行在主线程的   所以需要到主线程哪里去显示toast*/
    private void toastMsg(final String msg) {

        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }



    /**
     * 接收数据
     */
    public void receive() {


        if (socket != null && socket.isConnected()) {

            receiveThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    //while (true) {
                        try {

                            Log.i(TAG,"receive work");
                            // 步骤1：创建输入流对象InputStream
                            is = socket.getInputStream();


                            // 步骤2：创建输入流读取器对象 并传入输入流对象
                            // 该对象作用：获取服务器返回的数据
                            isr = new InputStreamReader(is,encoding);

                            br = new BufferedReader(isr);

                            // 步骤3：通过输入流读取器对象 接收服务器发送过来的数据
                            //response = br.readLine();
                            while((response = br.readLine())!=null) {
                                Log.i(TAG, response);
//                                buffer.append(response);
//                                response = buffer.toString();
//                                Log.i(TAG, response);
                                EventMsg msg = new EventMsg();
                                msg.setTag(response);
                                EventBus.getDefault().post(msg);

                            }

                        } catch (IOException e) {

                            releaseSocket();
                            e.printStackTrace();

                        }
                    //}
                }
            });
            receiveThread.start();


        } else {


            toastMsg("socket连接错误,请重试");
            releaseSocket();
        }

    }




    /*发送数据*/
    public void sendOrder(final String order) {
        if (socket != null && socket.isConnected()) {
            /*发送指令*/
            sendThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        outputStream = socket.getOutputStream();
                        if (outputStream != null) {
                            outputStream.write((order).getBytes(encoding));
                            outputStream.flush();
                        }

                    } catch (IOException e) {
                        //releaseSocket();
                        e.printStackTrace();
                    }

                }
            });
            sendThread.start();
        } else {
            toastMsg("socket连接错误,请重试");
            //releaseSocket();
        }
    }

    /*定时发送数据*/
    private void sendBeatData() {
        if (timer == null) {
            timer = new Timer();
        }

        if (task == null) {
            task = new TimerTask() {
                @Override
                public void run() {
                    try {
                        Log.i(TAG,"send test");
                        outputStream = socket.getOutputStream();

                        /*这里的编码方式根据你的需求去改*/
                        outputStream.write(("test").getBytes(encoding));
                        outputStream.flush();
//                        EventMsg msg = new EventMsg();
//                        msg.setTag("connectSucccess");
//                        EventBus.getDefault().post(msg);
                    } catch (Exception e) {
                        /*发送失败说明socket断开了或者出现了其他错误*/
//                        EventMsg msg = new EventMsg();
//                        msg.setTag("connectfaile");
//                        EventBus.getDefault().post(msg);
//                        toastMsg("连接断开，正在重连");
                        /*重连*/
                        releaseSocket();
                        e.printStackTrace();
                    }
                }
            };
        }

        timer.schedule(task, 0, 2000);
    }


    /*释放资源*/
    private void releaseSocket() {

        if (task != null) {
            task.cancel();
            task = null;
        }
        if (timer != null) {
            timer.purge();
            timer.cancel();
            timer = null;
        }

        if (outputStream != null) {
            try {
                outputStream.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
            outputStream = null;
        }

        if (socket != null) {
            try {
                socket.close();

            } catch (IOException e) {
            }
            socket = null;
        }

        if (connectThread != null) {
            connectThread = null;
        }

        /*重新初始化socket*/
        if (isReConnect) {
            initSocket();
        }

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
        isReConnect = false;
        releaseSocket();
    }

}
