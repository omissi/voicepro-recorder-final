# VoicePro Recorder Final V4 Fixed

مشروع Android كامل باستخدام Kotlin + Jetpack Compose.

هذه النسخة تصلح خطأ Build Debug APK الناتج من تعارض أسماء `item/items` داخل LazyColumn، وتبقي جميع الشاشات المطلوبة:

- Home dark UI مشابه للنموذج.
- Recording modes: Standard, Music & raw sound, Meetings & lectures, Device audio.
- Animated mic pulse.
- Top overflow menu: Import, Restore from Drive, Trash.
- Recording screen with timer, moving waveform, markers, pause/resume, save dialog.
- Saved successfully with Back/Home and quick actions.
- Recording list, Player, Voice changer, Trim/Cut, Settings, Pro/Remove ads, Trash, Live transcribe placeholder.
- Languages by system locale: English, Arabic, Turkish, Portuguese, Spanish, French, German.
- AdMob test banner and interstitial only after Save.

Package: `com.omissi.voiceprorecorder`


## V5 build fix
- Fixed Kotlin Offset compile errors by converting waveform sin/cos heights to Float before calling Offset().
- Artifact name: voicepro-recorder-final-v5-apks.


## V6 build fix
- Renamed RecorderViewModel.setRemoveAds() to updateRemoveAds() to avoid Kotlin JVM setter clash with var removeAds.
