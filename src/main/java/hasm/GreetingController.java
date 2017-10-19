package hasm;

import java.util.concurrent.atomic.AtomicLong;

import hack.core.Assembler;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
public class GreetingController {

    String userCode;

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @RequestMapping("/greeting")
    public Greeting greeting(@RequestParam(value="name", defaultValue="World") String name) {
        return new Greeting(counter.incrementAndGet(),
                String.format(template, name));
    }

    @RequestMapping(value = "/assemble",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    UserProgram assemble(@RequestParam("source") String source, @RequestParam("location") String location) {
        // your logic here

        UserProgram userProgram = new UserProgram();
        userProgram.setSource(source);
        Assembler assembler = new Assembler(source);
        //String hackCode = assembler.getHackCode();
        userProgram.setOutput(assembler.getHackCode());

        //return source + "0000000000\n1111111111\n0000111100001111";
        return userProgram;
    }
//    @RequestMapping(value = "/assemble",
//            method = RequestMethod.POST,
//            produces = MediaType.APPLICATION_JSON_VALUE)
//    public @ResponseBody
//    String assemble(@RequestParam("source") String source,@RequestParam("location") String location) {
//        // your logic here
//
//        SourceCode sourceCode = new SourceCode();
//        sourceCode.setSource(source + "0000000000\n1111111111\n0000111100001111");
//        //return source + "0000000000\n1111111111\n0000111100001111";
//        return source + "0000000000\n1111111111\n0000111100001111";
//    }

//    @GetMapping
//    @RequestMapping("/get")
//    public Map<String, String> sayHello() {
//        HashMap<String, String> map = new HashMap<>();
//        map.put("key", "value");
//        map.put("foo", "bar");
//        map.put("aa", "bb");
//        return map;
//    }

    @RequestMapping("/hi")
    public String hi() {
        return "hi dude";
    }
//    @RequestMapping(value = "/assemble", method = RequestMethod.POST, consumes = "text/plain")
//        public String someMethod(@RequestBody String postPayload) {
//            return postPayload;
//
//        }
//    }
//    @RequestMapping(value = "/", method = RequestMethod.POST)
//    public ResponseEntity<SourceCode> update(@RequestBody SourceCode sourceCode) {
//        if (sourceCode != null) {
//            sourceCode.setSource(sourceCode.getMiles() + 100);
//        }
//
//        // TODO: call persistence layer to update
//        return new ResponseEntity<Car>(car, HttpStatus.OK);
//


    @RequestMapping(value = "/")
    public ResponseEntity<UserProgram> get() {
        UserProgram sourceCode = new UserProgram();
        sourceCode.setOwner("someone");
        sourceCode.setSource("@100\nD=A\n@200\nD=D+A");
        return new ResponseEntity<UserProgram>(sourceCode, HttpStatus.OK);
    }

}
