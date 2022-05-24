# tc2037 - lexer

Lexer creado en clojure basado en automatas finitos deterministas

## funcionamiento

A través de un mapa de estados y trancisiones (automata finito),
se obtiene un _lexer_, que puede interpretar un sencillo _lenguaje_
compuesto por asignaciones y expresiones.

El modulo _html_ nos permite generar archivos html con resaltamiento
de la sintaxis del lenguaje.

![image](https://user-images.githubusercontent.com/40474768/169939311-cd6b2a50-7028-4b43-a6f8-a63b8b3becc0.png)

## requeriminetos para desarollar

- [lein](https://leiningen.org/#install)

```sh
# correr projecto
$ lein run
# compilar .jar
$ lein uberjar
```

## utilización

Descarga el .jar y correlo, solo se necesita java
```sh
$ java -jar lexer.jar
```
