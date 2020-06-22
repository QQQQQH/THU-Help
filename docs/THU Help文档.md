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
- 历史记录保存和离线消息
  - 聊天历史记录在前端和后端数据库都有备份
  - 消息推送历史记录在后端数据库保存
  - 用户上线时接收离线消息

---

## 前端代码说明

### MainActivity

主页面部分。主页面包含三个`Fragment`：

- `MainFragment`：所有待接收的任务列表，列表部分用一个`SwipeRefreshLayout`包裹一个`RecyclerView`
- `ChatFragment`：聊天列表页面，同样通过`RecyclerView`实现
- `MyFragment`：个人信息页面，登录前只有注册和登录按钮，登录后显示个人信息和其他按钮，点击充值、提现会弹出Dialog提示用户输入金额，点击用户名可以进入修改头像或用户信息的流程

### DealActivity

任务相关页面，包括不同类型的任务列表页面`DealListActivity`，任务发布页面`PublishDealActivity`和任务详情页面`DealInfoActivity`。

各种不同类型的任务列表页面使用同一个`DealListActivity`，通过传入的数据区分。`DealListActivity`通过`TabLayout`包含两个`DealListFragment`，分别表示「我发布的任务」和「他人发布的任务」，不同类型通过构造函数的参数区分，`DealListFragment`和`MainActivity.MainFragment`类似，同样使用`MissionListAdapter`。

### ChatActivity

私信聊天页面。同样采用`RecyclerView`，每一条消息是其中的一个`item`，并翻转`RecyclerView`以实现进入页面时总是在消息列表最底部。

### UserInfoActivity

包含注册页面`RegisterActivity`和登录页面`LoginActivity`和修改个人信息页面`EditProfileActivity`

### Service

用于聊天和接收任务状态变化通知`WebSocket`的`ChatWebSocketClientService`，同时这个`Service`在收到消息后会发出相应的`Broadcast`。在用户登录后，`MainActivity`会和这个服务绑定，打开聊天页面时`ChatActivity`也会和它绑定，二者都会注册相应的广播接收器。


---

## 后端代码说明

### common

common包中包含用于生成JSON字符串的实体类，`Message`保存了WebSocket返回JSON的信息，`Result`则保存普通的请求返回JSON的信息。

### controller

`UserAccountController`负责处理来自前端的注册、登录、修改信息等关系到用户账户信息的请求。用户登录时会生成并返回一个skey用于后续请求的验证。`UserDealController`处理发布、删除、接收、放弃、确认、完成任务等请求。`WebSocketController`控制跳转到浏览器前端WebSocket界面，方便客户端开发过程中对WebSocket的测试。

### entity

Mybatis-Plus数据库实体类。数据库包含以下几个表：

- user 存储用户账户信息，包括uid、学号、密码、当前skey、skey的有效期、头像等。其中头像用mediumtext类型存储，客户端将头像的jpg文件以字节数组的形式传到后端，后端再用base64加密成字符串后存进数据库。nginx、mysql、后端应用程序分别部署在不同的docker容器中，保证后端应用程序重新部署后用户的头像不会丢失。

- deal 存储任务信息，包括发布者uid、接受者uid、任务描述、奖励、联系方式和地点等。

- message_record 存储聊天记录，包括发送者uid、接收者uid、文字消息、发送时间等。

- notification_record 存储推送记录，包括接收者uid、推送类型、推送文字信息、推送时间等。

- address 已根据开发需求弃用。

其中message_record和notification_record两张表还包含一个receive_flag，用来记录该消息是否已被发送到客户端，实现离线消息。当用户上线连接WebSocket时，会查询数据库找到需要发送给该用户的离线消息，生成列表后通过WebSocket发送给客户端。

### service

`UserService`涉及到数据库访问的后端代码逻辑在此，该Service中的方法会返回对数据库的查询或处理结果。

`WebSocket`接收来自客户端的由WebSocket发送的聊天消息，向在线客户端发送消息，将需要对离线用户发送的消息存进数据库并在用户登录时发送。静态方法`sendNotification`在`UserService`中调用，向用户发送通知推送。