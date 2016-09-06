package com.karl;

import java.util.List;
import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.karl.db.domain.Player;
import com.karl.db.service.PlayerService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Main.class)
public class DBtest {

    @Autowired
    private PlayerService playerService;

    @Test
    public void testSave() {
        Player player = null;
        for (int i = 0; i < 10; i++) {
            player = new Player();
            player.setPoints(Long.valueOf(i));
            player.setWeId(UUID.randomUUID().toString());
            player.setRemarkName("test" + i);
            playerService.save(player);
        }
    }

    @Test
    public void testQuery() {
        testSave();
        List<Player> list = playerService.getPlayeresLikeRemarkName("test%");
        Player player = null;
        for (int i = 0; i < list.size(); i++) {
            player = list.get(i);
            System.out.println(player.toString());
        }
    }
}
