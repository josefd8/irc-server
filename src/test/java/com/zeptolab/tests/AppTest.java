package com.zeptolab.tests;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.*;

/**
 * Unit test for simple IRCServer.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        Queue<String> messages = new LinkedList<String>();

        messages.add("1");
        messages.add("2");
        messages.add("3");
        messages.add("4");

        for( String item : messages ){
            System.out.print(item + "\n");
        }

        messages.remove();
        messages.add("5");

        for( String item : messages ){
            System.out.print(item + "\n");
        }

    }

}
