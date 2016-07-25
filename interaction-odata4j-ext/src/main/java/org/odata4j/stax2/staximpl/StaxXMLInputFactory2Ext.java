package org.odata4j.stax2.staximpl;

/*
 * #%L
 * interaction-odata4j-ext
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

import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.core4j.Enumerable;
import org.odata4j.core.Throwables;
import org.odata4j.stax2.Attribute2;
import org.odata4j.stax2.Characters2;
import org.odata4j.stax2.EndElement2;
import org.odata4j.stax2.Namespace2;
import org.odata4j.stax2.QName2;
import org.odata4j.stax2.StartElement2;
import org.odata4j.stax2.XMLEvent2;
import org.odata4j.stax2.XMLEventReader2;
import org.odata4j.stax2.XMLInputFactory2;

/**
 * Copy of StaxXMLInputFactory2 from StaxXMLFactoryProvider2
 * This class can be removed when Stax allows options/attributes in the default factory implementation.
 */
public class StaxXMLInputFactory2Ext implements XMLInputFactory2 {

    private final XMLInputFactory factory;

    public StaxXMLInputFactory2Ext(XMLInputFactory factory) {
        this.factory = factory;
    }

    @Override
    public XMLEventReader2 createXMLEventReader(Reader reader) {
        try {
            XMLEventReader real = factory.createXMLEventReader(reader);
            return new StaxXMLEventReader2(real);
        } catch (XMLStreamException e) {
            throw Throwables.propagate(e);
        }
    }

    private static class StaxXMLEventReader2 implements XMLEventReader2 {
        private final XMLEventReader real;

        public StaxXMLEventReader2(XMLEventReader real) {
            this.real = real;
        }

        @Override
        public String getElementText() {
            try {
                return real.getElementText();
            } catch (XMLStreamException e) {
                throw Throwables.propagate(e);
            }
        }

        @Override
        public boolean hasNext() {
            return real.hasNext();
        }

        @Override
        public XMLEvent2 nextEvent() {
            try {
                return new StaxXMLEvent2(real.nextEvent());
            } catch (XMLStreamException e) {
                throw Throwables.propagate(e);
            }
        }
    }

    private static class StaxXMLEvent2 implements XMLEvent2 {
        private final XMLEvent real;

        public StaxXMLEvent2(XMLEvent real) {
            this.real = real;
        }

        @Override
        public String toString() {
            return String.format("%s[%s]", StaxXMLEvent2.class.getSimpleName(), getEventTypeName());
        }

        private String getEventTypeName() {
            switch (real.getEventType()) {
            case XMLStreamConstants.START_ELEMENT:
                return "START_ELEMENT";
            case XMLStreamConstants.END_ELEMENT:
                return "END_ELEMENT";
            case XMLStreamConstants.CHARACTERS:
                return "CHARACTERS";
            case XMLStreamConstants.ATTRIBUTE:
                return "ATTRIBUTE";
            case XMLStreamConstants.NAMESPACE:
                return "NAMESPACE";
            case XMLStreamConstants.PROCESSING_INSTRUCTION:
                return "PROCESSING_INSTRUCTION";
            case XMLStreamConstants.COMMENT:
                return "COMMENT";
            case XMLStreamConstants.START_DOCUMENT:
                return "START_DOCUMENT";
            case XMLStreamConstants.END_DOCUMENT:
                return "END_DOCUMENT";
            case XMLStreamConstants.DTD:
                return "DTD";
            default:
                return "UNKNOWN TYPE " + real.getEventType();
            }
        }

        public XMLEvent getXMLEvent() {
            return real;
        }

        @Override
        public EndElement2 asEndElement() {
            return new StaxEndElement2(real.asEndElement());
        }

        @Override
        public StartElement2 asStartElement() {
            return new StaxStartElement2(real.asStartElement());
        }

        @Override
        public Characters2 asCharacters() {
            return new StaxCharacters2(real.asCharacters());
        }

        @Override
        public boolean isEndElement() {
            return real.isEndElement();
        }

        @Override
        public boolean isStartElement() {
            return real.isStartElement();
        }

        @Override
        public boolean isCharacters() {
            return real.isCharacters();
        }

    }

    private static class StaxEndElement2 implements EndElement2 {
        private final EndElement real;

        public StaxEndElement2(EndElement real) {
            this.real = real;
        }

        @Override
        public QName2 getName() {
            return new QName2(real.getName().getNamespaceURI(), real.getName().getLocalPart());
        }
    }

    public static class StaxStartElement2 implements StartElement2 {
        public final StartElement real;

        public StaxStartElement2(StartElement real) {
            this.real = real;
        }

        @Override
        public QName2 getName() {
            return new QName2(real.getName().getNamespaceURI(), real.getName().getLocalPart());
        }

        @Override
        public Attribute2 getAttributeByName(String name) {
            return getAttributeByName(new QName2(name));
        }

        @Override
        public Attribute2 getAttributeByName(QName2 name) {
            Attribute att = real.getAttributeByName(StaxXMLFactoryProvider2.toQName(name));
            if (att == null)
                return null;
            return new StaxAttribute2(att);
        }

        @Override
        public Enumerable<Attribute2> getAttributes() {
            Iterator i = real.getAttributes();
            List<Attribute2> atts = new ArrayList<Attribute2>();
            while (i.hasNext()) {
                atts.add(new StaxAttribute2((Attribute) i.next()));
            }
            return Enumerable.create(atts);
        }

        @Override
        public Enumerable<Namespace2> getNamespaces() {
            Iterator i = real.getNamespaces();
            List<Namespace2> namespaces = new ArrayList<Namespace2>();
            while (i.hasNext()) {
                namespaces.add(new StaxNamespace2((Namespace) i.next()));
            }
            return Enumerable.create(namespaces);
        }
    }

    private static class StaxNamespace2 extends StaxAttribute2 implements Namespace2 {

        public StaxNamespace2(Namespace real) {
            super(real);
        }

        @Override
        public String getNamespaceURI() {
            return ((Namespace) real).getNamespaceURI();
        }

        @Override
        public String getPrefix() {
            return ((Namespace) real).getPrefix();
        }

        @Override
        public boolean isDefaultNamespaceDeclaration() {
            return ((Namespace) real).isDefaultNamespaceDeclaration();
        }

    }

    private static class StaxAttribute2 implements Attribute2 {
        protected final Attribute real;

        public StaxAttribute2(Attribute real) {
            this.real = real;
        }

        @Override
        public String getValue() {
            return real.getValue();
        }

        @Override
        public QName2 getName() {
            return new QName2(real.getName().getNamespaceURI(), real.getName().getLocalPart(), real.getName()
                    .getPrefix());
        }
    }

    private static class StaxCharacters2 implements Characters2 {
        protected final Characters real;

        public StaxCharacters2(Characters real) {
            this.real = real;
        }

        @Override
        public String getData() {
            return real.getData();
        }

    }

}