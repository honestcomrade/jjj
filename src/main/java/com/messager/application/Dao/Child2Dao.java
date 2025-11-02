package com.messager.application.Dao;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.messager.application.DataNotFoundException;
import com.messager.application.Models.Child2;
import java.sql.PreparedStatement;
import java.util.Objects;

@Repository
public class Child2Dao {
  private final JdbcTemplate jdbcTemplate;

  public Child2Dao(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  private static final RowMapper<Child2> CHILD2_ROW_MAPPER = (rs, rowNum) -> {
    Child2 c = new Child2();
    c.setId(rs.getLong("id"));
    c.setParentId(rs.getLong("parent_id"));
    c.setName(rs.getString("name"));
    return c;
  };

  /**
   * Idempotent get-or-create using PostgreSQL UPSERT.
   */
  public Child2 upsertReturning(Long parentId, String name) throws DataAccessException {
    String sql = "INSERT INTO child2(parent_id, name) VALUES (?, ?) " +
        "ON CONFLICT (parent_id, name) DO UPDATE SET name = EXCLUDED.name " +
        "RETURNING id, parent_id, name";
    return jdbcTemplate.queryForObject(sql, CHILD2_ROW_MAPPER, parentId, name);
  }

  public Child2 insertStrict(Child2 child2) throws DataAccessException {
    String sql = "INSERT INTO child2(parent_id, name) VALUES (?, ?)";
    KeyHolder keyHolder = new GeneratedKeyHolder();
    jdbcTemplate.update(con -> {
      PreparedStatement ps = con.prepareStatement(sql, new String[] { "id" });
      ps.setObject(1, child2.getParentId());
      ps.setString(2, child2.getName());
      return ps;
    }, keyHolder);
    try {
      child2.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
    } catch (Exception e) {
      throw new DataAccessException("Failed to retrieve generated key", e) {
      };
    }
    return child2;
  }

  public Child2 findByParentAndName(Long parentId, String name) throws DataAccessException {
    String sql = "SELECT id, parent_id, name FROM child2 WHERE parent_id = ? AND name = ?";
    try {
      return jdbcTemplate.queryForObject(sql, CHILD2_ROW_MAPPER, parentId, name);
    } catch (EmptyResultDataAccessException ex) {
      throw new DataNotFoundException("Child2 not found");
    }
  }
}
