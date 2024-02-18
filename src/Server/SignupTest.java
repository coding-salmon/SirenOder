package Server;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SignupTest {
    // Oracle 데이터베이스 연결 정보
	private static final String DB_URL = "jdbc:oracle:thin:@salmon:1521:XE"; // Oracle 서버 주소와 포트
    private static final String USER = "c##salmon"; // 데이터베이스 사용자 이름
    private static final String PASSWORD = "1234"; // 데이터베이스 비밀번호

    public static void main(String[] args) {
        String username = "testuser1"; // 새로운 사용자 이름
        String password = "testpassword1"; // 새로운 비밀번호

        // 회원가입 시도
        boolean registered = register(username, password);

        // 회원가입 결과 출력
        if (registered) {
            System.out.println("회원가입 성공!");
        } else {
            System.out.println("회원가입 실패?!");
        }
    }

    // 회원가입 메서드
    public static boolean register(String username, String password) {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            // Oracle 드라이버 로드
            Class.forName("oracle.jdbc.driver.OracleDriver");

            // 데이터베이스 연결
            connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);

            // SQL 쿼리 작성
            String query = "INSERT INTO users (username, password) VALUES (?, ?)";

            // PreparedStatement를 사용하여 SQL 쿼리 실행
            statement = connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, password);

            // 쿼리 실행
            int rowsInserted = statement.executeUpdate();

            // 회원가입 성공 여부 반환
            return rowsInserted > 0;
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        } finally {
            // 연결 및 statement 닫기
            try {
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        // 회원가입 실패 시 false 반환
        return false;
    }
}
