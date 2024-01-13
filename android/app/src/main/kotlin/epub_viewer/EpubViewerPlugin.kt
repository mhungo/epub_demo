package epub_viewer

import android.app.Activity
import android.content.Context
import android.util.Log
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.FlutterPlugin.FlutterPluginBinding
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.EventChannel.EventSink
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.PluginRegistry.Registrar
import java.lang.Boolean
import kotlin.Any
import kotlin.String
import kotlin.toString


/**
 * EpubReaderPlugin
 */
class EpubViewerPlugin(context: Context?,messenger: BinaryMessenger?) : MethodCallHandler{
    private var reader: Reader? = null
    private var config: ReaderConfig? = null
    private var context: Context? = context
    private var eventChannel: EventChannel? = null
    private var sink: EventSink? = null
    var messenger: BinaryMessenger? = messenger

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {

        if (call.method == "setConfig") {
            val arguments = call.arguments as Map<String, Any>
            val identifier = arguments["identifier"].toString()
            val themeColor = arguments["themeColor"].toString()
            val scrollDirection = arguments["scrollDirection"].toString()
            val nightMode = Boolean.parseBoolean(arguments["nightMode"].toString())
            val allowSharing = Boolean.parseBoolean(arguments["allowSharing"].toString())
            val enableTts = Boolean.parseBoolean(arguments["enableTts"].toString())
            config = ReaderConfig(
                context, identifier, themeColor,
                scrollDirection, allowSharing, enableTts, nightMode
            )
        } else if (call.method == "open") {
            val arguments = call.arguments as Map<String, Any>
            val bookPath = arguments["bookPath"].toString()
            val lastLocation = arguments["lastLocation"].toString()
            Log.i("opening", "In open function")
            if (sink == null) {
                Log.i("sink status", "sink is empty")
            }
            reader = Reader(context!!, config!!, sink)
            reader!!.open(bookPath, lastLocation)
        } else if (call.method == "close") {
            reader!!.close()
        } else if (call.method == "setChannel") {
            eventChannel = EventChannel(messenger, "page")
            eventChannel!!.setStreamHandler(object : EventChannel.StreamHandler {
                override fun onListen(o: Any, eventSink: EventSink) {
                    sink = eventSink
                    if (sink == null) {
                        Log.i("empty", "Sink is empty")
                    }
                }

                override fun onCancel(o: Any) {}
            })
        } else {
            result.notImplemented()
        }
    }

//    companion object {
//        private var activity: Activity? = null
//
//        var messenger: BinaryMessenger? = null
//        private var eventChannel: EventChannel? = null
//        private var sink: EventSink? = null
//        private const val channelName = "epub_viewer"

        /**
         * Plugin registration.
         */
//        fun registerWith(registrar: Registrar) {
//            context = registrar.context()
//            activity = registrar.activity()
//            messenger = registrar.messenger()
//            EventChannel(messenger, "page").setStreamHandler(object : EventChannel.StreamHandler {
//                override fun onListen(o: Any, eventSink: EventSink) {
//                    sink = eventSink
//                    if (sink == null) {
//                        Log.i("empty", "Sink is empty")
//                    }
//                }
//
//                override fun onCancel(o: Any) {}
//            })
//            val channel = MethodChannel(registrar.messenger(), "epub_viewer")
//            channel.setMethodCallHandler(EpubViewerPlugin())
//        }
//    }
}