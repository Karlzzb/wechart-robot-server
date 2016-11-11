<#compress>
${TOP}总۩积۩分۩榜${TOP}
▔▔▔▔▔▔▔▔▔
共:${rankSize?string["0"]}人
总积分:${sumPoint?string["0"]}分
────────────
<#if banker??>庄家 [${banker.remarkName}]${banker.points?string["0"]}</#if>
<#list rankList as player>${(player_index+1)?string["000"]}[${player.remarkName}]${player.points?string["0"]}
</#list>
────────────
${R}-专注报数-仅供娱乐,严禁赌博!
</#compress>