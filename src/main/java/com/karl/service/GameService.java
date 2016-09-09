package com.karl.service;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.karl.db.domain.Player;
import com.karl.db.service.PlayerService;
import com.karl.domain.PlayConfigDomain;
import com.karl.domain.RuntimeDomain;

@Service
public class GameService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GameService.class);

    @Autowired
    private RuntimeDomain runtimeDomain;

    @Autowired
    private PlayConfigDomain playConfigDomain;

    @Autowired
    private PlayerService playerService;

    @Autowired
    private WebWechat webWechat;

    /**
     * open the lottery
     */
    public void openLottery() {
        String result = "";

        Player player = null;
        Player banker = runningBanker();
        Long bankerResult = getResult(banker.getLatestLuck());

        // TODU need to sort by some rule???????????
        for (String remarkName : runningPlayers().keySet()) {
            player = runningPlayers().get(remarkName);
            player.setLatestResult(getResult(player.getLatestLuck(), bankerResult));
        }

        webWechat.webwxsendmsg(result);
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
     * Calculate banker result
     * 
     * @param luckInfo
     * @return
     */
    private Long getResult(Double luckInfo) {
        Long result = Long.MIN_VALUE;
        // TODO
        return result;
    }

    /**
     * Calculate player result
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

    private Player runningBanker() {
        return runningPlayers().get(runtimeDomain.bankerRemarkName);
    }

}
