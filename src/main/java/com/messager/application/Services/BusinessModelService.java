package com.messager.application.Services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.messager.application.Dao.BusinessModelDao;
import com.messager.application.Dao.BusinessModelReadDao;
import com.messager.application.Models.BusinessModel;
import com.messager.application.Models.Child1;
import com.messager.application.Models.Child2;
import com.messager.application.Models.Grandchild1;
import com.messager.application.Models.ParentEntity;
import com.messager.application.Models.dto.BusinessModelCreateRequest;
import com.messager.application.Models.dto.BusinessModelGraph;

@Service
public class BusinessModelService {
  private final ParentService parentService;
  private final Child1Service child1Service;
  private final Child2Service child2Service;
  private final Grandchild1Service grandchild1Service;
  private final BusinessModelDao businessModelDao;
  private final BusinessModelReadDao businessModelReadDao;

  public BusinessModelService(ParentService parentService, Child1Service child1Service, Child2Service child2Service,
      Grandchild1Service grandchild1Service, BusinessModelDao businessModelDao, BusinessModelReadDao businessModelReadDao) {
    this.parentService = parentService;
    this.child1Service = child1Service;
    this.child2Service = child2Service;
    this.grandchild1Service = grandchild1Service;
    this.businessModelDao = businessModelDao;
    this.businessModelReadDao = businessModelReadDao;
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
  public BusinessModelGraph create(BusinessModelCreateRequest req) {
    // No explicit locks needed; child creation is idempotent via UPSERT
    ParentEntity parent = parentService.getParentOrThrow(req.getParentId());

    Child1 child1 = this.getOrCreateChild1(parent.getId(), req.getChild1Name());
    Child2 child2 = this.getOrCreateChild2(parent.getId(), req.getChild2Name());
    Grandchild1 grandchild1 = this.getOrCreateGrandchild1(child1.getId(), req.getGrandchild1Name());

  BusinessModel created = businessModelDao.createBusinessModel(req.getName(), req.getType(), parent.getId(),
    child1.getId(), child2.getId(),
    grandchild1.getId(), req.getRequestSequence());

  // Final hydrate read to return fully joined references
  return businessModelReadDao.findGraphById(created.getId());
  }

  private Grandchild1 getOrCreateGrandchild1(Long child1Id, String name) {
    // Use UPSERT to make creation idempotent and thread-safe
    return grandchild1Service.upsertGetOrCreate(child1Id, name);
  }

  private Child1 getOrCreateChild1(Long parentId, String name) {
    // Use UPSERT to make creation idempotent and thread-safe
    return child1Service.upsertGetOrCreate(parentId, name);
  }

  private Child2 getOrCreateChild2(Long parentId, String name) {
    // Use UPSERT to make creation idempotent and thread-safe
    return child2Service.upsertGetOrCreate(parentId, name);
  }
}
