package com.mall;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 电商平台 — 唯一启动类
 *
 * 所有模块（auth、user、product、order、payment、admin）
 * 都在同一个 JVM 里运行，直接方法调用，不需要 RPC。
 *
 * 启动方式：
 *   方式1: mvnw spring-boot:run
 *   方式2: 在 IDE 中右键运行此类
 *   方式3: mvnw clean package -DskipTests && java -jar target/ease-mall-1.0.0-SNAPSHOT.jar
 *
 * 访问：
 *   接口文档: http://localhost:8080/doc.html
 *   注册接口: POST http://localhost:8080/auth/register
 *   登录接口: POST http://localhost:8080/auth/login
 */
@SpringBootApplication
@MapperScan("com.mall.**.mapper")  // 扫描所有子包下的 mapper
public class MallApplication {

    public static void main(String[] args) {
        SpringApplication.run(MallApplication.class, args);
        System.out.println("""

                ========================================
                  ease-mall 电商平台启动成功！端口: 8080
                  API 文档: http://localhost:8080/doc.html
                ========================================
                """);
    }
}
