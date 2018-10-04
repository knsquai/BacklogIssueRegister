package com.nittsu_infosys.tools.backlog;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;

import com.nittsu_infosys.tools.backlog.common.CsvReader;
import com.nittsu_infosys.tools.backlog.common.bean.DataBean;

public class App {

	public static ResourceBundle rb = null;
	public static WebDriver driver = null;
	public static Logger logger = LogManager.getLogger(App.class);
	public static long defaultWait = 30;

	public static void main(String[] args) {

		// input
		rb = ResourceBundle.getBundle("resource", SJIS_ENCODING_CONTROL);
		File inputFile = new File(rb.getString("input.csv.path"));
		List<DataBean> ticketInfoList = new ArrayList<DataBean>();
		if (inputFile.exists()) {
			CsvReader csvReader = new CsvReader();
			ticketInfoList = csvReader.opencsvToBean(inputFile);
		}

		System.setProperty("webdriver.chrome.driver", "./driver/chromedriver.exe");
		driver = new ChromeDriver();
		driver.manage().timeouts().implicitlyWait(defaultWait, TimeUnit.SECONDS);

		// ログイン
		driver.get("https://nisweb.backlog.jp/LoginDisplay.action?url=%2Fdashboard");
		driver.findElement(By.id("userId")).sendKeys(rb.getString("id"));
		driver.findElement(By.id("password")).sendKeys(rb.getString("pw"));
		driver.findElement(By.id("submit")).click();;

		for (DataBean each : ticketInfoList) {
			// 課題登録画面への遷移
			driver.get("https://nisweb.backlog.jp/add/KAIHATSU");
			// 各フィールドへの値セット
			// 親課題
			if (StringUtils.isNotBlank(each.getParentNumber())) {
				driver.findElement(By.xpath("//*[@id=\"AddIssueForm\"]/div[1]/div[1]/a")).click();
				sleep(2000);
				driver.findElement(By.xpath("//*[@id=\"AddIssueForm\"]/div[2]/div/input")).sendKeys(each.getParentNumber());
			}
			// 件名
			sleep(2000);
			driver.findElement(By.xpath("//*[@id=\"summaryInput\"]")).sendKeys(each.getSubject());
			// 詳細
			sleep(2000);
			driver.findElement(By.xpath("//*[@id=\"descriptionTextArea\"]")).sendKeys(each.getDetail());
			// 担当者
			if (StringUtils.isNotBlank(each.getPerson())) {
				sleep(2000);
				driver.findElement(By.xpath("//*[@id=\"select2-chosen-2\"]")).click();
				sleep(2000);
				driver.findElement(By.xpath("//*[@id=\"s2id_autogen2_search\"]")).sendKeys(each.getPerson());
				driver.findElement(By.xpath("//*[@id=\"select2-results-2\"]/li")).click();
			}

			// 優先度
			if (StringUtils.isNotBlank(each.getPriority())) {
				((JavascriptExecutor)driver).executeScript("arguments[0].setAttribute('style', 'display: block;');", driver.findElement(By.name("issue.priority")));
				WebElement prioritySelectEle = driver.findElement(By.name("issue.priority"));
				Select prioritySelect = new Select(prioritySelectEle);
				prioritySelect.selectByVisibleText(each.getPriority());
			}

			// マイルストーン
			if (StringUtils.isNotBlank(each.getMilestone())) {
				((JavascriptExecutor)driver).executeScript("arguments[0].setAttribute('style', 'display: block;');", driver.findElement(By.name("issue.fixedVersionIds")));
				WebElement milestoneEle = driver.findElement(By.name("issue.fixedVersionIds"));
				Select milestoneSelect = new Select(milestoneEle);
				milestoneSelect.selectByVisibleText(each.getMilestone());
			}

			// カテゴリー
			if (StringUtils.isNotBlank(each.getCategory())) {
				((JavascriptExecutor)driver).executeScript("arguments[0].setAttribute('style', 'display: block;');", driver.findElement(By.name("issue.componentIds")));
				WebElement categoryEle = driver.findElement(By.name("issue.componentIds"));
				Select categorySelect = new Select(categoryEle);
				categorySelect.selectByVisibleText(each.getCategory());
			}

			// 発生バージョン
			if (StringUtils.isNotBlank(each.getVersion())) {
				((JavascriptExecutor)driver).executeScript("arguments[0].setAttribute('style', 'display: block;');", driver.findElement(By.name("issue.affectedVersionIds")));
				WebElement verEle = driver.findElement(By.name("issue.affectedVersionIds"));
				Select verSelect = new Select(verEle);
				verSelect.selectByVisibleText(each.getVersion());
			}

			// 開始日
			if (StringUtils.isNotBlank(each.getStartDay())) {
				driver.findElement(By.name("issue.startDate")).sendKeys(each.getStartDay());
			}

			// 期限日
			if (StringUtils.isNotBlank(each.getLimitDay())) {
				driver.findElement(By.name("issue.limitDate")).sendKeys(each.getLimitDay());
			}

			// 予定時間
			if (StringUtils.isNotBlank(each.getExpectTime())) {
				driver.findElement(By.name("issue.estimatedHours")).sendKeys(each.getExpectTime());
			}

			// 実績時間
			if (StringUtils.isNotBlank(each.getActualTime())) {
				driver.findElement(By.name("issue.actualHours")).sendKeys(each.getActualTime());
			}

			// 登録ボタン押下
			((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", driver.findElement(By.xpath("//*[@id=\"AddIssueForm\"]/div[3]/div/div[6]/li[2]/button")));
			driver.findElement(By.xpath("//*[@id=\"AddIssueForm\"]/div[3]/div/div[6]/li[2]/button")).click();
			sleep(3000);
		}
	}

	public static void sleep(int waitMs) {
		try {
			// 設定時間wait
			Thread.sleep(waitMs);
		} catch (InterruptedException e) {
			logger.error("待機(sleep)処理中にエラーが発生しました。", e);
		}
	}

	private static ResourceBundle.Control SJIS_ENCODING_CONTROL = new ResourceBundle.Control() {
		/**
		 * SJIS エンコーディングのプロパティファイルから ResourceBundle オブジェクトを生成します。
		 * <p>
		 * 参考 : <a href=
		 * "http://jgloss.sourceforge.net/jgloss-core/jacoco/jgloss.util/UTF8ResourceBundleControl.java.html">
		 * http://jgloss.sourceforge.net/jgloss-core/jacoco/jgloss.util/
		 * UTF8ResourceBundleControl.java.html </a>
		 * </p>
		 *
		 * @throws IllegalAccessException
		 * @throws InstantiationException
		 * @throws IOException
		 */
		@Override
		public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader,
				boolean reload) throws IllegalAccessException, InstantiationException, IOException {
			String bundleName = toBundleName(baseName, locale);
			String resourceName = toResourceName(bundleName, "properties");

			try (InputStream is = loader.getResourceAsStream(resourceName);
					InputStreamReader isr = new InputStreamReader(is, "windows-31j");
					BufferedReader reader = new BufferedReader(isr)) {
				return new PropertyResourceBundle(reader);
			}
		}
	};
}
