/*
 * Copyright 2017 Nicholas Folse <https://github.com/NF1198>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tauterra.nettools;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

/**
 * An object builder that builds instances by consuming a StAX XML event stream.
 * Compose multiple builders together to parse arbitrarly complex XML documents.
 *
 * <p>This class represents a balance between streaming and DOM-based approaches to
 * XML parsing. Like a DOM-style parser, this class parses an entire XML
 * document to produce an object hierarchy. However, it builds the specified
 * objects directly and avoids creating an entire XML DOM. Instead, the parser
 * consumes the StAX XML event stream and applies user-defined handlers to each
 * element encountered in the stream.</p>
 *
 * <p>Each builder is configured with handlers for tags contained as immediate
 * children of the tag. Builders can be nested to process arbitrarly complex XML
 * documents.</p>
 *
 * <h3>Basic usage summary:</h3>
 * 
 * <p>1) Create a parser, specifying the type of object the
 * parser will create.</p>
 *
 * <pre>{@code
 * StAXObjectBuilder<Config>; config_parser = new
 * StAXObjectBuilder<>("config", Config.class);
 * }</pre>
 *
 * <p>2) Add handlers or attribute handlers to the builder. Handlers are specified
 * with a tag name and a {@code BiConsumer<U, String>}, where U represents the type
 * of the builder.</p>
 *
 * <pre>{@code
 * config_parser.addHandler("name", (config, value) -> config.setName(value));
 * }</pre>
 *
 * <p>3) Parse the document by calling {@link #parseDocument(javax.xml.stream.XMLEventReader) }</p>
 *
 * <pre>{@code
 * XMLInputFactory inputFactory = XMLInputFactory.newFactory(); InputStream in =
 * FileInputStream("config.xml"); XMLEventReader eventReader =
 * inputFactory.createXMLEventReader(in);
 *
 * Config config = config_parser.parseDocument(eventReader);
 * }</pre>
 *
 * <p>The StAXObjectBuilder accepts three types of handlers: leaf handlers
 * (specified as above), attribute handlers (these work just like leaf handlers,
 * but are used to process attributes), and nested handlers. Nested handlers
 * allow you to define a hierarchy of builders to parse a complex document.</p>
 *
 * <h3>Error handling:</h3>
 *
 * <p>The parse methods will throw XML errors if the document is malformed.</p>
 *
 * <p>By default, tags with no associated handler are ignored. Call
 * {@link #setMissingElementHandler(java.util.function.Consumer)} on the
 * top-level object builder to define a missing element handler. The
 * missing-element handler is applied with a top-down approach. Thus the
 * top-level handler will be called for missing handlers in nested builders.</p>
 *
 * @author Nicholas Folse <https://github.com/NF1198>
 * @param <U> The type of object that this builder will produce
 */
public class StAXObjectBuilder<U> {

    private final Supplier<U> supplier;
    private final String tagName;
    private final Map<String, BiConsumer<U, String>> handlerMap = new HashMap<>();
    private final Map<String, BiConsumer<U, String>> attrHandlerMap = new HashMap<>();
    private final Map<String, BiConsumer<U, ? extends Object>> objectHandlerMap = new HashMap<>();
    private final Map<String, StAXObjectBuilder<? extends Object>> builderMap = new HashMap<>();
    private BiConsumer<U, String> characterDataHandler = null;
    private Consumer<StartElement> missingHandlerHandler = null;

    /**
     * Return a new object builder
     *
     * @param tagName Defines the XML tag name that will be procesed using this
     * builder.
     * @param supplier Should create new instances of the specified type.
     */
    public StAXObjectBuilder(String tagName, Supplier<U> supplier) {
        this.tagName = tagName;
        this.supplier = supplier;
    }

    /**
     * Set the missing element handler for the builder.
     *
     * The missing element handler will be applied in a top-down approach during
     * parsing. Missing element handlers in nested builders will be ignored.
     *
     * @param handler
     */
    public void setMissingElementHandler(Consumer<StartElement> handler) {
        this.missingHandlerHandler = handler;
    }

    /**
     * Add a leaf-node handler.
     *
     * A leaf-node handler is used to process leaf-nodes in an XML document.
     * Leaf-nodes are defined as simple tags with no attributes.
     *
     * Create a distinct builder for leaf-nodes with attributes.
     *
     * @param tagName
     * @param handler
     */
    public void addHandler(String tagName, BiConsumer<U, String> handler) {
        handlerMap.put(tagName, handler);
    }

    /**
     * Add an attribute handler.
     *
     * An attribute hadler processes attributes defined on the node.
     *
     * @param tagName
     * @param handler
     */
    public void addAttributeHandler(String tagName, BiConsumer<U, String> handler) {
        attrHandlerMap.put(tagName, handler);
    }

    /**
     * Add a nested object builder handler.
     *
     * The nested builder will be be used to build nested objects, then the
     * handler will be called to apply the child object to the parent.
     *
     * @param <T>
     * @param builder
     * @param handler
     */
    public <T> void addHandler(StAXObjectBuilder<T> builder, BiConsumer<U, T> handler) {
        builderMap.put(builder.tagName, builder);
        objectHandlerMap.put(builder.tagName, handler);
    }

    /**
     * Set the handler for character data.
     *
     * This handler only applies to nodes that have attributes and character
     * data with no nested nodes.
     *
     * @param handler
     */
    public void setCharacterDataHandler(BiConsumer<U, String> handler) {
        this.characterDataHandler = handler;
    }

