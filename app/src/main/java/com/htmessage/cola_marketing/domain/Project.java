package com.htmessage.cola_marketing.domain;


import com.alibaba.fastjson.JSONObject;

import java.util.Date;

public class Project {
    private int id;
    private String explain_name; //项目名称
    private String explain_desc; //项目描述
    private Date explain_start; //开始时间
    private Date explain_end; //结束时间
    private String explain_scene; //场景
    private String support; //项目支持
    private String team_name; //团队名称
    private String team; //团队简介
    private String[] team_pic; //团队图片

    public Project() {}

    public Project(JSONObject object) {
        id = object.getInteger("explain_id");
        explain_desc = object.getString("explain_describe");
        explain_end = new Date(object.getLong("explain_end"));
        explain_name = object.getString("explain_name");
        explain_scene = object.getString("explain_scene");
        explain_start = new Date(object.getLong("explain_start"));
        support = object.getString("support");
        team = object.getString("team");
        team_name = object.getString("team_name");
        team_pic = object.getString("team_pic").isEmpty()? new String[0] : object.getString("team_pic").split(",");
    }

    public int getId() {
        return id;
    }

    public String getExplain_name() {
        return explain_name;
    }

    public void setExplain_name(String explain_name) {
        this.explain_name = explain_name;
    }

    public String getExplain_desc() {
        return explain_desc;
    }

    public void setExplain_desc(String explain_desc) {
        this.explain_desc = explain_desc;
    }

    public Date getExplain_start() {
        return explain_start;
    }

    public void setExplain_start(Date explain_start) {
        this.explain_start = explain_start;
    }

    public Date getExplain_end() {
        return explain_end;
    }

    public void setExplain_end(Date explain_end) {
        this.explain_end = explain_end;
    }

    public String getExplain_scene() {
        return explain_scene;
    }

    public void setExplain_scene(String explain_scene) {
        this.explain_scene = explain_scene;
    }

    public String getSupport() {
        return support;
    }

    public void setSupport(String support) {
        this.support = support;
    }

    public String getTeam_name() {
        return team_name;
    }

    public void setTeam_name(String team_name) {
        this.team_name = team_name;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public String[] getTeam_pic() {
        return team_pic;
    }

    public void setTeam_pic(String[] team_pic) {
        this.team_pic = team_pic;
    }
}
