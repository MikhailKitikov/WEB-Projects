package mksoft.sharemoments.entity;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author mk99
 */
@Entity
@Table(name = "Follower")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Follower.findAll", query = "SELECT f FROM Follower f"),
    @NamedQuery(name = "Follower.remove", query = "DELETE FROM Follower f WHERE f.whoName = :whoname AND f.whomName = :whomname"),
    @NamedQuery(name = "Follower.find", query = "SELECT f FROM Follower f WHERE f.whoName = :whoname AND f.whomName = :whomname")
    , @NamedQuery(name = "Follower.findByWhoName", query = "SELECT f FROM Follower f WHERE f.whoName = :whoName")
    , @NamedQuery(name = "Follower.findByWhomName", query = "SELECT f FROM Follower f WHERE f.whomName = :whomName")
    , @NamedQuery(name = "Follower.findById", query = "SELECT f FROM Follower f WHERE f.id = :id")})
public class Follower implements Serializable {

    private static final long serialVersionUID = 1L;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 60)
    @Column(name = "who_name")
    private String whoName;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 60)
    @Column(name = "whom_name")
    private String whomName;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;

    public Follower() {
    }

    public Follower(Integer id) {
        this.id = id;
    }

    public Follower(Integer id, String whoName, String whomName) {
        this.id = id;
        this.whoName = whoName;
        this.whomName = whomName;
    }

    public String getWhoName() {
        return whoName;
    }

    public void setWhoName(String whoName) {
        this.whoName = whoName;
    }

    public String getWhomName() {
        return whomName;
    }

    public void setWhomName(String whomName) {
        this.whomName = whomName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Follower)) {
            return false;
        }
        Follower other = (Follower) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "mksoft.sharemoments.entity.Follower[ id=" + id + " ]";
    }
    
}
