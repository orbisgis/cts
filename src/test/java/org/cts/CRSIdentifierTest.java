/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cts;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 *
 * @author ebocher
 */
public class CRSIdentifierTest {

    CRSFactory crsf = new CRSFactory();

    @Test
    public void testEPSG4326AuthorityName() throws Exception {
        assertTrue(crsf.getCRS("EPSG:4326").getAuthorityName().equals("EPSG"));
    }

    @Test
    public void testEPSG4326AuthorityKey() throws Exception {
        assertTrue(crsf.getCRS("EPSG:4326").getAuthorityKey().equals("4326"));
    }

    //Disable this test because the javadoc is not coherent
    @Test
    public void testEPSG4326Code() throws Exception {
        assertTrue(crsf.getCRS("EPSG:4326").getCode().equals("EPSG:4326"));
    }

    @Test
    public void testEPSG4326Name() throws Exception {
        assertTrue(crsf.getCRS("EPSG:4326").getName().equals("WGS 84"));
    }

    @Test
    public void testEPSG27572AuthorityName() throws Exception {
        assertTrue(crsf.getCRS("EPSG:27572").getAuthorityName().equals("EPSG"));
    }

    @Test
    public void testEPSG27572AuthorityKey() throws Exception {
        assertTrue(crsf.getCRS("EPSG:27572").getAuthorityKey().equals("27572"));
    }

    //Disable this test because the javadoc is not coherent
    @Test
    public void testEPSG27572Code() throws Exception {
        assertTrue(crsf.getCRS("EPSG:27572").getCode().equals("EPSG:27572"));
    }

    @Test
    public void testEPSG27572Name() throws Exception {
        assertTrue(crsf.getCRS("EPSG:27572").getName().equals("NTF (Paris) / Lambert zone II"));
    }
}
