package org.example.annotations;
<<<<<<< HEAD

=======
>>>>>>> cffc1cf01283d007f34d324665eef2bc27b5ae50
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LogDatasourceError {}