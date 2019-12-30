# 文件管理系统（仿百度网盘）

## 演示网站
[请点击查看演示](http://www.qiwenshare.com/essay/detail/24)

## 介绍
基于springboot + vue 框架开发的Web文件系统，旨在为用户提供一个简单、方便的文件存储方案，目前已经完成了主要的基础功能，能够以完善的目录结构体系，对文件进行管理 。

 **您可以在其基础上继续进行开发来完善其功能，成为本项目的贡献者之一** 

 **您也可以以该项目作为脚手架，进行其他项目的开发** 

## 功能
#### 用户操作
1. 用户的登录和注册
#### 文件操作
1. 用户可对目录结构进行增加，删除，修改 
2. 提供文件的上传，下载， 
3. 支持对zip文件的在线解压缩 
4. 支持文件和目录的移动和复制 
5. 多文件格式分类查看 
6. ...

## 软件架构
该项目采用前后端分离的方式进行开发和部署,主要用到以下关键技术

**前台**：elementui，vue,  swipper， nodejs， webpack

**后台框架**：springboot mybatis jpa

**数据结构**：递归算法，树的遍历和插入...


## 安装教程

1. 拉取代码
2. 本地创建数据库，名为file，将application.properties中连接数据库的密码替换为自己本地的
3. 点击根目录下install.bat进行编译
4. 编译完成之后会生成release发布包，进去点击startWeb.bat启动
5. 启动完成后即可访问：localhost:8080使用相关功能

## 目录说明

```

-file-common 公共模块
-file-web 文件代码
    |-src
        |-main Java代码
        |-resources 静态资源
            |-static 前台代码
```




## 使用说明
1、本项目为后端代码，但是已经集成了前台包，启动后可以直接使用
2、如果你需要前台代码进行学习或者完善，可以访问该地址进行拉取
前台代码源码：[源码](https://gitee.com/qiwen-cloud/qiwen-file-web)




## 部分功能演示截图
 **主页**  :yellow_heart: 
![输入图片说明](https://images.gitee.com/uploads/images/2019/1022/205351_b35a50d9_947714.png "屏幕截图.png")
 **创建文件夹**  :blue_heart: 
![输入图片说明](https://images.gitee.com/uploads/images/2019/1022/205531_b3bf3380_947714.png "屏幕截图.png")
 **操作列展示**  :purple_heart: 
![输入图片说明](https://images.gitee.com/uploads/images/2019/1022/205841_9544a2ef_947714.png "屏幕截图.png")
 **文件的复制和移动**  :heart: 
![输入图片说明](https://images.gitee.com/uploads/images/2019/1022/210106_d4b619f3_947714.png "屏幕截图.png")
 **文件在线解压缩**  :green_heart: 
![输入图片说明](https://images.gitee.com/uploads/images/2019/1022/210214_c00f5600_947714.png "屏幕截图.png")

## 参与贡献

1.  Fork 本仓库
2.  新建 Feat_xxx 分支
3.  提交代码
4.  新建 Pull Request


## 码云特技

1.  使用 Readme\_XXX.md 来支持不同的语言，例如 Readme\_en.md, Readme\_zh.md
2.  码云官方博客 [blog.gitee.com](https://blog.gitee.com)
3.  你可以 [https://gitee.com/explore](https://gitee.com/explore) 这个地址来了解码云上的优秀开源项目
4.  [GVP](https://gitee.com/gvp) 全称是码云最有价值开源项目，是码云综合评定出的优秀开源项目
5.  码云官方提供的使用手册 [https://gitee.com/help](https://gitee.com/help)
6.  码云封面人物是一档用来展示码云会员风采的栏目 [https://gitee.com/gitee-stars/](https://gitee.com/gitee-stars/)
