/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bnparser;

/**
 *
 * @author tara
 */
public class Parser extends dashparser.DashParser {

    public Parser() {
        super();
    }

    @Override
    public void setColumns(org.apache.poi.ss.usermodel.Row r) {
        super.setColumns(r); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void openFile(String fileName) {
        super.openFile(fileName); //To change body of generated methods, choose Tools | Templates.
    }
}
