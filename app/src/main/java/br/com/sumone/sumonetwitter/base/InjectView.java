package br.com.sumone.sumonetwitter.base;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Tiago on 04/08/2015.
 * Anotação para injetar views atomaticamente
 */
@Target(ElementType.FIELD)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface InjectView {
    int value();
}
