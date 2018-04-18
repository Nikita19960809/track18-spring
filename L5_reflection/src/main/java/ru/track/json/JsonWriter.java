package ru.track.json;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * сериализатор в json
 */
public class JsonWriter {


    public static String toJsonSafe(@Nullable Object object) {
        String str = "";
        try {
           str =  toJson(object);
        }catch (IllegalAccessException e) {
            throw new RuntimeException();
        }
        return str;
    }


    // В зависимости от типа объекта вызывает соответствующий способ сериализации
    public static String toJson(@Nullable Object object) throws IllegalAccessException {
        if (object == null) {
            return "null";
        }

        Class clazz = object.getClass();

        if (clazz.equals(String.class)
                || clazz.equals(Character.class)
                || clazz.isEnum()
                ) {
            System.out.println();
            return String.format("\"%s\"", object);
        }

        if (object instanceof Boolean || object instanceof Number) {
            System.out.println(object);
            return object.toString();
        }

        if (clazz.isArray()) {

            return toJsonArray(object);
        }

        if (object instanceof Collection) {
            return toJsonCollection(object);
        }

        if (object instanceof Map) {
            System.out.println("I am map");
            return toJsonMap(object);
        }

        return toJsonObject(object);
    }

    /**
     * Используется вспомогательный класс {@link Array}, чтобы работать с object instanceof Array
     * <p>
     * То есть чтобы получить i-й элемент массива, нужно вызвать {@link Array#get(Object, int)}, где i - это число от 0 до {@link Array#getLength(Object)}
     *
     * @param object - который Class.isArray()
     * @return строковое представление массива: [item1, item2, ...]
     */
    @NotNull
    private static String toJsonArray(@NotNull Object object) throws IllegalAccessException{

        int length = Array.getLength(object);

        StringBuilder str = new StringBuilder();
        str.append("[");
        for (int i = 0; i < length;i++) {
            str.append(toJson(Array.get(object, i)));
            str.append(",");
        }
        str.delete(str.length() - 1,str.length() );

        str.append("]");

        return str.toString();
    }

    /**
     * В 1 шаг приводится к Collection
     */
    @NotNull
    private static String toJsonCollection(@NotNull Object object) throws IllegalAccessException{
        Collection collection = (Collection) object;
        return toJsonArray(collection.toArray());
    }

    /**
     * Сконвертить мап в json. Формат:
     * {key:value, key:value,..}
     * <p>
     * На входе мы проверили, что это Map, можно просто кастовать Map map = (Map) object;
     */
    @NotNull
    private static String toJsonMap(@NotNull Object object) throws IllegalAccessException{
        Map<String,String> map = (Map)object;
        String r = String.join(",", map.entrySet().stream()
                // .map(e -> toJson(e.getValue()))
                .map(e -> String.format("\"%s\":%s", e.getKey(), toJsonSafe(e.getValue())))
                .collect(Collectors.toList())
        );

        return String.format("{%s}", r);

    }

    /**
     * 1) Чтобы распечатать объект, нужно знать его внутреннюю структуру, для этого нужно получить его Class-объект:
     * {@link Class} с помощью {@link Object#getClass()}
     * <p>
     * Получить поля класса можно с помощью {@link Class#getDeclaredFields()}
     * Приватные поля недоступны, нужно изменить в рантайм их accessibility: {@link Field#setAccessible(boolean)}
     * <p>
     * 2) Вторая часть задачи: {@link JsonNullable} и {@link SerializedTo}
     * Нужно проверить, что у класса/поля есть аннотация
     * <p>
     * {@link Class#getAnnotation(Class)} / {@link Field#getAnnotation(Class)}
     * и в зависимости от этого изменить поведение
     * <p>
     * NOTE: Удобно сложить все поля объекта в Map<String, String> то етсь {имя поля -> значение поля в json}
     * и воспользоваться методом {@link #formatObject(Map)}
     */
    @NotNull
    private static String toJsonObject(@NotNull Object object) throws IllegalAccessException {

        Class clazz = object.getClass();
        Map <String,String> map = new LinkedHashMap<>();
        Annotation annotation =clazz.getAnnotation(JsonNullable.class);
        Field[] fields = clazz.getDeclaredFields();


           for (int i = 0; i < fields.length;i++) {
                fields[i].setAccessible(true);
                Annotation annotation1 =fields[i].getAnnotation(SerializedTo.class);

                    Object value = fields[i].get(object);

                    if (annotation1 instanceof SerializedTo) {

                        if (annotation instanceof JsonNullable)
                            map.put(((SerializedTo) annotation1).value(), value != null ? toJson(value): null);

                        else if (value != null)
                            map.put(((SerializedTo) annotation1).value(), toJson(value));
                    } else {
                        if (annotation instanceof JsonNullable)
                            map.put(fields[i].getName(),value != null ? toJson(value) : null);

                        else if (value != null)
                            map.put(fields[i].getName(), toJson(value));
                        }
            }

    return formatObject(map);

    }


    /**
     * Вспомогательный метод для форматирования содержимого Map<K, V>
     *
     * @param map
     * @return "{key:value, key:value,..}"
     */
    @NotNull
    private static String formatObject(@NotNull Map<String, String> map) {
        String r = String.join(",", map.entrySet().stream()
               // .map(e -> toJson(e.getValue()))
                .map(e -> String.format("\"%s\":%s", e.getKey(), e.getValue()))
                .collect(Collectors.toList())
        );

        return String.format("{%s}", r);
    }

}
