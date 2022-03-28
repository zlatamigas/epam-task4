package epam.zlatamigas.multithreading.entity;

public class TruckTask {
    public enum TaskType {
        LOAD,
        UNLOAD,
        UNLOAD_AND_LOAD
    }

    private TaskType type;
    private int loadCount;
    private int unloadCount;

    public TruckTask(TaskType type, int count) {
        switch (type) {
            case LOAD -> {
                loadCount = count;
                unloadCount = 0;
                this.type = TaskType.LOAD;
            }
            case UNLOAD, UNLOAD_AND_LOAD -> {
                loadCount = 0;
                unloadCount = count;
                this.type = TaskType.UNLOAD;
            }
        }
    }

    public TruckTask(int loadCount, int unloadCount) {
        if (loadCount <= 0) {
            this.loadCount = 0;
            this.unloadCount = unloadCount;
            type = TaskType.UNLOAD;
        } else if (unloadCount <= 0) {
            this.loadCount = loadCount;
            this.unloadCount = 0;
            type = TaskType.LOAD;
        } else {
            this.loadCount = loadCount;
            this.unloadCount = unloadCount;
            type = TaskType.UNLOAD_AND_LOAD;
        }
    }

    public TaskType getType() {
        return type;
    }

    public int getLoadCount() {
        return loadCount;
    }

    public int getUnloadCount() {
        return unloadCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TruckTask truckTask = (TruckTask) o;
        return loadCount == truckTask.loadCount
                && unloadCount == truckTask.unloadCount
                && type != null
                && type == truckTask.type;
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + loadCount;
        result = 31 * result + unloadCount;
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TruckTask [");
        sb.append(type);
        switch (type) {
            case LOAD -> {
                sb.append(", loadCount=").append(loadCount);
            }
            case UNLOAD -> {
                sb.append(", unloadCount=").append(unloadCount);
            }
            case UNLOAD_AND_LOAD -> {
                sb.append(", loadCount=").append(loadCount);
                sb.append(", unloadCount=").append(unloadCount);
            }
        }
        sb.append(']');
        return sb.toString();
    }
}