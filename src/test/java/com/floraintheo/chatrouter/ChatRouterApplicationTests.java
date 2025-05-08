package com.floraintheo.chatrouter;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(TestConfig.class)
class ChatRouterApplicationTests {

	@Test
	void contextLoads() {
	}

}
