package Tests;

import model.Item;
import model.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestTransaction {
    Item cement;
    Item tiles;
    Transaction transaction1;

    @BeforeEach
    public void testSetup(){
        cement = new Item(2,2.0,30000);
        tiles = new Item(3,10.0,40000);
        //TODO 8: remove hard coded userid
        transaction1 = new Transaction("ibalihikya");
    }

    @Test
    void ComputeGrandTotalTest(){

        assertEquals(transaction1.computeGrandTotal(),0.0);
        transaction1.addItem(cement);
        assertEquals(transaction1.computeGrandTotal(),60000.0);
        transaction1.addItem(tiles);
        assertEquals(transaction1.computeGrandTotal(),460000.0);

    }

    @Test
    void AddItemTest(){
        assertEquals(transaction1.getItems().size(),0);
        transaction1.addItem(cement);
        assertEquals(transaction1.getItems().size(),1);
        transaction1.addItem(tiles);
        assertEquals(transaction1.getItems().size(),2);

    }

    @Test
    void RemoveItemTest(){
        assertEquals(transaction1.getItems().size(),0);
        transaction1.addItem(cement);
        assertTrue(transaction1.removeItem(cement));
        assertFalse(transaction1.removeItem(cement));
     }
/*
     //TODO 7: Test incomplete
    @Test
    void CommitTransactionTest(){
        transaction1.addItem(cement);
        transaction1.addItem(tiles);
        transaction1.commitTransaction();
    }*/
/*
    @Test
    void GenerateReceiptIdTest(){
        assertTrue(transaction1.generateReceiptId()>=1);
    }*/


}
