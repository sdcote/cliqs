/*
 * Copyright (c) 2015 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 *
 * Contributors:
 *   Stephan D. Cote 
 *      - Initial concept and initial implementation
 */
package coyote.commons;

//import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Date;

import org.junit.Test;


/**
 * 
 */
public class CipherUtilTest {

  /**
   * Test method for {@link coyote.commons.CipherUtil#encrypt(java.lang.String)}.
   */
  @Test
  public void testEncrypt() {
    String ciphertext = CipherUtil.encrypt( "This is a test" );
    assertNotNull( ciphertext );
  }




  /**
   * Test method for {@link coyote.commons.CipherUtil#decrypt(java.lang.String)}.
   */
  @Test
  public void testDecrypt() {
    String cleartext = CipherUtil.decrypt( "EJNSLHFGMETNYUJAT5KPTZ7J3YQFRKLNJJFKEJ4HSBBS54YDDDZQ====" );
    assertNotNull( cleartext );
  }




  @Test
  public void testRoundTrip() {
    String phrase = "This is a Test on: " + new Date().toString();

    String ciphertext = CipherUtil.encrypt( phrase );
    String cleartext = CipherUtil.decrypt( ciphertext );
    assertEquals( phrase, cleartext );
  }

}
