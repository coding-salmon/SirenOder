package Server;

import java.io.*;
import java.net.*;
import java.util.*;
import org.json.simple.*;
import org.json.simple.parser.*;

public class Chat {
    private ServerSocket serverSocket;
    private List<ChatHandler> clients = new ArrayList<>(); // 클라이언트들의 목록을 저장하는 리스트

    // 채팅 서버 생성자
    public Chat(int port) {
        try {
            serverSocket = new ServerSocket(port); // 서버 소켓 생성
            System.out.println("채팅 서버가 시작되었습니다.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 채팅 서버 시작 메서드
    public void start() {
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept(); // 클라이언트의 연결을 대기하고 클라이언트가 연결되면 소켓을 생성
                ChatHandler handler = new ChatHandler(clientSocket); // 클라이언트를 처리하는 핸들러 생성
                clients.add(handler); // 클라이언트 목록에 핸들러 추가
                handler.start(); // 핸들러 시작
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // 클라이언트를 처리하는 내부 클래스
    private class ChatHandler extends Thread {
        private Socket clientSocket; // 클라이언트 소켓
        private PrintWriter out; // 출력 스트림
        private BufferedReader in; // 입력 스트림

        // 핸들러 생성자
        public ChatHandler(Socket socket) {
            this.clientSocket = socket;
        }

        // 핸들러 실행 메서드
        public void run() {
            try {
                out = new PrintWriter(clientSocket.getOutputStream(), true); // 출력 스트림 생성
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); // 입력 스트림 생성

                String message;
                while ((message = in.readLine()) != null) { // 클라이언트로부터 메시지를 계속 받음
                    try {
                        JSONParser parser = new JSONParser();
                        JSONObject jsonMessage = (JSONObject) parser.parse(message); // JSON 형식의 메시지를 파싱

                        String messageType = (String) jsonMessage.get("type"); // 메시지 유형 확인
                        if ("message".equals(messageType)) { // 만약 메시지 유형이 "message"이면
                            String content = (String) jsonMessage.get("content"); // 메시지 내용 추출
                            System.out.println("클라이언트로부터 메시지 수신: " + content); // 받은 메시지 출력

                            // 모든 클라이언트에게 메시지 전송
                            for (ChatHandler client : clients) {
                                if (client != this) { // 자기 자신을 제외하고
                                    client.sendMessage(content); // 모든 클라이언트에게 메시지 전송
                                }
                            }
                        } else {
                            System.out.println("잘못된 메시지 형식입니다.");
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // 메시지를 클라이언트에게 전송하는 메서드
        public void sendMessage(String content) {
            JSONObject jsonMessage = new JSONObject(); // JSON 객체 생성
            jsonMessage.put("type", "broadcast"); // 메시지 유형 설정
            jsonMessage.put("content", content); // 메시지 내용 설정
            out.println(jsonMessage.toJSONString()); // JSON 형식의 메시지 전송
        }
    }

    // 채팅 서버 메인 메서드
    public static void main(String[] args) {
        Chat chatServer = new Chat(9999); // 포트 번호를 인자로 전달하여 채팅 서버 생성
        chatServer.start(); // 채팅 서버 시작
    }
}
