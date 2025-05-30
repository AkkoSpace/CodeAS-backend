# 代码规范

## 概述

本文档定义了 CodeAS Backend 项目的代码编写规范，确保代码质量、可读性和可维护性。

## Java 代码规范

### 基础规范

遵循 [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html) 和阿里巴巴Java开发手册。

### 命名规范

#### 包命名
```java
// 正确
space.akko.backend.user.service
space.akko.backend.auth.controller

// 错误
space.akko.Backend.User.Service
space.akko.backend.userService
```

#### 类命名
```java
// 正确 - 使用大驼峰命名法
public class UserService { }
public class AuthenticationController { }
public class DatabaseConfiguration { }

// 错误
public class userService { }
public class User_Service { }
```

#### 方法命名
```java
// 正确 - 使用小驼峰命名法
public User findUserById(Long id) { }
public boolean isUserActive() { }
public void updateUserStatus() { }

// 错误
public User FindUserById(Long id) { }
public boolean Is_User_Active() { }
```

#### 变量命名
```java
// 正确
private String userName;
private List<User> activeUsers;
private final int MAX_RETRY_COUNT = 3;

// 错误
private String user_name;
private List<User> ActiveUsers;
private final int maxRetryCount = 3; // 常量应全大写
```

### 代码结构

#### 类结构顺序
```java
public class UserService {
    // 1. 静态常量
    private static final String DEFAULT_ROLE = "USER";
    
    // 2. 实例变量
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    // 3. 构造函数
    public UserService(UserRepository userRepository, 
                      PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    // 4. 公共方法
    public User createUser(CreateUserRequest request) {
        // 实现
    }
    
    // 5. 私有方法
    private void validateUser(User user) {
        // 实现
    }
}
```

#### 方法长度
- 单个方法不超过50行
- 复杂逻辑拆分为多个私有方法
- 使用有意义的方法名描述功能

### 注释规范

#### 类注释
```java
/**
 * 用户服务类
 * 
 * 提供用户管理相关的业务逻辑，包括：
 * - 用户创建和更新
 * - 用户认证和授权
 * - 用户状态管理
 * 
 * @author akko
 * @since 1.0.0
 */
public class UserService {
}
```

#### 方法注释
```java
/**
 * 根据用户ID查找用户
 * 
 * @param id 用户ID，不能为null
 * @return 用户对象，如果不存在返回null
 * @throws IllegalArgumentException 当id为null时抛出
 */
public User findUserById(Long id) {
    if (id == null) {
        throw new IllegalArgumentException("用户ID不能为空");
    }
    return userRepository.findById(id).orElse(null);
}
```

#### 行内注释
```java
// 检查用户是否已激活
if (user.getStatus() == UserStatus.ACTIVE) {
    // 更新最后登录时间
    user.setLastLoginTime(LocalDateTime.now());
}
```

### 异常处理

#### 异常命名
```java
// 正确
public class UserNotFoundException extends RuntimeException { }
public class InvalidPasswordException extends RuntimeException { }

// 错误
public class UserNotFound extends RuntimeException { }
public class BadPassword extends RuntimeException { }
```

#### 异常处理模式
```java
// 正确 - 具体的异常处理
public User findUserById(Long id) {
    try {
        return userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException("用户不存在: " + id));
    } catch (DataAccessException e) {
        log.error("查询用户失败: id={}", id, e);
        throw new ServiceException("查询用户失败", e);
    }
}

// 错误 - 捕获所有异常
public User findUserById(Long id) {
    try {
        return userRepository.findById(id).orElse(null);
    } catch (Exception e) {
        return null; // 丢失异常信息
    }
}
```

## Spring Boot 规范

### 注解使用

#### Controller层
```java
@RestController
@RequestMapping("/api/v1/users")
@Validated
@Slf4j
public class UserController {
    
    @PostMapping
    public ResponseEntity<UserResponse> createUser(
            @Valid @RequestBody CreateUserRequest request) {
        // 实现
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(
            @PathVariable @Min(1) Long id) {
        // 实现
    }
}
```

