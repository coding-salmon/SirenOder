package Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import Server.Login;
import Server.Signup;

public class Client {

	private static BufferedReader in; // 서버로부터의 입력 스트림
	private static PrintWriter out; // 서버로의 출력 스트림

	public static void main(String[] args) {
		final String serverAddress = "localhost"; // 서버의 주소
		final int serverPort = 9999; // 서버의 포트 번호

		try ( // 소켓서버연결과 입출력
				Socket socket = new Socket(serverAddress, serverPort);
				PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
				BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in))) {

			// 사용자 입력받기 전에 일정 시간 동안 대기하는 스레드 슬립 적용

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			System.out.println("사이렌오더 서버에 연결되었습니다.");

			String userInput;
			while (true) {
				// 메뉴출력
				System.out.println("1.     [   로그인  ]     ");
				System.out.println("2.     [  회원가입  ]     ");
				System.out.println("3.     [   종료    ]     ");
				System.out.println("      메뉴를 선택하세요. >>  ");

				// 사용자 입력받기
				userInput = stdIn.readLine();

				switch (userInput) {

				case "1":
					login(stdIn, out, in); // 로그인 호출
					break;
				case "2":
					signup(stdIn, out, in); // 회원가입 호출
					break;
				case "3":
					System.out.println("사이렌오더 서버 연결을 종료합니다.");
					return;
				default:
					System.out.println("잘못된 입력입니다. 다시 선택해주세요.");
				}

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 로그인 기능 호출
	private static void login(BufferedReader stdIn, PrintWriter out, BufferedReader in) throws IOException {
		// 사용자 입력 정보 받기
		System.out.println("아이디를 입력하세요>>");
		String userid = stdIn.readLine();
		System.out.println("패스워드를 입력하세요>>");
		String password = stdIn.readLine();
			
		//로그인 요청 서버로 전송
		out.println("[LOGIN"+userid + "," +password);
		
		//서버로부터 응답받기
		String response = in.readLine();
		System.out.println(response);
		}

	// 회원가입 기능 호출
	private static void signup(BufferedReader stdIn, PrintWriter out, BufferedReader in) throws IOException {
		// 사용자 입력 정보 받기
		System.out.println("사용할 아이디를 입력하세요");
		String userid = stdIn.readLine();
		
		while(alreadyId(in, out, userid)) {
		System.out.println("이미 사용중인 아이디입니다. 다른 아이디를 입력하세요.");
		userid = stdIn.readLine(); // 사용자로부터 다른 아이디 입력 요청
		}
		System.out.println("사용할 패스워드를 입력하세요");
		String password = stdIn.readLine();
		}
			
	// 이미 사용 중인 아이디 확인하는 메서드
	private static boolean alreadyId(BufferedReader in, PrintWriter out, String userid) {

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// 서버로부터 이미 사용 중인 아이디인지 확인하는 메시지 전송
		out.println("[CheckID]" + userid);

		// 서버로부터 응답받기
		String response;
		try {
			response = in.readLine();
			return response.equals("true"); // 이미 사용 중인 아이디인 경우 true 반환
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
}
