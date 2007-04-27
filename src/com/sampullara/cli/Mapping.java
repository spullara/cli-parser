package com.sampullara.cli;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by IntelliJ IDEA.
 * User: sam
 * Date: May 4, 2006
 * Time: 6:28:53 PM
 * To change this template use File | Settings | File Templates.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface Mapping {
    /**
     * Textual command
     */
    String name();

    /**
     * Classname
     */
    Class command();
}
