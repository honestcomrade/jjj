package com.messager.application.Dao;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.messager.application.Models.BusinessModel;

@Repository
public class BusinessModelDao {
  private final JdbcTemplate jdbcTemplate;

  public BusinessModelDao(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  public BusinessModel insertStrict(String name, String type, Long parentId, Long child1Id, Long child2Id,
      Long grandchild1Id)
      throws DataAccessException {
    String sql = "INSERT INTO business_models(name, type, parent_id, child1_id, child2_id, grandchild1_id) " +
        "VALUES (?, ?, ?, ?, ?, ?) " +
        "RETURNING id, name, type, parent_id, child1_id, child2_id, grandchild1_id";
    return jdbcTemplate.queryForObject(sql, (rs, row) -> new BusinessModel(
        rs.getLong("id"), rs.getString("name"), rs.getString("type"),
        rs.getLong("parent_id"), rs.getLong("child1_id"), rs.getLong("child2_id"), rs.getLong("grandchild1_id")),
        name, type, parentId, child1Id, child2Id, grandchild1Id);
  }

  public BusinessModel findByNameAndType(String name, String type) throws DataAccessException {
    String sql = "SELECT id, name, type, parent_id, child1_id, child2_id, grandchild1_id FROM business_models WHERE name = ? AND type = ?";
    return jdbcTemplate.query(sql, ps -> {
      ps.setString(1, name);
      ps.setString(2, type);
    }, rs -> rs.next() ? new BusinessModel(
        rs.getLong("id"), rs.getString("name"), rs.getString("type"),
        rs.getLong("parent_id"), rs.getLong("child1_id"), rs.getLong("child2_id"), rs.getLong("grandchild1_id"))
        : null);
  }
}
