package Client;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Login {

	// Oracle 데이터베이스 연결 정보
	private static final String DB_URL = "jdbc:oracle:thin:@salmon:1521:XE"; // Oracle 서버 주소와 포트
	private static final String USER = "c##salmon"; // 데이터베이스 사용자 이름
	private static final String PASSWORD = "1234"; // 데이터베이스 비밀번호

	// Oracle 데이터베이스 연결 및 로그인 확인 메서드
	public static boolean login(String username, String password) {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;

		try {
			// Oracle 드라이버 로드
			Class.forName("oracle.jdbc.driver.OracleDriver");

			// 데이터베이스 연결
			connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);

			// SQL 쿼리 작성
			String query = "SELECT COUNT(*) FROM users WHERE username = ? AND password = ?";

			// PreparedStatement를 사용하여 SQL 쿼리 실행
			statement = connection.prepareStatement(query);
			statement.setString(1, username);
			statement.setString(2, password);

			// 쿼리 실행 결과를 받아옴
			resultSet = statement.executeQuery();

			// 결과 처리
			if (resultSet.next()) {
				int count = resultSet.getInt(1);
				return count == 1; // 사용자가 존재하면 true 반환
			}
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		} finally {
			// 연결, statement, resultSet 닫기
			try {
				if (resultSet != null)
					resultSet.close();
				if (statement != null)
					statement.close();
				if (connection != null)
					connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		// 사용자가 존재하지 않거나 예외 발생 시 false 반환
		return false;
	}
}
