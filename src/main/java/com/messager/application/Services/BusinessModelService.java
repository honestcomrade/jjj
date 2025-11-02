package com.messager.application.Services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

  /**
   * Creates a BusinessModel and its related child entities.
   * 
   * Uses pessimistic locking (SELECT ... FOR UPDATE) on the parent entity to serialize
   * concurrent operations under the same parent ID. This prevents race conditions when
   * multiple threads try to create child entities with the same names simultaneously.
   * 
   * How it works:
   * 1. Lock the parent row at the start of the transaction
   * 2. Other threads attempting to lock the same parent will wait
   * 3. Each thread processes sequentially, seeing previously created child entities
   * 4. No duplicate children are created due to serialization by parent ID
   */
  @Transactional
  public BusinessModel create(BusinessModelCreateRequest req) {
    // Lock the parent row to serialize all operations under this parent ID
    ParentEntity parent = parentService.getParentOrThrowWithLock(req.getParentId());

    Child1 child1 = this.getOrCreateChild1(parent.getId(), req.getChild1Name());
    Child2 child2 = this.getOrCreateChild2(parent.getId(), req.getChild2Name());
    Grandchild1 grandchild1 = this.getOrCreateGrandchild1(child1.getId(), req.getGrandchild1Name());

    return businessModelDao.createBusinessModel(req.getName(), req.getType(), parent.getId(), child1.getId(),
        child2.getId(),
        grandchild1.getId(), req.getRequestSequence());
  }

  private Grandchild1 getOrCreateGrandchild1(Long child1Id, String name) {
    try {
      return grandchild1Service.getByChildAndName(child1Id, name);
    } catch (DataNotFoundException ex) {
      Grandchild1 grandchild1 = new Grandchild1();
      grandchild1.setChild1Id(child1Id);
      grandchild1.setName(name);
      return grandchild1Service.save(grandchild1);
    }
  }

  private Child1 getOrCreateChild1(Long parentId, String name) {
    try {
      return child1Service.getByParentAndName(parentId, name);
    } catch (DataNotFoundException ex) {
      Child1 child1 = new Child1();
      child1.setParentId(parentId);
      child1.setName(name);
      return child1Service.save(child1);
    }
  }

  private Child2 getOrCreateChild2(Long parentId, String name) {
    try {
      return child2Service.getByParentAndName(parentId, name);
    } catch (DataNotFoundException ex) {
      Child2 child2 = new Child2();
      child2.setParentId(parentId);
      child2.setName(name);
      return child2Service.save(child2);
    }
  }
}
