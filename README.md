## Hack Assembler
This project is an online assembler for the Hack machine language.
It takes native code (can copy/paste from example asm files), and return immediately
the result assembly binary representation. The binary output can be saved as *.hack
file and can be loaded to a Hack CPU Emulator.

* The asm code should be error free
* newlines are ignored
* comments are ignored // only single line comment, like this

#### Structure
* Frontend
  * HTML/Javascript for getting user code for assembling.
  * Javascript with JQuery used for posting machine language 
  instruction to a REST service via async AJAX call.
* Backend
  * Spring boot application listens on port 8090 via embedded tomcat.
  * posting asm code will reply with the assembled code in a binary representation.

### Installation
* Backend:
> `~/$ cd /var/www/html`

> `/var/www/html/$ git clone git@github.com:chenchuk77/hasmproj.git`

> `/var/www/html/$ cd hasmproj`

> `/var/www/html/hasmproj/$ mvn clean install`

* Frontend:
Setup apache web server to listen on www.mysite.com, change the
server address in main.js to point to the backend address.

### Running the project
The project shipped with 2 scripts :
* `./start-backend.sh`
  * Starts the spring REST service that provides the assembler functionality.
* `./stop-backend.sh`
  * Stops the spring rest service

> `/var/www/html/hasmproj/$ ./start-backend.sh`

After starting the backend, it can process requests. open a web browser
 to http://www.mysite.com
 
### Examples
* /asm-examples folder you will find some source files. 
* http://hasm.devopskb.net/asm-examples/Add.asm
* http://hasm.devopskb.net/

### TODO
*  make flexible config, currently hardcoded in main.js file.
*  split project into core module and web module, add maven dependencies. 
*  ignore multiline comments