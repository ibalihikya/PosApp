package print;

import model.CustomerTransaction;
import model.Item;

import java.awt.*;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.util.ArrayList;

public class StatementPrinter implements Printable {
    ArrayList<CustomerTransaction> customerTransactions;
    private ReceiptHeader receiptHeader;
    public StatementPrinter(ArrayList<CustomerTransaction> customerTransactions, ReceiptHeader receiptHeader) {
        this.customerTransactions = customerTransactions;
        this.receiptHeader = receiptHeader;
    }

    @Override
    public int print(Graphics g, PageFormat pf, int page) throws PrinterException {
        Font fontbold = new Font("Serif", Font.BOLD, 10);
        Font fontplain = new Font("Serif", Font.PLAIN, 9);
        FontMetrics metrics = g.getFontMetrics(fontbold);
        int lineHeight = metrics.getHeight();


        if (page > 0) { /* We have only one page, and 'page' is zero-based */
            return NO_SUCH_PAGE;
        }

        /* User (0,0) is typically outside the imageable area, so we must
         * translate by the X and Y values in the PageFormat to avoid clipping
         */
        Graphics2D g2d = (Graphics2D)g;
        g2d.translate(pf.getImageableX(), pf.getImageableY());

        //render the text
        int y =0;
        y += lineHeight;
        g2d.setFont(fontbold);

        g.drawString(String.format("%34s\n", receiptHeader.getBusinessName()), 0, y);
        y += lineHeight;

        g2d.setFont(fontplain);
        g.drawString(String.format("%s %7s %2s\n", receiptHeader.getLocation(), "Tel:", receiptHeader.getTelephoneNumber1()
                + " | " + receiptHeader.getTelephoneNumber2()), 0, y);
        y += lineHeight;
        g.drawString(String.format("%s %2s %5s %2s\n", "TIN:", receiptHeader.getTin(), "Date:", receiptHeader.getTime()), 0, y);
        y += lineHeight;

        g2d.setFont(fontbold);

        CustomerTransaction tr1 = customerTransactions.get(0); //get the name from any transaction - in this case the first transaction in array
        g.drawString(String.format("%-20s", tr1.getFirstName() + " " + tr1.getLastName()), 0, y);

        g2d.setFont(fontplain);
        y += lineHeight;
        String heading = String.format("%-26s %-6s %-10s %7s \n", "DATE", "TYPE", "AMOUNT", "BALANCE");
        g.drawString(heading, 0, y);

        String underline = "-------------------------------------------------------------------";
        String underlineSubtotal = "____________________________________________";

        g.drawString(underlineSubtotal, 0, y);

        g2d.setFont(fontplain);
        y += lineHeight;
        int MAX_CHAR = 15;
        for(CustomerTransaction customerTransaction : customerTransactions){
            String date = String.format("%-17s", customerTransaction.getDate_created());

            if(customerTransaction.getTransaction_type().toString().equals("Inv") && customerTransaction.getItems().size()>0) {
                g2d.setFont(fontbold);
                g.drawString(date ,0, y);
                g.drawString(String.format("%-7s",customerTransaction.getTransaction_type()), 84, y);
                g.drawString(String.format("%,-11.0f",customerTransaction.getAmount()), 106, y);
                g.drawString(String.format("%,-10.0f",customerTransaction.getBalance()), 160, y);

                y += lineHeight;
                g2d.setFont(fontplain);

                g.drawString("Inv: "+Integer.toString(customerTransaction.getReceiptId()),0,y);

                y += lineHeight;

                for (Item item : customerTransaction.getItems()) {
                    String inputString = item.getProductName();
                    int maxLength = (inputString.length() < MAX_CHAR)?inputString.length():MAX_CHAR;
                    Boolean clipped = false;
                    if(inputString.length()>MAX_CHAR){
                        clipped = true;
                    }
                    inputString = inputString.substring(0, maxLength);
                    if(clipped){
                        inputString = inputString + "'";
                    }


                    g.drawString(inputString, 0, y);
                    g.drawString(String.format("%,-9.2f",item.getQuantity()), 86, y);
                    g.drawString(String.format("%-11s",item.getUnits()), 113, y);
                    g.drawString(String.format("%,-10.0f",item.getPrice()), 135, y);
                    g.drawString(String.format("%,10.0f",item.getTotalPrice()), 166, y);
                    y += lineHeight;
                }

                y += lineHeight;
            }
            else if(customerTransaction.getTransaction_type().toString().equals("Rec")){
                g2d.setFont(fontbold);
                g.drawString(date ,0, y);
                g.drawString(String.format("%-7s",customerTransaction.getTransaction_type()), 84, y);
                g.drawString(String.format("%,-11.0f",customerTransaction.getAmount()), 106, y);
                g.drawString(String.format("%,-10.0f",customerTransaction.getBalance()), 160, y);
                int receiptId = customerTransaction.getRecId();
                y += lineHeight;
                g2d.setFont(fontplain);
                g.drawString("Rec: "+Integer.toString(receiptId),0,y);
                y += lineHeight*2;

            }
        }

        /* tell the caller that this page is part of the printed document */
        return PAGE_EXISTS;
    }

