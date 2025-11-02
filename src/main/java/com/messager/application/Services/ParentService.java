package com.messager.application.Services;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.messager.application.Dao.ParentDao;
import com.messager.application.Models.ParentEntity;

@Service
public class ParentService {
  private final ParentDao parentDao;

  public ParentService(ParentDao parentDao) {
    this.parentDao = parentDao;
  }

  public ParentEntity getParentOrThrow(Long parentId) {
    ParentEntity parent = parentDao.findById(parentId);
    if (parent == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parent does not exist: " + parentId);
    }
    return parent;
  }
}
