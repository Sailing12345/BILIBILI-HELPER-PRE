package top.misec.task;

import com.google.gson.JsonObject;
import lombok.extern.log4j.Log4j2;
import top.misec.apiquery.ApiList;
import top.misec.apiquery.OftenApi;
import top.misec.login.Verify;
import top.misec.utils.HttpUtil;

import java.util.Objects;

import static top.misec.task.TaskInfoHolder.STATUS_CODE_STR;
import static top.misec.task.TaskInfoHolder.userInfo;

/**
 * 银瓜子换硬币
 *
 * @author @JunzhouLiu @Kurenai
 * @since 2020-11-22 5:25
 */
@Log4j2
public class Silver2coin implements Task {

	@Override
	public void run() {

		JsonObject queryStatus = HttpUtil.doGet(ApiList.getSilver2coinStatus);
		if (queryStatus == null || Objects.isNull(queryStatus.get("data"))) {
			log.error("获取银瓜子状态失败");
			return;
		}
		queryStatus = queryStatus.get("data").getAsJsonObject();
		//银瓜子兑换硬币汇率
		final int exchangeRate = 700;
		int silverNum = queryStatus.get("silver").getAsInt();

		if (silverNum < exchangeRate) {
			log.info("当前银瓜子余额为:{},不足700,不进行兑换", silverNum);
		} else {
			String requestBody = "csrf_token=" + Verify.getInstance().getBiliJct() +
					"&csrf=" + Verify.getInstance().getBiliJct();
			JsonObject resultJson = HttpUtil.doPost(ApiList.silver2coin, requestBody);

			int responseCode = resultJson.get(STATUS_CODE_STR).getAsInt();
			if (responseCode == 0) {
				log.info("银瓜子兑换硬币成功");

				double coinMoneyAfterSilver2Coin = OftenApi.getCoinBalance();

				log.info("当前银瓜子余额: {}", (silverNum - exchangeRate));
				log.info("兑换银瓜子后硬币余额: {}", coinMoneyAfterSilver2Coin);

				//兑换银瓜子后，更新userInfo中的硬币值
				if (userInfo != null) {
					userInfo.setMoney(coinMoneyAfterSilver2Coin);
				}
			} else {
				log.info("银瓜子兑换硬币失败 原因是:{}", resultJson.get("message").getAsString());
			}
		}
	}

	@Override
	public String getName() {
		return "银瓜子换硬币";
	}
}
