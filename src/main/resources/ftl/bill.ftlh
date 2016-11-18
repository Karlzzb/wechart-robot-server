第 ${currentGameId?string["0000"]} 期 ${.now?string["yyyyMMdd"]}

<#if winnerList?has_content>
----${Face}以下加分${Face}----

<#list winnerList as winner>
<#if winner.resultPoint <= 0>
${winner.remarkName} ${winner.betPoint?string["0"]} ${winner.resultRuleName}(${winner.luckInfo?string["0.##"]}) 喝水
<#else>
${winner.remarkName} ${winner.betPoint?string["0"]} ${winner.resultRuleName}(${winner.luckInfo?string["0.##"]}) 赢 ${winner.resultPoint?string["0"]}
</#if>
</#list>

</#if>
<#if loserList?has_content>
----/::<以下减分/::<----

<#list loserList as loser>
${loser.remarkName} ${loser.betPoint?string["0"]} ${loser.resultRuleName}(${loser.luckInfo?string["0.##"]}) 输 ${loser.resultPoint?abs?string["0"]}
</#list>

</#if>
<#if paceList?has_content>
----${RUN}以下跑路${RUN}----

<#list paceList as pace>
${pace.remarkName} ${pace.betPoint?string["0"]} ${pace.resultRuleName}(${pace.luckInfo?string["0.##"]}) 跑
</#list>

</#if>
<#if allInList?has_content>
----/:,@f以下梭哈/:,@f----

<#list allInList as allInPlayer>
${allInPlayer.remarkName} ${allInPlayer.betPoint?string["0"]} ${allInPlayer.resultRuleName}(${allInPlayer.luckInfo?string["0.##"]}) <#if allInPlayer.resultPoint <= 0>输<#else>赢</#if> ${allInPlayer.resultPoint?abs?string["0"]}
</#list>

</#if>
<#if expireList?has_content>
----${EXPIRE}以下超时${EXPIRE}----

<#list expireList as expirePlayer>
${expirePlayer.remarkName} ${expirePlayer.betPoint?string["0"]} ${expirePlayer.resultRuleName}(${expirePlayer.luckInfo?string["0.##"]}) 输 ${expirePlayer.resultPoint?abs?string["0"]}
</#list>

</#if>
<#if illegalPlayers?has_content>
----${FaceEM}抢包无分${FaceEM}----

<#list illegalPlayers as row>
${row}
</#list>

</#if>

头 包 时 间 : ${firstPackageTime}
庄 包 时 间 : ${bankerPackageTime}
尾 包 时 间 : ${lastPackageTime}
总 计 时 间 : ${packageInterval?string["0"]}秒
超 时 玩 家 : ${expireList?size?string["0"]} 个
--------------------------
本 局 庄 家 : ${gameInfo.bankerRemarkName}
<#if bankerTimeOut>
庄 家超时： ${bankerPackageTime}
</#if>
庄 包 : [${gameInfo.luckInfo?string["0.##"]}]  ${gameInfo.resultRuleName} [${gameInfo.resultTimes?string["0"]}倍]
吃 : ${loserList?size}家 /赔 : ${winnerList?size}家 /跑 : ${paceList?size}家
开 局 庄 分 : ${(bankerBetPoint - gameInfo.resultPoint)?string["0"]}
庄 家 获 利 : <#if (gameInfo.resultPoint + gameInfo.manageFee + gameInfo.packageFee + gameInfo.firstBankerFee + gameInfo.bankerWinCut) <= 0>输<#else>赢</#if>${(gameInfo.resultPoint + gameInfo.manageFee + gameInfo.packageFee + gameInfo.firstBankerFee + gameInfo.bankerWinCut)?abs?string["0"]}
<#if showManageFee>
管 理 费 用 : ${gameInfo.manageFee?string["0"]}
</#if>
发 包 金 额 : ${gameInfo.packageFee?string["0"]}
本 局 基 金 : ${gameInfo.bankerWinCut?string["0"]}
剩 余 庄 分 : ${bankerBetPoint?string["0"]}
═══════════
${R}-专注报数-仅供娱乐,严禁赌博!