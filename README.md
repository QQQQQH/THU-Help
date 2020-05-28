## 主要代码结构

- `mainActivity`包：主页面，包含两个`Fragment`：`MainFragment`（主页）和`MyFragment`（“我的”页面）
  - `MainFragment`：包含一个`RecyclerView`，用来显示任务列表
    - ` missionlist_item.xml`：`RecyclerView`中单个任务的模板布局
- `enterActivity`包：`LoginActivity`（登录页面）和`RegisterActivity`（注册页面）


## TO-DO

- 优化调整`MainFragment`中任务列表的样式布局，即调整`missionlist_item.xml`
- 在`mainActivity.MyFragment`中添加个人信息相关的内容