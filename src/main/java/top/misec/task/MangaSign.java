package top.misec.task;

import com.google.gson.JsonObject;
import lombok.extern.log4j.Log4j2;
import top.misec.apiquery.ApiList;
import top.misec.config.Config;
import top.misec.utils.HttpUtil;

/**
 * 漫画签到
 *
 * @author @JunzhouLiu @Kurenai
 * @since 2020-11-22 5:22
 */

@Log4j2
public class MangaSign implements Task {


	@Override
	public void run() {

		String platform = Config.getInstance().getDevicePlatform();
		String requestBody = "platform=" + platform;
		JsonObject result = HttpUtil.doPost(ApiList.Manga, requestBody);

		if (result == null) {
			log.info("哔哩哔哩漫画已经签到过了");
		} else {
			log.info("完成漫画签到");
		}
	}

	@Override
	public String getName() {
		return "漫画签到";
	}
}
