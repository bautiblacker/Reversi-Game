package model.wrappers;

import java.io.Serializable;

public class AI implements Serializable {
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
    public AI() {

    }

    public void setRole(int role) {
        this.role = role;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setParam(int param) {
        this.param = param;
    }

    public void setPrune(boolean prune) {
        this.prune = prune;
    }

    public int getRole() {
        return role;
    }

    public String getType() {
        return type;
    }

    public int getParam() {
        return param;
    }

    public boolean isPrune() {
        return prune;
    }

    @Override
    public String toString() {
        return "AI{" +
                "role=" + role +
                ", type='" + type + '\'' +
                ", param=" + param +
                ", prune=" + prune +
                '}';
    }
}
