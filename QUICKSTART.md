# 低代码平台快速启动指南

## 方式一：IntelliJ IDEA启动（推荐）

1. **打开IDEA**
   - 双击 `C:\Develope\IntelliJIDEA\IntelliJ IDEA 2025.3.2\bin\idea64.exe`
   - 打开项目：`D:\AsWork\lowcode-platform-server`

2. **等待依赖下载**
   - IDEA会自动识别Maven项目并下载依赖（首次需要几分钟）

3. **启动System服务**
   - 找到 `system-service/system-core/src/main/java/com/lowcode/platform/system/SystemApplication.java`
   - 右键 → Run 'SystemApplication'
   - 服务会在8081端口启动，使用内嵌H2数据库和Redis

4. **测试接口**
   - 访问：http://localhost:8081/doc.html
   - H2控制台：http://localhost:8081/h2-console (JDBC URL: jdbc:h2:mem:lowcode_platform)

---

## 方式二：命令行启动（需要Maven）

需要先配置JDK17和Maven：

```bash
# 下载JDK17
# https://adoptium.net/temurin/releases/?version=17

# 下载Maven
# https://maven.apache.org/download.cgi

# 配置环境变量后执行：
cd D:\AsWork\lowcode-platform-server
mvn spring-boot:run -pl system-service/system-core -Dspring-boot.run.profiles=dev
```

---

## 方式三：Docker启动（完整服务）

安装Docker Desktop后：

```bash
cd D:\AsWork\lowcode-platform-server
docker-compose up -d
```

---

## 当前dev配置说明

dev profile使用了：
- **H2内存数据库**：无需安装MySQL，数据在内存中
- **内嵌Redis**：无需安装Redis，自动启动
- **禁用Nacos**：单机测试，不需要服务注册

适合快速测试单个服务功能。