/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bnparser;

import java.time.LocalDate;

/**
 *
 * @author tara
 */
public class Job {

    private String jobNumber;
    private String jobName; //not sure this is really necessary
    private String customerName;
    private String lossCat; //loss category: residential or commercial
    private String jobStatus;
    private String lossType; //water, fire, etc. If there is a secondary type, that goes here instead
    private String referredBy;  //would it make more sense to have a custom object for the referral source, and then have the
    //referral type be a variable within that object?
    private String referralType;

    private double totalEstimates;
    private double totalInvoiced; //not sure this is necessary
    private double totalJobCost;
    private double totalCollected; //not sure this is necessary

    //dates are ordered as Verbed-Date instead of Date-Verbed for convenience w/ auto-complete
    private LocalDate receivedDate;
    private LocalDate paidDate;
    private LocalDate closedDate;
    private LocalDate invoicedDate;
    private LocalDate workAuthDate;

    //commenting the below out unless I determine that I need them, right now it's up in the air
//    private LocalDate estimatedDate;
//    private LocalDate invoicedDate;
//    private LocalDate workAuthDate;
    public Job(String jobNumber, String customerName) {
        this.jobNumber = jobNumber;
        this.customerName = customerName;
        //these are the only things in the constructor b/c they're the only fields that
        //every job is 100% guaranteed to have
    }

    public String getJobNumber() {
        return jobNumber;
    }

    public void setJobNumber(String jobNumber) {
        this.jobNumber = jobNumber;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getLossCat() {
        return lossCat;
    }

    public void setLossCat(String lossCat) {
        this.lossCat = lossCat;
    }

    public String getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(String jobStatus) {
        this.jobStatus = jobStatus;
    }

    public String getLossType() {
        return lossType;
    }

    public void setLossType(String lossType) {
        this.lossType = lossType;
    }

    public String getReferredBy() {
        return referredBy;
    }

    public void setReferredBy(String referredBy) {
        this.referredBy = referredBy;
    }

    public String getReferralType() {
        return referralType;
    }

    public void setReferralType(String referralType) {
        this.referralType = referralType;
    }

    public double getTotalEstimates() {
        return totalEstimates;
    }

    public void setTotalEstimates(double totalEstimates) {
        this.totalEstimates = totalEstimates;
    }

    public double getTotalInvoiced() {
        return totalInvoiced;
    }

    public void setTotalInvoiced(double totalInvoiced) {
        this.totalInvoiced = totalInvoiced;
    }

    public double getTotalJobCost() {
        return totalJobCost;
    }

    public void setTotalJobCost(double totalJobCost) {
        this.totalJobCost = totalJobCost;
    }

    public double getTotalCollected() {
        return totalCollected;
    }

    public void setTotalCollected(double totalCollected) {
        this.totalCollected = totalCollected;
    }

    public LocalDate getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(LocalDate receivedDate) {
        this.receivedDate = receivedDate;
    }

    public LocalDate getPaidDate() {
        return paidDate;
    }

    public void setPaidDate(LocalDate paidDate) {
        this.paidDate = paidDate;
    }

    public LocalDate getClosedDate() {
        return closedDate;
    }

    public void setClosedDate(LocalDate closedDate) {
        this.closedDate = closedDate;
    }

    public LocalDate getInvoicedDate() {
        return invoicedDate;
    }

    public void setInvoicedDate(LocalDate invoicedDate) {
        this.invoicedDate = invoicedDate;
    }

    public LocalDate getWorkAuthDate() {
        return workAuthDate;
    }

    public void setWorkAuthDate(LocalDate workAuthDate) {
        this.workAuthDate = workAuthDate;
    }

    
    
}
