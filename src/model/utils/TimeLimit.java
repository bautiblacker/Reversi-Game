package model.utils;

public class TimeLimit {
    private long timiLimit;
    private boolean aborted = false;

    public TimeLimit(long timiLimit){
       this.timiLimit = System.currentTimeMillis() + timiLimit;
    }

    public boolean isAborted() {
        return aborted;
    }

    public void setAborted(boolean aborted) {
        this.aborted = aborted;
    }

    public boolean isExceeded() {
        return System.currentTimeMillis() > timiLimit;
    }
}
