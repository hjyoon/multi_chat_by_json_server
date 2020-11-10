package multi_chat_by_json_server;

import java.io.*;
import java.util.*;
import java.net.*;

public class MySocketServer {
    private ServerSocket serverSocket;      // 서버 소켓
    private ObjectInputStream ois;  // 소켓으로부터 전달받은 메시지를 읽어들이기 위함
    private ObjectOutputStream oos;     // 클라이언트로 메시지를 보냄
    private int port;
    private ArrayList<MySocketInfo> al;

    public MySocketServer(int port) {
        this.port = port;
    }

    public void init() {
        al = new ArrayList<>();
        try {
            serverSocket = new ServerSocket(port); // 현재 아이피로 8981포트를 사용하여 서버 오픈
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            System.out.println("Server address is "+serverSocket.getInetAddress().getLocalHost().getHostAddress());
            System.out.println("Server port is "+serverSocket.getLocalPort());
            System.out.println(Util.time_now()+" Server is ready");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                MySocketInfo clientSocketInfo = new MySocketInfo(clientSocket);
                al.add(clientSocketInfo);

                MyServerThread server_thread = new MyServerThread(clientSocketInfo, al);
                server_thread.init();
                server_thread.start();
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

}