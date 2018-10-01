/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bnparser;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.time.temporal.ChronoUnit;

/**
 *
 *
 *
 * @author tara
 */
public class CycleTime {

    private FileWriter c;
    private ArrayList<Job> jl;
    private HashMap<Integer, String> typeKeys = new HashMap<>();

    private int typeCounter;

    //Not sure how to handle this. I'm hesitant to iterate over the full list every single time,
    //but it also feels cumbersome to make 10+ separate arrayLists for each damage type.
    //one way to handle this: since this class will have its own copy of the master jobList,
    //I can run a method to clean up this list, so remove non-accepted or WIP jobs (since right now
    //I'm only interested in jobs that have at least reached the A/R stage).
    public CycleTime() {

        typeKeys.put(1, "Fire - Structural Repairs");
        typeKeys.put(2, "Fire - Structural Cleaning/Deodorization");
        typeKeys.put(3, "Fire - Kitchen Grease");
        typeKeys.put(4, "Fire - smoke");
        typeKeys.put(5, "Water - Clean/Gray");
        typeKeys.put(6, "Water - Black");
        typeKeys.put(7, "Wind");
        typeKeys.put(8, "Hail");
        typeKeys.put(9, "Subsidence");
        typeKeys.put(10, "Falling Objects");
        typeKeys.put(11, "Vehicle Impact");
        typeKeys.put(12, "Vandalism");
        typeKeys.put(13, "Mold");
        typeKeys.put(14, "Other");

        try {
            c = new FileWriter("CycleTime.csv");
            c.append("Loss type,Invoiced count,Average days to invoiced,Paid count,Average days to paid\n");
        } catch (IOException ex) {
            Logger.getLogger(CycleTime.class.getName()).log(Level.SEVERE, null, ex);
        }

        jl = Parser.getJobList();

        pruneList();
        //now that we have a smaller list to work with, we can iterate over the main list.
        parseList();

        try {
            c.close();
        } catch (IOException ex) {
            Logger.getLogger(CycleTime.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void pruneList() {
        ArrayList<Job> ql = new ArrayList();

        for (Job job : jl) {
            //considering what to include in this check. Definitely non-accepted, but some older jobs might not have invoiced dates set. Then
            //again, I can't use those for this anyway, so what's the harm in removing them?
            if (job.getJobStatus().equals("Non-accepted") || job.getInvoicedDate() == null || job.getReceivedDate() == null) {
                ql.add(job);
            }
        }
        removePrunedJobs(ql);
    }

    private void removePrunedJobs(ArrayList<Job> ql) {
        for (Job job : ql) {
            //a job should never end up in ql unless it's in jl as well, but just in case:
            if (jl.contains(job)) {
                jl.remove(job);
            }
        }
    }

    private void parseList() {

        typeCounter = 1;

        int invCount;
        int invDays;
        int paidCount;
        int paidDays;

        while (typeCounter < 15) {
            invCount = 0;
            invDays = 0;
            paidCount = 0;
            paidDays = 0;

            for (Job job : jl) {
                if (job.getLossType().equals(typeKeys.get(typeCounter))) {
                    if (job.getInvoicedDate() != null) {
                        invCount++;
                        invDays += ChronoUnit.DAYS.between(job.getReceivedDate(), job.getInvoicedDate());
                        if (job.getPaidDate() != null) {
                            paidCount++;
                            paidDays += ChronoUnit.DAYS.between(job.getInvoicedDate(), job.getPaidDate());
                        }
                    }
                }
            }
            //do the math here to avoid divide-by-zero errors
            if (invCount > 0 && invDays > 0) {
                invDays = invDays / invCount;
                if (paidCount > 0 && paidDays > 0) {
                    paidDays = paidDays / paidCount;
                }
            }
            //this is outside the if statement so that even if there are no jobs of a specific type, we still get a line saying that
            dumpContents(invCount, invDays, paidCount, paidDays);

            typeCounter++;
        }
    }

    private void dumpContents(int invCount, int invDays, int paidCount, int paidDays) {
        try {
            c.append(typeKeys.get(typeCounter) + "," + invCount + "," + invDays + "," + paidCount + "," + paidDays + "\n");
        } catch (IOException ex) {
            Logger.getLogger(CycleTime.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
