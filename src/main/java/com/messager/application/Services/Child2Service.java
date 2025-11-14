package com.messager.application.Services;

import org.springframework.stereotype.Service;

import com.messager.application.Dao.Child2Dao;
import com.messager.application.Models.Child2;

@Service
public class Child2Service {
  private final Child2Dao child2Dao;

  public Child2Service(Child2Dao child2Dao) {
    this.child2Dao = child2Dao;
  }

  public Child2 getByParentAndName(Long parentId, String name) {
    return child2Dao.findByParentAndName(parentId, name);
  }

  public Child2 save(Child2 child2) {
    return child2Dao.save(child2);
  }
}
