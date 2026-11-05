import java.util.Scanner;

/**
 * MediQueue - Smart Emergency Priority System
 * Educational simulation only. Uses FICTIONAL patients. Not for real medical use.
 */
public class Main {

    // ---------- condition catalogue (fictional, with suggested scores) ----------
    private static final String[] CONDITIONS = {
        "Cardiac Arrest", "Severe Bleeding", "Stroke", "Breathing Difficulty",
        "Major Fracture", "High Fever", "Minor Burn", "Minor Cut"
    };
    private static final int[] SUGGESTED = { 98, 92, 90, 85, 65, 45, 35, 15 };

    private static final MaxHeap queue = new MaxHeap();
    private static int nextArrival = 1;
    private static Scanner in;

    public static void main(String[] args) {
        in = new Scanner(System.in);
        System.out.println("==============================================");
        System.out.println("  MediQueue - Smart Emergency Priority System");
        System.out.println("==============================================");
        System.out.println("Educational simulation only. Not for real medical use.\n");

        preloadPatients();
        System.out.println("8 fictional patients have been preloaded.\n");

        boolean running = true;
        while (running) {
            printMenu();
            String choice = readLine("Enter your choice: ");
            if (choice == null) break;            // input closed
            System.out.println();
            switch (choice.trim()) {
                case "1": registerPatient(); break;
                case "2": displayAll(); break;
                case "3": updatePriority(); break;
                case "4": servePatient(); break;
                case "5": peekPatient(); break;
                case "6": queue.displayArray(); break;
                case "7": queue.displayTree(); break;
                case "8": PerformanceComparison.run(queue.getSortedList()); break;
                case "9": heapDemo(); break;
                case "0": running = false; break;
                default:  System.out.println("Invalid choice. Please enter a number from 0 to 9.");
            }
            System.out.println();
        }
        System.out.println("Thank you for using MediQueue. Goodbye!");
    }

    private static void printMenu() {
        System.out.println("------------- MENU -------------");
        System.out.println(" 1. Register a patient");
        System.out.println(" 2. Display all patients in priority order");
        System.out.println(" 3. Update patient priority");
        System.out.println(" 4. Serve the highest-priority patient");
        System.out.println(" 5. Peek at the next patient");
        System.out.println(" 6. Display the heap array");
        System.out.println(" 7. Display the heap as a tree");
        System.out.println(" 8. Run a performance comparison");
        System.out.println(" 9. Show how the Max-Heap works (demo)");
        System.out.println(" 0. Exit");
        System.out.println("--------------------------------");
    }

    // ---------- input helpers (never throw on bad input) ----------
    private static String readLine(String prompt) {
        System.out.print(prompt);
        if (!in.hasNextLine()) return null;
        return in.nextLine();
    }

