# 动漫查询与播放APP (AnimeViewer) - 规格文档

## 1. 项目概述

- **项目名称**: AnimeViewer
- **项目类型**: Android移动应用
- **开发工具**: Android Studio
- **开发语言**: Java
- **核心功能**: 用户登录注册、多维度动漫搜索、动漫播放、个人空间、收藏功能、分类筛选

## 2. 技术架构

### 2.1 整体架构
```
com.example.animeviewer
├── ui/                    # 所有页面(Activity + Fragment)
│   ├── activity/          # Activity页面
│   └── fragment/          # Fragment页面
├── adapter/               # 列表适配器(RecyclerView)
├── bean/                  # 数据模型(User, Anime, Collect)
├── utils/                 # 工具类(网络、SP存储、加密)
├── service/               # 播放服务、后台服务
└── constants/             # 常量类(接口地址、分类)
```

### 2.2 技术选型
- **界面**: XML布局
- **列表**: RecyclerView
- **图片加载**: Glide
- **播放**: VideoView/系统播放器
- **存储**: SharedPreferences + 文件存储
- **架构**: Activity + Fragment + BottomNavigationView

## 3. 页面结构

| 页面 | 文件名 | 功能描述 |
|------|--------|----------|
| 启动页 | SplashActivity | 检查登录状态，自动跳转 |
| 登录页 | LoginActivity | 账号密码登录 |
| 注册页 | RegisterActivity | 注册账号、密码、昵称 |
| 主页 | MainActivity | 底部导航4个模块 |
| 首页 | HomeFragment | 推荐动漫、轮播图 |
| 搜索页 | SearchFragment | 多条件筛选搜索 |
| 分类页 | CategoryFragment | 国家+题材分类 |
| 我的 | MySpaceFragment | 个人空间 |
| 动漫列表 | AnimeListActivity | 展示搜索/分类结果 |
| 动漫详情 | AnimeDetailActivity | 封面、简介、评分、收藏 |
| 播放页 | VideoPlayActivity | 视频播放、选集切换 |

## 4. 数据模型

### 4.1 User (用户)
```
- id: String (用户ID)
- username: String (账号)
- password: String (密码)
- nickname: String (昵称)
- avatar: String (头像URL)
```

### 4.2 Anime (动漫)
```
- id: String (动漫ID)
- title: String (标题)
- cover: String (封面图)
- country: String (国家: CN/JP/US/KR)
- type: String[] (类型: 热血/恋爱/搞笑/科幻等)
- year: int (年代)
- rating: float (评分)
- description: String (简介)
- episodes: Episode[] (剧集列表)
-热度: int
```

### 4.3 Episode (剧集)
```
- episodeNumber: int (集数)
- title: String (集标题)
- videoUrl: String (播放地址)
```

### 4.4 Collect (收藏)
```
- userId: String (用户ID)
- animeId: String (动漫ID)
- collectTime: long (收藏时间)
```

## 5. 功能模块

### 5.1 用户系统
- 注册：账号、密码、昵称
- 登录：验证账号密码
- 记住登录状态 (SharedPreferences)
- 退出登录

### 5.2 动漫搜索
- 关键词搜索
- 国家筛选：中国、日本、美国、韩国
- 类型筛选：热血、恋爱、搞笑、科幻、悬疑、校园、冒险
- 年代筛选：2020-2024
- 排序：热度、评分、最新

### 5.3 分类浏览
- 按国家：国漫、日漫、美漫、韩漫
- 按题材：热血、恋爱、搞笑、科幻等
- 最新、热门推荐

### 5.4 收藏功能
- 收藏/取消收藏
- 个人空间展示收藏列表
- 本地JSON文件存储

### 5.5 动漫播放
- VideoView播放
- 剧集切换
- 播放进度记忆

### 5.6 个人空间
- 头像、昵称显示
- 我的收藏列表
- 观看历史
- 退出登录

## 6. 本地存储方案

### 6.1 SharedPreferences
- 登录状态 (isLogin)
- 当前用户信息 (userId, username, nickname)

### 6.2 本地JSON文件
- `users.json` - 用户数据
- `anime.json` - 动漫数据
- `collect.json` - 收藏数据
- `history.json` - 观看历史

## 7. 页面跳转流程

```
启动页(Splash)
    ↓
已登录 → 主页(MainActivity)
未登录 → 登录页(LoginActivity)
    ↓
注册 → 注册页(RegisterActivity)
    ↓
主页 → 首页(HomeFragment)
     → 搜索(SearchFragment) → 动漫列表 → 详情 → 播放
     → 分类(CategoryFragment) → 动漫列表 → 详情 → 播放
     → 我的(MySpaceFragment) → 收藏/历史/设置
```

## 8. 开发步骤

1. 搭建项目 + 底部导航
2. 登录注册页面
3. 首页 + 动漫列表
4. 搜索页面(多条件筛选)
5. 动漫详情 + 收藏功能
6. 播放页面
7. 个人空间
8. 测试优化
