<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<!-- default mixed analytics template -->
<html xmlns="http://www.w3.org/1999/xhtml"
	xmlns:wicket="http://wicket.apache.org/dtds.data/wicket-xhtml1.4-strict.dtd">
<head>
	<wicket:head>
		<!-- insert <head> content here -->
		
		<wicket:container wicket:id="analyticsMeta"></wicket:container>
		<!-- TODO move it to .css -->
		<style type="text/css">
			.analytics > img {
				display: none;
			}
		</style>
	</wicket:head>
</head>
<body>
	<wicket:panel>
	<div class="analytics">
		<!-- insert <body> content here -->
		
		<wicket:container wicket:id="wbt">
	<!-- START OF SmartSource Data Collector TAG -->
		<!-- Copyright (c) 1996-2011 Webtrends Inc.  All rights reserved. -->
		<!-- Version: 9.4.0 -->
		<!-- Tag Builder Version: 3.2  -->
		<!-- Created: 5/17/2011 9:14:52 AM -->
		<!-- ----------------------------------------------------------------------------------- -->
		<!-- Warning: The two script blocks below must remain inline. Moving them to an external -->
		<!-- JavaScript include file can cause serious problems with cross-domain tracking.      -->
		<!-- ----------------------------------------------------------------------------------- -->
		<script type="text/javascript">
		//<![CDATA[
		var _tag=new WebTrends();
		_tag.dcsGetId();
		//]]>
		</script>
		<script type="text/javascript">
		//<![CDATA[
		_tag.dcsCustom=function(){
		// Add custom parameters here.
		//_tag.DCSext.param_name=param_value;
		}
		_tag.dcsCollect();
		//]]>
		</script>
		<noscript>
		<div><img alt="DCSIMG" id="DCSIMG" width="1" height="1" src="//statse.webtrendslive.com/dcsvkan1xvz5bdvgbc5gky4uq_6f3k/njs.gif?dcsuri=/nojavascript&amp;WT.js=No&amp;WT.tv=9.4.0${wbt.noscript}&amp;dcssip=www.lloydstsb-offshore.com"/></div>
		</noscript>
		<!-- END OF SmartSource Data Collector TAG -->
		</wicket:container>
		
		<wicket:container wicket:id="dcs1">
	<!-- START OF DoubleClick Floodlight Tag: Please do not remove
		URL of the webpage where the tag is expected to be placed: http://tbc.co.uk
		This tag must be placed between the <body> and </body> tags, as close as possible to the opening tag.
		Creation Date: 06/15/2011
		-->
		<script type="text/javascript">
		var axel = Math.random() + "";
		var a = axel * 10000000000000;
		document.write('<img src="https://ad.doubleclick.net/activity;src=2502400;type=${dcs.type};cat=${dcs.cat};ord=${dcs.ord};num=' + a + '?" width="1" height="1" alt=""/>');
		</script>
		<noscript>
		<div><img src="https://ad.doubleclick.net/activity;src=2502400;type=${dcs.type};cat=${dcs.cat};ord=${dcs.ord};num=1?" width="1" height="1" alt=""/></div>
		</noscript>
		<!-- END OF DoubleClick Floodlight Tag: Please do not remove -->
		</wicket:container>
		
		<wicket:container wicket:id="dcs2">
		<!--
		URL of the webpage where the tag is expected to be placed: http://tbc.co.uk
		This tag must be placed between the <body> and </body> tags, as close as possible to the opening tag.
		Creation Date: 06/15/2011
		-->
		<div><img src="https://ad.doubleclick.net/activity;src=2502400;type=${dcs.type};cat=${dcs.cat};qty=1;cost=1;ord=${dcs.ord}?" width="1" height="1" alt=""/></div>
		<!-- END OF DoubleClick Floodlight Tag: Please do not remove -->
		</wicket:container>
		
		<wicket:container wicket:id="tbl">
	<!-- START OF TradeDoubler Data Collector TAG -->
		<div><img alt="G2 Lead" height="1" width="1" src="https://tbl.tradedoubler.com/report?organization=1252112&amp;event=${tbl.event}&amp;leadNumber=${tbl.leadNumber}&amp;checksum=${tbl.checksum}&amp;tduid=${tbl.tduid}"/></div>
		<!-- END OF TradeDoubler Data Collector TAG -->
		</wicket:container>
		
		<wicket:container wicket:id="omg">
	<!-- START OF OMG TAG -->
		<script src="https://track.omguk.com/94415/transaction.asp?APPID=${omg.appid}&amp;MID=94415&amp;PID=${omg.pid}&amp;status="></script>
		<noscript><div><img src="https://track.omguk.com/apptag.asp?APPID=${omg.appid}&amp;MID=94415&amp;PID=${omg.pid}&amp;status=" height="1" width="1" alt=""/></div></noscript>
		<!-- END OF OMG TAG -->
		</wicket:container>
		
		<wicket:container wicket:id="mdm">
	<!-- START OF Media Math TAG -->
		<script type="text/javascript" src="https://pixel.mathtag.com/event/js?mt_id=${mdm.mt_id}&amp;mt_adid=76&amp;v1=&amp;v2=&amp;v3=&amp;s1=&amp;s2=&amp;s3="></script>
		<!-- END OF Media Math TAG -->
		</wicket:container>
		
		<wicket:container wicket:id="zap1">
	<!-- START OF ZAP Trader TAG -->
		<!-- Segment Pixel - Lloyds International - DO NOT MODIFY -->
		<img src="https://secure.adnxs.com/seg?add=${zap.add}&amp;t=2" width="1" height="1" alt=""/>
		<!-- End of Segment Pixel -->
		</wicket:container>
		
		<wicket:container wicket:id="zap2">
		<!-- ZT Conversion Page Hybrid Pixel -Lloyds International- Conversion and Segment Pixel - DO NOT MODIFY -->
		<img src="https://secure.adnxs.com/px?id=${zap.id}&amp;seg=${zap.seg}" width="1" height="1" alt=""/>
		<!-- End of Segment Pixel -->
		<!-- END OF ZAP Trader TAG -->
		</wicket:container>
	</div>
	</wicket:panel>
</body>
</html>