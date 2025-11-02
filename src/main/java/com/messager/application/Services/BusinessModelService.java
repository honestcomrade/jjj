package com.messager.application.Services;

import org.springframework.dao.DataIntegrityViolationException;
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

    return businessModelDao.createBusinessModel(req.getName(), req.getType(), parent.getId(), child1.getId(),
        child2.getId(),
        grandchild1.getId(), req.getRequestSequence());
  }

  private Grandchild1 getOrCreateGrandchild1(Long child1Id, String name) {
    // Insert first; on unique violation fall back to read existing
    Grandchild1 toInsert = new Grandchild1();
    toInsert.setChild1Id(child1Id);
    toInsert.setName(name);
    try {
      return grandchild1Service.save(toInsert);
    } catch (DataIntegrityViolationException ex) {
      // Another thread created it first; attempt to read the committed row
      try {
        return grandchild1Service.getByChildAndName(child1Id, name);
      } catch (DataNotFoundException nf) {
        // If not yet visible, propagate original violation; controller/test may retry
        throw ex;
      }
    }
  }

  private Child1 getOrCreateChild1(Long parentId, String name) {
    Child1 toInsert = new Child1();
    toInsert.setParentId(parentId);
    toInsert.setName(name);
    try {
      return child1Service.save(toInsert);
    } catch (DataIntegrityViolationException ex) {
      try {
        return child1Service.getByParentAndName(parentId, name);
      } catch (DataNotFoundException nf) {
        throw ex;
      }
    }
  }

  private Child2 getOrCreateChild2(Long parentId, String name) {
    Child2 toInsert = new Child2();
    toInsert.setParentId(parentId);
    toInsert.setName(name);
    try {
      return child2Service.save(toInsert);
    } catch (DataIntegrityViolationException ex) {
      try {
        return child2Service.getByParentAndName(parentId, name);
      } catch (DataNotFoundException nf) {
        throw ex;
      }
    }
  }
}
