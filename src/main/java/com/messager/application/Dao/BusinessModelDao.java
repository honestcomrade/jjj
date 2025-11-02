package com.messager.application.Dao;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.messager.application.Models.BusinessModel;
import com.messager.application.Models.dto.BusinessModelGraph;

@Repository
public class BusinessModelDao {
  private final JdbcTemplate jdbcTemplate;

  public BusinessModelDao(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  public BusinessModel createBusinessModel(String name, String type, Long parentId, Long child1Id, Long child2Id,
      Long grandchild1Id, Integer requestSequence)
      throws DataAccessException {
    String sql = "INSERT INTO business_models(name, type, parent_id, child1_id, child2_id, grandchild1_id, request_sequence) "
        +
        "VALUES (?, ?, ?, ?, ?, ?, ?) " +
        "RETURNING id, name, type, parent_id, child1_id, child2_id, grandchild1_id, request_sequence";
    return jdbcTemplate.queryForObject(sql, (rs, row) -> new BusinessModel(
        rs.getLong("id"), rs.getString("name"), rs.getString("type"),
        rs.getLong("parent_id"), rs.getLong("child1_id"), rs.getLong("child2_id"), rs.getLong("grandchild1_id"),
        (Integer) rs.getObject("request_sequence")),
        name, type, parentId, child1Id, child2Id, grandchild1Id, requestSequence);
  }
}
