package com.messager.application.Services;

import org.springframework.stereotype.Service;

import com.messager.application.DataNotFoundException;
import com.messager.application.Dao.Grandchild1Dao;
import com.messager.application.Models.Grandchild1;

@Service
public class Grandchild1Service {
  private final Grandchild1Dao grandchild1Dao;

  public Grandchild1Service(Grandchild1Dao grandchild1Dao) {
    this.grandchild1Dao = grandchild1Dao;
  }

  public Grandchild1 getByChildAndName(Long child1Id, String name) throws DataNotFoundException {
    return grandchild1Dao.findByChildAndName(child1Id, name);
  }

  public Grandchild1 save(Grandchild1 grandchild1) {
    return grandchild1Dao.insertStrict(grandchild1);
  }
}
