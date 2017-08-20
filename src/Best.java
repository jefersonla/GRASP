package grasp;

public class Best {

    private double cost;
    private int[] vector;

    public Best(double cost, int[] vector) {
        this.cost = 0;
        this.vector = vector.clone();
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public int[] getVector() {
        return vector;
    }

    public void setVector(int[] vector) {
        this.vector = vector;
    }

    public void setPosVector(int pos, int value) {
        vector[pos] = value;
    }

    public int getVectorLength() {
        return vector.length;
    }

}
