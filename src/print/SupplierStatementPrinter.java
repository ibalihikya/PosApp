package print;

import model.*;

import java.awt.*;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.util.ArrayList;

public class SupplierStatementPrinter implements Printable {
    private ArrayList<SupplierTransaction> supplierTransactions;
    private ReceiptHeader receiptHeader;

    public SupplierStatementPrinter(ArrayList<SupplierTransaction> supplierTransactions, ReceiptHeader receiptHeader) {
        this.supplierTransactions = supplierTransactions;
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

        g2d.setFont(fontbold);
        SupplierTransaction transaction = supplierTransactions.get(0); //get supplier name from any transaction; in this case first transaction
        g.drawString(transaction.getSupplierName(),0,y);


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
        for(SupplierTransaction supplierTransaction : supplierTransactions){
            String date = String.format("%-17s", supplierTransaction.getDate_created());

            if(supplierTransaction.getTransaction_type().toString().equals("d") ) {
                g2d.setFont(fontbold);
                g.drawString(date ,0, y);
                g.drawString(String.format("%-7s",supplierTransaction.getTransaction_type()), 84, y);
                g.drawString(String.format("%,-11.0f",supplierTransaction.getAmount()), 106, y);
                g.drawString(String.format("%,-10.0f",supplierTransaction.getBalance()), 160, y);

                y += lineHeight;

                g2d.setFont(fontplain);
                for (Item item : supplierTransaction.getItems()) {
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
            else if(supplierTransaction.getTransaction_type().toString().equals("p")){
                g2d.setFont(fontbold);
                g.drawString(date ,0, y);
                g.drawString(String.format("%-7s",supplierTransaction.getTransaction_type()), 84, y);
                g.drawString(String.format("%,-11.0f",supplierTransaction.getAmount()), 106, y);
                g.drawString(String.format("%,-10.0f",supplierTransaction.getBalance()), 160, y);
                y += lineHeight*2;
            }
        }
        /* tell the caller that this page is part of the printed document */
        return PAGE_EXISTS;
    }
}
