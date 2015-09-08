import java.util.Comparator;

public interface Sorter {

    public Object[] sort(Object[] objects);
    
    public Comparator getComparator();
}
