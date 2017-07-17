package us.xingkong.bucketchat.app.network;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import Common.Tool;

/**
 * Created by 饶翰新 on 2017/7/15.
 */

public class Jwxt {

    Handler handler;
    private String cookie;
    private static final String parseURL = "http://www.xingkong.us/home/index.php/Home/index/pre_login";
    private static final String loginURL = "http://www.xingkong.us/home/index.php/Home/index/login";

    public Jwxt() {
        handler = new Handler();
        reset();
    }

    public void parse(final OnParseListener listener) {
        reset();
        new Thread()
        {
            @Override
            public void run() {
                URL u = null;
                try {
                    u = new URL(parseURL);
                    HttpURLConnection conn = (HttpURLConnection) u.openConnection();
                    conn.setRequestProperty("Cookie",cookie);
                    cookie += (getCookie(conn));
                    InputStream in =  conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuffer buffer = new StringBuffer();
                    int b;
                    while((b = reader.read()) != -1)
                    {
                        buffer.append((char)b);
                    }
                    String tmp = buffer.toString();

                    tmp = tmp.replaceFirst("jsonpReturn\\(","");
                    tmp = tmp.substring(0,tmp.lastIndexOf(");"));
                    in.close();

                    Result re = Tool.JSON2E(tmp,Result.class);
                    if(re == null || re.Status != 200) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {

                                listener.OnParseDone(null,null);
                            }
                        });
                        return;
                    }

                    u = new URL(re.url);

                    conn = (HttpURLConnection) u.openConnection();
                    conn.setRequestProperty("Cookie",cookie);
                    cookie += (getCookie(conn));

                    in = conn.getInputStream();

                    ByteArrayOutputStream buffer2 = new ByteArrayOutputStream();
;
                    while((b = in.read()) != -1)
                    {
                        buffer2.write(b);
                    }

                    buffer2.flush();
                    byte[] data = buffer2.toByteArray();
                    final Bitmap bitmap = BitmapFactory.decodeByteArray(data,0,data.length);
                    in.close();
                    buffer2.close();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.OnParseDone(bitmap,null);
                        }
                    });

                } catch (final MalformedURLException e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.OnParseDone(null,e);
                        }
                    });
                } catch (final IOException e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.OnParseDone(null,e);
                        }
                    });
                }

            }
        }.start();
    }

    public void login(final String username,final String password,final String code,final OnLoginListener listener){
        new Thread()
        {
            @Override
            public void run() {
                URL u = null;
                try {
                    u = new URL(loginURL + "?xh=" + username + "&pw=" + password + "&code=" + code);
                    HttpURLConnection conn = (HttpURLConnection) u.openConnection();
                    conn.setRequestProperty("Cookie", cookie);
                    cookie += (getCookie(conn));
                    InputStream in = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuffer buffer = new StringBuffer();
                    int b;
                    while ((b = reader.read()) != -1) {
                        buffer.append((char) b);
                    }
                    String tmp = buffer.toString();

                    tmp = tmp.replaceFirst("jsonpReturn\\(", "");
                    tmp = tmp.substring(0, tmp.lastIndexOf(");"));
                    in.close();
                    final Student stu = Tool.JSON2E(tmp,Student.class);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.OnLoginDone(stu,null);
                        }
                    });

                }catch (final IOException e)
                {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.OnLoginDone(null,e);
                        }
                    });

                }
            }
        }.start();

    }

    private String getCookie(HttpURLConnection conn) {
        String cookie = conn.getHeaderField("Set-Cookie");
        if(cookie != null)
            return cookie;
        else
            return "";
    }

    private void reset() {
        cookie = "";
    }

    class Result {
        public String Msg;
        public String url;
        int Status;

        public void setUrl(String url) {
            this.url = url;
        }

        public String getUrl() {
            return url;
        }

        public void setMsg(String msg) {
            Msg = msg;
        }

        public String getMsg() {
            return Msg;
        }

        public int getStatus() {
            return Status;
        }

        public void setStatus(int status) {
            Status = status;
        }

        @Override
        public String toString() {
            return "msg:" + Msg + " url:" + url + " code:" + Status;
        }
    }

    public class Student {
        public String xm;
        public String Msg;
        public int Status;

        public void setStatus(int status) {
            Status = status;
        }

        public void setMsg(String msg) {
            Msg = msg;
        }

        public void setXm(String xm) {
            this.xm = xm;
        }

        public String getMsg() {
            return Msg;
        }

        public int getStatus() {
            return Status;
        }

        public String getXm() {
            return xm;
        }
    }

    public interface OnParseListener {
        void OnParseDone(Bitmap pic,Exception e);
    }

    public interface OnLoginListener {
        void OnLoginDone(Student student,Exception e);
    }


}
