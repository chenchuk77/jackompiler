# Jackompiler
### Jack compiler

This project is a compiler for the [Jack](https://drive.google.com/file/d/1rbHGZV8AK4UalmdJyivgt0fpPiD1Q6Vk/view)
 language.

### Jack
* A simple OOP, Java-like language.
* Object-based, no inheritance.
* Multi-purpose.

### Building the compiler
```
$ mvn clean package
```
### Compile Jack code
The compiler can accept file/directory for compilation.
for each filename.jack it will create 2 files:
* filename.xml
  * An XML representation of the parser tokens.
* filename.vm
  * the compiled vm code.
```
$ ./jackompile.sh example.jack
$ ./jackompile.sh example-folder # compiles all jack files
```
***



## TODO
1. fix CORS
2. fix Rect.asm example on javascript page
3. add platform to run
