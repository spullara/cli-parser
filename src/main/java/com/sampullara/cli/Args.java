/*
 * Copyright (c) 2005, Sam Pullara. All Rights Reserved.
 * You may modify and redistribute as long as this attribution remains.
 */

package com.sampullara.cli;


import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.PrintStream;
import java.lang.reflect.*;
import java.util.*;

public class Args {

    /**
     * A convenience method for parsing and automatically producing error messages.
     *
     * @param target Either an instance or a class
     * @param args   The arguments you want to parse and populate
     * @return The list of arguments that were not consumed
     */
    public static List<String> parseOrExit(Object target, String[] args) {
        try {
            return parse(target, args);
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            Args.usage(target);
            System.exit(1);
            throw e;
        }
    }

    /**
     * Parse a set of arguments and populate the target with the appropriate values.
     *
     * @param target Either an instance or a class
     * @param args   The arguments you want to parse and populate
     * @return The list of arguments that were not consumed
     */
    public static List<String> parse(Object target, String[] args) {
        List<String> arguments = new ArrayList<String>();
        arguments.addAll(Arrays.asList(args));
        Class<?> clazz;
        if (target instanceof Class) {
            clazz = (Class) target;
        } else {
            clazz = target.getClass();
            try {
                BeanInfo info = Introspector.getBeanInfo(clazz);
                for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
                    processProperty(target, pd, arguments);
                }
            } catch (IntrospectionException e) {
                // If its not a JavaBean we ignore it
            }
        }

        // Check fields of 'target' class and its superclasses
      for (Class<?> currentClazz = clazz; currentClazz != null;  currentClazz = currentClazz.getSuperclass()) {
            for (Field field : currentClazz.getDeclaredFields()) {
                processField(target, field, arguments);
            }
        }

