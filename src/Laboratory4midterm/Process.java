
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Laboratory4midterm;


public class Process {
    String pid;
    int arrival, burst, priority;
    int start = -1, finish = -1;
    int waiting = 0, turnaround = 0;
    int remaining;
  

    public Process(String pid, int arrival, int burst, int priority) {
        this.pid = pid;
        this.arrival = arrival;
        this.burst = burst;
        this.priority = priority;
        this.remaining = burst;
    }
}