    /**
     * Parse an XML document.
     *
     * This function expects to encounter a START_DOCUMENT event. It then calls
     * {@link #parse(javax.xml.stream.XMLEventReader, javax.xml.stream.events.StartElement) }
     * to parse the stream.
     * 
     * @param eventReader
     * @return
     * @throws XMLStreamException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public U parseDocument(XMLEventReader eventReader) throws XMLStreamException, InstantiationException, IllegalAccessException {
        while (eventReader.hasNext()) {
            XMLEvent event = eventReader.nextEvent();
            if (event.getEventType() == XMLEvent.START_DOCUMENT) {
            }
            if (event.getEventType() == XMLEvent.START_ELEMENT) {
                StartElement startElement = event.asStartElement();
                if (this.tagName == null || startElement.getName().getLocalPart().equals(tagName)) {
                    return parse(eventReader, startElement);
                }
            }
            if (event.getEventType() == XMLEvent.END_DOCUMENT) {
                break;
            }
        }
        return null;
    }

    /**
     * Parse an object from a stream.
     * 
     * This method parses the specified element and applies handlers to build
     * an object. The object is returned.
     * 
     * In typical use-cases, you should just parse the entire document 
     * ({@link #parseDocument(javax.xml.stream.XMLEventReader) }) instead of calling 
     * this method directly.
     * 
     * The missing element handler defined on the builder instance will be used to
     * handle missing elements.
     * 
     * @param eventReader The xmlEventReader stream
     * @param start The first element that defines the object
     * @return
     * @throws XMLStreamException
     * @throws InstantiationException
     * @throws IllegalAccessException 
     */
    public U parse(XMLEventReader eventReader, StartElement start) throws XMLStreamException, InstantiationException, IllegalAccessException {
        return parse(eventReader, start, this.missingHandlerHandler);
    }

    /**
     * Parse an object from a stream.
     * 
     * This version of the method accepts an error handler for missing elements.
     * In most cases, you should call the version without specifying the handler.
     * 
     * @param eventReader
     * @param start
     * @param missingHandlerHandler
     * @return
     * @throws XMLStreamException
     * @throws InstantiationException
     * @throws IllegalAccessException 
     */
    public U parse(XMLEventReader eventReader, StartElement start, Consumer<StartElement> missingHandlerHandler) throws XMLStreamException, InstantiationException, IllegalAccessException {
        U result = supplier.get();
        Iterator<Attribute> attrsIter = start.getAttributes();
        while (attrsIter != null && attrsIter.hasNext()) {
            final Attribute attr = attrsIter.next();
            final String attrName = attr.getName().getLocalPart();
            if (attrHandlerMap.containsKey(attrName)) {
                BiConsumer<U, String> handler = attrHandlerMap.get(attrName);
                final String data = attr.getValue();
                handler.accept(result, data);
            }
        }
        OUTER:
        while (eventReader.hasNext()) {
            XMLEvent event = eventReader.nextEvent();
            switch (event.getEventType()) {
                case XMLEvent.START_ELEMENT:
                    StartElement startElement = event.asStartElement();
                    String elementName = startElement.getName().getLocalPart();
                    if (builderMap.containsKey(elementName)) {
                        // check if item is a object with an associated builder
                        BiConsumer<U, Object> handler = (BiConsumer<U, Object>) objectHandlerMap.get(elementName);
                        
                        StAXObjectBuilder<? extends Object> itemBuilder = builderMap.get(elementName);
                        Object item = itemBuilder.parse(eventReader, startElement, missingHandlerHandler);
                        handler.accept(result, item);
                    } else if (handlerMap.containsKey(elementName)) {
                        BiConsumer handler = handlerMap.get(elementName);
                        XMLEvent dataEvent = eventReader.nextEvent();
                        StringBuilder sb = new StringBuilder();
                        if (dataEvent.getEventType() == XMLEvent.CHARACTERS) {
                            while (dataEvent.getEventType() == XMLEvent.CHARACTERS) {
                                String data = dataEvent.asCharacters().getData();
                                sb.append(data);
                                dataEvent = eventReader.nextEvent();
                            }
                        } else if (dataEvent.getEventType() == XMLEvent.CDATA) {
                            while (dataEvent.getEventType() == XMLEvent.CDATA) {
                                String data = dataEvent.asCharacters().getData();
                                sb.append(data);
                                dataEvent = eventReader.nextEvent();
                            }
                        }
                        handler.accept(result, sb.toString());
                    } else {  // no handler for element
                        if (missingHandlerHandler != null) {
                            missingHandlerHandler.accept(startElement);
                        }
                        int level = 0;
                        while (eventReader.hasNext()) {
                            XMLEvent next = eventReader.nextEvent();
                            if (next.getEventType() == XMLEvent.START_ELEMENT) {
                                level++;
                                if (missingHandlerHandler != null) {
                                    missingHandlerHandler.accept(next.asStartElement());
                                }
                            } else if (next.getEventType() == XMLEvent.END_ELEMENT) {
                                if (level == 0) {
                                    break;
                                } else {
                                    level--;
                                }
                            }
                        }
                        
                    }   break;
                case XMLEvent.CHARACTERS:
                case XMLEvent.CDATA:
                    if (this.characterDataHandler != null) {
                        StringBuilder sb = new StringBuilder();
                        while (eventReader.hasNext() && (event.getEventType() == XMLEvent.CHARACTERS || event.getEventType() == XMLEvent.CDATA)) {
                            sb.append(event.asCharacters().getData());
                            event = eventReader.nextEvent();
                        }   this.characterDataHandler.accept(result, sb.toString());
                        break OUTER;
                    }
                    break;
                case XMLEvent.END_ELEMENT:
                    break OUTER;
                default:
                    break;
            }
        }
        return result;
    }

}