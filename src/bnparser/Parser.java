/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bnparser;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


/**
 *
 * @author tara
 */
public class Parser {

//    private String fn; //filename
    private static ArrayList<Job> jobList;

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yy h:m a");
    //this might break b/c all dates from Dash have times as well, but I want to disregard those if possible

    //these ints are the column #s where each value is located; doing this so that
    //I can make the parser method dynamic
    private int jobID;
    private int customerName;
    private int jobStatus;
    private int lossCat;
    private int lossType;
    private int lossTypeSec;
    private int referredBy;
    private int referralType;

    private int totalEstimates;
    private int totalInvoiced;
    private int totalJobCost;
    private int totalCollected;

    private int paidDate;
    private int invoicedDate;
    private int receivedDate;
    private int closedDate;
    private int workAuthDate;

    public Parser() {
        jobList = new ArrayList();
    }

    public void openFile(String fileName) {

        try {
            File inputFile = new File(fileName);
            InputStream inp = new FileInputStream(inputFile);
            XSSFWorkbook wb = new XSSFWorkbook(inp);

            Sheet sheet = wb.getSheetAt(0);

            //call setColumns to define all of the column variables
            setColumns(sheet.getRow(0));

            //starting at int i = 1 to skip the first row (which is just labels)
            for (int i = 1; i < sheet.getLastRowNum(); i++) {
                parseFile(sheet.getRow(i)); //this might need to be i+1
            }

            //finally
            inp.close();
            wb.close();

        } catch (Throwable ex) {
            Logger.getLogger(Parser.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * This method sets the column number for each field, so that the parseFile
     * method can get its values dynamically, rather than relying on the report
     * never being updated.
     *
     */
    private void setColumns(Row r) {
        //should this be <= instead of < ?
        for (int i = 0; i < r.getLastCellNum(); i++) {

            switch (r.getCell(i).getStringCellValue()) {
                case "Job Number":
                    jobID = i;
                    break;
                case "Customer":
                    customerName = i;
                    break;
                case "Loss Category":
                    lossCat = i;
                    break;
                case "Job Status":
                    jobStatus = i;
                    break;
                case "Type of Loss":
                    lossType = i;
                    break;
                case "Secondary Loss Type":
                    lossTypeSec = i;
                    break;
                case "Referred By":
                    referredBy = i;
                    break;
                case "Referral Type":
                    referralType = i;
                    break;
                case "Total Estimates":
                    totalEstimates = i;
                    break;
                case "Total Invoiced":
                    totalInvoiced = i;
                    break;
                case "Total Job Cost":
                    totalJobCost = i;
                    break;
                case "Collected Subtotal":
                    totalCollected = i;
                    break;
                case "Date Paid":
                    paidDate = i;
                    break;
                case "Date Invoiced":
                    invoicedDate = i;
                    break;
                case "Date Received":
                    receivedDate = i;
                    break;
                case "Date Closed":
                    closedDate = i;
                    break;
                case "Date of Work Authorization":
                    workAuthDate = i;
                    break;
                default:
                    System.out.println("Column name not found!");
            }
        }
    }

    private void parseFile(Row curRow) {

        if (curRow != null) {
//            if (curRow.getRowNum() == 164) {
//                System.out.println("Found Brandi");
//            }
            Job job = new Job(getCellVal(curRow, 0), getCellVal(curRow, 1));

            job.setLossCat(getCellVal(curRow, lossCat));

            job.setJobStatus(getCellVal(curRow, jobStatus));

            //if there is something in the Secondary Loss Type field, use that, otherwise just get the loss type
            if (getCellVal(curRow, lossTypeSec) != null) {
                job.setLossType(getCellVal(curRow, lossTypeSec));
            } else {
                job.setLossType(getCellVal(curRow, lossType));
            }

            job.setReferredBy(getCellVal(curRow, referredBy));
            job.setReferralType(getCellVal(curRow, referralType));

            if (curRow.getCell(totalEstimates).getCellTypeEnum() == CellType.NUMERIC) {
                job.setTotalEstimates(curRow.getCell(totalEstimates).getNumericCellValue());
            } else {
                if (curRow.getCell(totalEstimates).getStringCellValue().length() > 1) {
                    job.setTotalEstimates(Double.valueOf(curRow.getCell(totalEstimates).getStringCellValue()));
                }
            }

            if (curRow.getCell(totalInvoiced).getCellTypeEnum() == CellType.NUMERIC) {
                job.setTotalInvoiced(curRow.getCell(totalInvoiced).getNumericCellValue());
            } else {
                if (curRow.getCell(totalInvoiced).getStringCellValue().length() > 1) {
                    job.setTotalInvoiced(Double.valueOf(curRow.getCell(totalInvoiced).getStringCellValue()));
                }
            }

            if (curRow.getCell(totalJobCost).getCellTypeEnum() == CellType.NUMERIC) {
                job.setTotalJobCost(curRow.getCell(totalJobCost).getNumericCellValue());
            } else {
                if (curRow.getCell(totalJobCost).getStringCellValue().length() > 1) {
                    job.setTotalJobCost(Double.valueOf(curRow.getCell(totalJobCost).getStringCellValue()));
                }
            }

            if (curRow.getCell(totalCollected).getCellTypeEnum() == CellType.NUMERIC) {
                job.setTotalCollected(curRow.getCell(totalCollected).getNumericCellValue());
            } else {
                if (curRow.getCell(totalCollected).getStringCellValue().length() > 1) {
                    job.setTotalCollected(Double.valueOf(curRow.getCell(totalCollected).getStringCellValue()));
                }
            }

            if (handleDates(curRow.getCell(paidDate)) != null) {
                job.setPaidDate(handleDates(curRow.getCell(paidDate)));
            }
            job.setReceivedDate(handleDates(curRow.getCell(receivedDate)));
            job.setInvoicedDate(handleDates(curRow.getCell(invoicedDate)));
            job.setClosedDate(handleDates(curRow.getCell(closedDate)));
            job.setWorkAuthDate(handleDates(curRow.getCell(workAuthDate)));

            jobList.add(job);
        } else {
            System.out.println("A row passed to the parse method is null");
        }

    }

    private LocalDate handleDates(Cell cell) {
        DataFormatter d = new DataFormatter();

        String v = d.formatCellValue(cell);
        if (v.length() > 1) {
            return LocalDate.parse(v, formatter);
        } else {
            return null;
        }

    }

    private String getCellVal(Row row, int index) {
        if (row.getCell(index).getStringCellValue() != null && row.getCell(index).getStringCellValue().length() > 1) {
            return row.getCell(index).getStringCellValue();
        }
        return null;
    }

    public static ArrayList<Job> getJobList() {
        return jobList;
    }

    public static void setJobList(ArrayList<Job> jobList) {
        Parser.jobList = jobList;
    }

}
