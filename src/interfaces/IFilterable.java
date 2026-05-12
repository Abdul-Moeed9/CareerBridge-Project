package interfaces;
import java.util.List;


public interface IFilterable {
    List<Object> applyFilter(List<Object> items, Object criteria);
}