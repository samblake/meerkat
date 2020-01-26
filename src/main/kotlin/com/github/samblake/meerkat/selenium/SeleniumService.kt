package com.github.samblake.meerkat.selenium

import io.github.bonigarcia.wdm.WebDriverManager
import org.openqa.selenium.chrome.ChromeDriver
import ru.yandex.qatools.ashot.AShot
import ru.yandex.qatools.ashot.shooting.ShootingStrategies
import java.nio.file.Paths
import javax.imageio.ImageIO

fun main() {
    SeleniumService().screenshot("http://www.boardgamegeek.com")
}

class SeleniumService {

    val strategy = ShootingStrategies.viewportPasting(100);

    fun screenshot(url: String) {
        WebDriverManager.chromedriver().setup()
        val driver = ChromeDriver()
        driver.get(url)
        val fpScreenshot = AShot().shootingStrategy(strategy).takeScreenshot(driver);

        val dir = Paths.get("/home/sam/Pictures/ss/")
        dir.toFile().exists().not().run { dir.toFile().mkdirs() }
        val file = dir.resolve("bgg.png").toFile()
        ImageIO.write(fpScreenshot.getImage(),"PNG", file);
        driver.quit()
    }

}