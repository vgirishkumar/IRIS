package com.temenos.interaction.rimdsl.encoding;

import org.eclipse.emf.common.util.URI;
import org.eclipse.xtext.parser.IEncodingProvider;

/**
 * Responsible for Providing the Encoding for RIM
 * 
 * @author vgirishkumar
 *
 */
public class RIMEncodingProvider implements IEncodingProvider {

    @Override
    public String getEncoding(URI uri) {
        return "UTF-8";
    }

}
