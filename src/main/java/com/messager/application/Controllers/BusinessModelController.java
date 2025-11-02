package com.messager.application.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.messager.application.Models.dto.BusinessModelGraph;
import com.messager.application.Models.dto.BusinessModelCreateRequest;
import com.messager.application.Services.BusinessModelService;

@RestController
public class BusinessModelController {

  private final BusinessModelService service;

  @Autowired
  public BusinessModelController(BusinessModelService service) {
    this.service = service;
  }

  @PostMapping("/business-models")
  public BusinessModelGraph create(@RequestBody BusinessModelCreateRequest req) {
    return service.create(req);
  }
}
