package com.github.richardwilly98;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Assert;
import org.testng.annotations.Test;

public class TestParseDateTime {

    @Test(enabled = false)
    public void testParseDatetime() throws ParseException {
        String dateString = "2013-10-02T16:21:15EDT";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date date = dateFormat.parse(dateString);
        Assert.assertNotNull(date);
    }

}
