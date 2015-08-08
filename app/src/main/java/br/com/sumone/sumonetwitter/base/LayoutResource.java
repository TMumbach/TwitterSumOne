package br.com.sumone.sumonetwitter.base;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Tiago on 04/08/2015.
 *
 * Anotação para definir as configurações da activity
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface LayoutResource {
    int contentView();
    int toolbar() default -1;
    boolean displayHomeAsUp() default true;
}
