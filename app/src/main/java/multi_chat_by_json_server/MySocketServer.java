package multi_chat_by_json_server;

import java.io.*;
import java.util.*;
import java.net.*;

public class MySocketServer {
    private ServerSocket serverSocket;      // ���� ����
    private ObjectInputStream ois;  // �������κ��� ���޹��� �޽����� �о���̱� ����
    private ObjectOutputStream oos;     // Ŭ���̾�Ʈ�� �޽����� ����
    private int port;
    private ArrayList<MySocketInfo> al;

    public MySocketServer(int port) {
        this.port = port;
    }

    public void init() {
        al = new ArrayList<>();
        try {
            serverSocket = new ServerSocket(port); // ���� �����Ƿ� 8981��Ʈ�� ����Ͽ� ���� ����
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