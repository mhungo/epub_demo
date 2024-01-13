import 'dart:io';

import 'package:dio/dio.dart';
import 'package:file_picker/file_picker.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:path_provider/path_provider.dart';
import 'package:permission_handler/permission_handler.dart';
import 'package:epub_demo/native/epub_reader/epub_viewer.dart';
import 'package:epub_demo/native/epub_reader/model/enum/epub_scroll_direction.dart';
import 'package:epub_demo/native/epub_reader/model/epub_locator.dart';

class TestEpub extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<TestEpub> {
  final platform = MethodChannel('my_channel');
  bool loading = false;
  Dio dio = Dio();
  String filePath = "";

  @override
  void initState() {
    download();
    super.initState();
  }

  /// ANDROID VERSION
  Future<void> fetchAndroidVersion() async {
    final String? version = await getAndroidVersion();
    if (version != null) {
      String? firstPart;
      if (version.toString().contains(".")) {
        int indexOfFirstDot = version.indexOf(".");
        firstPart = version.substring(0, indexOfFirstDot);
      } else {
        firstPart = version;
      }
      int intValue = int.parse(firstPart);
      if (intValue >= 13) {
        await startDownload();
      } else {
        final PermissionStatus status = await Permission.storage.request();
        if (status == PermissionStatus.granted) {
          await startDownload();
        } else {
          await Permission.storage.request();
        }
      }
      print("ANDROID VERSION: $intValue");
    }
  }

  Future<String?> getAndroidVersion() async {
    try {
      final String version = await platform.invokeMethod('getAndroidVersion');
      return version;
    } on PlatformException catch (e) {
      print("FAILED TO GET ANDROID VERSION: ${e.message}");
      return null;
    }
  }

  download() async {
    if (Platform.isIOS) {
      final PermissionStatus status = await Permission.storage.request();
      if (status == PermissionStatus.granted) {
        await startDownload();
      } else {
        await Permission.storage.request();
      }
    } else if (Platform.isAndroid) {
      await fetchAndroidVersion();
    } else {
      PlatformException(code: '500');
    }
  }

  browseFile() async {
    FilePickerResult? result = await FilePicker.platform.pickFiles();

    if (result != null) {
      // File file = File(result.files.single.path!);
      setState(() {
        loading = false;
        filePath = result.files.single.path!;
      });

      EpubViewer.setConfig(
        themeColor: Theme.of(context).primaryColor,
        identifier: "book",
        scrollDirection: EpubScrollDirection.HORIZONTAL,
        allowSharing: true,
        enableTts: true,
        // nightMode: true,
      );

      EpubViewer.locatorStream.listen((locator) {
        print('LOCATOR: $locator');
      });

      EpubViewer.open(
        filePath,
        // lastLocation: EpubLocator.fromJson({
        //   "bookId": "2239",
        //   "href": "/OEBPS/ch06.xhtml",
        //   "created": 1539934158390,
        //   "locations": {"cfi": "epubcfi(/0!/4/4[simple_book]/2/2/6)"}
        // }),
      );
      // get current locator
      
    } else {
      // User canceled the picker
    }
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Vocsy Plugin E-pub example'),
        ),
        body: Center(
          child: loading
              ? Column(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    CircularProgressIndicator(),
                    Text('Downloading.... E-pub'),
                  ],
                )
              : Column(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    ElevatedButton(
                      onPressed: () async {
                        print("=====filePath======$filePath");
                        if (filePath == "") {
                          browseFile();
                        } else {
                          EpubViewer.setConfig(
                            themeColor: Theme.of(context).primaryColor,
                            identifier: "iosBook",
                            scrollDirection: EpubScrollDirection.HORIZONTAL,
                            allowSharing: true,
                            enableTts: true,
                            // nightMode: true,
                          );

                          // get current locator
                          EpubViewer.locatorStream.listen((locator) {
                            print('LOCATOR: $locator');
                          });

                          EpubViewer.open(
                            filePath,
                            lastLocation: EpubLocator.fromJson({
                              "bookId": "2239",
                              "href": "/OEBPS/ch06.xhtml",
                              "created": 1539934158390,
                              "locations": {
                                "cfi": "epubcfi(/0!/4/4[simple_book]/2/2/6)"
                              }
                            }),
                          );
                        }
                      },
                      child: Text('Browse E-pub'),
                    ),
                    ElevatedButton(
                      onPressed: () async {
                        print("=====filePath======$filePath");
                        if (filePath == "") {
                          download();
                        } else {
                          EpubViewer.setConfig(
                            themeColor: Theme.of(context).primaryColor,
                            identifier: "iosBook",
                            scrollDirection: EpubScrollDirection.ALLDIRECTIONS,
                            allowSharing: true,
                            enableTts: true,
                            nightMode: true,
                          );

                          // get current locator
                          EpubViewer.locatorStream.listen((locator) {
                            print('LOCATOR: $locator');
                          });

                          EpubViewer.open(
                            filePath,
                            lastLocation: EpubLocator.fromJson({
                              "bookId": "2239",
                              "href": "/OEBPS/ch06.xhtml",
                              "created": 1539934158390,
                              "locations": {
                                "cfi": "epubcfi(/0!/4/4[simple_book]/2/2/6)"
                              }
                            }),
                          );
                        }
                      },
                      child: Text('Open Online E-pub'),
                    ),
                    ElevatedButton(
                      onPressed: () async {
                        EpubViewer.setConfig(
                          themeColor: Theme.of(context).primaryColor,
                          identifier: "iosBook",
                          scrollDirection: EpubScrollDirection.ALLDIRECTIONS,
                          allowSharing: true,
                          enableTts: true,
                          nightMode: true,
                        );
                        // get current locator
                        EpubViewer.locatorStream.listen((locator) {
                          print('LOCATOR: $locator');
                        });
                        await EpubViewer.openAsset(
                          'assets/4.epub',
                          lastLocation: EpubLocator.fromJson({
                            "bookId": "2239",
                            "href": "/OEBPS/ch06.xhtml",
                            "created": 1539934158390,
                            "locations": {
                              "cfi": "epubcfi(/0!/4/4[simple_book]/2/2/6)"
                            }
                          }),
                        );
                      },
                      child: Text('Open Assets E-pub'),
                    ),
                  ],
                ),
        ),
      ),
    );
  }

  startDownload() async {
    setState(() {
      loading = true;
    });
    Directory? appDocDir = Platform.isAndroid
        ? await getExternalStorageDirectory()
        : await getApplicationDocumentsDirectory();

    String path = appDocDir!.path + '/sample.epub';
    File file = File(path);

    if (!File(path).existsSync()) {
      await file.create();
      await dio.download(
        "https://vocsyinfotech.in/envato/cc/flutter_ebook/uploads/22566_The-Racketeer---John-Grisham.epub",
        path,
        deleteOnError: true,
        onReceiveProgress: (receivedBytes, totalBytes) {
          print('Download --- ${(receivedBytes / totalBytes) * 100}');
          setState(() {
            loading = true;
          });
        },
      ).whenComplete(() {
        setState(() {
          loading = false;
          filePath = path;
        });
      });
    } else {
      setState(() {
        loading = false;
        filePath = path;
      });
    }
  }
}
