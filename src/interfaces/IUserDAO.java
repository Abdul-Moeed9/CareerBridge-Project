package interfaces;


public interface IUserDAO {
    Object findByID(int id);
    boolean save(Object obj);
    boolean update(Object obj);
    boolean delete(int id);
}
