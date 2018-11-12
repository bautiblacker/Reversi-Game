package model.ai;

import model.Point;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

public class Dot {
    private static int idCounter = 0;
    private int id;
    private Point point;
    private int value;
    private boolean chosen;
    private boolean pruned;

    private Collection<Dot> neighbours;

    public Dot(Point point, int value) {
        this.id = idCounter++;
        this.point = point;
        this.value = value;
        this.chosen = false;
        this.pruned = false;
        neighbours = new HashSet<>();
    }
    public Point getPoint() {
        return point;
    }
    public void setPoint(Point point) {
        this.point = point;
    }

    public void setChosen(boolean chosen) {
        this.chosen = chosen;
    }

    public void setPruned(boolean pruned) {
        this.pruned = pruned;
    }

    public Collection<Dot> getNeighbours() {
        return neighbours;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public static String tree(Dot dot) {
        StringBuilder str = new StringBuilder("digraph { \n");
        tree(dot, str);
        str.append("}\n");
        return str.toString();
    }
    public static void resetCounter() {
        idCounter = 0;
    }

    private static void tree(Dot dot, StringBuilder str) {
        str.append(dot.makeLabel()).append("\n");
        if(!dot.getNeighbours().isEmpty()){
            for(Dot nbor : dot.getNeighbours()){
                str.append(dot.getId()).append("->").append(nbor.getId()).append("\n");
                tree(nbor, str);
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dot dot = (Dot) o;
        return getValue() == dot.getValue() &&
                isChosen() == dot.isChosen() &&
                isPruned() == dot.isPruned() &&
                Objects.equals(getPoint(), dot.getPoint());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPoint(), getValue(), isChosen(), isPruned());
    }

    private int getId() {
        return id;
    }

    private int getValue() {
        return value;
    }

    private boolean isChosen() {
        return chosen;
    }

    private boolean isPruned() {
        return pruned;
    }

    private StringBuilder makeLabel() {
        StringBuilder str = new StringBuilder().append(getId()).append(" ").append("[label=");
        if(point == null)
            str.append("\"START \"").append("]").append("\n");
        else {
            str.append("\"").append(point).append(" ").append((pruned) ? "": value).append("\"").append(", ");

            if (pruned) {
                str.append("shape = ").append("\"rectangle\"").append(", ")
                        .append("style = ").append("\"filled\"");

            } else if (chosen) {
                str.append("shape = ").append("\"ellipse\"").append(", ")
                        .append("style = ").append("\"filled\"").append(", ")
                        .append("color = ").append("\"pink\"");
            } else
                str.append("shape = ").append("\"ellipse\"");
            str.append("]").append("\n");
            return str;
        }
        return str;
    }

}
