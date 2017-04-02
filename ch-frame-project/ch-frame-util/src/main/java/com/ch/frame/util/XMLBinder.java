package com.ch.frame.util;

import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.bind.*;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * @author david
 */
public final class XMLBinder {
    @SuppressWarnings("unchecked")
    public static <T> String toXML(T bean, String charset) {
        try {
            Class<T> beanClass = (Class<T>) bean.getClass();
            boolean hasXMLRootElementAnnotation = beanClass.isAnnotationPresent(XmlRootElement.class);
            JAXBContext context = JAXBContext.newInstance(beanClass);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.FALSE);
            marshaller.setProperty(Marshaller.JAXB_ENCODING, charset);
            StringWriter writer = new StringWriter();
            if (hasXMLRootElementAnnotation) marshaller.marshal(bean, writer);
            else {
                marshaller.marshal(new JAXBElement<T>(new QName("", beanClass.getSimpleName()), beanClass, bean), writer);
            }
            return writer.toString();
        } catch (JAXBException e) {
            throw new IllegalStateException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T fromXML(Class<T> beanClass, String xml, String charset) {
        try {
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            builderFactory.setValidating(false);
            builderFactory.setNamespaceAware(true);
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            builder.setEntityResolver(new EntityResolver() {
                public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
                    return new InputSource(new StringReader(""));
                }
            });
            Document document = builder.parse(new ByteArrayInputStream(xml.getBytes(charset)));
            JAXBContext context = JAXBContext.newInstance(beanClass);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            JAXBElement<T> element = unmarshaller.unmarshal(document, beanClass);
            return element.getValue();
        } catch (JAXBException | ParserConfigurationException | SAXException | IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
