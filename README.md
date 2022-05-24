# tc2037 - lexer

Lexer creado en clojure basado en automatas finitos deterministas

## funcionamiento

A través de un mapa de estados y trancisiones (automata finito),
se obtiene un _lexer_, que puede interpretar un sencillo _lenguaje_
compuesto por asignaciones y expresiones.

El modulo _html_ nos permite generar archivos html con resaltamiento
de la sintaxis del lenguaje.

![image](https://user-images.githubusercontent.com/40474768/169931115-48772618-0c9e-4db1-8d69-28bc4110dc22.png)

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
