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
##定时任务模式
- [x] 间隔调用
- [x] 单次调用
- [ ] cron表达式
- [ ] 固定次数，避开特殊时间/日期
