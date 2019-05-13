package mksoft.sharemoments.entity;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author mk99
 */
@Entity
@Table(name = "PhotoPost")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PhotoPost.findAll", query = "SELECT p FROM PhotoPost p ORDER BY p.date DESC")
    , @NamedQuery(name = "PhotoPost.findById", query = "SELECT p FROM PhotoPost p WHERE p.id = :id")
    , @NamedQuery(name = "PhotoPost.findBySrc", query = "SELECT p FROM PhotoPost p WHERE p.src = :src")
    , @NamedQuery(name = "PhotoPost.findByUsername", query = "SELECT p FROM PhotoPost p WHERE p.username = :username")
    , @NamedQuery(name = "PhotoPost.findByText", query = "SELECT p FROM PhotoPost p WHERE p.text = :text")})
public class PhotoPost implements Serializable {

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "postID")
    private Collection<PostLike> postLikeCollection;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "postID")
    private Collection<Comment> commentCollection;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 30)
    @Column(name = "src")
    private String src;
    @Size(max = 255)
    @Column(name = "text")
    private String text;
    @JoinColumn(name = "username", referencedColumnName = "username")
    @ManyToOne(optional = false)
    private User username;
    @Basic(optional = false)
    @NotNull
    @Column(name = "date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    public PhotoPost() {
    }

    public PhotoPost(Integer id) {
        this.id = id;
    }

    public PhotoPost(Integer id, String src) {
        this.id = id;
        this.src = src;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public User getUsername() {
        return username;
    }

    public void setUsername(User username) {
        this.username = username;
    }
    
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
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
        if (!(object instanceof PhotoPost)) {
            return false;
        }
        PhotoPost other = (PhotoPost) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "post" + id;
    }

    @XmlTransient
    public Collection<Comment> getCommentCollection() {
        return commentCollection;
    }

    public void setCommentCollection(Collection<Comment> commentCollection) {
        this.commentCollection = commentCollection;
    }

    @XmlTransient
    public Collection<PostLike> getPostLikeCollection() {
        return postLikeCollection;
    }

    public void setPostLikeCollection(Collection<PostLike> postLikeCollection) {
        this.postLikeCollection = postLikeCollection;
    }
    
}
