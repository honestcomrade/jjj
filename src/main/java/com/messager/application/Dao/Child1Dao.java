package com.messager.application.Dao;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.messager.application.DataNotFoundException;
import com.messager.application.Models.Child1;
import java.sql.PreparedStatement;
import java.util.Objects;

@Repository
public class Child1Dao {
  private final JdbcTemplate jdbcTemplate;

  public Child1Dao(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  private static final RowMapper<Child1> CHILD1_ROW_MAPPER = (rs, rowNum) -> {
    Child1 c = new Child1();
    c.setId(rs.getLong("id"));
    c.setParentId(rs.getLong("parent_id"));
    c.setName(rs.getString("name"));
    return c;
  };

  /**
   * Idempotent get-or-create using PostgreSQL UPSERT.
   * Returns the existing row if it already exists, or the newly inserted row.
   */
  public Child1 upsertReturning(Long parentId, String name) throws DataAccessException {
    String sql = "INSERT INTO child1(parent_id, name) VALUES (?, ?) " +
        "ON CONFLICT (parent_id, name) DO UPDATE SET name = EXCLUDED.name " +
        "RETURNING id, parent_id, name";
    return jdbcTemplate.queryForObject(sql, CHILD1_ROW_MAPPER, parentId, name);
  }

  public Child1 insertStrict(Child1 child1) throws DataAccessException {
    String sql = "INSERT INTO child1(parent_id, name) VALUES (?, ?)";
    KeyHolder keyHolder = new GeneratedKeyHolder();

    jdbcTemplate.update(con -> {
      PreparedStatement ps = con.prepareStatement(sql, new String[] { "id" });
      ps.setObject(1, child1.getParentId());
      ps.setString(2, child1.getName());
      return ps;
    }, keyHolder);

    try {
      child1.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
    } catch (Exception e) {
      throw new DataAccessException("Failed to retrieve generated key", e) {
      };
    }
    return child1;
  }

  public Child1 findByParentAndName(Long parentId, String name) throws DataAccessException {
    String sql = "SELECT id, parent_id, name FROM child1 WHERE parent_id = ? AND name = ?";
    try {
      return jdbcTemplate.queryForObject(sql, CHILD1_ROW_MAPPER, parentId, name);
    } catch (EmptyResultDataAccessException ex) {
      throw new DataNotFoundException("Child1 not found");
    }
  }
}
