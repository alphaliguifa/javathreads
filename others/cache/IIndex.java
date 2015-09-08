import java.util.Collection;

public interface IIndex {
    public String getName();

    public Collection getKeys();

    public Collection getValues();

    public Object getValue(Object key);
}
