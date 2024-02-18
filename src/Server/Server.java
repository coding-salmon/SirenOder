package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Server {
    private static final int PORT = 9999; // 서버가 수신 대기할 포트 번호
    private static final Logger logger = Logger.getLogger(Server.class.getName()); // 로깅을 위한 로거

    public static void main(String[] args) {
        ExecutorService executor = Executors.newCachedThreadPool(); // 클라이언트 처리를 위한 스레드 풀

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            logger.info("사이렌 오더 서버 시작"); // 서버 시작 로깅

            while (true) { // 무한 루프로 클라이언트 접속 대기
                Socket clientSocket = serverSocket.accept(); // 클라이언트 연결 수락
                logger.info("클라이언트가 접속함: " + clientSocket.getInetAddress().getHostAddress());

                ClientHandler clientHandler = new ClientHandler(clientSocket);
                executor.execute(clientHandler); // 클라이언트 처리를 위한 새 스레드 할당
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "서버 실행 중 오류 발생", e);
        } finally {
            executor.shutdown(); // 서버 종료 시 스레드 풀 종료
        }
    }

    // 클라이언트 요청을 처리하는 핸들러 클래스
    static class ClientHandler implements Runnable {
        private Socket clientSocket; // 클라이언트와의 연결 소켓

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try (
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            ) {
                String inputLine;
                while ((inputLine = in.readLine()) != null) { // 클라이언트로부터 메시지 수신
                    logger.info("클라이언트로부터의 메시지: " + inputLine);

                    JSONParser parser = new JSONParser();
                    try {
                        JSONObject jsonRequest = (JSONObject) parser.parse(inputLine);
                        String command = (String) jsonRequest.get("type");
                        processCommand(command, jsonRequest, out); // 수신된 명령 처리
                    } catch (ParseException e) {
                        logger.log(Level.SEVERE, "JSON 파싱 중 오류 발생", e);
                        JSONObject jsonResponse = new JSONObject();
                        jsonResponse.put("에러", "JSON 파싱 오류");
                        out.println(jsonResponse.toJSONString());
                    }
                }
            } catch (IOException e) {
                logger.log(Level.SEVERE, "클라이언트 핸들링 중 오류 발생", e);
            } finally {
                try {
                    clientSocket.close(); // 클라이언트 소켓 종료
                } catch (IOException e) {
                    logger.log(Level.SEVERE, "클라이언트 소켓 닫기 중 오류 발생", e);
                }
            }
        }

     // 클라이언트로부터 받은 명령을 처리하는 메서드
        private void processCommand(String command, JSONObject jsonRequest, PrintWriter out) {
            // 클라이언트로부터 받은 응답을 저장할 JSON 객체
            JSONObject jsonResponse = new JSONObject();
            
            // 클라이언트로부터 받은 명령에 따라 적절한 처리 수행
            switch (command) {
                case "login":
                    // 로그인 명령 처리
                    // jsonRequest에서 username과 password를 추출
                    String usernameLogin = (String) jsonRequest.get("userid");
                    String passwordLogin = (String) jsonRequest.get("password");
                    // Login 클래스의 login 메서드를 호출하여 로그인 처리
                    // 결과는 JSON 문자열 형태로 반환됨
                    String loginResult = Login.login(usernameLogin, passwordLogin);
                    // 처리 결과를 클라이언트에게 전송
                    out.println(loginResult);
                    break;
                case "signup":
                    // 회원가입 명령 처리
                    // jsonRequest에서 username과 password를 추출
                    String usernameSignup = (String) jsonRequest.get("userid");
                    String passwordSignup = (String) jsonRequest.get("password");
                    // Signup 클래스의 register 메서드를 호출하여 회원가입 처리
                    // 결과는 JSON 문자열 형태로 반환됨
                    String signupResult = Signup.register(usernameSignup, passwordSignup);
                    // 처리 결과를 클라이언트에게 전송
                    out.println(signupResult);
                    break;
                default:
                    // 알 수 없는 명령에 대한 처리
                    // 에러 메시지를 JSON 객체에 추가
                    jsonResponse.put("에러", "알 수 없는 명령");
                    // 에러 메시지를 클라이언트에게 전송
                    out.println(jsonResponse.toJSONString());
                    break;
            }
        }
    }
}
