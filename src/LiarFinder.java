import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.PriorityQueue;

public class LiarFinder {

    public int[] liarsCounts;
    private int[] lWitnesses;
    private int witnessesCount = 0;

    public LiarFinder(int maxNumber){
        lWitnesses = new int[maxNumber];
        liarsCounts = new int[maxNumber];
    }

    public void FindLiars(int n){

        boolean isPrime = true;

        var power2 = FindD(n);

        BigInteger d = BigInteger.valueOf(power2.d);


        BigInteger bigN = BigInteger.valueOf(n);

        for(int witness = 2; witness <= n / 2 ; witness++) {
            if (!IsComposite(d, BigInteger.valueOf(witness), bigN, power2.m)) {
                lWitnesses[witnessesCount++] = witness;
                lWitnesses[witnessesCount++] = n - witness;
            } else {
                isPrime = false;
            }
        }
        if(!isPrime) {
//            int prevWitness = -1;
            for (int i = 0; i < witnessesCount; i++) {
//                if(i < lPrimesCount - 1 || lWitnesses[i] != prevWitness) {
                    liarsCounts[lWitnesses[i]]++;
//                } else {
//                    System.out.println("Bruh");
//                }

//                prevWitness = lWitnesses[i];
            }
        }

        witnessesCount = 0;
    }

    private static PowerTwoFinder FindD(int n){
        int d = n - 1;
        int m = 0;
        do {
            d /= 2;
            m++;
        } while(d % 2 == 0);

        return new PowerTwoFinder(m, d);
    }

    private static boolean IsComposite(BigInteger d, BigInteger witness, BigInteger n, int m){
        for(int i = 0; i <= m; i++){
            var modpow = witness.modPow(d.multiply(BigInteger.valueOf(1L << i)), n);
            boolean isPrime = modpow.equals(BigInteger.ONE) || modpow.add(BigInteger.ONE).equals(n);
            if(isPrime){
                return false;
            }
        }

        return true;
    }

    public static LiarFinder collect(LiarFinder ... finders){
        if(finders == null || finders.length == 0){
            return null;
        }

        var results = finders[0].liarsCounts;
        for (int i = 1; i < finders.length; i++){
            var witnesses = finders[i].liarsCounts;
            for(int witness = 0; witness < finders[0].liarsCounts.length; witness++){
                results[witness] += witnesses[witness];
            }
        }

        return finders[0];
    }

    public LiarReport worstLiar(){
        LiarReport lr = new LiarReport(8);
        int worstLiar = -1;
        int worstLiarCount = -1;
        for(int i = 0; i < liarsCounts.length; i++){
            lr.Add(new LiarEntry(i, liarsCounts[i]));
        }

        return lr;
    }

    public void PrintToCSV(String filepath) throws IOException {
        FileWriter fw = new FileWriter(filepath);

        fw.write("number,lies\n");
        for(int i = 0; i < liarsCounts.length; i++){
            fw.write(i+ "," + liarsCounts[i] + "\n");
            fw.flush();
        }
    }
}

class PowerTwoFinder {
    public int m;
    public int d;

    public PowerTwoFinder(int m, int d) {
        this.m = m;
        this.d = d;
    }
}

class LiarReport {
    private int topN;
    private  int count;
    private  PriorityQueue<LiarEntry> worstLiars;
    private  int bestWorstOffender = -1;

    public LiarReport(int topN){
        worstLiars = new PriorityQueue<>(topN);
        this.topN = topN;
    }

    public void Add(LiarEntry le){
        if(le.liarCount >= bestWorstOffender){
            worstLiars.add(le);
            if(++count > topN) {
                worstLiars.remove();
                bestWorstOffender = worstLiars.peek().liarCount;
            }
        }
    }

    public String toString(){

        StringBuilder sb = new StringBuilder();
        int i = topN;
        while(!worstLiars.isEmpty()){
            var liar = worstLiars.remove();
            sb.append(i--);
            sb.append(": ");
            sb.append(liar.toString());
            sb.append("\n");
        }

        return sb.toString();
    }
}

class LiarEntry implements Comparable<LiarEntry>{
    public int witness;
    public int liarCount;

    public LiarEntry(int witness, int liarCount) {
        this.witness = witness;
        this.liarCount = liarCount;
    }

    public String toString(){
        return witness + " lied " + liarCount + " time" + (liarCount == 1 ? "" : "s");
    }

    @Override
    public int compareTo(LiarEntry o) {
        return this.liarCount - o.liarCount;
    }
}