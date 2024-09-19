package czescjestemadas.adaspluginlib.database;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DBField
{
	String name();

	String type();

	String PRIMARY_KEY = "integer primary key autoincrement";
}
