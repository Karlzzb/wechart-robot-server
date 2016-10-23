package com.karl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Random;

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
public class AppTest {

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
		Random r = new Random();
		runtimeDomain.setCurrentGameKey(AppUtils.PLAYLONGSPLIT);
		for (int i = 1; i < 11; i++) {
			Player pEntity = new Player();
			pEntity.setRemarkName("test" + i);
			pEntity.setPoints(Long.valueOf(Math.abs(r.nextInt(2000))));
			gameService.savePlayEntity(pEntity);
			if (i == 5) {
				runtimeDomain.setBankerRemarkName(pEntity.getRemarkName());
				runtimeDomain.setBankerBetPoint(pEntity.getPoints());
			}
		}
		runtimeDomain.setBankerIndex(3);
		runtimeDomain.setPackageNumber(20);
		runtimeDomain.setGlobalGameSignal(Boolean.TRUE);
		String content = gameService.declareGame();
		System.out.println(content);

		for (String remarkName : runtimeDomain.getRunningPlayeres().keySet()) {
			if (remarkName.equals(runtimeDomain.getBankerRemarkName())) {
				continue;
			}
			gameService.puttingBetInfo(
					runtimeDomain.getRunningPlayeres().get(remarkName)
							.getWebchatId(),
					runtimeDomain.getRunningPlayeres().get(remarkName)
							.getRemarkName(),
					String.valueOf(Math.abs(r.nextInt(20))
							+ "/"
							+ runtimeDomain.getRunningPlayeres()
									.get(remarkName).getPoints() / 5),
					Boolean.FALSE);
		}
		runtimeDomain.setGlobalGameSignal(Boolean.FALSE);
		content = gameService.declareGame();
		System.out.println(content);
		for (int i = 1; i < 20; i++) {
			gameService
					.puttingLuckInfo(
							i,
							"test" + i,
							new BigDecimal(r.nextDouble() * 9).setScale(2,
									BigDecimal.ROUND_HALF_UP).doubleValue(),
							new Date());
		}
		System.out.println(gameService.openLottery());
	}

	@Test
	public void testGameRoundAuto() {
		Random r = new Random();
		runtimeDomain.setCurrentGameKey(AppUtils.PLAYLUCKWAY);
		for (int i = 1; i < 11; i++) {
			Player pEntity = new Player();
			pEntity.setRemarkName("test" + i);
			pEntity.setPoints(Long.valueOf(Math.abs(r.nextInt(2000))));
			gameService.savePlayEntity(pEntity);
			if (i == 5) {
				runtimeDomain.setBankerRemarkName(pEntity.getRemarkName());
				runtimeDomain.setBankerBetPoint(pEntity.getPoints());
			}
		}
		runtimeDomain.setBankerIndex(0);
		runtimeDomain.setPackageNumber(20);
		String content = gameService.declareBetStar();
		System.out.println(gameService.publishRanking());
		System.out.println(content);

		runtimeDomain.setGlobalGameSignal(Boolean.FALSE);
		content = gameService.declareGame();
		System.out.println(content);
		for (int i = 1; i < 11; i++) {
			gameService
					.puttingLuckInfo(
							i,
							"test" + i,
							new BigDecimal(r.nextDouble() * 9).setScale(2,
									BigDecimal.ROUND_HALF_UP).doubleValue(),
							new Date());
		}
		System.out.println(gameService.openLottery());
		System.out.println(gameService.publishRanking());
	}

	@Test
	public void testGameRoundAuto2() {
		runtimeDomain.setCurrentGameKey(AppUtils.PLAYLUCKWAY);
		runtimeDomain.setCurrentLotteryRule(AppUtils.LOTTERYRULE2);
		Player pEntity = new Player();
		pEntity.setRemarkName("test1");
		pEntity.setPoints(10000L);
		gameService.savePlayEntity(pEntity);
		pEntity = new Player();
		pEntity.setRemarkName("test2");
		pEntity.setPoints(10000L);
		gameService.savePlayEntity(pEntity);
		pEntity = new Player();
		pEntity.setRemarkName("test3");
		pEntity.setPoints(3180L);
		gameService.savePlayEntity(pEntity);
			
			
		runtimeDomain.setBankerRemarkName("test3");
		runtimeDomain.setBankerBetPoint(3180L);
		runtimeDomain.setBankerIndex(3);
		runtimeDomain.setPackageNumber(10);
		runtimeDomain.setDefiendBet(100L);
		String content = gameService.declareBetStar();
		runtimeDomain.setGlobalGameSignal(true);
		System.out.println(gameService.publishRanking());
		System.out.println(content);
		gameService.puttingLuckInfo(1, "test1", Double.valueOf(0.06),
				new Date());
		gameService.puttingLuckInfo(2, "test2", Double.valueOf(0.4),
				new Date());
		gameService.puttingLuckInfo(3, "test3", Double.valueOf(0.54),
				new Date());
		runtimeDomain.setcurrentFirstPacageTime(new Date());
		runtimeDomain.setcurrentLastPacageTime(new Date());

		
		runtimeDomain.setGlobalGameSignal(Boolean.FALSE);
		content = gameService.declareGame();
		System.out.println(content);
		System.out.println(gameService.openLottery());
		System.out.println(gameService.publishRanking());
		
	}

}
