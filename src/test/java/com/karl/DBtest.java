package com.karl;

import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.karl.db.domain.Player;
import com.karl.db.service.PlayerService;
import com.karl.domain.RuntimeDomain;
import com.karl.service.GameService;
import com.karl.utils.AppUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Main.class)
public class DBtest {

    @Autowired
    private PlayerService playerService;
    
    @Autowired
    private GameService gameService;
    
    @Autowired
    private RuntimeDomain runtimeDomain;

    @Test
    public void testSave() {
        Player player = null;
        for (int i = 0; i < 10; i++) {
            player = new Player();
            player.setPoints(Long.valueOf(i));
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
    
    
    @Test
    public void testGameRound() {
    	runtimeDomain.setCurrentGameKey(AppUtils.PLAYLONG);
    	for (int i = 0; i < 20; i++) {
    		Player pEntity= new Player();
    		pEntity.setRemarkName("test"+i);
    		pEntity.setPoints(Long.valueOf(i*50));
        	gameService.savePlayEntity(pEntity);
        	if (i ==5) {
        		runtimeDomain.setBankerRemarkName(pEntity.getRemarkName());
        		runtimeDomain.setBankerBetPoint(pEntity.getPoints());
        	}
		}
		runtimeDomain.setBankerIndex(3);
		runtimeDomain.setPackageNumber(20);
    	runtimeDomain.setGlobalGameSignal(Boolean.TRUE);
    	String content = gameService.declareGame();
    	System.out.println(content);
    	
    	for (int i = 0; i < 20; i++) {
        	gameService.puttingBetInfo("1234324", "test"+i, "50", Boolean.FALSE);
		}
    	runtimeDomain.setGlobalGameSignal(Boolean.FALSE);
    	content = gameService.declareGame();
    	System.out.println(content);
    	for (int i = 0; i < 20; i++) {
        	gameService.puttingLuckInfo(i, "test"+i, Double.valueOf(i*10+"."+i), new Date());
		}
    	System.out.println(gameService.openLottery());
   }

}
