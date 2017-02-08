##后端运行步骤
1. 克隆代码
```
cd /*the directory you want to put the code*/
```
2. 编译生成运行包
```
mvn package
```

3. 进入target目录并运行
```
cd ./target
nohup java -jar /*target .jar file*/ &
```
使用nohup可以使退出ssh时后端依然在运行

4. 改动
改动文件之后，需要将原先的进程关闭，然后重新启动
