Project name - Device sharing system based on IOT and Block chain

The project is a device sharing system based on IoT and block chaining. The back-end framework uses 
the Spring Boot micro framework. The main logic is that the terminal detects the corresponding parameter 
changes through the sensor, and then the parameter data is packaged in the frame data of a specific 
protocol and transmitted to the back-end through the HTTP protocol, then the back-end parses the data and 
compares the data. Thus, the used state of the equipment is detected intelligently. And through some third 
party data analysis software powerBI, data analysis is generated and the report is displayed on the front page.
After the back-end gets the data, because there are many kinds of sensors and the data is transmitted in real 
time, the amount of data is very large. Therefore, when setting the alarm threshold of parameters, the KMeans 
clustering algorithm in machine learning is used to process, and the center of mass and distance of five clusters 
are obtained, so the parameters alarm threshold can be set dynamically. Block chain is a new Internet technology, 
in the process of equipment leasing, the need for equipment transactions, equipment status (may fail), leasing costs 
of the transfer certificate. Block chains can ensure that data is not tampered with, so that equipment leasing keep 
going in a reliable, safe and convenient environment. 


项目名称 - 基于IOT和区块链的设备共享系统

项目基于IOT和区块链的设备共享系统，后端框架用的是Spring Boot微框架。主要逻辑是终端通过传感器检测到相
应的参数变化，然后将参数数据打包在特定协议的帧数据里通过http协议传到后端，后端再进行解析数据，数据比
对。从而智能地检测到设备的使用状态。再通过一些第三方数据分析软件OpowerBI对数据分析生成报表显示在前端
页面。其中后端在获取到数据后，由于传感器种类特别多，并且数据是实时传输的，所以数据量非常大，因此在设置
参数报警阈值时，采用KMeans聚类方法去处理，得到五类簇中心点以及质心距离，从而动态设置参数报警阈值。而
区块链是一种新的互联网技术，在设备租用的过程中，需要对设备交易，设备状态(可能出现故障)，租赁产生费用的
转账进行存证。区块链能保证数据不被篡改，从而使得设备的租赁能在可靠、安全、便捷的环境下继续进行下去。