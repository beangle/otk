# OTK Native-Image 构建与适配

> 最后更新：2026-09-21
>
> 本文说明 beangle-otk-ws 原生镜像的构建方式、为跑通它做的应用侧改动与元数据配置，
> 以及重新采集这些元数据的方法。通用的构建/打包/增量补丁/上传规则见 `beangle/build`
> 的 `docs/native.md`，本文不重复。

## 一、状态（GraalVM CE 25.3.4.1 / JDK 25）

| 项 | 结果 |
| --- | --- |
| 镜像 | 成功，`otk-ws` 151MB，发行包 tar.gz 62MB |
| 启动 | Tomcat 0.1s；CDI 52 beans；Action 扫描 11 actions / 16 mappings |
| `/captcha/image/{id}` | 成功，JPEG，90x35、768 色（BufferedImage + Graphics2D + HarfBuzz 排版）；曾因缺一条 HarfBuzz 反射注册而只能画出白底，见 4.2 |
| `/code/qr/{content}`、`/code/bar/{content}` | 成功，PNG |
| `/doc/excel` | 成功，xlsx（POI） |
| `/doc/pdf?url=...` | 成功，38KB PDF（走 Chrome CDP，需要宿主有 Chrome/wkhtmltopdf） |
| `/lang/en/check` | 成功（LanguageTool） |
| `/sns/person/pinyin/{name}`、`/sns/person/id/{idcard}` | 成功（pinyin4j、本地行政区划数据） |
| `/sys/time/*`、`/security/password/generate`、`/about` | 成功 |
| `/net/url/*` | 需带鉴权的 Redis，本机 NOAUTH，与镜像无关 |

## 二、构建与运行

```bash
export GRAALVM_HOME=/home/chaostone/local/graalvm-community-25.3.4.1+1.1
sbt -batch "nativeDist"     # 链接镜像 → 打 tar.gz → 安装到 ~/.m2/snapshots/...
sbt -batch "nativeDistUpload"   # 可选，上传到 sas.openurp.net 的 native 仓库

# 运行（无需任何 -D 参数）
target/out/jvm/u/beangle-otk-ws/native-image/otk-ws --port=8081
```

`build.sbt` 里的镜像入口与选项：

| 设置 | 值 | 说明 |
| --- | --- | --- |
| `Compile / mainClass` | `org.beangle.sas.engine.tomcat.Bootstrap` | 由 `beangle-sas-engine` 提供，起内嵌 Tomcat |
| `nativeImageOutput` | `<NativeImage/target>/otk-ws` | 镜像文件名 |
| `-Os` | | 体积优先 |
| `--sun-misc-unsafe-memory-access=allow` | | 关掉 Scala `LazyVals` 触发的 JDK 24+ `Unsafe` 弃用告警 |
| `-H:+AddAllCharsets` | | 镜像内置全部字符集 |
| `-H:+UnlockExperimentalVMOptions` / `-H:+ReportExceptionStackTraces` | | 允许实验选项、构建失败时打印完整栈 |
| `-H:IncludeResourceBundles=org.apache.xmlbeans.impl.regex.message` | | XmlBeans（POI 解析 xlsx）的报错信息 |
| `--initialize-at-build-time=ch.qos.logback,org.slf4j,org.xml.sax,org.w3c.dom,javax.xml` | | 提前初始化，减少运行期开销 |

`beangle-sas-engine` 与 `tomcat-embed-core` 以 `% "provided"` 引入：它们只服务于镜像入口，
既不进 WAR，也不随 POM 传递。

## 三、应用侧改动：AwtInitializer

`src/main/scala/org/beangle/otk/config/AwtInitializer.scala` + `beangle.xml` 的
`<web><initializer>`，`order = -2`，即早于 she 的 `ConfigInitializer(-1)`、
`ContainerInitializer(0)`——在任何会用到 AWT 的 Bean 初始化之前执行。

只在原生镜像里做事（用 `org.graalvm.nativeimage.imagecode` 判定），JVM/WAR 部署下是空操作：

1. 把 `java.awt.headless` 固定为 `true`。服务端不渲染窗口，且镜像落在带 `DISPLAY`
   的机器上时，JDK 会走 X11 分支（`X11GraphicsEnvironment.initDisplay` → JNI
   `FindClass sun/awt/SunToolkit`）并失败。
2. `java.home` 若为空（原生镜像没有这个属性），指向 `<java.io.tmpdir>/beangle-otk-home`
   并补出 `lib` 子目录，详见 4.4。

## 四、native-image 元数据

