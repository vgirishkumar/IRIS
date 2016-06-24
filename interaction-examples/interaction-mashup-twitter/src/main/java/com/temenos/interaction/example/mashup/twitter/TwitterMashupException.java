package com.temenos.interaction.example.mashup.twitter;

/*
 * #%L
 * interaction-example-mashup-twitter
 * %%
 * Copyright (C) 2012 - 2016 Temenos Holdings N.V.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */


/**
 * Extension of RuntimeException to use on mashup twitter example project
 *
 * @author clopes
 *
 */
public class TwitterMashupException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Construct a new exception
     *
     * @param cause
     *            error cause
     */
    public TwitterMashupException(Throwable cause) {
        super(cause);
    }
    
    /**
     * Construct a new exception
     *
     * @param message
     *            error message
     */
    public TwitterMashupException(String message) {
        super(message);
    }
    
    /**
     * Construct a new exception
     *
     * @param message
     *            error message
     * @param cause
     *            error cause       
     */
    public TwitterMashupException(String message, Throwable cause) {
        super(message, cause);
    }
}
