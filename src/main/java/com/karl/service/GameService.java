package com.karl.service;

import java.util.Date;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.karl.db.domain.Player;
import com.karl.db.service.PlayerService;
import com.karl.domain.LotteryRule;
import com.karl.domain.PlayConfigDomain;
import com.karl.domain.RuntimeDomain;
import com.karl.utils.AppUtils;
import com.karl.utils.DigitalUtils;

@Service
public class GameService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GameService.class);

    @Autowired
    private RuntimeDomain runtimeDomain;

    @Autowired
    private PlayConfigDomain playConfigDomain;

    @Autowired
    private PlayerService playerService;

    /**
     * open the lottery
     */
    public void openLottery() {
        String result = "";

        Player player = null;
        Player banker = runningBanker();
        Long bankerResult = getResult(banker.getLatestLuck());

        for (String remarkName : runningPlayers().keySet()) {
            player = runningPlayers().get(remarkName);
            player.setLatestResult(getResult(player.getLatestLuck(), bankerResult));
        }
        resetPlayers(AppUtils.sortByValue(runningPlayers()));
    }

    /**
     * put bet info into the current player list
     * 
     * @param remarkName
     * @param betInfo
     */
    public void puttingBetInfo(String remarkName, Double betInfo) {
        Player player = runningPlayers().get(remarkName);
        if (player == null) {
            player = new Player();
            player.setRemarkName(remarkName);
        }
        player.setLatestBet(betInfo.longValue());
        player.setLatestBetTime(new Date().getTime());
        runningPlayers().put(remarkName, player);
    }

    /**
     * put luck info into the current player list
     * 
     * @param remarkName
     * @param luckInfo
     */
    public void puttingLuckInfo(String remarkName, Double luckInfo) {
        Player player = runningPlayers().get(remarkName);
        if (player == null) {
            player = new Player();
            player.setRemarkName(remarkName);
        }
        player.setLatestLuck(luckInfo);
        runningPlayers().put(remarkName, player);
    }

    /**
     * Calculate single player result
     * 
     * @param luckInfo
     * @return
     */
    private Long getResult(Double luckInfo) {
        Long result = Long.valueOf(0);
        EnumSet<LotteryRule> theRule = currentRule();
        for (Iterator<LotteryRule> iterator = theRule.iterator(); iterator.hasNext();) {
            LotteryRule lotteryRule = (LotteryRule) iterator.next();
            if (lotteryRule.getRuleResult(luckInfo)) {
                result = lotteryRule.getTimes();
                break;
            }
        }
        if (result.compareTo(Long.valueOf(0)) == 0) {
            result = DigitalUtils.getSumFromDouble(luckInfo);
        }
        return result;
    }

    /**
     * Calculate player result comparing to banker
     * 
     * @param luckInfo
     * @param bankerResult
     * @return
     */
    private Long getResult(Double luckInfo, Long bankerResult) {
        Long selfResult = getResult(luckInfo);
        return selfResult - bankerResult;
    }

    private Map<String, Player> runningPlayers() {
        return runtimeDomain.getRunningPlayeres();
    }

    private void resetPlayers(Map<String, Player> players) {
        runtimeDomain.setRunningPlayeres(players);
    }

    private Player runningBanker() {
        return runningPlayers().get(runtimeDomain.bankerRemarkName);
    }

    private EnumSet<LotteryRule> currentRule() {
        return runtimeDomain.getCurrentRule();
    }

}
