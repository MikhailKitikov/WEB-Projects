package mksoft.sharemoments.db;

import java.util.List;
import mksoft.sharemoments.entity.PhotoPost;
import mksoft.sharemoments.entity.User;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

public class DataHelper {

    private SessionFactory sessionFactory = null;
    private static DataHelper dataHelper;

    private DataHelper() {
        sessionFactory = HibernateUtil.getSessionFactory();
    }

    public static DataHelper getInstance() {
        return dataHelper == null ? new DataHelper() : dataHelper;
    }

    private Session getSession() {
        return sessionFactory.getCurrentSession();
    }
   
    public List<PhotoPost> getAllPosts() {
        return getSession().createCriteria(PhotoPost.class).list();
    }

    public List<User> getAllUsers() {
        return getSession().createCriteria(User.class).list();
    }

//    public List<Book> getBooksByGenre(Long genreId) {
//        return getSession().createCriteria(Book.class).add(Restrictions.eq("genre.id", genreId)).list();
//    }
//
//    public List<Book> getBooksByLetter(Character letter) {
//        return getBookList("name", letter.toString(), MatchMode.START);
//    }

    public List<PhotoPost> getPostsByUser(String userID) {
        return getPostList("userID", userID, MatchMode.ANYWHERE);
    }

//    public List<Book> getBooksByName(String bookName) {
//        return getBookList("name", bookName, MatchMode.ANYWHERE);
//    }

//    public byte[] getContent(Long id) {
//        return (byte[]) getFieldValue("content", id);
//    }
//
//    public byte[] getImage(Long id) {
//        return (byte[]) getFieldValue("image", id);
//    }

    public User getUser(long id) {
        return (User) getSession().get(User.class, id);
    }

    private List<PhotoPost> getPostList(String field, String value, MatchMode matchMode) {
        return getSession().createCriteria(PhotoPost.class).add(Restrictions.ilike(field, value, matchMode)).list();
    }

    private Object getFieldValue(String field, Long id) {
        return getSession().createCriteria(PhotoPost.class).setProjection(Projections.property(field)).add(Restrictions.eq("ID", id)).uniqueResult();
    }
}