import java.util.ArrayList;
import java.util.List;

public class PerformanceComparison {

    public static void run(List<Patient> source) {
        if (source.isEmpty()) {
            System.out.println("No patients available for comparison.");
            return;
        }

        List<Patient> fifo = new ArrayList<Patient>(source);
        List<Patient> unsorted = new ArrayList<Patient>(source);
        MaxHeap heap = new MaxHeap(source);

        List<String> fifoOrder = new ArrayList<String>();
        List<String> unsortedOrder = new ArrayList<String>();
        List<String> heapOrder = new ArrayList<String>();

        // FIFO: removal is O(1); it deliberately ignores urgency.
        int fifoHead = 0;
        while (fifoHead < fifo.size()) {
            Patient p = fifo.get(fifoHead++);
            fifoOrder.add(label(p));
        }

        // Unsorted list: scan remaining patients to locate the best priority.
        long unsortedComparisons = 0;
        while (!unsorted.isEmpty()) {
            int bestIndex = 0;
            for (int i = 1; i < unsorted.size(); i++) {
                unsortedComparisons++;
                if (comesBefore(unsorted.get(i), unsorted.get(bestIndex))) bestIndex = i;
            }
            unsortedOrder.add(label(unsorted.remove(bestIndex)));
        }

        // Max-Heap: comparisons are counted inside its priority comparator.
        heap.resetComparisonCount();
        while (!heap.isEmpty()) heapOrder.add(label(heap.removeMax()));
        long heapComparisons = heap.getComparisonCount();

        System.out.println("\nPERFORMANCE COMPARISON");
        System.out.printf("%-18s | %-13s | %s%n", "Method", "Comparisons", "Removal complexity");
        System.out.println("-------------------+---------------+--------------------");
        System.out.printf("%-18s | %-13d | %s%n", "FIFO queue", 0, "O(1), ignores priority");
        System.out.printf("%-18s | %-13d | %s%n", "Unsorted list", unsortedComparisons, "O(n) per removal");
        System.out.printf("%-18s | %-13d | %s%n", "Max-Heap", heapComparisons, "O(log n) per removal");

        System.out.println("\nTHEORETICAL COMPLEXITY");
        System.out.println("FIFO removal       : O(1), but ignores priority");
        System.out.println("Unsorted list removal: O(n)");
        System.out.println("Max-Heap insertion : O(log n)");
        System.out.println("Max-Heap removal   : O(log n)");
        System.out.println("Max-Heap peek      : O(1)");

        System.out.println("\nSERVING ORDER (ID:Name[score])");
        System.out.println("FIFO       : " + fifoOrder);
        System.out.println("Unsorted   : " + unsortedOrder);
        System.out.println("Max-Heap   : " + heapOrder);
        System.out.println("\nNote: comparison counts are actual key comparisons for the operations shown.");
        System.out.println("FIFO uses zero priority comparisons because it never checks priority.");
    }

    private static boolean comesBefore(Patient a, Patient b) {
        if (a.getPriorityScore() != b.getPriorityScore()) {
            return a.getPriorityScore() > b.getPriorityScore();
        }
        return a.getArrivalOrder() < b.getArrivalOrder();
    }

    private static String label(Patient p) {
        return p.getPatientId() + ":" + p.getPatientName() + "[" + p.getPriorityScore() + "]";
    }
}
