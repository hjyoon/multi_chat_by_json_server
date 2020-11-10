package multi_chat_by_json_server;

import java.io.*;
import java.util.*;
import java.net.*;

import com.google.gson.*;

public class MyServerThread extends Thread {
    private MySocketInfo clientSocketInfo;
    private ObjectInputStream ois;  // �������κ��� ���޹��� �޽����� �о���̱� ����
    private ObjectOutputStream oos;
    private ArrayList<MySocketInfo> al;
    private String nickname;
    private Gson gson;

    public MyServerThread(MySocketInfo clientSocketInfo, ArrayList<MySocketInfo> al) {
        this.clientSocketInfo = clientSocketInfo;
        this.al = al;
    }

    public void init() {
        gson = new Gson();
        oos = clientSocketInfo.getOOS();
        ois = clientSocketInfo.getOIS();
    }

    // Ŭ���̾�Ʈ �� ������ ��ε�ĳ����
    public void broadcast(String receivedData, boolean b) { // �ι�° ���ڰ� true�� ���, ������, false�� ���, �ڽ��� ����.
        System.out.println(receivedData);   // ������ ���

        // ��� Ŭ���̾�Ʈ���� �޽��� ��ε�ĳ����
        synchronized (al) {
            for (int i=0; i<al.size(); i++) {
                MySocketInfo client = al.get(i);
                if(client == clientSocketInfo && b == false) {
                    continue;
                }
                try {
                    ObjectOutputStream oos_tmp = client.getOOS();
                    Data data = new Data("sendMsg", receivedData);
                    String json = gson.toJson(data);
                    oos_tmp.writeObject(json);
                }
                catch (Exception e) {
                    e.printStackTrace();
                    al.remove(i);
                }
            }
        }
    }

    public String accept_message() {
        return clientSocketInfo.getSocket()+" \""+nickname+"\" has accepted "+"(total clients : "+al.size()+")";
    }

    public String disconnect_message() {
        return clientSocketInfo.getSocket()+" \""+nickname+"\" has disconnected "+"(total clients : "+al.size()+")";
    }

    @Override
    public void run() {
        try {
            while(true) {
                // Ŭ���̾�Ʈ ������ �������� ������°� �ƴ� ���
                if(!clientSocketInfo.getSocket().isConnected() && clientSocketInfo.getSocket().isClosed()) {
                    clientSocketInfo.getSocket().close();
                    al.remove(clientSocketInfo);
                    broadcast(Util.time_now()+" "+disconnect_message(), true);
                    break;
                }

                // Ŭ���̾�Ʈ�� ���� �����͸� �о���� ����
                String receivedData = (String)ois.readObject();

                // Ŭ���̾�Ʈ�κ��� �޽����� ������ �ƴ� ���
                if(receivedData == null) {
                    clientSocketInfo.getSocket().close();
                    al.remove(clientSocketInfo);
                    broadcast(Util.time_now()+" "+disconnect_message(), true);
                    break;
                }

                // ���� ������ �Ľ�
                Data data = gson.fromJson(receivedData, Data.class);
                if(data.getOp().equals("sendMsg")) {
                    // Ŭ���̾�Ʈ�� ���� �޼��� ���
                    broadcast(Util.time_now()+" "+nickname+"> "+data.getData(), false);    // Ŭ���̾�Ʈ �� ������ ��ε�ĳ����
                }
                else if(data.getOp().equals("setNickname")) {
                    nickname = data.getData();
                    // ���� �޼��� ���
                    broadcast(Util.time_now()+" "+accept_message(), true);    // Ŭ���̾�Ʈ �� ������ ��ε�ĳ����
                }

            }
        }
        catch (SocketException e) {
            al.remove(clientSocketInfo);
            broadcast(Util.time_now()+" "+disconnect_message(), true);
        }
        catch (Exception e) {
            e.printStackTrace();
            al.remove(clientSocketInfo);
            broadcast(Util.time_now()+" "+disconnect_message(), true);
        }
    }
}