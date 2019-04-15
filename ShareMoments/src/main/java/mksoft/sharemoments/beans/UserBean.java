package mksoft.sharemoments.beans;

import java.awt.BorderLayout;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.inject.Named;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import mksoft.sharemoments.entity.PhotoPost;
import mksoft.sharemoments.entity.User;

/**
 *
 * @author mk99
 */
@ManagedBean(name = "userBean", eager = true)
@SessionScoped
public class UserBean implements Serializable {
    
    private static final long serialVersionUID = 1L;    
    private Connection dbConnection;
    private User currentUser;
    
    public UserBean () throws ClassNotFoundException, SQLException 
    {
        status = false;
        establishConnection();
    }
    
    public void establishConnection() throws ClassNotFoundException, SQLException 
    {        
        dbConnection = getConnection("profiles");
    }
    
    public int findUser(String loginArg, String passwordArg) throws ClassNotFoundException, SQLException
    {
        PreparedStatement pstmt = dbConnection.prepareStatement("SELECT user_id, login, password FROM User WHERE login = '" + loginArg + "'");
        ResultSet rs = pstmt.executeQuery(); 

        if (!rs.next()) return -1;        
        if (rs.getString("password").equals(passwordArg)) {
            currentUser = new User(rs.getString("login"), rs.getString("password"));
            currentUser.setUserId(rs.getInt("user_id"));
            return 1;
        }
        return 0;        
    }
    
    public List<User> getUsers() throws ClassNotFoundException, SQLException 
    {
        PreparedStatement pstmt = dbConnection.prepareStatement("SELECT user_id, login, password FROM User");
        ResultSet rs = pstmt.executeQuery();
        
        List<User> users = new ArrayList<>();
        while (rs.next()) {    
            User temp = new User(rs.getString("login"), rs.getString("password"));
            temp.setUserId(rs.getInt("user_id"));
            users.add(temp); 
        }

        // close resources
        rs.close();
        pstmt.close();

        return users;

    }
    
    public List<PhotoPost> getPhotoPosts() throws ClassNotFoundException, SQLException 
    {        
        PreparedStatement pstmt = dbConnection.prepareStatement("SELECT ID, userID, src, text FROM PhotoPost WHERE userID='" + currentUser.getUserId() + "'");
        ResultSet rs = pstmt.executeQuery();
        
        List<PhotoPost> photoPosts = new ArrayList<>();
        
        while (rs.next()) {            
            PhotoPost temp = new PhotoPost(currentUser, rs.getString("src"), rs.getString("text"));
            temp.setId(rs.getInt("ID"));
            photoPosts.add(temp); 
        }            

        // close resources
        rs.close();
        pstmt.close();

        return photoPosts;
    }
    
    private Connection getConnection(String dbName) throws ClassNotFoundException 
    {        
        Connection connection = null;

        String url = "jdbc:mysql://localhost/" + dbName;
        String username = "guest";
        String password = "11111guest";

        try {           
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(url, username, password);
        } 
        catch (SQLException ex) {
            System.out.println("in exec");
            System.out.println(ex.getMessage());
        }
        
        return connection;
    }
    
    private String login;
    private String password;
    private boolean status;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    public String display() {
        return "display";
    }
    
    public String showResult() throws ClassNotFoundException, SQLException 
    {      
        if (login == null || password == null) {
            return "index";
        }
        
        int res = findUser(login, password);
        //System.out.print(res);
        
        switch (res) {
            case -1:
                status = false;
                return "User not found :(";
            case 0:
                status = false;
                return "Incorrect password :(";
            default:
                status = true;
                return "You are welcome, " + login + "!";
        }        
    }
    
    public String viewUsers()
    {
        return (status ? "user" : "display");
    }
    
    public String showWall()
    {
        return (status ? "profilePage" : "display");
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

}