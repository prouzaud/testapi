package com.xxx.test.api.nrt.apinrt.util.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value="/test")
public class TestController {

    @GetMapping("/get")
    public String getWithBody() {
        return "{\"status\":\"success\"}";
    }

    @PostMapping(value="/post")
    public ResponseEntity<String> post(@RequestBody CallRequest callRequest) throws InterruptedException {
        return processRequest(callRequest);
    }

    @PutMapping("/put")
    public ResponseEntity<String> put(@RequestBody CallRequest callRequest) throws InterruptedException {
        return processRequest(callRequest);
    }

    @DeleteMapping("/delete")
    public String delete() {
        return "{\"status\":\"success\"}";
    }

    private static ResponseEntity<String> processRequest(CallRequest callRequest) throws InterruptedException {
        Thread.sleep(callRequest.getExpectedDuration());
        return ResponseEntity
                .status(callRequest.getExpectedHttpStatus())
                .body(callRequest.getExpectedPayload());
    }
}
