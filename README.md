##后端运行步骤

1. 设置环境变量为环境名称， 对应为config的名字。 例如， 产品环境为prod， 测试环境为test。 在linux里， 在~/.bash_profile里加入
  
  ```
  export INTELAB_ENV={环境名称}
  ```
  
  在/etc/environment里加入
  HOME=/home/ilabservice
  INTELAB_ENV={环境名称}
 
 * 重新打开shell。

2. 克隆代码
 
 - intelab-wbe
 - intelab-configs
 - intelab-tools
  
 * 修改intelab-configs/{环境名称}/application.properties里的数据库地址指向该环境的数据库。
 * copy intelab-configs/{环境名称}/log4j2.xml到 intelab-wbe/src/main/resources/
 
3. 编译生成运行包
  
  ```
  mvn package
  ```
  
4. copy intelab-wbe/scripts/upstart/intelab-wbe.conf 到/etc/init/


5. 运行 wbe

```
  sudo start intelab-wbe
```

如果使用intellij运行，需要在edit configuration 中，spring boot setting 里添加spring.config.location，值为config的文件路径名

PS: 以下两个components是合同WBE一起工作的
* socket server 类似操作. 
* rabbitmq 运行正常 

5. 改动
  改动文件之后，需要将原先的进程关闭，然后重新启动

##数据库迁移步骤
1. 由于azure.cn中的数据库服务默认不允许外网访问，所以需要在manage.windowsazure.cn中，将mysql database里的ilb-dev-db数据库配置中加上自己的ip，方可访问。
2. 使用workbench添加源数据库和目标数据库的链接，使用migrate wizard拷贝数据库
3. azure上的数据库用户名ils-dev-db%iLS_Dev_DB， 密码iLabService123
