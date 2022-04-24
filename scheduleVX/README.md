#使用Vert.x 构建一个任务调度器带Web  

## 技术栈 
| 名称 | 功能 |
| --- | --- |
| Vert.x | 响应式框架 |
| Lombok | 代码工具|
| ~~hutool-http~~| ~~网络请求工具~~|
| fast-json| json工具|

## Version List
### 0.0.1
- [x] 简单的本地版本封装
- [x] HttpUtil封装
- [x] WebService注册
- [ ] 支持corn表达式
- [ ] 封装Schedule运行时
- [ ] web
- [x] 控制启动和停止
- [x] 建设服务总线

##定时任务模式
- [x] 间隔调用
- [x] 单次调用
- [ ] cron表达式
- [ ] 固定次数，避开特殊时间/日期

#Plugins
###JVM监控
> 会使用的一些基本类：
> - ClassLoadingMXBean：监控类加载系统。
> - CompilationMXBean：监控编译系统。
> - GarbageCollectionMXBean：监控 JVM 的垃圾收集器。
> - MemoryMXBean：监控 JVM 的堆和非堆内存空间。
> - MemoryPoolMXBean：监控 JVM 分配的内存池。
> - RuntimeMXBean：监控运行时系统。该 MXBean 提供的有用监控指标很少，但它确实提供了 JVM 的输入参数和启动时间及运行时间，这两者在其他派生指标中都是很有用的。
> - ThreadMXBean：监控线程系统。
>核心类：
> - MBeanServerConnection
   
