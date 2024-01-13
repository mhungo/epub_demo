package epub_viewer

import android.content.Context
import android.util.Log
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import epub_core.FolioReader
import epub_core.FolioReader.OnClosedListener
import epub_core.model.HighLight
import epub_core.model.HighLight.HighLightAction
import epub_core.model.locators.ReadLocator
import epub_core.util.OnHighlightListener
import epub_core.util.ReadLocatorListener
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.EventChannel.EventSink
import io.flutter.plugin.common.MethodChannel
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader


class Reader internal constructor(
    private val context: Context,
    private val readerConfig: ReaderConfig,
    sink: EventSink?
) :
    OnHighlightListener, ReadLocatorListener, OnClosedListener {
    var folioReader: FolioReader
    var result: MethodChannel.Result? = null
    private var eventChannel: EventChannel? = null
    private var pageEventSink: EventSink?
    private var read_locator: ReadLocator? = null

    init {
        highlightsAndSave
        //setPageHandler(messenger);
        folioReader = FolioReader.get()
            .setOnHighlightListener(this)
            .setReadLocatorListener(this)
            .setOnClosedListener(this)
        pageEventSink = sink
    }

    fun open(bookPath: String, lastLocation: String?) {
        Thread {
            try {
                Log.i("SavedLocation", "-> savedLocation -> $lastLocation")
                if (lastLocation != null && !lastLocation.isEmpty()) {
                    val readLocator = ReadLocator.fromJson(lastLocation)
                    folioReader.setReadLocator(readLocator)
                }
                folioReader.setConfig(readerConfig.config, true)
                    .openBook(bookPath)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }

    fun close() {
        folioReader.close()
    }

    private fun setPageHandler(messenger: BinaryMessenger) {
//        final MethodChannel channel = new MethodChannel(registrar.messenger(), "page");
//        channel.setMethodCallHandler(new EpubKittyPlugin());
        Log.i("event sink is", "in set page handler:")
        eventChannel = EventChannel(messenger, PAGE_CHANNEL)
        try {
            eventChannel!!.setStreamHandler(object : EventChannel.StreamHandler {
                override fun onListen(o: Any, eventSink: EventSink) {
                    Log.i("event sink is", "this is eveent sink:")
                    pageEventSink = eventSink
                    if (pageEventSink == null) {
                        Log.i("empty", "Sink is empty")
                    }
                }

                override fun onCancel(o: Any) {}
            })
        } catch (err: Error) {
            Log.i("and error", "error is $err")
        }
    }

    private val highlightsAndSave: Unit
        private get() {
            Thread {
                var highlightList: ArrayList<HighLight?>? = null
                val objectMapper = ObjectMapper()
                try {
                    highlightList = objectMapper.readValue(
                        loadAssetTextAsString("highlights/highlights_data.json"),
                        object :
                            TypeReference<List<HighlightData?>?>() {})
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                if (highlightList == null) {
                    folioReader.saveReceivedHighLights(highlightList) {
                        //You can do anything on successful saving highlight list
                    }
                }
            }.start()
        }

    private fun loadAssetTextAsString(name: String): String? {
        var `in`: BufferedReader? = null
        try {
            val buf = StringBuilder()
            val `is` = context.assets.open(name)
            `in` = BufferedReader(InputStreamReader(`is`))
            var str: String?
            var isFirst = true
            while (`in`.readLine().also { str = it } != null) {
                if (isFirst) isFirst = false else buf.append('\n')
                buf.append(str)
            }
            return buf.toString()
        } catch (e: IOException) {
            Log.e("Reader", "Error opening asset $name")
        } finally {
            if (`in` != null) {
                try {
                    `in`.close()
                } catch (e: IOException) {
                    Log.e("Reader", "Error closing asset $name")
                }
            }
        }
        return null
    }

    override fun onFolioReaderClosed() {
        Log.i("readLocator", "-> saveReadLocator -> " + (read_locator?.toJson() ?: ""))
        if (pageEventSink != null && read_locator !=null) {
            pageEventSink!!.success(read_locator!!.toJson())
        }
    }

    override fun onHighlight(highlight: HighLight, type: HighLightAction) {}
    override fun saveReadLocator(readLocator: ReadLocator) {
        read_locator = readLocator
    }

    companion object {
        private const val PAGE_CHANNEL = "sage"
    }
}