    /** Returns the score (1-100) or -1 if the text is not a whole number in range. */
    private static int parseScore(String text) {
        try {
            int v = Integer.parseInt(text.trim());
            return (v >= 1 && v <= 100) ? v : -1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /** Keeps asking until a valid score is entered. Returns -1 if input is closed. */
    private static int askScore(String prompt) {
        while (true) {
            String s = readLine(prompt);
            if (s == null) return -1;
            int v = parseScore(s);
            if (v != -1) return v;
            System.out.println("  Invalid score. Enter a whole number from 1 to 100.");
        }
    }

    // ---------- preloaded fictional data ----------
    private static void preloadPatients() {
        add("P001", "Aarav Kumar",   "Minor Cut",            15);
        add("P002", "Meera Nair",    "High Fever",           45);
        add("P003", "Rohan Das",     "Cardiac Arrest",       98);
        add("P004", "Sana Iyer",     "Major Fracture",       65);
        add("P005", "Vikram Rao",    "Breathing Difficulty", 85);
        add("P006", "Diya Menon",    "Minor Burn",           35);
        add("P007", "Karan Singh",   "Stroke",               90);
        add("P008", "Lakshmi Pillai","Breathing Difficulty", 85);   // same score as P005 -> tie-break
    }

    private static void add(String id, String name, String cond, int score) {
        queue.insert(new Patient(id, name, cond, score, nextArrival++));
    }

    // ---------- 1. register ----------
    private static void registerPatient() {
        String id;
        while (true) {
            id = readLine("Patient ID: ");
            if (id == null) return;
            id = id.trim();
            if (id.isEmpty()) { System.out.println("  ID cannot be empty."); continue; }
            if (queue.contains(id)) { System.out.println("  That ID already exists. Use a different ID."); continue; }
            break;
        }

        String name;
        while (true) {
            name = readLine("Patient name: ");
            if (name == null) return;
            name = name.trim();
            if (!name.isEmpty()) break;
            System.out.println("  Name cannot be empty.");
        }

        System.out.println("Emergency conditions:");
        for (int i = 0; i < CONDITIONS.length; i++) {
            System.out.printf("  %d. %-22s (suggested score %d)%n", i + 1, CONDITIONS[i], SUGGESTED[i]);
        }
        System.out.println("  " + (CONDITIONS.length + 1) + ". Other (enter condition and score manually)");

        String condition = null;
        int score = -1;
        while (condition == null) {
            String c = readLine("Choose condition number: ");
            if (c == null) return;
            int num;
            try {
                num = Integer.parseInt(c.trim());
            } catch (NumberFormatException e) {
                System.out.println("  Please enter a number from 1 to " + (CONDITIONS.length + 1) + ".");
                continue;
            }
            if (num >= 1 && num <= CONDITIONS.length) {
                condition = CONDITIONS[num - 1];
                int suggested = SUGGESTED[num - 1];
                while (score == -1) {
                    String s = readLine("Suggested score is " + suggested
                            + ". Press Enter to accept, or type a score (1-100): ");
                    if (s == null) return;
                    if (s.trim().isEmpty()) { score = suggested; }
                    else {
                        score = parseScore(s);
                        if (score == -1) System.out.println("  Invalid score. Enter a whole number from 1 to 100.");
                    }
                }
            } else if (num == CONDITIONS.length + 1) {
                String other;
                while (true) {
                    other = readLine("Condition description: ");
                    if (other == null) return;
                    other = other.trim();
                    if (!other.isEmpty()) break;
                    System.out.println("  Condition cannot be empty.");
                }
                condition = other;
                score = askScore("Priority score (1-100): ");
                if (score == -1) return;
            } else {
                System.out.println("  Please enter a number from 1 to " + (CONDITIONS.length + 1) + ".");
            }
        }

        Patient p = new Patient(id, name, condition, score, nextArrival++);
        queue.insert(p);
        System.out.println("\n>> Registered: " + p.getPatientName() + " (" + p.getPatientId() + ") - "
                + p.getCondition() + ", score " + p.getPriorityScore() + ", level " + p.getPriorityLevel()
                + ", arrival #" + p.getArrivalOrder());
        System.out.println(Patient.tableHeader());
        System.out.println(p.toRow());
    }

    // ---------- 2. display ----------
    private static void displayAll() {
        System.out.println("Patients in priority order (" + queue.size() + " waiting):");
        queue.displayAllInPriorityOrder();
    }

    // ---------- 3. update ----------
    private static void updatePriority() {
        if (queue.isEmpty()) {
            System.out.println("There are no patients in the queue to update.");
            return;
        }
        String id = readLine("Enter patient ID to update: ");
        if (id == null) return;
        Patient p = queue.find(id.trim());
        if (p == null) {
            System.out.println("No patient with ID '" + id.trim() + "' was found.");
            return;
        }
        System.out.println("Current record:");
        System.out.println(Patient.tableHeader());
        System.out.println(p.toRow());
        int oldScore = p.getPriorityScore();
        String oldLevel = p.getPriorityLevel();
        int newScore = askScore("New priority score (1-100): ");
        if (newScore == -1) return;
        queue.updatePriority(p.getPatientId(), newScore);
        System.out.println("\n>> Updated " + p.getPatientName() + " (" + p.getPatientId() + "): score "
                + oldScore + " -> " + newScore + ", level " + oldLevel + " -> " + p.getPriorityLevel()
                + (newScore > oldScore ? " (moved up the heap)" : newScore < oldScore ? " (moved down the heap)" : " (no change)"));
        System.out.println("Next patient to be served: " + queue.peek().getPatientName()
                + " (" + queue.peek().getPatientId() + ")");
    }

    // ---------- 4. serve ----------
    private static void servePatient() {
        Patient p = queue.extractMax();
        if (p == null) {
            System.out.println("The queue is empty. There is no patient to serve.");
            return;
        }
        System.out.println(">> Now serving the highest-priority patient:");
        System.out.println(Patient.tableHeader());
        System.out.println(p.toRow());
        System.out.println(queue.size() + " patient(s) still waiting.");
        Patient next = queue.peek();
        if (next != null) System.out.println("Next in line: " + next.getPatientName() + " (" + next.getPatientId() + ")");
    }

    // ---------- 5. peek ----------
    private static void peekPatient() {
        Patient p = queue.peek();
        if (p == null) {
            System.out.println("The queue is empty. No patient is waiting.");
            return;
        }
        System.out.println("Next patient to be served (not removed):");
        System.out.println(Patient.tableHeader());
        System.out.println(p.toRow());
    }

    // ---------- 9. step-by-step demo ----------
    private static void heapDemo() {
        System.out.println("=== How the Max-Heap works (demo with separate fictional data) ===");
        System.out.println("Rule: every parent has a priority >= its children, so the root is always the most urgent.");
        System.out.println("Array indexes: parent = (i - 1) / 2, left child = 2i + 1, right child = 2i + 2.\n");

        MaxHeap demo = new MaxHeap();
        demo.setTrace(true);
        String[] names = { "Demo-A", "Demo-B", "Demo-C", "Demo-D", "Demo-E" };
        String[] conds = { "Minor Cut", "High Fever", "Cardiac Arrest", "Major Fracture", "Stroke" };
        int[] scores   = { 15, 45, 98, 65, 90 };

        System.out.println("STEP 1 - Insert patients. Each goes at the END of the array, then heapify-up");
        System.out.println("         swaps it with its parent while it outranks that parent.\n");
        for (int i = 0; i < names.length; i++) {
            System.out.println("  Insert " + names[i] + " (" + conds[i] + ", score " + scores[i] + ")");
            demo.insert(new Patient("D" + (i + 1), names[i], conds[i], scores[i], i + 1));
            System.out.print("    ");
            demo.displayArray();
        }
        System.out.println("\nThe heap as a tree now:");
        demo.displayTree();

        System.out.println("\nSTEP 2 - Peek: the root (index 0) is the highest priority: "
                + demo.peek().getPatientName() + " (score " + demo.peek().getPriorityScore() + "). No removal, O(1).");

        System.out.println("\nSTEP 3 - Remove the highest priority. The last element moves to the root,");
        System.out.println("         then heapify-down swaps it with its stronger child until the property holds.\n");
        Patient removed = demo.extractMax();
        System.out.println("  Removed: " + removed.getPatientName() + " (score " + removed.getPriorityScore() + ")");
        System.out.print("    ");
        demo.displayArray();
        System.out.println("  New root: " + demo.peek().getPatientName() + " (score " + demo.peek().getPriorityScore() + ")");

        System.out.println("\nSTEP 4 - Update priority: raise D1 (Demo-A) from 15 to 95 -> heapify-up.\n");
        demo.updatePriority("D1", 95);
        System.out.print("    ");
        demo.displayArray();
        System.out.println("  New root: " + demo.peek().getPatientName() + " (score " + demo.peek().getPriorityScore() + ")");

        System.out.println("\nBoth insert and remove touch at most one path from root to leaf, so they take O(log n).");
        System.out.println("(This demo used its own heap; your real queue was not changed.)");
    }
}