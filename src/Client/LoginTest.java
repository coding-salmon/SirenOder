package Client;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginTest {
    public static void main(String[] args) {
        // JDBC 연결 정보 설정
        String DB_URL = "jdbc:oracle:thin:@salmon:1521:XE"; // Oracle 서버 주소와 포트
        String USER = "c##salmon"; // 데이터베이스 사용자 이름
        String PASSWORD = "1234"; // 데이터베이스 비밀번호

        // 테스트할 사용자 이름과 비밀번호
        String username = "testuser";
        String inputPassword = "testpassword";

        // 데이터베이스 연결 및 로그인 확인
        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE username = ? AND password = ?")) {

            // PreparedStatement에 사용자 이름과 비밀번호 설정
            statement.setString(1, username);
            statement.setString(2, inputPassword);

            // SQL 쿼리 실행
            ResultSet resultSet = statement.executeQuery();

            // 결과 확인
            if (resultSet.next()) {
                System.out.println("로그인 성공: 사용자 " + username);
            } else {
                System.out.println("로그인 실패: 사용자 " + username);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
