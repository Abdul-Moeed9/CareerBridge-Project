package interfaces;
import java.util.List;

public interface IRankable {
    List<Object> rank(List<Object> items);
    double computeScore(Object item);
    List<Object> sortByScore(List<Object> items);
}