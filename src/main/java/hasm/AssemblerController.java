package hasm;

import java.util.concurrent.atomic.AtomicLong;

import hack.core.Assembler;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
public class AssemblerController {

//    private static final String template = "Hello, %s!";
//    private final AtomicLong counter = new AtomicLong();

//    @RequestMapping("/greeting")
//    public Greeting greeting(@RequestParam(value="name", defaultValue="World") String name) {
//        return new Greeting(counter.incrementAndGet(),
//                String.format(template, name));
//    }

    @RequestMapping(value = "/assemble",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    UserProgram assemble(@RequestParam("source") String source, @RequestParam("location") String location) {

        UserProgram userProgram = new UserProgram();
        userProgram.setSource(source);
        Assembler assembler = new Assembler(source);
        userProgram.setOutput(assembler.getHackCode());
        return userProgram;
    }

    @RequestMapping("/hi")
    public String hi() {
        return "hi dude";
    }

//    @RequestMapping(value = "/")
//    public ResponseEntity<UserProgram> get() {
//        UserProgram sourceCode = new UserProgram();
//        sourceCode.setOwner("someone");
//        sourceCode.setSource("@100\nD=A\n@200\nD=D+A");
//        return new ResponseEntity<UserProgram>(sourceCode, HttpStatus.OK);
//    }

}
