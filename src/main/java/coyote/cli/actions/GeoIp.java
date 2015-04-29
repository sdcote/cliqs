/*
 * Copyright (c) 2015 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 *
 * Contributors:
 *   Stephan D. Cote 
 *      - Initial API and implementation
 */
package coyote.cli.actions;

import org.apache.commons.cli.Options;
import org.apache.http.HttpHost;
import org.apache.http.client.methods.HttpGet;

import coyote.dataframe.DataFrame;
import coyote.dataframe.marshal.JSONMarshaler;


/**
 * This is an example of how to call a REST service in the action. 
 */
public class GeoIp extends RestAction {

  /**
   * Basic constructor
   */
  public GeoIp() {

  }




  /**
   * @see coyote.cli.actions.AbstractAction#buildOptions(org.apache.commons.cli.Options)
   */
  @Override
  public void buildOptions( final Options o ) {

  }




  /**
   * @see coyote.cli.actions.AbstractAction#getHelp()
   */
  @Override
  public String getHelp() {
    final StringBuffer b = new StringBuffer();
    // _______----:----1----:----2----:----3----:----4----:----5----:----6----:----7----:----8
    b.append( "Get the location of the computer based on the geolocation of the IP address.\r\n" );
    return b.toString();
  }




  /**
   * @see coyote.cli.actions.AbstractAction#validate()
   */
  @Override
  public void validate() throws ActionException {

  }




  /**
   * This executes the action with the currently set attributes.
   * 
   * @throws ActionException
   * 
   * @see coyote.cli.actions.Action#execute()
   */
  @Override
  public void execute() throws ActionException {

    DataFrame retval = null;

    // Create the request for the URI
    final HttpGet request = new HttpGet( "http://www.telize.com/geoip" );
    request.setConfig( config );

    // The host of our targeted instance
    final HttpHost target = new HttpHost( "www.telize.com", 80, "http" );;

    // This is how you would set the credentials for the target, if necessary
    //setCredentials( target, "username", "PaS5w0rD" );

    try {

      // Execute the request
      Response response = execute( target, request );

      // get the results of processing the request
      retval = response.getResult();

      // Printout the formatted JSON data received
      info( JSONMarshaler.toFormattedString( retval ) );

    } catch ( final Exception e ) {
      throw new ActionException( "Could not get geolocation", e );
    }

  }

}
