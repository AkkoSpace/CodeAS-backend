package space.akko;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 应用启动测试
 *
 * @author akko
 * @since 1.0.0
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
class BackendApplicationTests {

    @Test
    void contextLoads() {
        // 测试Spring上下文是否能正常加载
        System.out.println("Spring context loaded successfully!");
        assertTrue(true); // 简单的断言，确保测试通过
    }

    @Test
    void simpleTest() {
        // 一个简单的测试，确保测试框架工作正常
        assertEquals(2, 1 + 1);
        assertNotNull("test");
        System.out.println("Simple test passed!");
    }
}
