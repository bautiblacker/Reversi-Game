package utils;

public class AI {
    private int role;
    private String type;
    private int param;
    private boolean prune;

    public AI(int role, String type, int param, boolean prune) {
        this.role = role;
        this.type = type;
        this.param = param;
        this.prune = prune;
    }
}
