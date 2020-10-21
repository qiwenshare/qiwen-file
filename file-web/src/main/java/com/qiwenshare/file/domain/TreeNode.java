package com.qiwenshare.file.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 树节点
 */
public class TreeNode {
    /**
     * 节点id
     */
    private Long id;
    /**
     * 节点名
     */
    private String label;
    /**
     * 深度
     */
    private Long depth;
    /**
     * 是否被关闭
     */
    private String state = "closed";

    /**
     * 属性集合
     */
    private Map<String, String> attributes = new HashMap<>();
    /**
     * 子节点列表
     */
    private List<TreeNode> children = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Long getDepth() {
        return depth;
    }

    public void setDepth(Long depth) {
        this.depth = depth;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    public List<TreeNode> getChildren() {
        return children;
    }

    public void setChildren(List<TreeNode> children) {
        this.children = children;
    }
}
