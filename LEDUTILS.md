# LED控制工具类接口说明

## 概述
`LEDUtils` 是一个用于控制LED灯的工具类，支持单色LED的开关控制和闪烁控制。

## 常量定义

| 常量名 | 值 | 说明 |
|--------|----|------|
| `RED` | 0 | 红色LED |
| `GREEN` | 1 | 绿色LED |
| `BLUE` | 2 | 蓝色LED |

## 方法说明

### 1. 设置LED开关状态

```java
static public void setled(int color, boolean onoff)
```

**功能描述**：设置指定颜色LED的开关状态

**参数说明**：
- `color`：LED颜色，可选值：`RED`、`GREEN`、`BLUE`
- `onoff`：开关状态，`true`表示亮，`false`表示灭

**使用示例**：
```java
// 点亮红色LED
LEDUtils.setled(LEDUtils.RED, true);

// 关闭绿色LED
LEDUtils.setled(LEDUtils.GREEN, false);
```

### 2. 设置LED闪烁模式

```java
static public void setled(int color, int ontime, int offtime, boolean onoff)
```

**功能描述**：设置指定颜色LED的闪烁模式

**参数说明**：
- `color`：LED颜色，可选值：`RED`、`GREEN`、`BLUE`
- `ontime`：LED亮的时间（单位：毫秒）
- `offtime`：LED灭的时间（单位：毫秒）
- `onoff`：闪烁开关，`true`表示开始闪烁，`false`表示停止闪烁

**使用示例**：
```java
// 设置蓝色LED以500ms亮、500ms灭的频率闪烁
LEDUtils.setled(LEDUtils.BLUE, 500, 500, true);

// 停止红色LED闪烁
LEDUtils.setled(LEDUtils.RED, 0, 0, false);
```

## 注意事项

1. **文件路径**：该类通过操作Linux系统的sysfs接口来控制LED，对应的设备文件路径为：
   - 红色LED：`/sys/class/leds/red/`
   - 绿色LED：`/sys/class/leds/green/`
   - 蓝色LED：`/sys/class/leds/blue/`

2. **亮度控制**：
   - 亮状态：写入"255"
   - 灭状态：写入"0"

3. **闪烁控制**：
   - 使用timer触发器实现闪烁功能
   - 通过`delay_on`和`delay_off`文件设置闪烁时间参数

