package mksoft.sharemoments.entity;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author mk99
 */
@Entity
@Table(name = "UserData")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "UserData.findAll", query = "SELECT u FROM UserData u"),
    @NamedQuery(name = "UserData.changeAvatar", query = "UPDATE UserData u SET u.avatar = :avatar WHERE u.username = :username"),
    @NamedQuery(name = "UserData.changeInfo", query = "UPDATE UserData u SET u.name = :name, u.bio = :bio WHERE u.username = :username")
    , @NamedQuery(name = "UserData.findByName", query = "SELECT u FROM UserData u WHERE u.name = :name")
    , @NamedQuery(name = "UserData.findByBio", query = "SELECT u FROM UserData u WHERE u.bio = :bio")
    , @NamedQuery(name = "UserData.findByAvatar", query = "SELECT u FROM UserData u WHERE u.avatar = :avatar")
    , @NamedQuery(name = "UserData.findByUsername", query = "SELECT u FROM UserData u WHERE u.username = :username")})
public class UserData implements Serializable {

    private static final long serialVersionUID = 1L;
    @Size(max = 60)
    @Column(name = "name")
    private String name;
    @Size(max = 120)
    @Column(name = "bio")
    private String bio;
    @Size(max = 60)
    @Column(name = "avatar")
    private String avatar;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 60)
    @Column(name = "username")
    private String username;
    @JoinColumn(name = "username", referencedColumnName = "username", insertable = false, updatable = false)
    @OneToOne(optional = false)
    private User user;

    public UserData() {
    }

    public UserData(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (username != null ? username.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof UserData)) {
            return false;
        }
        UserData other = (UserData) object;
        if ((this.username == null && other.username != null) || (this.username != null && !this.username.equals(other.username))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "mksoft.sharemoments.entity.UserData[ username=" + username + " ]";
    }
    
}
