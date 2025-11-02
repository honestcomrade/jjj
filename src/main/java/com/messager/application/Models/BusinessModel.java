package com.messager.application.Models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BusinessModel {
  private Long id;
  private String name;
  private String type;
  private Long parentId;
  private Long child1Id;
  private Long child2Id;
  private Long grandchild1Id;
  private Integer requestSequence;
}
