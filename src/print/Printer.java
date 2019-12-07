package print;

import model.DocType;
import model.Item;
import model.Transaction;

import java.awt.*;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;

public class Printer implements Printable {
    private Transaction transaction;
    private ReceiptHeader receiptHeader;
    DocType docType;

    public Printer(Transaction transaction, ReceiptHeader receiptHeader, DocType docType) {
        this.transaction = transaction;
        this.receiptHeader = receiptHeader;
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
        Graphics2D g2d = (Graphics2D) g;
        g2d.translate(pf.getImageableX(), pf.getImageableY());

        //render the text
        int y = 0;
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
        switch (docType) {
            case RECEIPT:
                g.drawString(String.format("%s %2s \n", "Receipt No:", receiptHeader.getReceiptNumber()), 0, y);
                if(receiptHeader.getFirstname() !=null && receiptHeader.getLastname() !=null){
                    y += lineHeight;
                    g.drawString(String.format("%s %s %s\n", receiptHeader.getFirstname(), " ", receiptHeader.getLastname()), 0, y);
                }
                break;
            case INVOICE:
                g.drawString(String.format("%s %2s \n", "Invoice No:", receiptHeader.getInvoiceNumber()), 0, y);
                y += lineHeight;
                g.drawString(String.format("%s %s\n", receiptHeader.getFirstname(), receiptHeader.getLastname()), 0, y);
                break;
        }
        y += lineHeight;

        String heading = String.format("%-17s %16s %6s %6s %9s\n", "Item", "Qty", "Unit", "Price", "Total");
        String underline = "-------------------------------------------------------------------";
        String underlineSubtotal = "____________________________________________";

        g.drawString(underlineSubtotal, 0, y);
        y += lineHeight;
        g2d.setFont(fontbold);
        g.drawString(heading, 0, y);

        g2d.setFont(fontplain);
        y += lineHeight;
        g.drawString(underline, 0, y);
        int MAX_CHAR = 15;

        double vatTotal = 0.0;

        for (Item item : transaction.getItems()) {
            String inputString = item.getProductName();
            int maxLength = (inputString.length() < MAX_CHAR) ? inputString.length() : MAX_CHAR;
            Boolean clipped = false;
            if (inputString.length() > MAX_CHAR) {
                clipped = true;
            }
            inputString = inputString.substring(0, maxLength);
            if (clipped) {
                inputString = inputString + "'";
            }
            y += lineHeight;
            g2d.setFont(fontplain);

            inputString = String.format("%-17s", inputString);
            g.drawString(inputString, 0, y);
            g2d.setFont(fontplain);
            g.drawString(String.format("%,-9.2f",item.getQuantity()), 86, y);
            g.drawString(String.format("%-11s",item.getUnits()), 113, y);
            g.drawString(String.format("%,-10.0f",item.getPrice()), 135, y);
            g.drawString(String.format("%,-10.0f",item.getTotalPrice()), 166, y);

            vatTotal += item.getVat_amount()* item.getQuantity();
        }
        y += lineHeight;
        g.drawString(underlineSubtotal, 0, y);
        y += lineHeight;
        //g2d.setFont(fontbold);
        g.drawString(String.format("%-64s %,-11.0f", "TOTAL", transaction.computeGrandTotal()),
                0, y);

        y += lineHeight;

        g2d.setFont(fontplain);
        g.drawString(underlineSubtotal, 0, y);
        y += lineHeight;

        g.drawString(String.format("%-68s %,-11.0f", "Cash", receiptHeader.getCash()), 0, y);
        y += lineHeight;
        g.drawString(String.format("%-65s  %,-11.0f", "Change", receiptHeader.getChange()), 0, y);
        y += lineHeight;
        g.drawString(underline, 0, y);

        //TODO: Ugly code. This conditional prevents null pointer exception if an invoice is printed and there is no balance.
        if (docType != DocType.INVOICE) {
            if (receiptHeader.getBalance() > 0.0) {
                y += lineHeight;
                g.drawString(String.format("%-65s  %,-11.0f", "Balance", receiptHeader.getBalance()), 0, y);
            }
        }



        g2d.setFont(fontplain);

        if(vatTotal>0) {
            y += lineHeight*2;

            g.drawString(String.format("%s \n", "Prices include VAT where applicable."), 0, y);

//        String heading2 = String.format("%-17s %16s %6s %6s %9s\n", "Item", "Qty", "Unit", "Price", "Total");
//
//        g.drawString(heading2, 0, y);

            y += lineHeight * 1;

            g.drawString(String.format("%-10s  %,-11.2f", "VAT(18%)   - ", vatTotal), 0, y);

            y += lineHeight*1;

            g.drawString(underline, 0, y);
        }
        y += lineHeight*2;

        g.drawString(String.format("%s \n", "Thank you.") ,0, y);
        y += lineHeight;
        g.drawString(String.format("%s %2s \n", "You were served by:",receiptHeader.getUserName()),0, y);



        /* tell the caller that this page is part of the printed document */
        return PAGE_EXISTS;
    }
}
