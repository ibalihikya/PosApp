package print;

import model.DocType;
import model.Invoice;
import model.Item;
import model.Receipt;

import java.awt.*;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;

//TODO: this class should eventually replace Printer

public class Printer2 implements Printable {
    private Invoice invoice;
    private Header header;
    private Receipt receipt;
    DocType docType;

    public Printer2(Invoice invoice, Receipt receipt,Header header, DocType docType) {
        this.invoice = invoice;
        this.header = header;
        this.receipt = receipt;
        this.docType = docType;
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
        g.drawString(String.format("%34s\n",header.getBusinessName()), 0, y);
        y += lineHeight;

        g2d.setFont(fontplain);
        g.drawString(String.format("%s %7s %2s\n",header.getLocation(),"Tel:",header.getTelephoneNumber1() + " | " +
                                                                        header.getTelephoneNumber2()), 0, y);
        y += lineHeight;

//        g.drawString(String.format("%s %2s %5s %2s\n","TIN:",header.getTin(),"Date:",invoice.getDate_created()), 0, y);
//        y += lineHeight;

        switch (docType) {
            case RECEIPT:
                //TODO: will print wrong time when reprinting old receipt; use the time from db
                Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());
                String time  = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(timestamp);

                g.drawString(String.format("%s %2s %5s %2s\n","TIN:",header.getTin(),"Date:",time), 0, y);
                y += lineHeight;

                g.drawString(String.format("%s %2s \n", "Receipt No:", receipt.getReceiptId()), 0, y);
                if(receipt.getFirstname()!=null && receipt.getLastname() != null){
                    y += lineHeight;
                    g.drawString(String.format("%s %s %s\n", receipt.getFirstname(), " ", receipt.getLastname()), 0, y);
                }
                break;
            case INVOICE:
                //TODO: Ensure this print the right time
                g.drawString(String.format("%s %2s %5s %2s\n","TIN:",header.getTin(),"Date:",invoice.getDate_created()), 0, y);
                y += lineHeight;

                g.drawString(String.format("%s %5s %s %s %s \n", "Invoice No:", invoice.getId()), 0, y);

                y += lineHeight;

                g.drawString(String.format("%s %s %s\n", "Invoice No:",invoice.getFirstName(), " ", invoice.getLastName()), 0, y);
                break;
        }
        y += lineHeight;

        String heading = String.format("%-15s %16s %6s %6s %9s\n", "Item", "Qty", "Unit", "Price", "Total");
        String underline =          "-------------------------------------------------------------------";
        String underlineSubtotal =  "____________________________________________";

        g.drawString(underlineSubtotal, 0, y);
        y += lineHeight;
        g2d.setFont(fontbold);
        g.drawString(heading, 0, y);

        g2d.setFont(fontplain);
        y += lineHeight;
        g.drawString(underline, 0, y);
        int MAX_CHAR = 15;
        for(Item item : invoice.getItems()){
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
            y += lineHeight;
            g2d.setFont(fontplain);

            inputString = String.format("%-17s",inputString);
            g.drawString(inputString,0,y);
            //y += lineHeight;
            g2d.setFont(fontplain);
            g.drawString(String.format("%,-9.2f",item.getQuantity()), 86, y);
            g.drawString(String.format("%-11s",item.getUnits()), 113, y);
            g.drawString(String.format("%,-10.0f",item.getPrice()), 135, y);
            g.drawString(String.format("%,-10.0f",item.getTotalPrice()), 166, y);

        }
        y += lineHeight;
        g.drawString(underlineSubtotal, 0, y);
        y += lineHeight;
        //g2d.setFont(fontbold);
        g.drawString(String.format("%-64s %,-11.0f", "TOTAL", invoice.getAmount()),
                0, y);

        y += lineHeight;

        g2d.setFont(fontplain);
        g.drawString(underline, 0, y);
        y += lineHeight;

        if(receipt.getPartialPayment()!=0) { //This is the old balance
            g.drawString(String.format("%-68s %,-11.0f", "Paid", receipt.getPartialPayment()), 0, y);
            y += lineHeight;
        }
        g.drawString(String.format("%-68s %,-11.0f", "Cash", receipt.getCashReceived()), 0, y);
        y += lineHeight;
        g.drawString(String.format("%-65s  %,-11.0f", "Change", receipt.getChange()), 0, y);
        y += lineHeight;
        g.drawString(underline, 0, y);

        if(receipt.getBalance()>0.0){
            y += lineHeight;
            g.drawString(String.format("%-65s  %,-11.0f", "Balance", receipt.getBalance()), 0, y);
        }

        y += lineHeight*1;

        g2d.setFont(fontplain);
        g.drawString(String.format("%s \n", "Thank you. Please come again.") ,0, y);
        y += lineHeight;
        g.drawString(String.format("%s %2s \n", "You were served by:",invoice.getUsername()),0, y);



        /* tell the caller that this page is part of the printed document */
        return PAGE_EXISTS;
    }
}
