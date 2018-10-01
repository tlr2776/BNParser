/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bnparser;

import dashparser.Job;
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
public class BidsLeadsSales {

    private FileWriter bls;

    private ArrayList<Job> leads;
    private ArrayList<Job> bids;
    private ArrayList<Job> sales;

    private ArrayList<Job> jl;

//    //these are the counts of how many bids, leads, and sales we have.
    private int leadsCount;
    private int bidsCount;
    private int salesCount;
//    
//    //this is the sum value of our bids and sales
    private double bidsAmt;
    private double salesAmt;

    private int referralCounter;

    private HashMap<Integer, String> referralKeys = new HashMap<>();

    public BidsLeadsSales() {
//        leads = new ArrayList();
        bids = new ArrayList();
        sales = new ArrayList();

        leads = Parser.getJobList();
        referralCounter = 1;

        try {
            bls = new FileWriter("BidsLeadsSales.csv");
            bls.write("Bids Leads and Sales,# Of Leads,# Of Bids,# Of Sales, $ Volume of Bids, $ Volume of Sales\n");
        } catch (IOException ex) {
            Logger.getLogger(BidsLeadsSales.class.getName()).log(Level.SEVERE, null, ex);
        }

        //this populates the values that are being checked for
        referralKeys.put(1, "Previous Customer");
        referralKeys.put(2, "Referral");
        referralKeys.put(3, "Signs");
        referralKeys.put(4, "Internet");
        referralKeys.put(5, "Builder");
        referralKeys.put(6, "Independent Adjuster");
        referralKeys.put(7, "Company Adjuster");
        referralKeys.put(8, "Public Adjuster");
        referralKeys.put(9, "Independent Agent");
        referralKeys.put(10, "Company Agent");
        referralKeys.put(11, "Preferred Program");
        referralKeys.put(12, "Other");
        referralKeys.put(13, "Fire Department");
        referralKeys.put(14, "Plumber");
        referralKeys.put(15, "Property Manager");

        fillArrays();
        iterateArrays();

        try {
            bls.close();
        } catch (IOException ex) {
            Logger.getLogger(BidsLeadsSales.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void fillArrays() {
        for (Job job : leads) {
            //we are checking both estimates and invoices to account for jobs (mostly board-ups) that
            //don't have any estimates set
            if (job.getTotalEstimates() > 0 || job.getTotalInvoiced() > 0) {
                //should never be negative, so just doing > 0
                bids.add(job);
                if (job.getWorkAuthDate() != null && (job.getTotalEstimates() > 0 || job.getTotalInvoiced() > 0)) {
                    //I'm not sure how LocalDate works, this might be a 0 but I think null is correct
                    sales.add(job);
                }
            }
        }
    }

    private void iterateArrays() {
        referralCounter = 1;

        while (referralCounter < 16) {
            leadsCount = 0;
            bidsCount = 0;
            salesCount = 0;

            bidsAmt = 0;
            salesAmt = 0;

            for (Job job : leads) {
                if (job.getReferralType() != null) {
                    //if the referral type maps to the referral type that we're on right now
                    if (job.getReferralType().equals(referralKeys.get(referralCounter))) {
                        leadsCount++;

                        //we don't need to check the referral type for the same job under bids and sales,
                        //we only need to check if this job is present in those lists
                        if (bids.contains(job)) {
                            bidsCount++;
                            //to deal with jobs that are invoiced but don't have estimates attached,
                            //we substitute the invoice value
                            if (job.getTotalEstimates() == 0) {
                                if (job.getTotalInvoiced() > 0) {
                                    bidsAmt += job.getTotalInvoiced();
                                }
                            } else {
                                bidsAmt += job.getTotalEstimates();
                            }
                            if (sales.contains(job)) {
                                salesCount++;
                                //to deal with jobs that are invoiced but don't have estimates attached,
                                //we substitute the invoice value
                                if (job.getTotalEstimates() == 0) {
                                    if (job.getTotalInvoiced() > 0) {
                                        salesAmt += job.getTotalInvoiced();
                                    }
                                } else {
                                    salesAmt += job.getTotalEstimates();
                                }
                            }
                        }
                    }
                }
            }
            dumpContents();
            referralCounter++;
        }
    }

    private void dumpContents() {
        try {
            bls.append(referralKeys.get(referralCounter) + "," + leadsCount + "," + bidsCount + "," + salesCount + "," + bidsAmt + "," + salesAmt + "\n");
        } catch (IOException ex) {
            Logger.getLogger(BidsLeadsSales.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
