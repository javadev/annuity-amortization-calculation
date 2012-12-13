/*
 * $Id$
 *
 * Copyright 2012 Valentyn Kolesnikov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.amortization;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.mycila.xmltool.XMLDoc;
import com.mycila.xmltool.XMLTag;

/**.
 * @author Valentyn Kolesnikov
 * @version $Revision$ $Date$
 */
public abstract class AbstractTarificationTestCase {

    private <T> void copyDataToObject(XMLTag xmlTag, T inputParam)
            throws IllegalAccessException, ParseException {
        for (java.util.Iterator<XMLTag> iterator = xmlTag.duplicate().getChilds().iterator();
            iterator.hasNext(); ) {
            XMLTag xmlTag2 = iterator.next();
            Class<?> clazz = inputParam.getClass();
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                if (field.getName().equals(xmlTag2.getCurrentTagName())) {
                    copyClassFields(inputParam, xmlTag2.getText(), field);
                    break;
                } else if (xmlTag2.hasAttribute("name") && field.getName().equals(xmlTag2.getAttribute("name"))) {
                    createSubClass(inputParam, xmlTag2, field);
                }
            }
        }
    }

    private <T> void createSubClass(T inputParam, XMLTag xmlTag2, Field field) throws IllegalAccessException,
            ParseException {
        try {
            Class<?> clazz2 = Class.forName(field.getType().getCanonicalName());
            Object item = clazz2.newInstance();
            Field[] fields2 = clazz2.getDeclaredFields();
            for (java.util.Iterator<XMLTag> iterator2 = xmlTag2.duplicate().getChilds().iterator();
                iterator2.hasNext(); ) {
                XMLTag xmlTag3 = iterator2.next();
                for (Field field2 : fields2) {
                    if (field2.getName().equals(xmlTag3.getCurrentTagName())) {
                        copyClassFields(item, xmlTag3.getText(), field2);
                        break;
                    }
                }
            }
            field.setAccessible(true);
            field.set(inputParam, item);
        } catch (ClassNotFoundException e) {
            LOG.error(this, e.getMessage());
        } catch (InstantiationException e) {
            LOG.error(this, e.getMessage());
        }
    }

    private <T> void copyClassFields(T inputParam, String text, Field field) throws IllegalAccessException,
            ParseException {
        field.setAccessible(true);
        if ("double".equals(field.getType().getName())) {
            field.setDouble(inputParam, Double.valueOf(text));
        } else if ("int".equals(field.getType().getName())) {
            field.setInt(inputParam, Integer.valueOf(text));
        } else if ("long".equals(field.getType().getName())) {
            field.setLong(inputParam, Integer.valueOf(text));
        } else if ("java.lang.Long".equals(field.getType().getName())) {
            field.set(inputParam, Long.valueOf(text));
        } else if ("java.util.Date".equals(field.getType().getName())) {
            field.set(inputParam, new SimpleDateFormat("ddMMyyyy").parse(text));
        } else {
            LOG.error(this, "unsupported field type: " + field.getType().getName());
        }
    }

    /**
     * Creates object from xml data for the specified testName and class.
     * @param <T> the object type
     * @param testName the test name
     * @param clazz the class object
     * @return the new object with data
     */
    protected <T> T createObjectFromData(String testName, Class<T> clazz) {
        String xpath = testName + "/" + clazz.getName().replaceFirst(".*\\.(\\w+)", "$1");
        XMLTag xmlTag = XMLDoc.from(getClass().getResource(getClass().getName().replaceFirst(
                ".*\\.(\\w+)", "$1.xml")), true);
        T item = null;
        try {
            item = clazz.newInstance();
        } catch (InstantiationException e) {
            LOG.error(this, e.getMessage());
        } catch (IllegalAccessException e) {
            LOG.error(this, e.getMessage());
        }
        if (xmlTag.hasTag(xpath + "[1]")) {
            xmlTag.gotoTag(xpath + "[1]");
            try {
                copyDataToObject(xmlTag, item);
            } catch (IllegalAccessException e) {
                LOG.error(this, e.getMessage());
            } catch (ParseException e) {
                LOG.error(this, e.getMessage());
            }
        } else {
            LOG.error(this, "xpath not found " + xpath);
        }
        return item;
    }

    /**
     * Creates list of object from xml data for the specified testName and class.
     * @param <I> the object type
     * @param testName the test name
     * @param clazz the class object
     * @return the new object with data
     */
    protected <I> List<I> createListFromData(String testName, Class<I> clazz) {
        String xpath = testName + "/" + clazz.getName().replaceFirst(".*\\.(\\w+)", "$1");
        XMLTag xmlTag = XMLDoc.from(getClass().getResource(getClass().getName().replaceFirst(
                ".*\\.(\\w+)", "$1.xml")), true);
        int index = 1;
        List<I> items = new ArrayList<I>();
        while (xmlTag.hasTag(xpath + "[" + index + "]")) {
            XMLTag xmlTag2 = xmlTag.duplicate().gotoTag(xpath + "[" + index + "]");
            I item = null;
            try {
                item = clazz.newInstance();
                copyDataToObject(xmlTag2, item);
            } catch (InstantiationException e) {
                LOG.error(this, e.getMessage());
                break;
            } catch (IllegalAccessException e) {
                LOG.error(this, e.getMessage());
                break;
            } catch (ParseException e) {
                LOG.error(this, e.getMessage());
                break;
            }
            items.add(item);
            index += 1;
        }
        return items;
    }
}
