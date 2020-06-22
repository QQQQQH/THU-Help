# THU Help大作业文档

## 小组和选题

- 选题：校园众包应用
- 小组成员：
  - 沈翘楚 2017011476
  - 司徒轩 2017011449
  - 野田豪 2017080345

---

## 成员分工

- 后端：司徒轩
- 前端
  - 聊天部分：野田豪，沈翘楚
  - 个人资料部分：司徒轩
  - 任务部分：沈翘楚

---

## 功能实现

应用实现的功能包括：

- 用户注册登录
- 修改头像
  - 通过相册图片上传
  - 通过相机拍照上传
- 修改个人信息
- 发布，删除，接受，放弃，完成，确认任务
- 在消息列表通过关键词搜索任务
- 查看和自己相关的任务（包括自己发布的和自己接受的处在各个状态的任务）
- 任务状态变化实时信息通知
- 私信聊天
  - 聊天列表页面最新的对话位于最上方
  - 聊天页面最新的消息在最下方，进入时视图位于最下方

---

## 前端代码简要说明

### MainActivity

主页面部分。主页面包含三个`Fragment`：

- `MainFragment`：所有待接收的任务列表，列表部分用一个`SwipeRefreshLayout`包裹一个`RecyclerView`
- `ChatFragment`：聊天列表页面，同样通过`RecyclerView`实现
- `MyFragment`：个人信息页面，登录前只有注册和登录按钮，登录后显示个人信息和其他按钮

### DealActivity

任务相关页面，包括不同类型的任务列表页面`DealListActivity`，任务发布页面`PublishDealActivity`和任务详情页面`DealInfoActivity`。

各种不同类型的任务列表页面使用同一个`DealListActivity`，通过传入的数据区分。`DealListActivity`通过`TabLayout`包含两个`DealListFragment`，分别表示「我发布的任务」和「他人发布的任务」，不同类型通过构造函数的参数区分，`DealListFragment`和`MainActivity.MainFragment`类似，并使用同一个`MissionListAdapter`。

### ChatActivity

私信聊天页面。同样采用`RecyclerView`，每一条消息是其中的一个`item`，并翻转`RecyclerView`以实现进入页面时总是在消息列表最底部。

### UserInfoActivity

包含注册页面`RegisterActivity`和登录页面`LoginActivity`和修改个人信息页面`EditProfileActivity`

### Service

用于聊天和接收任务状态变化通知`WebSocket`的`ChatWebSocketClientService`，同时这个`Service`在收到消息后会发出相应的`Broadcast`。在用户登录后，`MainActivity`会和这个服务绑定，打开聊天页面时`ChatActivity`也会和它绑定，二者都会注册相应的广播接收器。

