import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.mockito.ArgumentMatchers.anyString;

public class UserManagementServiceTest {

    private Connection mockConnection;
    private PreparedStatement mockStatement;
    private ResultSet mockResultSet;
    private UserManagementService userManagementService;

    @BeforeEach
    public void setup() throws Exception {
        // Mock the dependencies
        mockConnection = Mockito.mock(Connection.class);
        mockStatement = Mockito.mock(PreparedStatement.class);
        mockResultSet = Mockito.mock(ResultSet.class);

        // Create an instance of the class under test
        userManagementService = new UserManagementService();

        // Set up the mock behavior
        Mockito.when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        Mockito.when(mockStatement.executeUpdate()).thenReturn(1);
        Mockito.when(mockStatement.getResultSet()).thenReturn(mockResultSet);
        Mockito.when(mockResultSet.next()).thenReturn(true);
    }

    @Test
    public void testUpdateAuthKey() throws Exception {
        // Set up test data
        String email = "test@example.com";

        // Call the method
        String authKey = userManagementService.updateAuthKey(email);

        // Verify the SQL query and parameters
        Mockito.verify(mockConnection).prepareStatement(
                "UPDATE users SET authKey = ?, authKeyExpiry = ? WHERE email = ?");
        Mockito.verify(mockStatement).setString(1, Mockito.anyString());
        Mockito.verify(mockStatement).setString(2, Mockito.anyString());
        Mockito.verify(mockStatement).setString(3, email);

        // Verify that the method returns the expected authKey
        Assertions.assertNotNull(authKey);

        // Verify that the connection, statement, and result set are closed
        Mockito.verify(mockResultSet).close();
        Mockito.verify(mockStatement).close();
        Mockito.verify(mockConnection).close();
    }
}
