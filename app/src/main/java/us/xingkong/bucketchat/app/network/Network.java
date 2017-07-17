package us.xingkong.bucketchat.app.network;

import java.io.IOException;
import java.util.ArrayList;

import network.TestClient;
import network.bucketobject.Message;
import network.bucketobject.Query;
import network.bucketobject.USER;
import network.connection.Connection;
import network.listener.BucketListener;
import network.listener.LoginListener;
import network.listener.MessageListener;
import network.listener.OnlineListListener;
import network.listener.QueryListener;
import us.xingkong.bucketchat.app.network.listener.BucketHandlerListener;
import us.xingkong.bucketchat.app.network.listener.LoginHandlerListener;
import us.xingkong.bucketchat.app.network.listener.MessageHandlerListener;
import us.xingkong.bucketchat.app.network.listener.OnlineListHandlerListener;
import us.xingkong.bucketchat.app.network.listener.SigninHandlerListener;
import us.xingkong.bucketchat.app.network.listener.SigninListener;
import us.xingkong.bucketchat.app.network.listener.UpdateHandlerListener;

/**
 * Created by 饶翰新 on 2017/7/14.
 */

public class Network {

    public static final String TEST_HOST = "dustlight.cn";
    public static final int TEST_PORT = 6654;

    private String host;
    private int port;
    private USER user;

    private TestClient client;
    private boolean isVaild;
    private ArrayList<BucketListener> listener;

    public Network(String host, int port)
    {
        setHost(host);
        setPort(port);
        listener = new ArrayList<BucketListener>();
    }

    public Network()
    {
        this(TEST_HOST,TEST_PORT);
    }

    public void finish() throws IOException {
        if(client != null && client.getConn() != null)
            client.getConn().finish();
    }

    public void Login(final USER user,final LoginListener loginListener) {
        final LoginListener l = new LoginHandlerListener(loginListener);
        if(user == null)
            l.onDone(null,false);
        new Thread()
        {
            @Override
            public void run() {
                try {

                    client = new TestClient(Network.this.host,Network.this.port);
                    client.Login(user, new LoginListener() {
                        @Override
                        public void onDone(Connection connection, boolean b) {
                            if(b)
                                Network.this.user = user;
                            else
                                Network.this.user = null;

                            l.onDone(connection,b);
                        }

                        @Override
                        public void onException(Exception e) {
                            l.onException(e);
                        }

                        @Override
                        public void onDisconnection(Connection conn) {
                            l.onDisconnection(conn);
                        }
                    });
                    client.setListener(new BucketListener() {
                        @Override
                        public void onDataCome(Connection connection, String s) {
                            for(BucketListener l : listener)
                            {
                                if(checkListener(l)){
                                    l.onDataCome(connection,s);
                                }
                            }
                        }

                        @Override
                        public void onDisconnection(Connection connection) {
                            for(BucketListener l : listener)
                            {
                                if(checkListener(l)){
                                    l.onDisconnection(connection);
                                }
                            }
                            isVaild = false;
                        }
                    });
                    isVaild = true;
                } catch (IOException e) {
                    isVaild = false;
                    l.onDisconnection(null);
                }
            }
        }.start();


    }

    public void reLogin(LoginListener listener) {
        Login(user,listener);
    }

    public void SendMessage(final Message msg) {
        new Thread(){
            @Override
            public void run() {
                try {
                    client.sendMessage(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }

    public void Signin(final USER user,SigninListener signinListener) {
       final  SigninHandlerListener l = new SigninHandlerListener(signinListener);
        new Thread()
        {
            @Override
            public void run() {
                try {
                    client = new TestClient(Network.this.host,Network.this.port);
                    client.Signin(user, new LoginListener() {
                        @Override
                        public void onDone(Connection connection, boolean b) {
                            if(b)
                                Network.this.user = user;
                            else
                                Network.this.user = null;

                            l.onDone(connection,b);
                        }

                        @Override
                        public void onException(Exception e) {
                            l.onException(e);
                        }

                        @Override
                        public void onDisconnection(Connection conn) {
                            l.onDisconnection(conn);
                        }
                    });
                    client.setListener(new BucketListener() {
                        @Override
                        public void onDataCome(Connection connection, String s) {
                            for(BucketListener l : listener)
                            {
                                if(checkListener(l)){
                                    l.onDataCome(connection,s);
                                }
                            }
                        }

                        @Override
                        public void onDisconnection(Connection connection) {
                            for(BucketListener l : listener)
                            {
                                if(checkListener(l)){
                                    l.onDisconnection(connection);
                                }
                            }
                            isVaild = false;
                        }
                    });
                    isVaild = true;
                } catch (IOException e) {
                    isVaild = false;
                    l.onDisconnection(null);
                }
            }
        }.start();



    }

    public void setMessageListener(MessageListener messageListener) {
        client.setMessageListener(new MessageHandlerListener(messageListener));
    }

    public void Update(final Object o,final UpdateHandlerListener listener) {

        new Thread()
        {
            @Override
            public void run() {
                try {
                    client.Update(o);
                } catch (IOException e) {
                    if(listener != null)
                        listener.onException(e);
                    else
                        e.printStackTrace();
                }
            }
        }.start();

    }

    public void Update(Object o)
    {
        Update(o,null);
    }

    public void Update(final ArrayList array, final UpdateHandlerListener listener) {
        new Thread()
        {
            @Override
            public void run() {
                try {
                    client.Update(array);
                } catch (IOException e) {
                    if(listener != null)
                        listener.onException(e);
                    else
                        e.printStackTrace();
                }
            }
        }.start();

    }

    public void Update(ArrayList array)
    {
        Update(array);
    }

     public <T> void Query(final Query query, final QueryListener<T> queryListener) {
        new Thread()
        {
            @Override
            public void run() {
                try {
                    client.Query(query,queryListener);
                } catch (IOException e) {
                    queryListener.onException(e);
                }
            }
        }.start();

    }

    public void getOnlineList(OnlineListListener onlineListListener) {
        final OnlineListHandlerListener l = new OnlineListHandlerListener(onlineListListener);
        new Thread()
        {
            @Override
            public void run() {
                try {
                    client.getOnlineList(l);
                } catch (IOException e) {
                    System.out.println(e);
                    l.onException(e);
                }
            }
        }.start();

    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getHost() {
        return host;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public boolean isVaild() {
        return isVaild;
    }

    public USER getUser() {
        return user;
    }

    public void addListener(BucketListener listener) {
        this.listener.add(new BucketHandlerListener(listener));
    }

    private boolean checkListener(BucketListener l) {
        if(l == null)
        {
            listener.remove(l);
            return false;
        }
        return true;
    }
}
