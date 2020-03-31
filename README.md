# AutosmeltFix

修复匠魂扩展激光枪熔炼模式刷物品的bug

## BUG重现

1. A圈地,在里面摆上原矿,B没有A领地的破坏权限
2. B用激光枪熔炼模式右键A领地里面的原矿,可以获得一个矿物锭,并且原矿不会消失

## 修复说明

### 0.前置插件

Residence领地插件

### 1.加载一次插件,修改配置文件

```yml
Option:
  method: default
  oreID: 14,15,16,21,56,73,127,153,256,291,292,308,809,832,833,359,1058,1496,1504,1931
  debug: false
Message:
  warn: '[§aWrenchFix§f]检测到你正在你用自动冶炼bug,你已被警告'
  kick: '[§aWrenchFix§f]检测到你正在你用自动冶炼bug,你已被kick'
  ban: '[§aWrenchFix§f]检测到你正在你用自动冶炼bug,你已被ban'
```

Option.method: default/warn/kick/ban 四种处理方式
oreID: 配置原矿id,mod原矿必须填写方块id而不是拿在手上时的物品id,可以通过debug: true来看或者用创世神的/info

### 2.重载插件,即可使用

### 其他说明

本插件无指令,无权限配置
