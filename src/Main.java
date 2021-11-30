import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.Executors;

public class Main {
    //    static final int MAX = (int) Integer.MAX_VALUE - 8;
//    static final int MAX = (int) 1e6;
    static final int MAX = (int) 1e6;
    //    static int[] liarCounts = new int[MAX];
    static final int THREAD_COUNT = 24;

    private static final int SUBDIVISIONS = 400;
    private static final int total = MAX / 2 - 1;

    private static LiarFinder[] liarFinders = new LiarFinder[THREAD_COUNT];

    private static volatile Integer count = 0;
    private static volatile double percentComplete = 0;

    public synchronized static void IncrementPercentage(int lcount){
        count += lcount;
        double newPercentComplete =  (100.0 * count) / total;
        double targeet = percentComplete + (100.0  / SUBDIVISIONS);
        if(targeet < newPercentComplete){
            percentComplete = newPercentComplete;
            System.out.printf("%.3f%n", newPercentComplete);
        }
//        else {
//            System.out.println("ROGN: " + count + " " + newPercentComplete + " " + targeet);
//        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {

        var threadPool = Executors.newFixedThreadPool(THREAD_COUNT);

        Thread[] threads = new Thread[THREAD_COUNT];

//        ArrayList<Callable<Integer>> tasks = new ArrayList<>(THREAD_COUNT);


        LiarFinder llf = new LiarFinder(1000);
        llf.FindLiars(23);

        for (int i = 0; i < THREAD_COUNT; i++) {
            liarFinders[i] = new LiarFinder(MAX);
            final int thread = i;
            final LiarFinder lf = liarFinders[i];
            Runnable r = new Runnable() {
                @Override
                public void run()  {
                    System.out.println("Starting " + thread);
                    int starting = MAX - thread * 2 - ((MAX + 1) % 2);
//                    int starting = MAX - (thread * 2) * 3;
                    int lcount = 0;
                    for (int n = starting; n > 2; n -= THREAD_COUNT * 2) {
                        lf.FindLiars(n);
                        if(++lcount % 100 == 0){
                            IncrementPercentage(lcount);
                            lcount = 0;
                        }
                    }

                    System.out.println("Finished " + thread);
                }
            };
            threads[i] = new Thread(r);
            threads[i].start();
        }

        System.out.println("It's Started");

        System.out.println("Joining");
        for(Thread thread : threads){
            thread.join();
            System.out.println("JOINED");
        }

        System.out.println("Collecting");
        LiarFinder result = LiarFinder.collect(liarFinders);

        System.out.println(result.worstLiar());

        System.out.println("Writing results to a file");
        result.PrintToCSV("lyingcount.csv");
        System.out.println("Completed");

    }
}
