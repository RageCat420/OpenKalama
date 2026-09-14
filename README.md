# Kalama (Phoenix) 客户端 — 源码还原版本
## 进度80%（正在持续修复）

这是从混淆后的 `phoenix-1.21.1.jar` 还原回来的源码工程。原始源码树结构与 `Base`（粘液助手 SlimefunHelper 开源版）同源：客户端是在同一框架上开发、混淆后发布的版本。

## 当前状态（重要）

- **混淆已完全移除**：字符串解密、类/方法/字段重命名、垃圾类删除、Minecraft intermediary 重映射全部完成。
- **可运行 jar**: `Kalama-1.21.1-deobfuscated.jar` 已生成在上级目录 — 可直接放入 `.minecraft/mods/` 使用。Phantom Shield 保护已移除，字符串加密保留（运行时由每个类的 static 块自行解密，不依赖 skidonion），功能与原版完全一致。
- **编译状态**：`gradlew compileJava` 尚有约 5500 个编译错误（430 个文件）。这些错误是 Java 反编译器的固有产物——泛型擦除导致字节码中的类型信息丢失，反编译器无法重建正确的类型参数和方法签名。每个错误都是局部的，在 IDE 中逐条修复即可（大部分是添加强制转换或修正方法名）。按每个错误 2-5 分钟估算，完全修复约需 **180-450 小时**。错误详情见 `deobfuscation/compile_errors.json`。
- `unreachable_backup/` 存放因无引用而移出的死类（混淆器垃圾、未注册模块等），随时可以拷回。

## 工程结构

```
Source/
├── build.gradle / settings.gradle / gradle.properties   # Fabric Loom 工程（沿用 Base 的构建配置）
├── gradle/ + gradlew                                    # Gradle Wrapper（需 JDK 21）
├── libs/ + mapping/ + src/stubs/                        # 构建依赖（baritone、yarn 映射、JsMacros 桩）
├── src/main/java/me/matl114/...                         # 还原出的客户端源码（约 1300 个活动类）
├── src/main/resources/                                  # 资源（assets、mixins 配置、默认配置、词典等）
├── unreachable_backup/                                  # 已移出的无引用类（97 个），可随时恢复
└── deobfuscation/                                       # 类名/方法名/字段名映射表 + 全部还原工具脚本
```

## 还原过程中对混淆的处理

原 jar 使用 **Phantom Shield X v0.2.3.3**（skidonion）处理，包含以下混淆手段，全部已在还原中移除/抵消：

| 混淆手段 | 处理方式 |
|---|---|
| 类名/包名混淆（`a.a.b.a.d.X` 等） | Base 编译产物字节码指纹匹配（929 类 1.0 分精确匹配）+ 内部类 InnerClasses 证据 + 模块显示名 三方证据还原 |
| 字符串加密（DES/CBC + 字节替换表 + SHA-256 校验，443 个字符串池） | 字节码层面完整解密并回写为字符串常量，删除解密代码 |
| Minecraft intermediary 名（`class_2338`/`method_19538`） | Base 自带 yarn 映射 + 官方 yarn build.3 合并，并以对应代际 intermediary MC jar 作 classpath 重映射（方法/字段名也可还原） |
| 混淆器注入的垃圾类/水印类（圣经经文假方法、静态字段位移类） | 分多轮按引用图可达性分析删除（约 190 个），保留在 git 历史/备份中 |
| Windows 大小写不敏感的 160 个类名碰撞 | 先在字节码层唯一化改名再反编译，避免文件互相覆盖 |
| 方法名/字段名混淆 | 指纹匹配（3206 方法 + 1725 字段）+ 手工对齐框架 API（BaseModule/ModuleManager/ModulePath/WrapperSettingBuilder 全套）+ 沿继承链传播改名（子类覆写同步还原） |
| record 类重复字段伪影 | 自动修复 96 个 record 文件（组件与重复字段合并） |
| Access Widener 为 intermediary 格式 | 翻译为 named 格式（99 条全部还原） |

**`skidonion/WepAq/PhantomShieldX64.diy`（实为 DLL）与 `native_jvm.lib`**：经全量扫描，客户端自身的类中没有任何 native 方法，native 保护只用于保护 Phantom Shield 自己的运行时。还原源码**完全不需要**这两个文件，已随运行时一并移除。

## 与 Base（SlimefunHelper）的模块差异

- **Kalama 独有模块**：`ElytraSlowFall`（move 分类）。
- **Base 独有模块**（客户端没有）：FakeLag、FakePlayer、FakeBlockManager、NoGround、SwimControl、SearchLabel、TrialInfoESP、WalkControl、XaeroMapScanner、RecipePreview。
- 其余 175 个模块两边同名对应（部分类名在 Base 中被重命名，如 BadPackets→BadPacketsFix、Proxy→ConnectionProxy）。

## 已知残留

1. 约 6000 个编译错误待人工修完（泛型转换、方法引用等反编译瑕疵），集中在 `gui/`、`hacks/modules/combat|move|survival` 的少数大文件。
2. 部分辅助类名仍是 `XxxHelperX` / `XxxSubHelperX` 占位名（原始名不可考），逻辑完整。
3. ~170 个冷门 Minecraft 成员仍是 `field_XXXX`/`method_XXXX`（作者自定义 yarn 未收录）。
4. 模块显示名与类文件名可能不一致（如 `PrinterRewrite` 显示名 "Printer"），按字节码证据命名。

## 如何构建

```bash
# 需要 JDK 21
./gradlew compileJava   # 当前会报错，见上
./gradlew build         # 完全修复后可打包
```

`fabric.mod.json` 的 entrypoint 已由混淆名 `a.a.b` 改回 `me.matl114.SlimefunHelper`（入口类实名，模块 id 仍为 `kalama`）。

## 混淆对抗细节备忘

- 字符串池格式：`ISO_8859_1 字节数组 → 查表替换 → DES/CBC/PKCS5（零 IV）→ SHA-256 完整性尾部 → [u16 长度][UTF-8]` 序列化
- 完整映射表 `deobfuscation/mapping_full.tsv`：`P` 包（83）/ `C` 类（1047）/ `M` 方法（3206）/ `F` 字段（1725）
- 还原工具脚本在 `deobfuscation/tools/`（解密、重映射、指纹匹配、record 修复、死类分析等全流程可复现）
