package Tests;

import model.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class TestItem {
    Item item;

    @BeforeEach
    public void testSetup(){
        item = new Item(1,5,1000);
    }

    @Test
    public void computeTotalPriceTest(){

       assertEquals(item.computeTotalPrice(),5000.00);

    }






}
