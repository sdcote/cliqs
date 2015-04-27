/*
 * Copyright (c) 2014 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 *
 * Contributors:
 *   Stephan D. Cote 
 *      - Initial API and implementation
 */
package coyote.cli.template;

import java.io.IOException;
import java.util.Hashtable;


/**
 * Parses a String and replaces variables with the values represented in a
 * table of symbols.
 *
 * <p>This utility searches for tags delimited by the opening &quot;[#&quot;
 * and the closing &quot;#]&quot; string sequences. This parser then replaces
 * the tokens within the delimiters with the string values those tokens
 * represent.</p>
 *
 * <p> If the token is preceded with a &quot;$&quot;, then the token is treated
 * as a key to be used to lookup an object in a symbol table. That object's
 * {@code toString} method is called and the returning value placed in the
 * position where the token was found. This is analogous to a variable lookup.
 * If the token is not found in the table or the symbols object returns a null
 * string, then the string &quot;null&quot; is returned.</p>
 *
 * <p>Tokens that are not preceded with a &quot;$&quot; are treated as class
 * tokens when encountered, the parser attempts to create a new instance if
 * that class, calling the new instances {@code toString} method after
 * it's constructor. If the class is not found, a string value of
 * &quot;null&quot; is returned.</p>
 *
 */
public class Template extends StringParser {
  private static SymbolTable symbols = new SymbolTable();
  private final Hashtable<String, Object> classCache = new Hashtable<String, Object>();

  private static final String OPEN_TAG = "[#";
  private static final String CLOSE_TAG = "#]";




  /**
   * Lookup the given tag in the given symbol table and object cache.
   *
   * @param tag the tag this method is to resolve
   * @param symbols The hash of scalar typed data mapped by name
   * @param cache the hash table of object instances that may provide dynamic data
   *
   * @return a string representing the data behind the given tag.
   */
  public static String resolve( final String tag, final SymbolTable symbols, final Hashtable cache ) {
    final StringBuffer retval = new StringBuffer();

    final StringParser parser = new StringParser( tag );

    try {
      while ( !parser.eof() ) {
        final String token = parser.readToken();

        if ( ( token == null ) || ( token.length() < 1 ) ) {
          break;
        }

        // Those tokens with a $ is a straight symbol table lookup
        if ( token.startsWith( "$" ) ) {
          final String key = token.substring( 1 );
          retval.append( symbols.getString( key ) );
        } else {
          // Must be a class; see if it is a method or constructor
          // reference

          //
          // Complex class logic removed for this project.
          //

        }
      }
    } catch ( final Exception ex ) {

    }

    return retval.toString();
  }




  /**
   * Convert the template into a string using the given symbol map.
   *
   * <p>This is where all the work takes place.
   *
   * @param template The string representing the template data
   * @param symbols the SymbolTable to us when resolving tokens
   * @param cache
   *
   * @return a string representing the fully-resolved template.
   *
   * @throws TemplateException
   */
  public static String toString( final Template template, SymbolTable symbols, final Hashtable cache ) throws TemplateException {
    if ( template != null ) {
      if ( symbols == null ) {
        symbols = new SymbolTable();
      }

      final StringBuffer buffer = new StringBuffer();

      try {
        // Keep looping
        while ( !template.eof() ) {
          final String userText = template.readToPattern( OPEN_TAG );

          if ( userText != null ) {
            buffer.append( userText );
          }

          // if we are at the End Of the File, then we are done
          if ( template.eof() ) {
            break;
          } else {
            // Skip past the opening tag delimiter
            template.skip( OPEN_TAG.length() );

            // Start reading the contents of the tag
            final String tag = template.readToPattern( CLOSE_TAG );

            // If we are at EOF then the read terminated before the
            // closing tag was encountered. This means the template
            // is not complete.
            if ( template.eof() ) {
              buffer.append( "TEMPLATE ERROR: reached EOF before finding closing delimiter '" + CLOSE_TAG + "' at " + template.getPosition() );

              return buffer.toString();
            }

            // read past the closing delimiter
            template.skip( CLOSE_TAG.length() );

            // OK, now perform our replacement magik
            if ( ( tag != null ) && ( tag.length() > 0 ) ) {
              // resolve the tag using the given symbol table and
              // class cache
              buffer.append( resolve( tag, symbols, cache ) );
            }

          }
        }
      } catch ( final IOException ioe ) {
        throw new TemplateException( "IOE", ioe );
      }

      return buffer.toString();
    }

    return null;
  }




  /**
   * Constructor Template
   *
   * @param string the template text
   */
  public Template( final String string ) {
    super( string );
  }




  /**
   * Construct a Template with the given string using the given symbol table.
   * 
   * <p><strong>NOTE:</strong> This replaces the static symbol table for ALL 
   * templates.</p>
   *
   * @param string the template text
   * @param symbols the symbol table to use 
   */
  public Template( final String string, final SymbolTable symbols ) {
    super( string );

    Template.symbols = symbols;
  }




  /**
   * Method addSymbol
   *
   * @param name
   * @param value
   */
  public void addSymbol( final String name, final Object value ) {
    if ( ( name != null ) && ( value != null ) ) {
      symbols.put( name, value );
    }
  }




  /**
   * Get the object with the given name.
   *
   * @param name The name of the object to retrieve.
   *
   * @return the object with the given name or null if not found.
   */
  public Object get( final String name ) {
    if ( ( name != null ) && ( name.length() > 0 ) ) {
      return classCache.get( name );
    }

    return null;
  }




  /**
   * Method getSymbols
   *
   * @return the map of keys to symbols...the symbol table for this template
   */
  public SymbolTable getSymbols() {
    return symbols;
  }




  /**
   * Method mergeSymbols
   *
   * @param table
   */
  public void mergeSymbols( final SymbolTable table ) {
    symbols.merge( table );
  }




  /**
   * Method put
   *
   * @param obj
   */
  public void put( final Object obj ) {
    if ( obj != null ) {
      classCache.put( obj.getClass().getName(), obj );
    }
  }




  /**
   * Method setSymbols
   *
   * @param symbols
   */
  public void setSymbols( final SymbolTable symbols ) {
    Template.symbols = symbols;
  }




  /**
   * Return this template as a string, resolving all the tags.
   *
   * @return the String representing the resolved template.
   */
  @Override
  public String toString() {
    try {
      // call our static method with our instance attributes
      return toString( this, symbols, classCache );
    } catch ( final TemplateException te ) {
      System.err.println( te.getMessage() );
      System.err.println( te.getContext() );

      throw new IllegalArgumentException( "Parser error" );
    }
  }

}