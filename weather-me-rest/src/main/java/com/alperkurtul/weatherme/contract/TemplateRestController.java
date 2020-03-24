package com.alperkurtul.weatherme.contract;

import com.alperkurtul.weatherme.bean.TemplateResponse;
import com.alperkurtul.weatherme.bean.TemplateRequest;
import org.springframework.web.bind.annotation.*;

//@Validated
@RequestMapping(value = "/sample/v1")
@CrossOrigin("*")
public interface TemplateRestController {

    @GetMapping(value = "/getreq/{key}")
    TemplateResponse getTemplate(@PathVariable("key") String key) throws Exception;

    @PostMapping(value = "/putreq")
    TemplateResponse setTemplate(@RequestBody TemplateRequest request) throws Exception;

}
