package com.messager.application.Dao;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.messager.application.DataNotFoundException;
import com.messager.application.Models.Grandchild1;
import java.sql.PreparedStatement;
import java.util.Objects;

@Repository
public class Grandchild1Dao {
  private final JdbcTemplate jdbcTemplate;

  public Grandchild1Dao(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  private static final RowMapper<Grandchild1> GRANDCHILD1_ROW_MAPPER = (rs, rowNum) -> {
    Grandchild1 g = new Grandchild1();
    g.setId(rs.getLong("id"));
    g.setChild1Id(rs.getLong("child1_id"));
    g.setName(rs.getString("name"));
    return g;
  };

  /**
   * Idempotent get-or-create using PostgreSQL UPSERT.
   */
  public Grandchild1 upsertReturning(Long child1Id, String name) throws DataAccessException {
    String sql = "INSERT INTO grandchild1(child1_id, name) VALUES (?, ?) " +
        "ON CONFLICT (child1_id, name) DO UPDATE SET name = EXCLUDED.name " +
        "RETURNING id, child1_id, name";
    return jdbcTemplate.queryForObject(sql, GRANDCHILD1_ROW_MAPPER, child1Id, name);
  }

  public Grandchild1 save(Grandchild1 grandchild1) throws DataAccessException {
    String sql = "INSERT INTO grandchild1(child1_id, name) VALUES (?, ?)";
    KeyHolder keyHolder = new GeneratedKeyHolder();
    jdbcTemplate.update(con -> {
      PreparedStatement ps = con.prepareStatement(sql, new String[] { "id" });
      ps.setObject(1, grandchild1.getChild1Id());
      ps.setString(2, grandchild1.getName());
      return ps;
    }, keyHolder);
    try {
      grandchild1.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
    } catch (Exception e) {
      throw new DataAccessException("Failed to retrieve generated key", e) {
      };
    }
    return grandchild1;
  }

  public Grandchild1 findByChildAndName(Long child1Id, String name) throws DataAccessException {
    String sql = "SELECT id, child1_id, name FROM grandchild1 WHERE child1_id = ? AND name = ?";
    try {
      return jdbcTemplate.queryForObject(sql, GRANDCHILD1_ROW_MAPPER, child1Id, name);
    } catch (EmptyResultDataAccessException ex) {
      throw new DataNotFoundException("Grandchild1 not found");
    }
  }
}
