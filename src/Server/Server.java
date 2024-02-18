package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static final int PORT = 9999; // 서버 포트 번호

    public static void main(String[] args) {
        ExecutorService executor = Executors.newCachedThreadPool(); // 유동적인 스레드 풀

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
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

    // 클라이언트 핸들러 클래스
    static class ClientHandler implements Runnable {
        private Socket clientSocket;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                 PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    System.out.println("클라이언트로부터의 메시지: " + inputLine);

                    // 클라이언트가 보낸 메시지에 따라 로그인 또는 회원가입 처리
                    if (inputLine.equals("로그인")) {
                        // 로그인 처리를 위해 로그인 클래스 호출
                        boolean loggedIn = Login.login(/* 사용자 이름 */, /* 비밀번호 */);
                        // 로그인 결과에 따른 응답을 클라이언트에게 보냄
                        out.println("로그인 결과: " + (loggedIn ? "성공" : "실패"));
                    } else if (inputLine.equals("회원가입")) {
                        // 회원가입 처리를 위해 회원가입 클래스 호출
                        boolean registered = Signup.register(/* 사용자 이름 */, /* 비밀번호 */);
                        // 회원가입 결과에 따른 응답을 클라이언트에게 보냄
                        out.println("회원가입 결과: " + (registered ? "성공" : "실패"));
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
