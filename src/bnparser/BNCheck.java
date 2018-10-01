/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bnparser;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import dashparser.Job;

/**
 * The class that iterates over the parsed data and looks for missing or invalid
 * entries. Examples:
 *
 * 1) Does this job have a referral source? Does that referral source have an
 * associated type?
 *
 * 2) If the damage type is fire or water, is there a sub-type?
 *
 * 3) Is there a category (residential or commercial) assigned?
 *
 * @author tara
 */
public class BNCheck {

    private ArrayList<Job> jl;
    private ArrayList<Job> ql; //queueList

    private FileWriter problemLog;

    public BNCheck() {
        jl = dashparser.DashParser.getJobList();
        ql = new ArrayList<>();

        for (Job job : jl) {
            standardizeReferrals(job);
            jobsToClose(job);
            checkCategory(job);
            checkLossTypes(job);
            checkForRemoval(job);
        }

        removeQueuedJobs();

        //finally
        if (problemLog != null) {
            try {
                problemLog.close();
            } catch (IOException ex) {
                Logger.getLogger(BNCheck.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void standardizeReferrals(Job job) {
        //this does not report an error, it just changes certain fields to use standard terms
        //TODO in the future I may make this throw an error if it finds a referral source that it can't standardize
        if (job.getReferredBy() == null) {
            reportProblems(job, "noRefSource");
            job.setReferralType("Other");
        } else {
            if (job.getReferralType() != null) {
                if (job.getReferralType().equals("Employee")
                        || job.getReferralType().equals("Friend, Sub, BNI of VRS")
                        || job.getReferralType().equals("Friend of Employee")) {
                    job.setReferralType("Referral");
                }
            } else {
                reportProblems(job, "noRefType");
                job.setReferralType("Other");
            }
        }
    }

    private void checkLossTypes(Job job) {
        if (job.getLossType() == null) {
            reportProblems(job, "lossType");
        } else if (job.getLossType().equals("Water") || job.getLossType().equals("Fire")) {
            reportProblems(job, "secLoss");
        }
    }

    private void jobsToClose(Job job) {
        //if a job is in A/R status but has been paid in full:
        if (job.getJobStatus().equals("Accounts Receivable")) {
            if (job.getTotalInvoiced() == job.getTotalCollected()) {
                reportProblems(job, "AR");
            }
        }
    }

    private void checkCategory(Job job) {
        //if a job does not have a category (commercial v residential) set:
        if (job.getLossCat() == null) {
            reportProblems(job, "lossCat");
        }

    }

    /**
     * Looks at the job to ensure it should be included in BN#. It adds invalid
     * jobs to a separate array but remove them from the main list, as that
     * needs to be done outside of the main for-each loop.
     *
     * @param job The job passed from the for-each loop.
     */
    private void checkForRemoval(Job job) {
        //this should check jobs for things pointing to it not being "valid" for this BN list.
        //Eg, the word "Test" in the job name, or a long gap between payment and closing (to catch jobs
        //that were paid in full in '17 but not actually closed until '18). Due to the fact that this
        //is in a for each loop, the job can't be removed until the loop is done, so this method
        //will just create an alternate list of jobs, after which a method will be called to remove all jobs
        //in the second array from the first array.

        //the returns here are to prevent a job from being added to the ql Array multiple times
        if (job.getCustomerName() != null) {
            //TODO actually add the job name field to the Dash report dumb dumb. then include
            //the job name variable in the parser. Don't replace the customer name check here, just
            //also add a job name check
            if (job.getCustomerName().matches("(Test|test)")) {
                ql.add(job);
                return;
            }
        }

        LocalDate paid = job.getPaidDate();
        LocalDate closed = job.getClosedDate();

        if (paid != null && closed != null) {
            //if the job was paid in full the previous year, but closed this year, and there is a gap of longer than 20 days between these dates,
            //assume the delay was an oversight and remove the job.
            if (paid.getYear() > LocalDate.now().getYear()) {
                if (closed.getYear() == LocalDate.now().getYear()) {
                    if (ChronoUnit.DAYS.between(paid, closed) > 20) {
                        ql.add(job);
                    }
                }
            }
            return;
        }
    }

    /**
     * Compares the main job list to a secondary list of invalid jobs, and
     * removes matches from the main list.
     */
    private void removeQueuedJobs() {
        for (Job job : ql) {
            if (jl.contains(job)) {
                jl.remove(job);
            }
        }
    }

    /**
     * This method sends error messages to a .txt file, based on the string
     * passed as an argument.
     *
     * @param job The job in which the error was found.
     * @param type The String telling the method what the error was.
     */
    private void reportProblems(Job job, String type) {
        String jobID = job.getJobNumber();

        try {
            if (problemLog == null) {
                problemLog = new FileWriter("problemLog.txt");
            }

            switch (type) {
                case "AR":
                    problemLog.append(jobID + " can be closed.\n");
                    break;
                case "noRefSource":
                    problemLog.append(jobID + " has no referral source.\n");
                    break;
                case "noRefType":
                    problemLog.append(jobID + " has a referral source with no referral type assigned.\n");
                    break;
                case "lossCat":
                    problemLog.append(jobID + " does not have a category set.\n");
                    break;
                case "secLoss":
                    problemLog.append(jobID + " is missing a secondary loss type.\n");
                    break;
                case "lossType":
                    problemLog.append(jobID + " does not have a loss type set.\n");
                    break;
                default:
                    throw new AssertionError();
            }
        } catch (IOException ex) {
            Logger.getLogger(BNCheck.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
