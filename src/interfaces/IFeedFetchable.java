package interfaces;
import java.util.List;
import domain.Offering;

public interface IFeedFetchable {
    List<Offering> fetch();
    boolean validate(String data);
    List<Offering> parse(String rawData);
}