        for (String argument : arguments) {
            if (argument.startsWith("-")) {
                throw new IllegalArgumentException("Invalid argument: " + argument);
            }
        }
        return arguments;
    }

    private static void processField(Object target, Field field, List<String> arguments) {
        Argument argument = field.getAnnotation(Argument.class);
        if (argument != null) {
            boolean set = false;
            for (Iterator<String> i = arguments.iterator(); i.hasNext();) {
                String arg = i.next();
                String prefix = argument.prefix();
                String delimiter = argument.delimiter();
                if (arg.startsWith(prefix)) {
                    Object value;
                    String name = getName(argument, field);
                    String alias = getAlias(argument);
                    arg = arg.substring(prefix.length());
                    Class<?> type = field.getType();
                    if (arg.equals(name) || (alias != null && arg.equals(alias))) {
                        i.remove();
                        value = consumeArgumentValue(type, argument, i);
                        if (!set) {
                            setField(type, field, target, value, delimiter);
                        } else {
                            addArgument(type, field, target, value, delimiter);
                        }
                        set = true;
                    }
                    if (set && !type.isArray()) break;
                }
            }
            if (!set && argument.required()) {
                String name = getName(argument,  field);
                throw new IllegalArgumentException("You must set argument " + name);
            }
        }
    }

    private static void addArgument(Class type, Field field, Object target, Object value, String delimiter) {
        try {
            Object[] os = (Object[]) field.get(target);
            Object[] vs = (Object[]) getValue(type, value, delimiter);
            Object[] s = (Object[]) Array.newInstance(type.getComponentType(), os.length + vs.length);
            System.arraycopy(os, 0, s, 0, os.length);
            System.arraycopy(vs, 0, s, os.length, vs.length);
            field.set(target, s);
        } catch (IllegalAccessException iae) {
            throw new IllegalArgumentException("Could not set field " + field, iae);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Could not find constructor in class " + type.getName() + " that takes a string", e);
        }
    }

    private static void addPropertyArgument(Class type, PropertyDescriptor property, Object target, Object value, String delimiter) {
        try {
            Object[] os = (Object[]) property.getReadMethod().invoke(target);
            Object[] vs = (Object[]) getValue(type, value, delimiter);
            Object[] s = (Object[]) Array.newInstance(type.getComponentType(), os.length + vs.length);
            System.arraycopy(os, 0, s, 0, os.length);
            System.arraycopy(vs, 0, s, os.length, vs.length);
            property.getWriteMethod().invoke(target, (Object) s);
        } catch (IllegalAccessException iae) {
            throw new IllegalArgumentException("Could not set property " + property, iae);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Could not find constructor in class " + type.getName() + " that takes a string", e);
        } catch (InvocationTargetException e) {
            throw new IllegalArgumentException("Failed to validate argument " + value + " for " + property);
        }
    }

    private static void processProperty(Object target, PropertyDescriptor property, List<String> arguments) {
        Method writeMethod = property.getWriteMethod();
        if (writeMethod != null) {
            Argument argument = writeMethod.getAnnotation(Argument.class);
            if (argument != null) {
                boolean set = false;
                for (Iterator<String> i = arguments.iterator(); i.hasNext();) {
                    String arg = i.next();
                    String prefix = argument.prefix();
                    String delimiter = argument.delimiter();
                    if (arg.startsWith(prefix)) {
                        Object value;
                        String name = getName(argument, property);
                        String alias = getAlias(argument);
                        arg = arg.substring(prefix.length());
                        Class<?> type = property.getPropertyType();
                        if (arg.equals(name) || (alias != null && arg.equals(alias))) {
                            i.remove();
                            value = consumeArgumentValue(type, argument, i);
                            if (!set) {
                                setProperty(type, property, target, value, delimiter);
                            } else {
                                addPropertyArgument(type, property, target, value, delimiter);
                            }
                            set = true;
                        }
                        if (set && !type.isArray()) break;
                    }
                }
                if (!set && argument.required()) {
                    String name = getName(argument,  property);
                    throw new IllegalArgumentException("You must set argument " + name);
                }
            }
        }
    }

    /**
     * Generate usage information based on the target annotations.
     *
     * @param target An instance or class.
     */
    public static void usage(Object target) {
        usage(System.err, target);
    }

    /**
     * Generate usage information based on the target annotations.
     *
     * @param errStream A {@link java.io.PrintStream} to print the usage information to.
     * @param target An instance or class.
     */
    public static void usage(PrintStream errStream, Object target) {
        Class<?> clazz;
        if (target instanceof Class) {
            clazz = (Class) target;
        } else {
            clazz = target.getClass();
        }
        errStream.println("Usage: " + clazz.getName());
        for (Class<?> currentClazz = clazz; currentClazz != null;  currentClazz = currentClazz.getSuperclass()) {
          for (Field field : currentClazz.getDeclaredFields()) {
            fieldUsage(errStream, target, field);
          }
        }
        try {
            BeanInfo info = Introspector.getBeanInfo(clazz);
            for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
                propertyUsage(errStream, target, pd);
            }
        } catch (IntrospectionException e) {
            // If its not a JavaBean we ignore it
        }
    }

    private static void fieldUsage(PrintStream errStream, Object target, Field field) {
        Argument argument = field.getAnnotation(Argument.class);
        if (argument != null) {
            String name = getName(argument, field);
            String alias = getAlias(argument);
            String prefix = argument.prefix();
            String delimiter = argument.delimiter();
            String description = argument.description();
            makeAccessible(field);
            try {
                Object defaultValue = field.get(target);
                Class<?> type = field.getType();
                propertyUsage(errStream, prefix, name, alias, type, delimiter, description, defaultValue);
            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException("Could not use thie field " + field + " as an argument field", e);
            }
        }
    }

    private static void propertyUsage(PrintStream errStream, Object target, PropertyDescriptor field) {
        Method writeMethod = field.getWriteMethod();
        if (writeMethod != null) {
            Argument argument = writeMethod.getAnnotation(Argument.class);
            if (argument != null) {
                String name = getName(argument, field);
                String alias = getAlias(argument);
                String prefix = argument.prefix();
                String delimiter = argument.delimiter();
                String description = argument.description();
                try {
                    Method readMethod = field.getReadMethod();
                    Object defaultValue;
                    if (readMethod == null) {
                        defaultValue = null;
                    } else {
                        defaultValue = readMethod.invoke(target, (Object[]) null);
                    }
                    Class<?> type = field.getPropertyType();
                    propertyUsage(errStream, prefix, name, alias, type, delimiter, description, defaultValue);
                } catch (IllegalAccessException e) {
                    throw new IllegalArgumentException("Could not use thie field " + field + " as an argument field", e);
                } catch (InvocationTargetException e) {
                    throw new IllegalArgumentException("Could not get default value for " + field, e);
                }
            }
        }

    }

    private static void propertyUsage(PrintStream errStream, String prefix, String name, String alias, Class<?> type, String delimiter, String description, Object defaultValue) {
        StringBuilder sb = new StringBuilder("  ");
        sb.append(prefix);
        sb.append(name);
        if (alias != null) {
            sb.append(" (");
            sb.append(prefix);
            sb.append(alias);
            sb.append(")");
        }
        if (type == Boolean.TYPE || type == Boolean.class) {
            sb.append(" [flag] ");
            sb.append(description);
        } else {
            sb.append(" [");
            if (type.isArray()) {
                String typeName = getTypeName(type.getComponentType());
                sb.append(typeName);
                sb.append("[");
                sb.append(delimiter);
                sb.append("]");
            } else {
                String typeName = getTypeName(type);
                sb.append(typeName);
            }
            sb.append("] ");
            sb.append(description);
            if (defaultValue != null) {
                sb.append(" (");
                if (type.isArray()) {
                    List<Object> list = new ArrayList<Object>();
                    int len = Array.getLength(defaultValue);
                    for (int i = 0; i < len; i++) {
                        list.add(Array.get(defaultValue, i));
                    }
                    sb.append(list);
                } else {
                    sb.append(defaultValue);
                }
                sb.append(")");
            }

        }
        errStream.println(sb);
    }

    private static String getTypeName(Class<?> type) {
        String typeName = type.getName();
        int beginIndex = typeName.lastIndexOf(".");
        typeName = typeName.substring(beginIndex + 1);
        return typeName;
    }

    static String getName(Argument argument, PropertyDescriptor property) {
        String name = argument.value();
        if (name.equals("")) {
            name = property.getName();
        }
        return name;

    }

    private static Object consumeArgumentValue(Class<?> type, Argument argument, Iterator<String> i) {
        Object value;
        if (type == Boolean.TYPE || type == Boolean.class) {
            value = true;
        } else {
            if (i.hasNext()) {
                value = i.next();
                i.remove();
            } else {
                throw new IllegalArgumentException("Must have a value for non-boolean argument " + argument.value());
            }
        }
        return value;
    }

    static void setProperty(Class<?> type, PropertyDescriptor property, Object target, Object value, String delimiter) {
        try {
            value = getValue(type, value, delimiter);
            property.getWriteMethod().invoke(target, value);
        } catch (IllegalAccessException iae) {
            throw new IllegalArgumentException("Could not set property " + property, iae);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Could not find constructor in class " + type.getName() + " that takes a string", e);
        } catch (InvocationTargetException e) {
            throw new IllegalArgumentException("Failed to validate argument " + value + " for " + property);
        }
    }

    static String getAlias(Argument argument) {
        String alias = argument.alias();
        if (alias.equals("")) {
            alias = null;
        }
        return alias;
    }

    static String getName(Argument argument, Field field) {
        String name = argument.value();
        if (name.equals("")) {
            name = field.getName();
        }
        return name;
    }

    static void setField(Class<?> type, Field field, Object target, Object value, String delimiter) {
        makeAccessible(field);
        try {
            value = getValue(type, value, delimiter);
            field.set(target, value);
        } catch (IllegalAccessException iae) {
            throw new IllegalArgumentException("Could not set field " + field, iae);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Could not find constructor in class " + type.getName() + " that takes a string", e);
        }
    }

    private static Object getValue(Class<?> type, Object value, String delimiter) throws NoSuchMethodException {
        if (type != String.class && type != Boolean.class && type != Boolean.TYPE) {
            if (type.isArray()) {
                String string = (String) value;
                String[] strings = string.split(delimiter);
                type = type.getComponentType();
                if (type == String.class) {
                    value = strings;
                } else {
                    Object[] array = (Object[]) Array.newInstance(type, strings.length);
                    for (int i = 0; i < array.length; i++) {
                        array[i] = createValue(type, strings[i]);
                    }
                    value = array;
                }
            } else {
                value = createValue(type, value);
            }
        }
        return value;
    }

    private static Object createValue(Class<?> type, Object value) throws NoSuchMethodException {
        Constructor<?> init = type.getDeclaredConstructor(String.class);
        try {
            value = init.newInstance(value);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to convert " + value + " to type " + type.getName(), e);
        }
        return value;
    }

    private static void makeAccessible(AccessibleObject ao) {
        if (ao instanceof Member) {
            Member member = (Member) ao;
            if (!Modifier.isPublic(member.getModifiers())) {
                ao.setAccessible(true);
            }
        }
    }

}
