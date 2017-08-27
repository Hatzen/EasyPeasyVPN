package de.hartz.vpn.Utilities;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by kaiha on 09.06.2017.
 * An annotation that indicates that a specific method must be run under the operating system windows.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Windows {
}