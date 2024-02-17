package Server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    public static void main(String[] args) {
    	ExecutorService executor = Executors.newCachedThreadPool(); // 유동적인 스레드 풀
        try (ServerSocket serverSocket = new ServerSocket(9000)) {
            System.out.println("사이렌 오더 서버 시작");

            while (true) {
                Socket clientSocket = serverSocket.accept(); // 클라이언트 접속 대기

                System.out.println("클라이언트가 접속함: " + clientSocket.getInetAddress().getHostAddress());

                ClientHandler clientHandler = new ClientHandler(clientSocket);
                executor.execute(clientHandler); // 클라이언트 핸들러 실행
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            executor.shutdown(); // 서버 종료 시 스레드 풀 종료
        }
    }

    static class ClientHandler implements Runnable {
        private Socket clientSocket;
        private Scanner in;
        private PrintWriter out;
        private String clientAddress;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
            this.clientAddress = socket.getInetAddress().getHostAddress();
        }

        @Override
        public void run() {
            try {
                in = new Scanner(clientSocket.getInputStream());
                out = new PrintWriter(clientSocket.getOutputStream(), true);

                while (in.hasNextLine()){ //클라이언트로부터의 입력이 있는 동안 반복
                    String receivedMessage = in.nextLine(); // 클라이언트로부터 메시지 수신

                    System.out.println("클라이언트 [" + clientAddress + "]로부터 수신: " + receivedMessage);

                    // 클라이언트에게 응답 전송
                    out.println("서버에서 클라이언트 [" + clientAddress + "]로 보낸 응답: " + receivedMessage);

                    if (receivedMessage.equals("exit")) {
                        break; // 클라이언트가 종료 메시지를 보내면 연결 종료
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
