package czescjestemadas.adaspluginlib.command.node.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(CompleterNodes.class)
public @interface NodeCompleter
{
    String value();

    boolean ignoreCase() default true;
}
