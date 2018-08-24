package print;

import model.Item;
import model.Transaction;

import java.awt.*;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;

public class ReceiptPrinter implements Printable {
    private Transaction transaction;
    private ReceiptHeader receiptHeader;
    public ReceiptPrinter(Transaction transaction, ReceiptHeader receiptHeader) {
        this.transaction = transaction;
        this.receiptHeader = receiptHeader;
    }

    @Override
    public int print(Graphics g, PageFormat pf, int page) throws PrinterException {
        Font font = new Font("Serif", Font.PLAIN, 5);
        FontMetrics metrics = g.getFontMetrics(font);
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
        y += lineHeight*2;

        g.drawString(String.format("%30s\n",receiptHeader.getBusinessName()), 0, y);
        y += lineHeight*2;
        g.drawString(String.format("%s %2s\n","Tel:",receiptHeader.getTelephoneNumber()),0, y);
        y += lineHeight*2;
        g.drawString(String.format("%s %2s \n", "Date:",receiptHeader.getTime()),0, y);
        y += lineHeight*2;
        g.drawString(String.format("%s %2s \n", "Cashier:",receiptHeader.getUserName()),0, y);
        y += lineHeight*2;
        g.drawString(String.format("%s %2s \n", "Receipt No:",receiptHeader.getReceiptNumber()),0, y);

        y += lineHeight*2;

        String heading = String.format("%-15s %5s %10s %10s\n", "Item", "Qty", "Cost", "Total");
        String underline =          "-----------------------------------------------";
        String underlineSubtotal =  "_____________________________";

        y += lineHeight*2;
        g.drawString(heading, 0, y);
        y += lineHeight;
        g.drawString(underline, 0, y);
        y += lineHeight*2;
        for(Item item : transaction.getItems()){
            y += lineHeight;
            //g.drawString(Integer.toString(item.getProductId()),0,y);
            g.drawString(item.getProductName(),0,y);
            y += lineHeight*2;
            g.drawString( item.toString(),0, y);
            y += lineHeight;

        }
        y += lineHeight;
        g.drawString(underlineSubtotal, 0, y);
        y += lineHeight*2;
        g.drawString(String.format("%s %,42.0f", "TOTAL", transaction.computeGrandTotal()),
                0, y);


        /* tell the caller that this page is part of the printed document */
        return PAGE_EXISTS;
    }
}
