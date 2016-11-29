<#compress>
有效押注:${betSize?string["0"]}人
押注总积分:${sumPoint?string["0"]}分
<#list betList as betTrace>
▔▔▔▔▔▔▔▔▔
${notice}[${betTrace.remarkName}]
第${betTrace.betIndexString}门 押 ${betTrace.betPoint?string["0"]}
</#list>
专注报数-仅供娱乐,严禁赌博!
</#compress>