package multi_chat_by_json_server;

import java.io.*;
import java.util.*;
import java.net.*;

import com.google.gson.*;

public class MyServerThread extends Thread {
    private MySocketInfo clientSocketInfo;
    private ObjectInputStream ois;  // 소켓으로부터 전달받은 메시지를 읽어들이기 위함
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

    // 클라이언트 및 서버에 브로드캐스팅
    public void broadcast(String receivedData, boolean b) { // 두번째 인자가 true일 경우, 모두출력, false일 경우, 자신은 제외.
        System.out.println(receivedData);   // 서버에 출력

        // 모든 클라이언트에게 메시지 브로드캐스팅
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
                // 클라이언트 소켓이 닫혔더나 연결사태가 아닌 경우
                if(!clientSocketInfo.getSocket().isConnected() && clientSocketInfo.getSocket().isClosed()) {
                    clientSocketInfo.getSocket().close();
                    al.remove(clientSocketInfo);
                    broadcast(Util.time_now()+" "+disconnect_message(), true);
                    break;
                }

                // 클라이언트로 부터 데이터를 읽어오기 위함
                String receivedData = (String)ois.readObject();

                // 클라이언트로부터 메시지가 정상이 아닌 경우
                if(receivedData == null) {
                    clientSocketInfo.getSocket().close();
                    al.remove(clientSocketInfo);
                    broadcast(Util.time_now()+" "+disconnect_message(), true);
                    break;
                }

                // 받은 데이터 파싱
                Data data = gson.fromJson(receivedData, Data.class);
                if(data.getOp().equals("sendMsg")) {
                    // 클라이언트가 보낸 메세지 출력
                    broadcast(Util.time_now()+" "+nickname+"> "+data.getData(), false);    // 클라이언트 및 서버에 브로드캐스팅
                }
                else if(data.getOp().equals("setNickname")) {
                    nickname = data.getData();
                    // 입장 메세지 출력
                    broadcast(Util.time_now()+" "+accept_message(), true);    // 클라이언트 및 서버에 브로드캐스팅
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