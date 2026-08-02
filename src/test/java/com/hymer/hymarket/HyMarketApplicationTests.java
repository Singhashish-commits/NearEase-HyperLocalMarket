package com.hymer.hymarket;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(TestElasticsearchConfig.class)
class HyMarketApplicationTests {

    @Test
    void contextLoads() {
    }

}