    //wide format printer
//    @Override
//    public int print(Graphics g, PageFormat pf, int page) throws PrinterException {
//        Font fontbold = new Font("Serif", Font.BOLD, 10);
//        Font fontplain = new Font("Serif", Font.PLAIN, 7);
//        FontMetrics metrics = g.getFontMetrics(fontbold);
//        int lineHeight = metrics.getHeight();
//
//
//        if (page > 0) { /* We have only one page, and 'page' is zero-based */
//            return NO_SUCH_PAGE;
//        }
//
//        /* User (0,0) is typically outside the imageable area, so we must
//         * translate by the X and Y values in the PageFormat to avoid clipping
//         */
//        Graphics2D g2d = (Graphics2D)g;
//        g2d.translate(pf.getImageableX(), pf.getImageableY());
//
//        //render the text
//        int y =0;
//        y += lineHeight;
//        g2d.setFont(fontbold);
//        CustomerTransaction tr1 = customerTransactions.get(0); //get the name from any transaction - in this case the first transaction in array
//        g.drawString(String.format("%-20s", tr1.getFirstName() + " " + tr1.getLastName()), 0, y);
//
//        g2d.setFont(fontplain);
//        y += lineHeight;
//        String heading = String.format("%-32s %-117s %20s %15s %15s\n", "DATE", "DESCRIPTION", "INVOICE", "RECEIPT", "BALANCE");
//        g.drawString(heading, 0, y);
//
//        g2d.setFont(fontplain);
//        y += lineHeight;
//        int MAX_CHAR = 20;
//        for(CustomerTransaction customerTransaction : customerTransactions){
//            String date = String.format("%-40s", customerTransaction.getDate_created());
//
//            if(customerTransaction.getTransaction_type().toString().equals("Inv") && customerTransaction.getItems().size()>0) {
//                g.drawString(date ,0, y);
//                for (Item item : customerTransaction.getItems()) {
//                    String inputString = item.getProductName();
//                    int maxLength = (inputString.length() < MAX_CHAR)?inputString.length():MAX_CHAR;
//                    Boolean clipped = false;
//                    if(inputString.length()>MAX_CHAR){
//                        clipped = true;
//                    }
//                    inputString = inputString.substring(0, maxLength);
//                    if(clipped){
//                        inputString = inputString + "'";
//                    }
//
//                    g.drawString(String.format("%-30s",inputString),70,y);
//                    g.drawString(String.format("%10.2f",item.getQuantity()),170,y);
//                    g.drawString(String.format("%10s",item.getUnits()),200,y);
//                    g.drawString(String.format("%10.2f",item.getPrice()),240,y);
//                    g.drawString(String.format("%10.2f",item.getTotalPrice()),290,y);
//                    y += lineHeight;
//                }
//
//                String amountsString = String.format("%20.2f %20s %20.2f", customerTransaction.getAmount(),
//                         "", customerTransaction.getBalance());
//                g.drawString(amountsString, 320, y - lineHeight);
//            }
//            else if(customerTransaction.getTransaction_type().toString().equals("Rec")){
//                g.drawString(date ,0, y);
//
//                String paymentString = String.format("%20s %20.2f %20.2f"," ", customerTransaction.getAmount(),
//                        customerTransaction.getBalance());
//                g.drawString(String.format("%-24s","PAYMENT"), 70, y);
//                g.drawString(paymentString,320,y);
//                y += lineHeight;
//            }
//        }
//
//        /* tell the caller that this page is part of the printed document */
//        return PAGE_EXISTS;
//    }
}
