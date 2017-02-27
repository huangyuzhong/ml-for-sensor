##后端运行步骤

1. 设置环境变量为环境名称， 对应为config的名字。 例如， 产品环境为prod， 测试环境为test。 在linux里， 在~/.bash_profile里加入
  ```
  export INTELAB_ENV={环境名称}
  ```
 * 重新打开shell。

2. 克隆代码
  ```
  cd /*the directory you want to put the code*/
  ```

3. 编译生成运行包
  ```
  mvn package
  ```

4. 进入target目录并运行
  ```
  cd ./target
  nohup java -jar /*target .jar file*/ &
  ```
  使用nohup可以使退出ssh时后端依然在运行

5. 改动
  改动文件之后，需要将原先的进程关闭，然后重新启动

##数据库迁移步骤
1. 由于azure.cn中的数据库服务默认不允许外网访问，所以需要在manage.windowsazure.cn中，将mysql database里的ilb-dev-db数据库配置中加上自己的ip，方可访问。
2. 使用workbench添加源数据库和目标数据库的链接，使用migrate wizard拷贝数据库
3. azure上的数据库用户名ils-dev-db%iLS_Dev_DB， 密码iLabService123
