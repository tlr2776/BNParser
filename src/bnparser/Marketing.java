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

/**
 *
 * @author tara
 */
public class Marketing {

    private int jobCount;

    private double jobVol;

    private int typeCounter;

    private HashMap<Integer, String> typeKeys = new HashMap<>();

    private FileWriter marketing;

    private ArrayList<Job> resList;
    private ArrayList<Job> commList;

    /**
     *
     */
    public Marketing() {

        typeKeys.put(1, "Fire - Structural Repairs");
        typeKeys.put(2, "Fire - Structural Cleaning/Deodorization");
        typeKeys.put(3, "Fire - Kitchen grease");
        typeKeys.put(4, "Fire - smoke");
        typeKeys.put(5, "Water - Clean/Gray");
        typeKeys.put(6, "Water - Black");
        typeKeys.put(7, "Wind");
        typeKeys.put(8, "Hail");
        typeKeys.put(9, "Subsidence");
        typeKeys.put(10, "Falling objects");
        typeKeys.put(11, "Vehicle Impact");
        typeKeys.put(12, "Vandalism");
        typeKeys.put(13, "Mold");
        typeKeys.put(14, "Other");

        resList = new ArrayList<>();
        commList = new ArrayList<>();

        catSplitter();

        try {
            marketing = new FileWriter("marketing.csv");
            marketing.append("Residential jobs sold,# of Residential,$ Vol\n");
            compileList(resList);

            marketing.append("\n\nCommercial jobs sold,# of Commercial,$ Vol\n");
            compileList(commList);

            marketing.close();
        } catch (IOException ex) {
            Logger.getLogger(Marketing.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * This splits the list of jobs into two separate lists, one for residential
     * jobs and one for commercial. The full job list is local, because we have
     * no need to refer to it again after this. We are excluding jobs without
     * Work Auth dates from this because for the marketing category, we're only
     * interested in jobs under contract.
     *
     */
    private void catSplitter() {
        ArrayList<Job> jl = Parser.getJobList();

        for (Job job : jl) {
//            if (job.getLossType().equals("Vandalism")) {
//                System.out.println("hey we found one!");
//            }
            if (job.getWorkAuthDate() != null || job.getTotalInvoiced() > 0) {
                if (job.getLossCat() != null) {
                    if (job.getLossCat().equals("Residential")) {
                        resList.add(job);
                    } else if (job.getLossCat().equals("Commercial")) {
                        commList.add(job);
                    }
                }
            }
        }
    }

    private void compileList(ArrayList<Job> jl) {
        typeCounter = 1;

        while (typeCounter < 15) {

            jobCount = 0;
            jobVol = 0;

            for (Job job : jl) {
                if (job.getLossType().equals(typeKeys.get(typeCounter))) {
                    if (job.getWorkAuthDate() != null && (job.getTotalEstimates() > 0 || job.getTotalInvoiced() > 0)) {
                        jobCount++;
                        if (job.getTotalEstimates() > 0) {
                            jobVol += job.getTotalEstimates();
                        } else {
                            jobVol += job.getTotalInvoiced();
                        }
                    }
                }
            }
            dumpContents();
            typeCounter++;
        }
    }

    private void dumpContents() {
        try {
            marketing.append(typeKeys.get(typeCounter) + "," + jobCount + "," + jobVol + "\n");
        } catch (IOException ex) {
            Logger.getLogger(Marketing.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
