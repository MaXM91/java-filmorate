package ru.yandex.practicum.filmorate.validators;

import javax.validation.Constraint;
import javax.validation.constraints.Past;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME) @Constraint(validatedBy = MinimumDateValidator.class) @Past public @interface MinimumDate {
    String message() default "Date must not be before {value}";

    Class<?>[] groups() default {};

    Class<?>[] payload() default {};

    String value() default "1895-12-28";                                       // Минимальное значение даты
}

/*
@Documented – указывает, что помеченная таким образом аннотация должна быть добавлена в javadoc поля/метода.

@Target – указывает, что именно мы можем пометить этой аннотацией.

ElementType.PACKAGE – только для пакетов;
ElementType.TYPE – только для классов;
ElementType.CONSTRUCTOR – только для конструкторов;
ElementType.METHOD – только для методов;
ElementType.FIELD – только для атрибутов(переменных) класса;
ElementType.PARAMATER – только для параметров метода;
ElementType.LOCAL_VARIABLE – только для локальных переменных.

@Retention – позволяет указать жизненный цикл аннотации: будет она присутствовать только в исходном коде, в
скомпилированном файле, или она будет также видна и в процессе выполнения.

RetentionPolicy.CLASS – будет присутствовать в скомпилированном файле;
RetentionPolicy.RUNTIME – будет присутствовать только в момент выполнения;
RetentionPolicy.SOURCE – будет присутствовать только в исходном коде.

@Constraint – список реализаций данного интерфейса


message – возвращает ключ по умолчанию для создания сообщений об ошибках, это позволяет нам использовать интерполяцию
 сообщений
groups - позволяет нам указывать группы проверки для наших ограничений
 */