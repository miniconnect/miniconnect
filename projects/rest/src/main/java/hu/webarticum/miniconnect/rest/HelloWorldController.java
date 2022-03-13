package hu.webarticum.miniconnect.rest;

import java.util.Map;

import javax.validation.constraints.Pattern;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.QueryValue;

@Controller("/tables")
public class HelloWorldController {
    
    private final TestBean testBean;
    
    
    public HelloWorldController(TestBean testBean) {
        this.testBean = testBean;
    }
    

    @Get("/{tableName}")
    public Map<String, String> index(
            
            @QueryValue("tableName")
            @Pattern(regexp = "a.*")
            String tableName
            
            ) {
        return Map.of(
                "message", testBean.message(),
                "table", tableName);
    }
    
}
