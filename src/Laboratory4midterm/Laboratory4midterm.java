package Laboratory4midterm;

import java.util.*;

public class Laboratory4midterm {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n=== CPU SCHEDULING MENU ===");
            System.out.println("1. First Come First Serve (FCFS)");
            System.out.println("2. Shortest Job First (Non-Preemptive)");
            System.out.println("3. Shortest Job First (Preemptive)");
            System.out.println("4. Priority Scheduling (Non-Preemptive)");
            System.out.println("5. Priority Scheduling (Preemptive)");
            System.out.println("6. Exit");
            System.out.print("Choose: ");
            int ch = sc.nextInt();
            if (ch == 6) break;

            boolean needPriority = (ch == 4 || ch == 5);

            System.out.print("\nEnter number of processes: ");
            int n = sc.nextInt();
            ArrayList<Process> list = new ArrayList<>();

            for (int i = 0; i < n; i++) {
                System.out.println("\nProcess " + (i + 1));
                System.out.print("Process ID: ");
                String pid = sc.next();
                System.out.print("Arrival Time: ");
                int at = sc.nextInt();
                System.out.print("Burst Time: ");
                int bt = sc.nextInt();
                int pr = 0;
                if (needPriority) {
                    System.out.print("Priority (higher = higher priority): ");
                    pr = sc.nextInt();
                }
                list.add(new Process(pid, at, bt, pr));
            }

            switch (ch) {
                case 1 -> fcfs(copy(list));
                case 2 -> sjfNP(copy(list));
                case 3 -> sjfPre(copy(list));
                case 4 -> prioNP(copy(list));
                case 5 -> prioPre(copy(list));
                default -> System.out.println("Invalid choice!");
            }
        }
        sc.close();
    }

    static ArrayList<Process> copy(ArrayList<Process> src) {
        ArrayList<Process> out = new ArrayList<>();
        for (Process p : src)
            out.add(new Process(p.pid, p.arrival, p.burst, p.priority));
        return out;
    }

    // ---------- FCFS ----------
    static void fcfs(ArrayList<Process> p) {
        p.sort(Comparator.comparingInt(a -> a.arrival));
        int time = 0;
        System.out.println("\nGantt Chart:");
        for (Process x : p) {
            if (time < x.arrival) {
                System.out.print("[Idle " + time + "-" + x.arrival + "] ");
                time = x.arrival;
            }
            x.start = time;
            x.finish = time + x.burst;
            x.turnaround = x.finish - x.arrival;
            x.waiting = x.start - x.arrival;
            time = x.finish;
            System.out.print("[" + x.pid + " " + x.start + "-" + x.finish + "] ");
        }
        print(p);
    }

    // ---------- SJF NON-PREEMPTIVE ----------
    static void sjfNP(ArrayList<Process> p) {
        int time = 0, done = 0;
        boolean[] fin = new boolean[p.size()];
        System.out.println("\nGantt Chart:");
        while (done < p.size()) {
            int idx = -1, min = Integer.MAX_VALUE;
            for (int i = 0; i < p.size(); i++) {
                if (!fin[i] && p.get(i).arrival <= time && p.get(i).burst < min) {
                    min = p.get(i).burst;
                    idx = i;
                }
            }
            if (idx == -1) {
                System.out.print("[Idle " + time + "-" + (time + 1) + "] ");
                time++;
                continue;
            }
            Process cur = p.get(idx);
            cur.start = time;
            cur.finish = time + cur.burst;
            cur.turnaround = cur.finish - cur.arrival;
            cur.waiting = cur.start - cur.arrival;
            time = cur.finish;
            fin[idx] = true;
            done++;
            System.out.print("[" + cur.pid + " " + cur.start + "-" + cur.finish + "] ");
        }
        print(p);
    }

    // ---------- SJF PREEMPTIVE ----------
    static void sjfPre(ArrayList<Process> p) {
        int time = 0, done = 0;
        System.out.println("\nGantt Chart:");
        while (done < p.size()) {
            int idx = -1, minRem = Integer.MAX_VALUE;
            for (int i = 0; i < p.size(); i++) {
                Process pr = p.get(i);
                if (pr.arrival <= time && pr.remaining > 0 && pr.remaining < minRem) {
                    minRem = pr.remaining;
                    idx = i;
                }
            }
            if (idx == -1) {
                System.out.print("[Idle " + time + "-" + (time + 1) + "] ");
                time++;
                continue;
            }
            Process cur = p.get(idx);
            if (cur.start == -1) cur.start = time;
            System.out.print("[" + cur.pid + " " + time + "-" + (time + 1) + "] ");
            cur.remaining--;
            time++;
            if (cur.remaining == 0) {
                cur.finish = time;
                cur.turnaround = cur.finish - cur.arrival;
                cur.waiting = cur.turnaround - cur.burst;
                done++;
            }
        }
        print(p);
    }

    // ---------- PRIORITY NON-PREEMPTIVE ----------
    static void prioNP(ArrayList<Process> p) {
        int time = 0, done = 0;
        boolean[] fin = new boolean[p.size()];
        System.out.println("\nGantt Chart:");
        while (done < p.size()) {
            int idx = -1, highest = Integer.MIN_VALUE;
            for (int i = 0; i < p.size(); i++) {
                if (!fin[i] && p.get(i).arrival <= time && p.get(i).priority > highest) {
                    highest = p.get(i).priority;
                    idx = i;
                }
            }
            if (idx == -1) {
                System.out.print("[Idle " + time + "-" + (time + 1) + "] ");
                time++;
                continue;
            }
            Process cur = p.get(idx);
            cur.start = time;
            cur.finish = time + cur.burst;
            cur.turnaround = cur.finish - cur.arrival;
            cur.waiting = cur.start - cur.arrival;
            time = cur.finish;
            fin[idx] = true;
            done++;
            System.out.print("[" + cur.pid + " " + cur.start + "-" + cur.finish + "] ");
        }
        print(p);
    }

    // ---------- PRIORITY PREEMPTIVE ----------
    static void prioPre(ArrayList<Process> p) {
        int time = 0, done = 0;
        System.out.println("\nGantt Chart:");
        while (done < p.size()) {
            int idx = -1, highest = Integer.MIN_VALUE;
            for (int i = 0; i < p.size(); i++) {
                Process pr = p.get(i);
                if (pr.arrival <= time && pr.remaining > 0 && pr.priority > highest) {
                    highest = pr.priority;
                    idx = i;
                }
            }
            if (idx == -1) {
                System.out.print("[Idle " + time + "-" + (time + 1) + "] ");
                time++;
                continue;
            }
            Process cur = p.get(idx);
            if (cur.start == -1) cur.start = time;
            System.out.print("[" + cur.pid + " " + time + "-" + (time + 1) + "] ");
            cur.remaining--;
            time++;
            if (cur.remaining == 0) {
                cur.finish = time;
                cur.turnaround = cur.finish - cur.arrival;
                cur.waiting = cur.turnaround - cur.burst;
                done++;
            }
        }
        print(p);
    }

  // ---------- PRINT OUTPUT TABLE ----------
