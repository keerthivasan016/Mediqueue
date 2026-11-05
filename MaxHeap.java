import java.util.ArrayList;
import java.util.List;

public class MaxHeap {
    private final ArrayList<Patient> heap;
    private long comparisonCount;

    public MaxHeap() {
        heap = new ArrayList<Patient>();
        comparisonCount = 0;
    }

    // Makes an independent heap container. Patient objects are shared intentionally;
    // this is safe for the performance/demo copies because they do not update scores.
    public MaxHeap(List<Patient> patients) {
        this();
        for (Patient patient : patients) insert(patient);
        resetComparisonCount();
    }

    public int size() { return heap.size(); }
    public boolean isEmpty() { return heap.isEmpty(); }
    public long getComparisonCount() { return comparisonCount; }
    public void resetComparisonCount() { comparisonCount = 0; }

    // Max-Heap property: every parent has priority >= each of its children.
    // Higher score wins; for equal scores, the earlier arrival (smaller order) wins.
    private boolean higherPriority(Patient a, Patient b) {
        comparisonCount++;
        if (a.getPriorityScore() != b.getPriorityScore()) {
            return a.getPriorityScore() > b.getPriorityScore();
        }
        return a.getArrivalOrder() < b.getArrivalOrder();
    }

    // Parent index = (i - 1) / 2
    // Left child index = 2 * i + 1
    // Right child index = 2 * i + 2
    public void insert(Patient patient) {
        heap.add(patient);
        heapifyUp(heap.size() - 1);
    }

    // Heapify-up: move the inserted/updated item toward the root while it
    // has higher priority than its parent.
    private void heapifyUp(int index) {
        while (index > 0) {
            int parent = (index - 1) / 2;
            if (!higherPriority(heap.get(index), heap.get(parent))) break;
            swap(index, parent);
            index = parent;
        }
    }

    public Patient peek() {
        return heap.isEmpty() ? null : heap.get(0);
    }

    public Patient removeMax() {
        if (heap.isEmpty()) return null;

        Patient highest = heap.get(0);
        Patient last = heap.remove(heap.size() - 1);

        if (!heap.isEmpty()) {
            heap.set(0, last);
            heapifyDown(0);
        }
        return highest;
    }

    // Heapify-down: move the root/reduced-priority item toward the leaves
    // by repeatedly swapping it with its higher-priority child.
    private void heapifyDown(int index) {
        while (true) {
            int left = 2 * index + 1;
            int right = 2 * index + 2;
            int best = index;

            if (left < heap.size() && higherPriority(heap.get(left), heap.get(best))) {
                best = left;
            }
            if (right < heap.size() && higherPriority(heap.get(right), heap.get(best))) {
                best = right;
            }
            if (best == index) break;

            swap(index, best);
            index = best;
        }
    }

    public boolean updatePriority(String patientId, int newScore) {
        if (newScore < 1 || newScore > 100) return false;

        for (int i = 0; i < heap.size(); i++) {
            if (heap.get(i).getPatientId().equalsIgnoreCase(patientId)) {
                int oldScore = heap.get(i).getPriorityScore();
                heap.get(i).setPriorityScore(newScore);
                if (newScore > oldScore) {
                    heapifyUp(i);
                } else if (newScore < oldScore) {
                    heapifyDown(i);
                }
                return true;
            }
        }
        return false;
    }

    public Patient findById(String patientId) {
        for (Patient patient : heap) {
            if (patient.getPatientId().equalsIgnoreCase(patientId)) return patient;
        }
        return null;
    }

    public boolean containsId(String patientId) {
        return findById(patientId) != null;
    }

    public List<Patient> getHeapArrayCopy() {
        return new ArrayList<Patient>(heap);
    }

    public void displayArray() {
        if (heap.isEmpty()) {
            System.out.println("(Heap is empty)");
            return;
        }
        System.out.println("Heap array (level-order):");
        for (int i = 0; i < heap.size(); i++) {
            Patient p = heap.get(i);
            System.out.printf("[%d] %s (score=%d, arrival=%d)%n",
                    i, p.getPatientName(), p.getPriorityScore(), p.getArrivalOrder());
        }
    }

    public void displayTree() {
        if (heap.isEmpty()) {
            System.out.println("(Heap is empty)");
            return;
        }
        System.out.println("Heap binary tree (root first):");
        displayTree(0, 0, "ROOT");
    }

    private void displayTree(int index, int depth, String label) {
        if (index >= heap.size()) return;
        Patient p = heap.get(index);
        String indent = repeat("    ", depth);
        System.out.printf("%s%s: %s [score=%d, arrival=%d]%n",
                indent, label, p.getPatientName(),
                p.getPriorityScore(), p.getArrivalOrder());
        displayTree(2 * index + 1, depth + 1, "L");
        displayTree(2 * index + 2, depth + 1, "R");
    }

    public void displayPriorityOrder() {
        if (heap.isEmpty()) {
            System.out.println("(No patients in the queue)");
            return;
        }
        // Work on a copied heap so displaying the order does not destroy the original.
        MaxHeap copy = new MaxHeap();
        copy.heap.addAll(heap);
        System.out.println(PatientTable.header());
        while (!copy.isEmpty()) System.out.println(copy.removeMax().toTableRow());
    }

    public void displayHeapifyDemo() {
        MaxHeap demo = new MaxHeap();
        int[] scores = {45, 92, 65, 98, 35};
        System.out.println("Step-by-step Max-Heap demo (scores only):");
        for (int score : scores) {
            demo.insert(new Patient("D" + score, "Demo", "Demo condition",
                    score, score));
            System.out.print("Inserted " + score + " -> heap array: ");
            demo.printScores();
        }
        System.out.println("The root is always the highest-priority item.");
        System.out.println("Removing the root replaces it with the last item, then heapify-down restores the property.");
        Patient removed = demo.removeMax();
        System.out.println("Removed highest score: " + removed.getPriorityScore());
        System.out.print("After removal -> heap array: ");
        demo.printScores();
    }

    private void printScores() {
        System.out.print("[");
        for (int i = 0; i < heap.size(); i++) {
            if (i > 0) System.out.print(", ");
            System.out.print(heap.get(i).getPriorityScore());
        }
        System.out.println("]");
    }

    private void swap(int a, int b) {
        Patient temp = heap.get(a);
        heap.set(a, heap.get(b));
        heap.set(b, temp);
    }

    private String repeat(String value, int count) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < count; i++) result.append(value);
        return result.toString();
    }
}
