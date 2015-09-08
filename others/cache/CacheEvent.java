import java.util.EventObject;

public class CacheEvent extends EventObject implements ICacheEvent {

    private static final long serialVersionUID = 5668925269277365323L;

    private int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public CacheEvent(Object source, int type) {
        super(source);
        this.type = type;
    }

}
