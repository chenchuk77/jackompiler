package hasm;

import java.util.concurrent.atomic.AtomicLong;

import hack.core.Assembler;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
public class AssemblerController {

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
        return "hi";
    }
}
