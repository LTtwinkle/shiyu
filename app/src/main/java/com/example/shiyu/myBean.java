package com.example.shiyu;

public class myBean {
    //获取文件夹列表，获取笔记列表
    private Integer userID; //存放用户ID
    private Integer ID; //存放文件夹ID
    private String type; //使用类型
    private String title;   //存放文件内容
    private String timeDis;    //存放修改时间差
    private Boolean isSelect; //标记选中


    public myBean(String type,String title,String timeDis,Integer ID){
        this.ID = ID;
        this.type = type;
        this.title = title;
        this.timeDis = timeDis;
    }

    public String getType(){
        return type;
    }
    public void setType(String type){
        this.type = type;
    }
    public String getTitle(){
        return title;
    }
    public void setTitle(String title){
        this.title = title;
    }
    public String getTimeDis(){
        return timeDis;
    }
    public void setTimeDis(String timeDis){
        this.timeDis = timeDis;
    }
    public Integer getUserID(){
        return userID;
    }
    public void setUserID(Integer userID){
        this.userID = userID;
    }
    public Integer getID(){
        return ID;
    }
    public void setID(Integer ID){
        this.ID = ID;
    }
    public Boolean getIsSelect(){
        return isSelect;
    }
    public void setIsSelect(Boolean isSelect){
        this.isSelect = isSelect;
    }
}