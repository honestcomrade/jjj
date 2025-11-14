package com.messager.application.Services;

import org.springframework.stereotype.Service;

import com.messager.application.Dao.Child1Dao;
import com.messager.application.Models.Child1;

@Service
public class Child1Service {
  private final Child1Dao child1Dao;

  public Child1Service(Child1Dao child1Dao) {
    this.child1Dao = child1Dao;
  }

  public Child1 getByParentAndName(Long parentId, String name) {
    return child1Dao.findByParentAndName(parentId, name);
  }

  public Child1 save(Child1 child1) {
    return child1Dao.save(child1);
  }
}
