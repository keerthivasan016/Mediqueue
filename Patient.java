public class Patient {
    private final String patientId;
    private final String patientName;
    private final String condition;
    private int priorityScore;
    private final int arrivalOrder;

    public Patient(String patientId, String patientName, String condition,
                   int priorityScore, int arrivalOrder) {
        this.patientId = patientId;
        this.patientName = patientName;
        this.condition = condition;
        setPriorityScore(priorityScore);
        this.arrivalOrder = arrivalOrder;
    }

    public String getPatientId() { return patientId; }
    public String getPatientName() { return patientName; }
    public String getCondition() { return condition; }
    public int getPriorityScore() { return priorityScore; }
    public int getArrivalOrder() { return arrivalOrder; }

    public void setPriorityScore(int priorityScore) {
        if (priorityScore < 1 || priorityScore > 100) {
            throw new IllegalArgumentException("Priority score must be from 1 to 100.");
        }
        this.priorityScore = priorityScore;
    }

    public String getPriorityLevel() {
        if (priorityScore >= 80) return "CRITICAL";
        if (priorityScore >= 60) return "SERIOUS";
        if (priorityScore >= 30) return "MODERATE";
        return "MINOR";
    }

    public String toTableRow() {
        return String.format("%-8s | %-14s | %-22s | %5d | %-8s | %7d",
                patientId, patientName, condition, priorityScore,
                getPriorityLevel(), arrivalOrder);
    }

    @Override
    public String toString() {
        return toTableRow();
    }
}


// Package-private display helper kept in Patient.java so the project uses four source files.
final class PatientTable {
    private PatientTable() { }

    public static String header() {
        return "ID       | Name           | Condition              | Score | Level    | Arrival"
             + "\n---------+----------------+------------------------+-------+----------+--------";
    }

    public static void printPatients(java.util.List<Patient> patients) {
        if (patients.isEmpty()) {
            System.out.println("(No patients in the queue)");
            return;
        }
        System.out.println(header());
        for (Patient p : patients) System.out.println(p.toTableRow());
    }
}
