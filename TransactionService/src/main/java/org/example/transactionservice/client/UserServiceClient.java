package org.example.transactionservice.client;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value="UserServiceClient",url = "http://localhost:8001")
public interface UserServiceClient {
    @GetMapping("/user")
    ObjectNode getUser(@RequestParam("phoneNo") String phoneNo, @RequestHeader("Authorization") String authValue);
}