#### Service层
```java
@Service
@Transactional(readOnly = true)
@Slf4j
public class UserService {
    
    @Transactional
    public User createUser(CreateUserRequest request) {
        // 实现
    }
}
```

#### Repository层
```java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    @Query("SELECT u FROM User u WHERE u.email = :email AND u.status = 'ACTIVE'")
    Optional<User> findActiveUserByEmail(@Param("email") String email);
}
```

### 配置规范

#### 配置类
```java
@Configuration
@EnableConfigurationProperties(AppProperties.class)
public class DatabaseConfiguration {
    
    @Bean
    @Primary
    public DataSource primaryDataSource() {
        // 实现
    }
}
```

#### 属性配置
```java
@ConfigurationProperties(prefix = "app")
@Data
public class AppProperties {
    private Security security = new Security();
    private Database database = new Database();
    
    @Data
    public static class Security {
        private String jwtSecret;
        private Duration jwtExpiration = Duration.ofHours(24);
    }
}
```

## 数据库规范

### 实体类规范

```java
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;
    
    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;
    
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private UserStatus status;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
```

### 数据库命名规范

- 表名：小写，下划线分隔，复数形式 (`users`, `user_roles`)
- 字段名：小写，下划线分隔 (`user_name`, `created_at`)
- 索引名：`idx_表名_字段名` (`idx_users_email`)
- 外键名：`fk_表名_字段名` (`fk_users_role_id`)

## API 设计规范

### RESTful API

```java
// 正确的RESTful设计
@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    
    @GetMapping                          // GET /api/v1/users
    public Page<UserResponse> getUsers() { }
    
    @GetMapping("/{id}")                 // GET /api/v1/users/1
    public UserResponse getUser(@PathVariable Long id) { }
    
    @PostMapping                         // POST /api/v1/users
    public UserResponse createUser(@RequestBody CreateUserRequest request) { }
    
    @PutMapping("/{id}")                 // PUT /api/v1/users/1
    public UserResponse updateUser(@PathVariable Long id, 
                                  @RequestBody UpdateUserRequest request) { }
    
    @DeleteMapping("/{id}")              // DELETE /api/v1/users/1
    public void deleteUser(@PathVariable Long id) { }
}
```

### 响应格式

```java
// 统一响应格式
@Data
@Builder
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private String timestamp;
    private String path;
}

// 使用示例
@GetMapping("/{id}")
public ResponseEntity<ApiResponse<UserResponse>> getUser(@PathVariable Long id) {
    UserResponse user = userService.findById(id);
    return ResponseEntity.ok(
        ApiResponse.<UserResponse>builder()
            .success(true)
            .message("查询成功")
            .data(user)
            .timestamp(LocalDateTime.now().toString())
            .build()
    );
}
```

## 测试规范

### 单元测试

```java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @InjectMocks
    private UserService userService;
    
    @Test
    @DisplayName("应该成功创建用户")
    void shouldCreateUserSuccessfully() {
        // Given
        CreateUserRequest request = CreateUserRequest.builder()
            .username("testuser")
            .email("test@example.com")
            .password("password123")
            .build();
        
        User savedUser = User.builder()
            .id(1L)
            .username("testuser")
            .email("test@example.com")
            .build();
        
        when(passwordEncoder.encode("password123")).thenReturn("encoded_password");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        
        // When
        User result = userService.createUser(request);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("testuser");
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        
        verify(userRepository).save(any(User.class));
        verify(passwordEncoder).encode("password123");
    }
}
```

### 集成测试

```java
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.yml")
class UserControllerIntegrationTest {
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Autowired
    private UserRepository userRepository;
    
    @Test
    @DisplayName("应该成功创建用户")
    void shouldCreateUserSuccessfully() {
        // Given
        CreateUserRequest request = CreateUserRequest.builder()
            .username("testuser")
            .email("test@example.com")
            .password("password123")
            .build();
        
        // When
        ResponseEntity<ApiResponse> response = restTemplate.postForEntity(
            "/api/v1/users", request, ApiResponse.class);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().isSuccess()).isTrue();
        
        Optional<User> savedUser = userRepository.findByUsername("testuser");
        assertThat(savedUser).isPresent();
    }
}
```

## 日志规范

### 日志级别

