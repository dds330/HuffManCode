public class Pair implements Comparable<Pair>{
    private char value;
    private double prob;

    public Pair(char value, double prob){
        this.value = value;
        this.prob = prob;
    }

    public char getValue(){return this.value;}
    public double getProb(){return prob;}

    @Override
    public int compareTo(Pair P) {
        return Double.compare(this.getProb(), P.getProb());
    }
}