static void print(ArrayList<Process> p) {
    p.sort(Comparator.comparing(pr -> pr.pid));
    double totalWT = 0, totalTT = 0, totalBurst = 0;
    int highestCT = 0;

    // detect if any process has priority ≠ 0 → means it’s a priority algorithm
    boolean showPriority = false;
    for (Process x : p) {
        if (x.priority != 0) {
            showPriority = true;
            break;
        }
    }

    // display table header depending on algorithm type
    if (showPriority)
        System.out.println("\n\nPID\tAT\tBT\tPRI\tCT\tTT\tWT");
    else
        System.out.println("\n\nPID\tAT\tBT\tCT\tTT\tWT");

    // for building AWT and ATT formulas
    StringBuilder wtFormula = new StringBuilder("(");
    StringBuilder ttFormula = new StringBuilder("(");

    for (int i = 0; i < p.size(); i++) {
        Process x = p.get(i);
        if (x.finish == -1) {
            x.finish = x.arrival + x.burst;
            x.turnaround = x.finish - x.arrival;
            x.waiting = x.turnaround - x.burst;
        }

        if (showPriority)
            System.out.printf("%s\t%d\t%d\t%d\t%d\t%d\t%d%n",
                    x.pid, x.arrival, x.burst, x.priority,
                    x.finish, x.turnaround, x.waiting);
        else
            System.out.printf("%s\t%d\t%d\t%d\t%d\t%d%n",
                    x.pid, x.arrival, x.burst,
                    x.finish, x.turnaround, x.waiting);

        totalWT += x.waiting;
        totalTT += x.turnaround;
        totalBurst += x.burst;
        if (x.finish > highestCT) highestCT = x.finish;

        wtFormula.append(x.waiting);
        ttFormula.append(x.turnaround);
        if (i < p.size() - 1) {
            wtFormula.append("+");
            ttFormula.append("+");
        }
    }

    wtFormula.append(")/").append(p.size());
    ttFormula.append(")/").append(p.size());

    double cpuUtil = (totalBurst / highestCT) * 100;
    double awt = totalWT / p.size();
    double att = totalTT / p.size();

    System.out.println("\n----------------------------------");
    System.out.printf("- CPU Utilization: %.0f%%%n", cpuUtil);
    System.out.printf("- Average Waiting Time (AWT): %s = %.2f%n", wtFormula.toString(), awt);
    System.out.printf("-Average Turnaround Time (ATT): %s = %.2f%n", ttFormula.toString(), att);
    System.out.println("----------------------------------\n");
}}