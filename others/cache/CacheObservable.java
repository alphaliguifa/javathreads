public class CacheObservable extends Observable {

    public void fire(CacheEvent event) {
        this.setChanged();
        super.notifyObservers(event);
    }
}
