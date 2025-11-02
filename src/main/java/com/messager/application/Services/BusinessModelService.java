package com.messager.application.Services;

import org.springframework.stereotype.Service;

import com.messager.application.DataNotFoundException;
import com.messager.application.Dao.BusinessModelDao;
import com.messager.application.Models.BusinessModel;
import com.messager.application.Models.Child1;
import com.messager.application.Models.Child2;
import com.messager.application.Models.Grandchild1;
import com.messager.application.Models.ParentEntity;
import com.messager.application.Models.dto.BusinessModelCreateRequest;

@Service
public class BusinessModelService {
  private final ParentService parentService;
  private final Child1Service child1Service;
  private final Child2Service child2Service;
  private final Grandchild1Service grandchild1Service;
  private final BusinessModelDao businessModelDao;

  public BusinessModelService(ParentService parentService, Child1Service child1Service, Child2Service child2Service,
      Grandchild1Service grandchild1Service, BusinessModelDao businessModelDao) {
    this.parentService = parentService;
    this.child1Service = child1Service;
    this.child2Service = child2Service;
    this.grandchild1Service = grandchild1Service;
    this.businessModelDao = businessModelDao;
  }

  public BusinessModel create(BusinessModelCreateRequest req) {
    ParentEntity parent = parentService.getParentOrThrow(req.getParentId());

    Child1 child1 = this.getOrCreateChild1(parent.getId(), req.getChild1Name());
    Child2 child2 = this.getOrCreateChild2(parent.getId(), req.getChild2Name());
    Grandchild1 grandchild1 = this.getOrCreateGrandchild1(child1.getId(), req.getGrandchild1Name());

    return businessModelDao.insertStrict(req.getName(), req.getType(), parent.getId(), child1.getId(),
        child2.getId(),
        grandchild1.getId(), req.getRequestSequence());
  }

  private Grandchild1 getOrCreateGrandchild1(Long child1Id, String name) {
    try {
      return grandchild1Service.getByChildAndName(child1Id, name);
    } catch (DataNotFoundException ex) {
      try {
        Grandchild1 grandchild1 = new Grandchild1();
        grandchild1.setChild1Id(child1Id);
        grandchild1.setName(name);
        return grandchild1Service.save(grandchild1);
      } catch (org.springframework.dao.DuplicateKeyException dkEx) {
        // Race condition: another thread created it between our find and insert
        // Retry the find to get the entity created by the winning thread
        return grandchild1Service.getByChildAndName(child1Id, name);
      }
    }
  }

  private Child1 getOrCreateChild1(Long parentId, String name) {
    try {
      return child1Service.getByParentAndName(parentId, name);
    } catch (DataNotFoundException ex) {
      try {
        Child1 child1 = new Child1();
        child1.setParentId(parentId);
        child1.setName(name);
        return child1Service.save(child1);
      } catch (org.springframework.dao.DuplicateKeyException dkEx) {
        // Race condition: another thread created it between our find and insert
        // Retry the find to get the entity created by the winning thread
        return child1Service.getByParentAndName(parentId, name);
      }
    }
  }

  private Child2 getOrCreateChild2(Long parentId, String name) {
    try {
      return child2Service.getByParentAndName(parentId, name);
    } catch (DataNotFoundException ex) {
      try {
        Child2 child2 = new Child2();
        child2.setParentId(parentId);
        child2.setName(name);
        return child2Service.save(child2);
      } catch (org.springframework.dao.DuplicateKeyException dkEx) {
        // Race condition: another thread created it between our find and insert
        // Retry the find to get the entity created by the winning thread
        return child2Service.getByParentAndName(parentId, name);
      }
    }
  }
}
