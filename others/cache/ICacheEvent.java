public interface ICacheEvent {
    public Object getSource();

    public int getType();

    public static final int REMOVE = 1;

    public static final int REMOVE_ALL = 2;

    public static final int UPDATE = 3;

    public static final int UPDATE_ALL = 4;
}
