package com.messager.application.Dao;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.messager.application.Models.ParentEntity;

@Repository
public class ParentDao {
  private final JdbcTemplate jdbcTemplate;

  public ParentDao(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  public ParentEntity findById(Long id) throws DataAccessException {
    String sql = "SELECT id FROM parents WHERE id = ?";
    return jdbcTemplate.query(sql, ps -> ps.setLong(1, id), rs -> {
      if (rs.next()) {
        ParentEntity parent = new ParentEntity();
        parent.setId(rs.getLong("id"));
        return parent;
      }
      return null;
    });
  }
}
