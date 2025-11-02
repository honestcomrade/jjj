package com.messager.application.Models.dto;

public class BusinessModelGraph {
  private Long id;
  private String name;
  private String type;
  private Integer requestSequence;
  private ParentDto parent;
  private Child1Dto child1;
  private Child2Dto child2;
  private Grandchild1Dto grandchild1;

  public static class ParentDto {
    private Long id;
    private String name;
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
  }

  public static class Child1Dto {
    private Long id;
    private Long parentId;
    private String name;
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
  }

  public static class Child2Dto {
    private Long id;
    private Long parentId;
    private String name;
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
  }

  public static class Grandchild1Dto {
    private Long id;
    private Long child1Id;
    private String name;
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getChild1Id() { return child1Id; }
    public void setChild1Id(Long child1Id) { this.child1Id = child1Id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
  }

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }
  public String getType() { return type; }
  public void setType(String type) { this.type = type; }
  public Integer getRequestSequence() { return requestSequence; }
  public void setRequestSequence(Integer requestSequence) { this.requestSequence = requestSequence; }
  public ParentDto getParent() { return parent; }
  public void setParent(ParentDto parent) { this.parent = parent; }
  public Child1Dto getChild1() { return child1; }
  public void setChild1(Child1Dto child1) { this.child1 = child1; }
  public Child2Dto getChild2() { return child2; }
  public void setChild2(Child2Dto child2) { this.child2 = child2; }
  public Grandchild1Dto getGrandchild1() { return grandchild1; }
  public void setGrandchild1(Grandchild1Dto grandchild1) { this.grandchild1 = grandchild1; }
}
