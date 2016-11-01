package com.karl;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.karl.db.domain.Player;
import com.karl.db.service.PlayerService;
import com.karl.domain.RuntimeDomain;
import com.karl.service.GameService;
import com.karl.utils.StringUtils;

import freemarker.core.ParseException;
import freemarker.template.Configuration;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.TemplateNotFoundException;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Main.class)
public class FreemarkerTest {
	
	@Autowired
	private PlayerService playerService;

	@Autowired
	private GameService gameService;

	@Autowired
	private RuntimeDomain runtimeDomain;
	
	Configuration ftlCfg;
	
	public void buildFtl() {
		ftlCfg = new Configuration(Configuration.VERSION_2_3_23);
		try {
			ftlCfg.setDirectoryForTemplateLoading(new File(FreemarkerTest.class.getResource("/ftl").getFile()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		ftlCfg.setDefaultEncoding("UTF-8");
		ftlCfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
		ftlCfg.setLogTemplateExceptions(false);
	}

	
	@Test
	public void testPublic() throws TemplateNotFoundException, MalformedTemplateNameException, ParseException, IOException, TemplateException {
		buildFtl();
		Map root = new HashMap();
		Template temp = ftlCfg.getTemplate("ranking.ftlh");
		

		List<Player> playerList = playerService.getPlayerListDescPoint();
		if (playerList == null) {
			return;
		}
		String bankerReMarkerName = "insom";//runtimeDomain.getBankerRemarkName();
		Player banker = null;
		
		
		int order = 1;
		Long sumPoint = 0L;
		List<Player> resultList = new ArrayList<Player>();
		for (int i = 0; i < playerList.size(); i++) {
			if (playerList.get(i).getPoints().compareTo(Long.valueOf(0)) == 0) {
				continue;
			}
			
			if (bankerReMarkerName != null
					&& bankerReMarkerName.equals(playerList.get(i)
							.getRemarkName())) {
				banker = playerList.get(i);
				sumPoint += playerList.get(i).getPoints();
				continue;
			}

			sumPoint += playerList.get(i).getPoints();
			resultList.add(playerList.get(i));
		}

		root.put("sumPoint", sumPoint);
		root.put("rankSize", banker==null?resultList.size():resultList.size()+1);
		root.put("rankList", resultList);
		root.put("R", StringUtils.labelR());
		root.put("TOP", StringUtils.labelTOP());
		root.put("banker", banker);
		temp.process(root, new OutputStreamWriter(System.out));
	}
	
	@Test
	public void testGamePublic() {
		System.out.println(gameService.publishPointRanks());
	}
	
}
