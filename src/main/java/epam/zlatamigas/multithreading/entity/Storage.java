package epam.zlatamigas.multithreading.entity;

public class Storage {

    private final int capacity;
    private int occupiedSize;

    public Storage(int capacity, int occupiedSize) {
        this.capacity = capacity;
        this.occupiedSize = occupiedSize;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getOccupiedSize() {
        return occupiedSize;
    }

    public void setOccupiedSize(int occupiedSize) {
        this.occupiedSize = occupiedSize;
    }

    public int getAvailableSize() {
        return capacity - occupiedSize;
    }

    public boolean addGoods(int goodsCount) {
        boolean result = false;
        if (goodsCount > 0 && goodsCount <= getAvailableSize()) {
            occupiedSize += goodsCount;
            result = true;
        }
        return result;
    }

    public boolean removeGoods(int goodsCount) {
        boolean result = false;
        if (goodsCount > 0 && goodsCount <= occupiedSize) {
            occupiedSize -= goodsCount;
            result = true;
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Storage storage = (Storage) o;
        return capacity == storage.capacity
                && occupiedSize == storage.occupiedSize;
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + capacity;
        result = 31 * result + occupiedSize;
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Storage [");
        sb.append(occupiedSize).append("/").append(capacity).append(" filled]");
        return sb.toString();
    }
}
