package epub_viewer

import android.content.Context
import android.graphics.Color
import epub_core.Config


class ReaderConfig(
    context: Context?, identifier: String?, themeColor: String?,
    scrollDirection: String, allowSharing: Boolean, showTts: Boolean, nightMode: Boolean
) {
    private val identifier: String? = null
    private val themeColor: String? = null
    private val scrollDirection: String? = null
    private val allowSharing = false
    private val showTts = false
    private val nightMode = false
    var config: Config

    init {

//        config = AppUtil.getSavedConfig(context);
//        if (config == null)
        config = Config()
        if (scrollDirection == "vertical") {
            config.setAllowedDirection(Config.AllowedDirection.ONLY_VERTICAL)
        } else if (scrollDirection == "horizontal") {
            config.setAllowedDirection(Config.AllowedDirection.ONLY_HORIZONTAL)
        } else {
            config.setAllowedDirection(Config.AllowedDirection.VERTICAL_AND_HORIZONTAL)
        }
        config.setThemeColorInt(Color.parseColor(themeColor))
        config.setNightThemeColorInt(Color.parseColor(themeColor))
        config.isShowRemainingIndicator = true
        config.setShowTts(showTts)
        config.setNightMode(nightMode)
    }
}