| 文件 | 内容 | 来源 |
| --- | --- | --- |
| `META-INF/native-image/jdk-awt/` | java.desktop 的 JNI 回调/字段（`ColorModel`、`Raster`/`SampleModel` 族、`sun.font.*`、`sun.java2d.*`、JPEG ImageIO）+ HarfBuzz 的 FFM `foreign` 段 + `sun/awt/resources/awt*.properties`、ICU | tracing agent（见 4.1~4.3） |
| `META-INF/native-image/jdk-management/` | `sun.management.VMManagementImpl` 等 JMX 的 JNI 字段 | tracing agent |
| `META-INF/native-image/otk-cdp/` | CDP WebSocket 客户端（Tyrus/Grizzly）的反射与资源 | tracing agent，见 4.4 |
| `META-INF/native-image/{jdk-xml,log4j}/` | java.xml 序列化、log4j2 的资源 | 沿用 ems 的采集结果 |
| `META-INF/beangle/aot-registrars.txt` → `org.beangle.otk.aot.OtkAotHints` | otk 自身的资源（pinyin 数据、行政区划、LanguageTool 语言与规则类） | 构建期扫描 classpath |
| `resource_managed/.../native-image/beangle/` | AotPlugin 汇总的 1400+ 反射条目与资源 glob | 构建期生成，不要手改 |

### 4.1 验证码为什么需要 JNI 注册

`GmailEngine` 画图走的是 `BufferedImage.createGraphics()`，JDK 里这条路是 **JNI** 而非反射：

- `java.awt.image.ColorModel.<clinit>` → `initIDs` → `GetFieldID(nBits)` /
  `GetStaticMethodID(getRGBdefault)`，缺失时报
  `NoSuchFieldError: java.awt.image.ColorModel.nBits`；
- `sun.font.*`、`sun.java2d.loops.*`、JPEG 编码器的回调同理。

这些条目由 tracing agent 在 JVM 上跑真实接口采集（见第五节），按类型合并进
`jdk-awt/reachability-metadata.json`。注意 `sun.font.Font2D` 的回调名随 JDK 变化（JDK 21
是 `charToGlyph`，25 是 `charToGlyphRaw`/`charToVariationGlyphRaw`），升级 JDK 必须复核。

### 4.2 HarfBuzz 的 FFM 注册与那条静默失败的反射

JDK 24 起文本排版默认引擎是 HarfBuzz，`sun.font.HBShaper` 通过 **FFM（Panama）downcall**
调 `libharfbuzz`。native-image 要求逐条登记 downcall/upcall，否则运行期抛
`MissingForeignRegistrationError`，所以 `jdk-awt/reachability-metadata.json` 里有一整段
`foreign`（5 个 downcall + 6 个 `HBShaper` 静态 upcall + 1 个通用 upcall）。

但**只登记 `foreign` 还不够**。`HBShaper` 给 HarfBuzz 建 face 时要回调字体表数据，
`FaceRef.createFace()` 是这样拿方法句柄的：

```java
get_table_data_fn = getBoundUpcallStub(Arena.ofAuto(), Font2D.class, font2D,
    "getFontTableData", JAVA_INT, JAVA_INT, ADDRESS);
...
MethodHandle mh = MH_LOOKUP.findStatic(HBShaper.class, mName, mType);   // 找不到就 return null
private void createFace() {
    try { ... } catch (Throwable t) { }      // ← 异常被吞掉
}
```

`findStatic` 在 native 里查的是反射注册表：**没有为
`sun.font.HBShaper.getFontTableData(Font2D, int, MemorySegment)` 登记反射，它就不会出现在
方法表里，`findStatic` 返回 null，upcall stub 为 null，face 为 null**，于是
`SunLayoutEngine.layout` 里 `if (face != null)` 不成立——整个排版过程**悄无声息地什么都不做**。

症状很难自己指向这里：

- 接口不报错、返回 200，只是图片是纯白底（distinctColor=1）；
- `Graphics2D.drawString(String, int, int)` 照常正常（它不走 HarfBuzz layout）；
- 只有 `TextLayout` 系列失效：`new TextLayout(aci, frc).getAdvance() == 0`、`getBounds()` 全 0，
  而 `Font.createGlyphVector` / `getLineMetrics` / `getStringBounds` 都还是对的；
- 日志里没有任何异常，因为异常在 `catch (Throwable)` 里被丢弃。

因此条目必须手工保留（agent 其实采得到，见第五节）：

```json
{
  "type": "sun.font.HBShaper",
  "methods": [
    {"name": "getFontTableData",
     "parameterTypes": ["sun.font.Font2D", "int", "java.lang.foreign.MemorySegment"]}
  ]
}
```

