package com.messager.application.Models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BusinessModelCreateRequest {
  private String name;
  private String type;
  private Long parentId;
  private String child1Name;
  private String child2Name;
  private String grandchild1Name;
}
