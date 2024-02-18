package Server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;



public class Signup {
    // Oracle 데이터베이스 연결 정보
    private static final String DB_URL = "jdbc:oracle:thin:@salmon:1521:XE"; // Oracle 서버 주소와 포트
    private static final String USER = "c##salmon"; // 데이터베이스 사용자 이름
    private static final String PASSWORD = "1234"; // 데이터베이스 비밀번호

    // 회원가입 메서드
    public static boolean register(String username, String password) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            // Oracle 드라이버 로드
            Class.forName("oracle.jdbc.driver.OracleDriver");

            // 데이터베이스 연결
            connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            
            //이미 사용 중인 아이디인지 확인하는 쿼리 작성
            String query = "SELECT COUNT(*) FROM users WHERE username = ?";
            
            // PreparedStatement를 사용하여 SQL 쿼리 실행
            statement = connection.prepareStatement(query);
            statement.setString(1, username);
            
            
            // 쿼리 실행 결과물 받아옴
            resultSet = statement.executeQuery();
            
            //이미 사용중인 아이디가 있으면 회원가입 실패
            if(resultSet.next() && resultSet.getInt(1)>0) {
            	System.out.println("이미 사용 중인 아이디입니다. 다른 아이디를 입력하세요.");
            	return false;
            
            }
            
            // 회원가입 SQL 쿼리 작성
            query = "INSERT INTO users (username, password) VALUES (?, ?)";

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

        // 회원가입 실패 시 false 반환
        return false;
    }
}
