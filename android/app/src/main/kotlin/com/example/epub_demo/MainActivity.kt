package com.example.epub_demo

import epub_viewer.EpubViewerPlugin
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel

class MainActivity: FlutterActivity() {
    private val epubViewerChannelName = "epub_viewer"
    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        //2
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, epubViewerChannelName).setMethodCallHandler(EpubViewerPlugin(context, flutterEngine.dartExecutor.binaryMessenger))
    }
}