排查手法：把问题缩到 40 行以内的小程序（`BufferedImage` + `Graphics2D` +
`TextLayout(aci, frc)`）单独打一个 native 镜像，打印 `getAdvance()`，比反复重建整个服务快得多。

### 4.3 java.home 与字体配置（最容易踩的一节）

原生镜像里 `System.getProperty("java.home")` **是 null**（JVM 上永远非空），而字体代码依赖它：

1. `sun.awt.FontConfiguration.findFontConfigFile` 在 `java.home` 为空时直接
   `java.lang.Error: java.home property not set`；
2. 即便 `java.home` 有值，`X11FontManager.createFontConfiguration` 也只在
   `<java.home>/lib` **存在**（即 `foundOsSpecificFile()` 为 false）时，才会先试
   `FcFontConfiguration`（从 fontconfig 合成字体配置），否则回落到 `MFontConfiguration`
   并抛 `RuntimeException: Fontconfig head is null, check your fonts or fonts configuration`。

`AwtInitializer` 因此造一个带 `lib` 子目录的 `java.home`。字形数据的来源：

- 有缓存：读 `~/.java/fonts/<jdk 版本>/fcinfo-*.properties`；
- 没缓存：镜像内的 `libfontmanager` 现场调 fontconfig 合成一次，并写回该缓存
  （已验证：`-Duser.home=<空目录>` 时也能自建缓存）。

也就是说 native 版的字体能力依赖宿主机的 fontconfig，这与 JVM 部署一致；换机器首启会慢一点。

### 4.4 /doc/pdf 需要 CDP 客户端的反射

PDF 由 `ChromePdfMaker` 通过 CDP over WebSocket 驱动 Chrome。Tyrus 用
ServiceLoader/`Class.forName` 发现 `GrizzlyClientContainer` 并实例化，`AotPlugin` 的静态
分析看不到这些类，缺了就会在 `ClientManager.createClient()` 处抛
`DeploymentException: org.glassfish.tyrus.container.grizzly.client.GrizzlyClientContainer`。
`otk-cdp/reachability-metadata.json` 收录了 agent 采到的 22 个类型（Tyrus 容器/编解码器、
Grizzly 传输类）与 4 条 Grizzly 资源。

## 五、重新采集 agent 元数据

```bash
# 1) 取一份真实 classpath
sbt -batch "export Compile/fullClasspath"   # 把 ${OUT}/${CSR_CACHE}/${IVY_HOME} 展开成实际路径

# 2) 在 JVM 上跑真实服务，逐个打接口；关键是要覆盖 AWT/HarfBuzz 与 PDF 两条路
java -Djava.awt.headless=true -Duser.home=/tmp/fresh-home \
  -agentlib:native-image-agent=config-output-dir=/tmp/otk-agent \
  -cp <classpath> org.beangle.sas.engine.tomcat.Bootstrap --port=8090
curl -s -o /dev/null http://localhost:8090/captcha/image/<50 字符 id>
curl -s -o /dev/null "http://localhost:8090/doc/pdf?url=http://localhost:8090/about"
kill -INT <pid>     # 必须优雅退出，agent 在关机钩子里写文件

# 3) 只挑本次关心的部分合并进 META-INF/native-image/*，不要整份替换
```

三个注意点：

- `-Duser.home=<空目录>` 很重要：`~/.java/fonts` 有缓存时 JVM 不会走 `FontConfigManager`，
  agent 就采不到那些 JNI 条目。
- agent 的整份输出远大于实际需要（BouncyCastle、POI schema 等），按包名过滤后再合并。
- **过滤粒度是"条目"，不是"文件"，更不能按 `jniAccessible` 挑。** `jdk-awt/` 这份看起来
  是 JNI 清单，但 4.2 里那条 `HBShaper.getFontTableData` 是 agent 采到的普通反射条目
  （无 `jniAccessible`），过滤掉它就会退化成一个能启动、接口 200、却画不出字的镜像。
  本次事故就是按 `jniAccessible` 收敛时把它丢掉了。

## 六、已知限制

- `/net/url/*` 需要 Redis（本机未鉴权，报 NOAUTH），与镜像无关。
- `/doc/pdf` 依赖宿主机的 Chrome 或 wkhtmltopdf；镜像本身不内含浏览器。
- `/doc/excel`、`/doc/docx`、`/doc/pdf` 若改用 LibreOffice（`jodconverter-local`）路径，
  同样要求宿主机装有 LibreOffice。
- 镜像内的 `java.home` 指向 `/tmp` 下的自建目录（见 4.3）；若宿主机 `/tmp` 不可写，
  验证码等用到字体的接口会在启动时失败。
