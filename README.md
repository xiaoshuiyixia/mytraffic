基于 JavaFX 实现直行道路交通仿真，支持车辆生成、信号灯控制、模拟启动/暂停/导出等功。
•	采用 JavaFX + MVC 架构 + 子控制器分层设计，结构清晰、可扩展性强；
•	基于线程池 + DCL 双重锁机制实现车辆与红绿灯的高并发控制；
•	使用观察者模式联动 UI，实现状态变化时界面自动刷新；
•	实现配置热更新，可通过手动或大模型文字识别修改红绿灯时长；
•	支持一键导出车辆数据为 CSV 文件，便于后续分析；
•	引入异常捕获 + 弹窗提示机制，提升系统稳定性与用户体验；
•	运用简单工厂模式统一创建对象，提升模块解耦性；
•	使用 TestFX + Mockito 编写核心模块测试用例，保障系统功能稳定；

