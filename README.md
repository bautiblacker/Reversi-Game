Descomprimir el zip y navegar hasta la carpeta donde se encuentran los datos.

El programa se ejecuta de la siguente manera: 
```
$ java -jar TPE_EDA.jar -size [n] -ai [m] -mode [time|depth] -param [k] -prune [on|off] -load [file]
```

Donde:
1. -size [n]: determina el tamaño del tablero. “n” debe ser un número entero.
2. -ai [m]: determina el rol de la AI. “m” es un número que significa:

   0: no hay AI. Juegan dos jugadores humanos
   
   1: AI mueve primero
   
   2: AI mueve segundo
   

3. -mode [time|depth]: determina si el algoritmo minimax se corre por tiempo o por
profundidad.
4. -param [k]: acompaña al parámetro anterior. En el caso de “time”, k deben ser los
segundos. En el caso de “depth”, debe ser la profundidad del árbol.
5. -prune [on|off]: activa o desactiva la poda.
6. -load [file]: opcional. Este parámetro carga la partida previamente guardada.
“file” debe ser una referencia al archivo donde se guardó el tablero.
