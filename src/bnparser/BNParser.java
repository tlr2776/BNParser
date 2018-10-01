/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bnparser;

/**
 * Design goals right now: I want this to be a simple command-line program that
 * iterates over the .csv file (the export of the BN report from Dash) and
 * vomits its results into (one or more) new .csv file(s) that reflect the Bids
 * Leads & Sales data and the Marketing Data sheet. I don't see much point in
 * making the code update an existing .csv--seems simpler (w/o sacrificing
 * functionality) to just overwrite the old file, since the old jobs should
 * still be there. The only problem with this right now is how to get the jobs
 * that were received in '17 but only ended in '18 here--I think this will be
 * something that needs to be handled in the actual export phase: change the
 * filters have another block linked by an OR statement that checks for jobs
 * that ended in '18.
 *
 * Possibly also an error log of some sort, which spits out jobs that are
 * missing certain fields?
 *
 * Also: this is not directly BN# related, but it's easier to do it here than to
 * make a separate piece of software that will basically look at the exact same
 * data and have a lot of the same code. Cycle time for jobs could be created in
 * a separate .csv sheet.
 *
 *
 *
 * @author tara
 */
public class BNParser {

//    private BNCheck bnc;
    private static String fn; //file name

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        Parser p = new Parser();

        //if there are no arguments, then by default run all operations
        if (args.length == 0) {
            System.out.println("Error: No file found. Please try again.");
        } else {
            for (String arg : args) {
                if (arg.endsWith("xlsx")) {
                    fn = arg;
                    //two for-each loops are needed here so that we can first identify the file
                }
            }
            //check to make sure a file has been found--if not, end the process
            if (fn == null) {
                System.out.println("I can't find a valid file to read, please try again.");
                return;
            } else {
                p.openFile(fn);
            }
            if (args.length == 1) {
                //this won't actually work--there needs to be at least one argument (the filename)
                BNCheck bnc = new BNCheck();
                BidsLeadsSales bls = new BidsLeadsSales();
                Marketing mk = new Marketing();
                CycleTime ct = new CycleTime();
            } else {
                //this else condition should only fire if there are 2+ arguments (the filename and at least one argument)
                for (String arg : args) {
                    if (arg.equals("b")) {
                        //I'm not doing this as "-b" b/c I want to make it as simple as possible
                        //for the people here who aren't tech-literate
                        BNCheck bnc = new BNCheck();
                        BidsLeadsSales bls = new BidsLeadsSales();
                    }
                    if (arg.equals("m")) {
                        Marketing mk = new Marketing();
                    }
                    if (arg.equals("c")) {
                        CycleTime ct = new CycleTime();
                    }
                }
            }
        }
    }
}
