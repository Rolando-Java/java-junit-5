package org.bardales.junit.ejemplos.util;

public class AllStatic {

    public static String formatName(String name) {
        return String.format("Hello %s, I've got a good day.", name.toUpperCase());
    }

    public static String sayHelloStatic() {
        return "Static hello";
    }

    public static String sendGreeting() {
       String phrase = sayHelloStatic();
       String phraseFormated = formatName(phrase);
       return phraseFormated;
    }

}
