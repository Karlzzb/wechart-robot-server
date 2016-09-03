package com.karl;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.karl.service.WebWechat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Main.class)
public class LoginTests {

    @Autowired
    private WebWechat webWechat;

    @Test
    public void testConnection() {
        assertTrue(webWechat.getRuntimeDomain() != null);
    }

}
