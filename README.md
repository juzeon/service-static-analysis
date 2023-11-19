# service-static-analysis

- 接口消息错误
  - [x] 不可识别的消息报文\*
  - [x] 消息体错误\*
  - [x] *消息过长*
  - [ ] 正常的例外消息\*
  - [ ] 消息语义错误\*
- 功能调用和交互
  - [ ] 会话ID重复\*
  - [ ] 消息有效期很短\*
  - [ ] 响应消息超时或丢失
  - [ ] 频繁收到非正常连接请求
  - [ ] 连续多次协商失败
  - [ ] 反复接收到导致崩溃的消息\*
  - [ ] 部分消息错误\*
- 设计缺陷
  - [ ] 反复重试
  - [ ] 高优先级消息响应超时
  - [ ] 不合理依赖\*
- TCP接口
  - [ ] SOCKET被占用
  - [ ] SOCKET连接数占满
  - [ ] 连接中断
  - [ ] 连接闪断
  - [ ] 传输延迟
  - [ ] 连接吊死
  - [ ] 连接单通
  - [ ] 建链异常
- UDP接口
  - [ ] 错包\*
  - [ ] 报文重复\*
  - [ ] 丢包
  - [ ] 延迟
  - [ ] 抖动\*
  - [ ] 乱序\*
- 数据传输
  - [ ] 文件传输中断
  - [ ] FTP占用大量磁盘IO
  - [ ] 数据传输丢失
- HTTP接口
  - [ ] 重复请求\*
  - [ ] POST请求异常
  - [ ] 消息体截断