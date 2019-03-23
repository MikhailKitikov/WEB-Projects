package mksoft.testjsf;

import java.awt.BorderLayout;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.inject.Named;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 *
 * @author mk99
 */
@ManagedBean(name = "userBean")
@SessionScoped
public class UserBean implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    public int findUser(String login1, String password) throws ClassNotFoundException, SQLException
    {
        Connection connect = getConnection("profiles");
        PreparedStatement pstmt = connect.prepareStatement("SELECT user_id, login, password FROM User WHERE login='" + login1 + "'");
        //pstmt.setString(1, "login1");
        ResultSet rs = pstmt.executeQuery();

        if (!rs.next())
            return -1;
        
        return (rs.getString("password")).equals(password) ? 1 : 0;
        
    }
    
    public List<User> getUsers() throws ClassNotFoundException, SQLException {

        Connection connect = getConnection("profiles");
        PreparedStatement pstmt = connect.prepareStatement("SELECT user_id, login, password FROM User");
        ResultSet rs = pstmt.executeQuery();
        
        List<User> users = new ArrayList<>();

        while (rs.next()) {            
            users.add(new User(rs.getInt("user_id"), rs.getString("login"), rs.getString("password"))); 
        }

        // close resources
        rs.close();
        pstmt.close();
        connect.close();

        return users;

    }
    
    private Connection getConnection(String dbName) throws ClassNotFoundException 
    {        
        Connection connection = null;

        String url = "jdbc:mysql://localhost/" + dbName;
        String username = "root";
        String password = "MY2630142@sql_root99";

        try {           
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(url, username, password);
            //System.out.println("Connection established" + connection);
        } 
        catch (SQLException ex) {
            System.out.println("in exec");
            System.out.println(ex.getMessage());
        }
        
        return connection;
    }

}