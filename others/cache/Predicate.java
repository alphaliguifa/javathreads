import java.io.Serializable;

public interface Predicate extends Serializable {

    boolean evaluate(Object obj);

}
