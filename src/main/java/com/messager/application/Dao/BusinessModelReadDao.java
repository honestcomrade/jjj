package com.messager.application.Dao;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.messager.application.Models.dto.BusinessModelGraph;

@Repository
public class BusinessModelReadDao {
  private final JdbcTemplate jdbcTemplate;

  public BusinessModelReadDao(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

   public BusinessModelGraph findGraphById(Long businessModelId) throws EmptyResultDataAccessException {
    final String sql =
        "SELECT " +
        "  bm.id            AS bm_id, " +
        "  bm.name          AS bm_name, " +
        "  bm.type          AS bm_type, " +
        "  bm.request_sequence AS bm_request_sequence, " +
        "  p.id             AS p_id, " +
        "  p.name           AS p_name, " +
        "  c1.id            AS c1_id, " +
        "  c1.parent_id     AS c1_parent_id, " +
        "  c1.name          AS c1_name, " +
        "  c2.id            AS c2_id, " +
        "  c2.parent_id     AS c2_parent_id, " +
        "  c2.name          AS c2_name, " +
        "  gc1.id           AS gc1_id, " +
        "  gc1.child1_id    AS gc1_child1_id, " +
        "  gc1.name         AS gc1_name " +
        "FROM business_models bm " +
        "JOIN parents     p   ON p.id  = bm.parent_id " +
        "JOIN child1      c1  ON c1.id = bm.child1_id " +
        "JOIN child2      c2  ON c2.id = bm.child2_id " +
        "JOIN grandchild1 gc1 ON gc1.id = bm.grandchild1_id " +
        "WHERE bm.id = ?";

    return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
      BusinessModelGraph.ParentDto parent = new BusinessModelGraph.ParentDto();
      parent.setId(rs.getLong("p_id"));
      parent.setName(rs.getString("p_name"));

      BusinessModelGraph.Child1Dto child1 = new BusinessModelGraph.Child1Dto();
      child1.setId(rs.getLong("c1_id"));
      child1.setParentId(rs.getLong("c1_parent_id"));
      child1.setName(rs.getString("c1_name"));

      BusinessModelGraph.Child2Dto child2 = new BusinessModelGraph.Child2Dto();
      child2.setId(rs.getLong("c2_id"));
      child2.setParentId(rs.getLong("c2_parent_id"));
      child2.setName(rs.getString("c2_name"));

      BusinessModelGraph.Grandchild1Dto grandchild1 = new BusinessModelGraph.Grandchild1Dto();
      grandchild1.setId(rs.getLong("gc1_id"));
      grandchild1.setChild1Id(rs.getLong("gc1_child1_id"));
      grandchild1.setName(rs.getString("gc1_name"));

      BusinessModelGraph graph = new BusinessModelGraph();
      graph.setId(rs.getLong("bm_id"));
      graph.setName(rs.getString("bm_name"));
      graph.setType(rs.getString("bm_type"));
      graph.setRequestSequence((Integer) rs.getObject("bm_request_sequence"));
      graph.setParent(parent);
      graph.setChild1(child1);
      graph.setChild2(child2);
      graph.setGrandchild1(grandchild1);
      return graph;
    }, businessModelId);
  }
  
}
