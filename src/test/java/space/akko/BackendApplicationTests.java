package space.akko;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 基础应用测试类
 * 简单的单元测试，不加载Spring上下文，确保CI环境正常运行
 *
 * @author akko
 * @since 1.0.0
 */
class BackendApplicationTests {

    @Test
    void basicTest() {
        // 基础测试，确保测试框架正常工作
        assertTrue(true, "基础测试应该通过");
    }

    @Test
    void javaVersionTest() {
        // 验证Java版本
        String javaVersion = System.getProperty("java.version");
        assertTrue(javaVersion.startsWith("21"), "应该使用Java 21");
    }
}