- **ERROR**: 系统错误，需要立即处理
- **WARN**: 警告信息，可能影响系统运行
- **INFO**: 重要的业务流程信息
- **DEBUG**: 调试信息，开发环境使用

### 日志格式

```java
@Slf4j
@Service
public class UserService {
    
    public User createUser(CreateUserRequest request) {
        log.info("开始创建用户: username={}", request.getUsername());
        
        try {
            User user = buildUser(request);
            User savedUser = userRepository.save(user);
            
            log.info("用户创建成功: id={}, username={}", 
                    savedUser.getId(), savedUser.getUsername());
            
            return savedUser;
        } catch (Exception e) {
            log.error("用户创建失败: username={}", request.getUsername(), e);
            throw new ServiceException("用户创建失败", e);
        }
    }
}
```

## 性能规范

### 数据库查询优化

```java
// 正确 - 使用分页查询
@Query("SELECT u FROM User u WHERE u.status = :status")
Page<User> findByStatus(@Param("status") UserStatus status, Pageable pageable);

// 正确 - 使用投影减少数据传输
@Query("SELECT new space.akko.backend.user.dto.UserSummary(u.id, u.username, u.email) " +
       "FROM User u WHERE u.status = 'ACTIVE'")
List<UserSummary> findActiveUserSummaries();

// 错误 - 查询所有数据
@Query("SELECT u FROM User u")
List<User> findAllUsers(); // 可能返回大量数据
```

### 缓存使用

```java
@Service
public class UserService {
    
    @Cacheable(value = "users", key = "#id")
    public User findById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException("用户不存在"));
    }
    
    @CacheEvict(value = "users", key = "#user.id")
    public User updateUser(User user) {
        return userRepository.save(user);
    }
}
```

## 安全规范

### 输入验证

```java
@PostMapping
public ResponseEntity<UserResponse> createUser(
        @Valid @RequestBody CreateUserRequest request) {
    // @Valid 注解自动验证请求参数
}

@Data
public class CreateUserRequest {
    
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度必须在3-50之间")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "用户名只能包含字母、数字和下划线")
    private String username;
    
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;
    
    @NotBlank(message = "密码不能为空")
    @Size(min = 8, message = "密码长度至少8位")
    private String password;
}
```

### 敏感信息处理

```java
// 正确 - 不记录敏感信息
log.info("用户登录: username={}", username);

// 错误 - 记录敏感信息
log.info("用户登录: username={}, password={}", username, password);

// 正确 - 响应中不包含敏感信息
@JsonIgnore
private String passwordHash;

@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
private String password;
```

## 工具配置

### IDE配置

#### IntelliJ IDEA
1. 导入 Google Java Style 配置
2. 启用自动格式化
3. 配置代码检查规则

#### VS Code
1. 安装 Java Extension Pack
2. 配置 Checkstyle 插件
3. 启用自动保存格式化

### Maven配置

```xml
<!-- 代码格式化插件 -->
<plugin>
    <groupId>com.spotify.fmt</groupId>
    <artifactId>fmt-maven-plugin</artifactId>
    <version>2.21.1</version>
    <executions>
        <execution>
            <goals>
                <goal>format</goal>
            </goals>
        </execution>
    </executions>
</plugin>

<!-- 代码检查插件 -->
<plugin>
    <groupId>com.puppycrawl.tools</groupId>
    <artifactId>checkstyle</artifactId>
    <version>10.12.4</version>
    <configuration>
        <configLocation>checkstyle.xml</configLocation>
    </configuration>
</plugin>
```

## 检查清单

### 代码提交前检查

- [ ] 代码格式符合规范
- [ ] 所有方法都有适当的注释
- [ ] 异常处理完整
- [ ] 单元测试覆盖率 > 80%
- [ ] 没有硬编码的配置值
- [ ] 日志记录适当
- [ ] 性能考虑合理
- [ ] 安全检查通过

### 代码审查检查

- [ ] 业务逻辑正确
- [ ] 代码可读性良好
- [ ] 错误处理完善
- [ ] 测试用例充分
- [ ] 文档更新及时
- [ ] 性能影响评估
- [ ] 安全风险评估
