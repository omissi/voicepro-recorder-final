package com.omissi.voiceprorecorder

import android.Manifest
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin
import kotlin.random.Random

private val Bg = Color(0xFF101014)
private val Bg2 = Color(0xFF15161B)
private val CardBg = Color(0xFF22232A)
private val CardBg2 = Color(0xFF2A2B34)
private val TextWhite = Color(0xFFF8F8FB)
private val Muted = Color(0xFF8F8F9F)
private val Accent = Color(0xFFFF3A18)
private val Accent2 = Color(0xFFFF7A1A)
private val BlueMarker = Color(0xFF0A84FF)
private val DividerDark = Color(0xFF292A31)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MobileAds.initialize(this) {}
        AdController.loadInterstitial(this)
        setContent {
            val vm: RecorderViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
                factory = RecorderViewModel.Factory(application)
            )
            VoiceProTheme {
                VoiceProApp(vm)
            }
        }
    }
}

@Composable
private fun VoiceProTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = androidx.compose.material3.darkColorScheme(
            background = Bg,
            surface = Bg,
            primary = Accent,
            onPrimary = Color.White,
            onBackground = TextWhite,
            onSurface = TextWhite,
            secondary = Accent2
        ),
        content = content
    )
}

data class RecordingMode(
    val id: String,
    val title: String,
    val subtitle: String,
    val illustration: String,
    val needsDeviceAudio: Boolean = false
)

data class RecordingItem(
    val title: String,
    val path: String,
    val sizeLabel: String,
    val durationMs: Long,
    val format: String = "M4A",
    val tag: String = "Untagged",
    val createdAt: Long = System.currentTimeMillis(),
    val markers: List<Long> = emptyList()
)

object Strings {
    private val lang: String get() = Locale.getDefault().language.lowercase(Locale.ROOT)
    private val data = mapOf(
        "homeTitle" to mapOf("en" to "VoicePro Recorder", "ar" to "مسجل الصوت", "tr" to "Ses Kaydedici", "pt" to "Gravador", "es" to "Grabadora", "fr" to "Enregistreur", "de" to "Recorder"),
        "live" to mapOf("en" to "Live transcribe", "ar" to "تفريغ مباشر", "tr" to "Canlı yazıya dök", "pt" to "Transcrição ao vivo", "es" to "Transcripción en vivo", "fr" to "Transcription en direct", "de" to "Live-Transkription"),
        "liveSub" to mapOf("en" to "Real-time text transcription", "ar" to "تحويل الصوت إلى نص مباشرة", "tr" to "Gerçek zamanlı metin", "pt" to "Transcrição em tempo real", "es" to "Texto en tiempo real", "fr" to "Texte en temps réel", "de" to "Text in Echtzeit"),
        "settings" to mapOf("en" to "Settings", "ar" to "الإعدادات", "tr" to "Ayarlar", "pt" to "Configurações", "es" to "Ajustes", "fr" to "Réglages", "de" to "Einstellungen"),
        "recording" to mapOf("en" to "Recording", "ar" to "التسجيل", "tr" to "Kayıt", "pt" to "Gravação", "es" to "Grabación", "fr" to "Enregistrement", "de" to "Aufnahme"),
        "save" to mapOf("en" to "Save", "ar" to "حفظ", "tr" to "Kaydet", "pt" to "Salvar", "es" to "Guardar", "fr" to "Enregistrer", "de" to "Speichern"),
        "saved" to mapOf("en" to "Saved successfully", "ar" to "تم الحفظ بنجاح", "tr" to "Başarıyla kaydedildi", "pt" to "Salvo com sucesso", "es" to "Guardado correctamente", "fr" to "Enregistré", "de" to "Gespeichert"),
        "startNew" to mapOf("en" to "Start a new recording", "ar" to "بدء تسجيل جديد", "tr" to "Yeni kayıt başlat", "pt" to "Nova gravação", "es" to "Nueva grabación", "fr" to "Nouvel enregistrement", "de" to "Neue Aufnahme"),
        "list" to mapOf("en" to "Check my recording list", "ar" to "عرض قائمة التسجيلات", "tr" to "Kayıt listem", "pt" to "Lista de gravações", "es" to "Lista de grabaciones", "fr" to "Liste des enregistrements", "de" to "Aufnahmeliste"),
        "removeAds" to mapOf("en" to "Remove all ads", "ar" to "إزالة جميع الإعلانات", "tr" to "Reklamları kaldır", "pt" to "Remover anúncios", "es" to "Quitar anuncios", "fr" to "Supprimer les pubs", "de" to "Werbung entfernen"),
        "language" to mapOf("en" to "Language", "ar" to "اللغة", "tr" to "Dil", "pt" to "Idioma", "es" to "Idioma", "fr" to "Langue", "de" to "Sprache")
    )
    fun t(key: String, fallback: String = key): String = data[key]?.get(lang) ?: data[key]?.get("en") ?: fallback
}

class RecorderViewModel(app: Application) : AndroidViewModel(app) {
    class Factory(private val app: Application) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = RecorderViewModel(app) as T
    }

    val modes = listOf(
        RecordingMode("standard", "Standard", "Widely compatible, high-quality sound reproduction.", "mic"),
        RecordingMode("music", "Music & raw sound", "Preserve the original sound, perfect for music recording.", "turntable"),
        RecordingMode("meeting", "Meetings & lectures", "Enhanced sound capture, ideal for conference recording.", "typewriter"),
        RecordingMode("device", "Device audio", "Capture sound from the device when Android allows it.", "phone", true)
    )

    val markers = mutableStateListOf<Long>()
    var currentMode by mutableStateOf(modes.first())
        private set
    var elapsedMs by mutableStateOf(0L)
        private set
    var isRecording by mutableStateOf(false)
        private set
    var isPaused by mutableStateOf(false)
        private set
    var lastSaved by mutableStateOf<RecordingItem?>(null)
        private set
    var errorMessage by mutableStateOf<String?>(null)
    var removeAds by mutableStateOf(false)
        private set

    private var timerJob: Job? = null
    private var recorder: MediaRecorder? = null
    private var currentFile: File? = null
    private var startRealtime = 0L
    private var baseElapsed = 0L
    private var player: MediaPlayer? = null
    var playingPath by mutableStateOf<String?>(null)
        private set

    private val prefs = app.getSharedPreferences("voicepro-prefs", Context.MODE_PRIVATE)

    init {
        removeAds = prefs.getBoolean("remove_ads", false)
    }

    private fun recordingsDir(): File {
        val dir = File(getApplication<Application>().getExternalFilesDir(null), "Music/VoiceProRecorder")
        if (!dir.exists()) dir.mkdirs()
        return dir
    }

    fun updateRemoveAds(enabled: Boolean) {
        removeAds = enabled
        prefs.edit().putBoolean("remove_ads", enabled).apply()
    }

    fun startNewSession() {
        stopPlayback()
        markers.clear()
        elapsedMs = 0L
        isPaused = false
        lastSaved = null
        errorMessage = null
        currentFile = null
        currentMode = modes.first()
    }

    fun setMode(mode: RecordingMode) {
        currentMode = mode
    }

    @Suppress("DEPRECATION")
    fun startRecording(mode: RecordingMode) {
        if (isRecording) return
        startNewSession()
        currentMode = mode
        val file = File(recordingsDir(), "temp_${System.currentTimeMillis()}.m4a")
        currentFile = file
        runCatching {
            recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(getApplication())
            } else {
                MediaRecorder()
            }.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setAudioEncodingBitRate(128000)
                setAudioSamplingRate(44100)
                setOutputFile(file.absolutePath)
                prepare()
                start()
            }
            isRecording = true
            isPaused = false
            startRealtime = System.currentTimeMillis()
            baseElapsed = 0L
            launchTimer()
        }.onFailure {
            errorMessage = it.message ?: "Recording failed"
            currentFile = null
            recorder = null
        }
    }

    private fun launchTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (isRecording) {
                if (!isPaused) elapsedMs = baseElapsed + (System.currentTimeMillis() - startRealtime)
                delay(150)
            }
        }
    }

    fun togglePause() {
        if (!isRecording) return
        if (isPaused) {
            runCatching { if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) recorder?.resume() }
            startRealtime = System.currentTimeMillis()
            isPaused = false
        } else {
            baseElapsed = elapsedMs
            runCatching { if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) recorder?.pause() }
            isPaused = true
        }
    }

    fun addMarker() {
        if (elapsedMs >= 0) markers.add(elapsedMs)
    }

    fun stopForSaveDialog() {
        if (!isRecording) return
        baseElapsed = elapsedMs
        runCatching { recorder?.stop() }
        runCatching { recorder?.reset() }
        runCatching { recorder?.release() }
        recorder = null
        isRecording = false
        isPaused = false
        timerJob?.cancel()
    }

    fun discardRecording() {
        stopForSaveDialog()
        currentFile?.delete()
        currentFile = null
        startNewSession()
    }

    fun commitSave(title: String, tag: String): RecordingItem? {
        stopForSaveDialog()
        val source = currentFile ?: return null
        val cleanTitle = sanitize(title.ifBlank { defaultTitle() })
        var dest = File(recordingsDir(), "$cleanTitle.m4a")
        if (dest.exists()) dest = File(recordingsDir(), "${cleanTitle}_${System.currentTimeMillis()}.m4a")
        source.renameTo(dest)
        val item = buildItem(dest, tag, markers.toList())
        lastSaved = item
        currentFile = null
        markers.clear()
        return item
    }

    fun recordings(): List<RecordingItem> {
        return recordingsDir().listFiles { f -> f.isFile && f.extension.lowercase(Locale.ROOT) in setOf("m4a", "mp4", "aac") }
            ?.sortedByDescending { it.lastModified() }
            ?.map { buildItem(it, "Untagged", emptyList()) }
            ?: emptyList()
    }

    fun selectRecording(item: RecordingItem) {
        lastSaved = item
    }

    fun deleteRecording(item: RecordingItem) {
        File(item.path).delete()
        if (lastSaved?.path == item.path) lastSaved = null
    }

    fun importAudio(uri: Uri, context: Context) {
        viewModelScope.launch {
            runCatching {
                val name = "Imported_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())}.m4a"
                val dest = File(recordingsDir(), name)
                context.contentResolver.openInputStream(uri)?.use { input ->
                    dest.outputStream().use { output -> input.copyTo(output) }
                }
                lastSaved = buildItem(dest, "Imported", emptyList())
            }.onFailure { errorMessage = it.message ?: "Import failed" }
        }
    }

    fun togglePlay(item: RecordingItem) {
        val file = File(item.path)
        if (!file.exists()) return
        if (playingPath == item.path) {
            stopPlayback()
            return
        }
        stopPlayback()
        runCatching {
            player = MediaPlayer().apply {
                setDataSource(item.path)
                prepare()
                start()
                setOnCompletionListener { stopPlayback() }
            }
            playingPath = item.path
        }.onFailure { errorMessage = it.message ?: "Playback failed" }
    }

    fun stopPlayback() {
        runCatching { player?.stop() }
        runCatching { player?.release() }
        player = null
        playingPath = null
    }

    private fun buildItem(file: File, tag: String, markers: List<Long>): RecordingItem {
        val duration = durationOf(file)
        return RecordingItem(
            title = file.nameWithoutExtension,
            path = file.absolutePath,
            sizeLabel = formatSize(file.length()),
            durationMs = if (duration > 0) duration else max(elapsedMs, 0L),
            tag = tag,
            createdAt = file.lastModified(),
            markers = markers
        )
    }

    private fun durationOf(file: File): Long {
        return runCatching {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(file.absolutePath)
            val raw = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            retriever.release()
            raw?.toLongOrNull() ?: 0L
        }.getOrDefault(0L)
    }

    fun defaultTitle(): String = "VoicePro_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())}"

    private fun sanitize(value: String): String = value.replace(Regex("[^A-Za-z0-9_ء-ي-]"), "_").take(50)

    private fun formatSize(bytes: Long): String {
        if (bytes < 1024) return "$bytes B"
        val kb = bytes / 1024.0
        if (kb < 1024) return String.format(Locale.US, "%.2fKB", kb)
        return String.format(Locale.US, "%.1f MB", kb / 1024.0)
    }
}

object AdController {
    private const val BANNER_ID = "ca-app-pub-3940256099942544/6300978111"
    private const val INTERSTITIAL_ID = "ca-app-pub-3940256099942544/1033173712"
    private var interstitialAd: InterstitialAd? = null
    private var loading = false

    fun loadInterstitial(context: Context) {
        if (loading || interstitialAd != null) return
        loading = true
        InterstitialAd.load(
            context,
            INTERSTITIAL_ID,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    loading = false
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    interstitialAd = null
                    loading = false
                }
            }
        )
    }

    fun showAfterSaveOnly(activity: Activity, onClosed: () -> Unit) {
        val ad = interstitialAd
        if (ad == null) {
            loadInterstitial(activity)
            onClosed()
            return
        }
        interstitialAd = null
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                loadInterstitial(activity)
                onClosed()
            }

            override fun onAdFailedToShowFullScreenContent(error: com.google.android.gms.ads.AdError) {
                loadInterstitial(activity)
                onClosed()
            }
        }
        ad.show(activity)
    }

    @Composable
    fun Banner(enabled: Boolean, modifier: Modifier = Modifier) {
        if (!enabled) return
        AndroidView(
            modifier = modifier.fillMaxWidth().height(56.dp),
            factory = { context ->
                AdView(context).apply {
                    setAdSize(AdSize.BANNER)
                    adUnitId = BANNER_ID
                    loadAd(AdRequest.Builder().build())
                }
            }
        )
    }
}

@Composable
fun VoiceProApp(vm: RecorderViewModel) {
    val nav = rememberNavController()
    val context = LocalContext.current
    LaunchedEffect(vm.errorMessage) {
        vm.errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            vm.errorMessage = null
        }
    }
    Surface(color = Bg, modifier = Modifier.fillMaxSize()) {
        NavHost(navController = nav, startDestination = "home") {
            composable("home") { HomeScreen(nav, vm) }
            composable("recording") { RecordingScreen(nav, vm) }
            composable("saved") { SavedScreen(nav, vm) }
            composable("list") { RecordingListScreen(nav, vm) }
            composable("player") { PlayerScreen(nav, vm, vm.lastSaved ?: vm.recordings().firstOrNull()) }
            composable("voice") { VoiceChangerScreen(nav, vm, vm.lastSaved ?: vm.recordings().firstOrNull()) }
            composable("trim") { TrimCutScreen(nav, vm, vm.lastSaved ?: vm.recordings().firstOrNull()) }
            composable("settings") { SettingsScreen(nav, vm) }
            composable("pro") { ProScreen(nav, vm) }
            composable("trash") { TrashScreen(nav, vm) }
            composable("transcribe") { LiveTranscribeScreen(nav) }
        }
    }
}

@Composable
fun HomeScreen(nav: NavHostController, vm: RecorderViewModel) {
    val context = LocalContext.current
    var menuOpen by remember { mutableStateOf(false) }
    val importLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) vm.importAudio(uri, context)
    }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            vm.startRecording(vm.currentMode)
            nav.navigate("recording")
        } else {
            Toast.makeText(context, "Microphone permission is required", Toast.LENGTH_LONG).show()
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(Bg).statusBarsPadding()) {
        Column(modifier = Modifier.weight(1f).padding(horizontal = 28.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth().height(72.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BrandTitle()
                Spacer(Modifier.weight(1f))
                IconButton(onClick = { nav.navigate("pro") }) {
                    Text("♛", color = Accent2, fontSize = 32.sp, fontWeight = FontWeight.Black)
                }
                IconButton(onClick = { nav.navigate("settings") }) {
                    Icon(Icons.Default.Settings, null, tint = TextWhite, modifier = Modifier.size(34.dp))
                }
                Box {
                    IconButton(onClick = { menuOpen = true }) {
                        Icon(Icons.Default.MoreVert, null, tint = TextWhite, modifier = Modifier.size(34.dp))
                    }
                    DropdownMenu(
                        expanded = menuOpen,
                        onDismissRequest = { menuOpen = false },
                        modifier = Modifier.background(CardBg2)
                    ) {
                        DropdownMenuItem(text = { MenuText("Import") }, onClick = { menuOpen = false; importLauncher.launch(arrayOf("audio/*")) })
                        DropdownMenuItem(text = { MenuText("Restore from Drive") }, onClick = { menuOpen = false; Toast.makeText(context, "Drive restore screen is ready", Toast.LENGTH_SHORT).show() })
                        DropdownMenuItem(text = { MenuText("Trash") }, onClick = { menuOpen = false; nav.navigate("trash") })
                    }
                }
            }

            PromoCard(onClick = { nav.navigate("transcribe") })
            Spacer(Modifier.height(26.dp))
            ModeCarousel(vm) { mode ->
                vm.setMode(mode)
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                    vm.startRecording(mode)
                    nav.navigate("recording")
                } else {
                    permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                }
            }
        }
        AdController.Banner(enabled = !vm.removeAds)
        BottomHomeBar(selectedRecord = true, onRecord = {}, onList = { nav.navigate("list") })
    }
}

@Composable
fun BrandTitle() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("∪", color = Accent, fontSize = 42.sp, fontWeight = FontWeight.Black)
        Text("Recorder", color = TextWhite, fontSize = 36.sp, fontWeight = FontWeight.Black)
    }
}

@Composable
fun MenuText(value: String) {
    Text(value, color = TextWhite, fontSize = 28.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp))
}

@Composable
fun PromoCard(onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(CardBg)
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(78.dp).clip(CircleShape).background(Color(0xFF30313A)), contentAlignment = Alignment.Center) {
            Text("T", color = Color.Black, fontSize = 30.sp, fontWeight = FontWeight.Black,
                modifier = Modifier.size(54.dp).clip(CircleShape).background(Color(0xFFD9D9DC)).wrapContentSize(Alignment.Center))
            Text("↻", color = Accent, fontSize = 30.sp, modifier = Modifier.align(Alignment.BottomStart))
        }
        Spacer(Modifier.width(18.dp))
        Column(Modifier.weight(1f)) {
            Text(Strings.t("live"), color = TextWhite, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Text(Strings.t("liveSub"), color = Muted, fontSize = 20.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        Text("›", color = TextWhite, fontSize = 46.sp, fontWeight = FontWeight.Light)
    }
}

@Composable
fun ModeCarousel(vm: RecorderViewModel, onStart: (RecordingMode) -> Unit) {
    val scroll = rememberScrollState()
    Row(
        modifier = Modifier.fillMaxWidth().horizontalScroll(scroll),
        horizontalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Spacer(Modifier.width(6.dp))
        vm.modes.forEachIndexed { index, mode ->
            RecordingModeCard(mode = mode, selected = index == vm.modes.indexOf(vm.currentMode), onClick = { onStart(mode) })
        }
        Spacer(Modifier.width(6.dp))
    }
    Spacer(Modifier.height(18.dp))
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        vm.modes.forEachIndexed { i, _ ->
            Box(
                Modifier.padding(4.dp).size(if (i == vm.modes.indexOf(vm.currentMode)) 10.dp else 9.dp)
                    .clip(CircleShape)
                    .background(if (i == vm.modes.indexOf(vm.currentMode)) TextWhite else Color(0xFF3E404A))
            )
        }
    }
}

@Composable
fun RecordingModeCard(mode: RecordingMode, selected: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier.width(304.dp).height(520.dp).clickable(onClick = onClick),
        shape = RoundedCornerShape(34.dp),
        colors = CardDefaults.cardColors(containerColor = if (selected) Color(0xFF202127) else Color(0xFF18191F))
    ) {
        Column(Modifier.fillMaxSize().padding(30.dp)) {
            ModeIllustration(mode.illustration, Modifier.size(160.dp))
            Spacer(Modifier.weight(1f))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(mode.title, color = TextWhite, fontSize = 38.sp, lineHeight = 42.sp, fontWeight = FontWeight.Black, modifier = Modifier.weight(1f))
                if (mode.needsDeviceAudio) Text("?", color = Muted, fontSize = 30.sp, modifier = Modifier.padding(start = 8.dp).border(2.dp, Muted, CircleShape).size(34.dp).wrapContentSize(Alignment.Center))
            }
            Spacer(Modifier.height(14.dp))
            Text(mode.subtitle, color = TextWhite, fontSize = 21.sp, lineHeight = 26.sp)
            Spacer(Modifier.height(48.dp))
            AnimatedMicButton(size = 112.dp, onClick = onClick)
            Spacer(Modifier.height(22.dp))
        }
    }
}

@Composable
fun ModeIllustration(kind: String, modifier: Modifier) {
    Canvas(modifier = modifier) {
        when (kind) {
            "mic" -> {
                drawRoundRect(Color(0xFFE8E8EC), topLeft = Offset(size.width * .32f, size.height * .12f), size = Size(size.width * .36f, size.height * .54f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(45f, 45f))
                drawRoundRect(Color(0xFF222838), topLeft = Offset(size.width * .28f, size.height * .48f), size = Size(size.width * .44f, size.height * .22f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f, 8f))
                drawLine(Accent, Offset(size.width * .5f, size.height * .72f), Offset(size.width * .5f, size.height * .88f), 6f, StrokeCap.Round)
                drawLine(Color(0xFF2E3445), Offset(size.width * .32f, size.height * .9f), Offset(size.width * .68f, size.height * .9f), 5f, StrokeCap.Round)
            }
            "turntable" -> {
                drawRoundRect(Color(0xFF2B3140), topLeft = Offset(size.width * .1f, size.height * .24f), size = Size(size.width * .78f, size.height * .46f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(10f, 10f))
                drawCircle(Color(0xFFD4D5DA), radius = size.minDimension * .20f, center = Offset(size.width * .42f, size.height * .47f))
                drawCircle(Color(0xFF17181E), radius = size.minDimension * .06f, center = Offset(size.width * .42f, size.height * .47f))
                drawLine(Accent, Offset(size.width * .70f, size.height * .25f), Offset(size.width * .82f, size.height * .62f), 7f, StrokeCap.Round)
            }
            "typewriter" -> {
                drawRoundRect(Color(0xFF2E3445), topLeft = Offset(size.width * .12f, size.height * .42f), size = Size(size.width * .78f, size.height * .30f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(12f, 12f))
                drawRoundRect(Color(0xFFD6D6DA), topLeft = Offset(size.width * .22f, size.height * .20f), size = Size(size.width * .54f, size.height * .28f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f, 8f))
                for (r in 0..2) for (c in 0..8) drawCircle(Color.White.copy(.7f), 4f, Offset(size.width * (.24f + c * .055f), size.height * (.51f + r * .07f)))
            }
            else -> {
                drawRoundRect(Color(0xFF242A38), topLeft = Offset(size.width * .22f, size.height * .16f), size = Size(size.width * .30f, size.height * .58f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(18f, 18f))
                drawLine(Accent, Offset(size.width * .29f, size.height * .44f), Offset(size.width * .45f, size.height * .44f), 8f, StrokeCap.Round)
                drawLine(Accent, Offset(size.width * .34f, size.height * .35f), Offset(size.width * .34f, size.height * .55f), 8f, StrokeCap.Round)
                drawLine(Accent, Offset(size.width * .52f, size.height * .50f), Offset(size.width * .78f, size.height * .28f), 5f, StrokeCap.Round)
                drawLine(Accent, Offset(size.width * .78f, size.height * .28f), Offset(size.width * .9f, size.height * .40f), 5f, StrokeCap.Round)
            }
        }
    }
}

@Composable
fun AnimatedMicButton(size: Dp, onClick: () -> Unit) {
    val transition = rememberInfiniteTransition(label = "micPulse")
    val pulse by transition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.18f,
        animationSpec = infiniteRepeatable(tween(850, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "pulse"
    )
    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Box(Modifier.size(size * pulse).clip(CircleShape).background(Accent.copy(alpha = 0.20f)))
        Box(
            Modifier.size(size).clip(CircleShape)
                .background(Brush.horizontalGradient(listOf(Accent2, Accent)))
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Text("🎙", color = Color.White, fontSize = (size.value * .36f).sp)
        }
    }
}

@Composable
fun BottomHomeBar(selectedRecord: Boolean, onRecord: () -> Unit, onList: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().height(92.dp).clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)).background(CardBg).navigationBarsPadding(),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(52.dp).clip(CircleShape).background(Color.White).clickable(onClick = onRecord), contentAlignment = Alignment.Center) {
            Box(Modifier.size(15.dp).clip(CircleShape).background(if (selectedRecord) Accent else Muted))
        }
        Text("≡♪", color = Muted, fontSize = 34.sp, fontWeight = FontWeight.Bold, modifier = Modifier.clickable(onClick = onList))
    }
}

@Composable
fun RecordingScreen(nav: NavHostController, vm: RecorderViewModel) {
    var showSaveDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    Column(Modifier.fillMaxSize().background(Bg).statusBarsPadding()) {
        Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(Modifier.height(44.dp))
            Text(formatHms(vm.elapsedMs), color = TextWhite, fontSize = 58.sp, fontWeight = FontWeight.Black)
            Text(vm.currentMode.title, color = Muted, fontSize = 28.sp)
            Spacer(Modifier.height(44.dp))
            RecordingWaveform(elapsedMs = vm.elapsedMs, markers = vm.markers, modifier = Modifier.fillMaxWidth().height(350.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                SmallPlaybackButton("‹|", onClick = {})
                Spacer(Modifier.width(36.dp))
                SmallPlaybackButton("▶", onClick = {})
                Spacer(Modifier.width(36.dp))
                SmallPlaybackButton("|›", onClick = {})
            }
            Spacer(Modifier.weight(1f))
            Row(Modifier.fillMaxWidth().padding(horizontal = 48.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                CircleAction(Icons.Default.Info, "Mark") { vm.addMarker() }
                Box(Modifier.size(108.dp).clip(CircleShape).background(Brush.horizontalGradient(listOf(Accent2, Accent))).clickable { vm.togglePause() }, contentAlignment = Alignment.Center) {
                    Text(if (vm.isPaused) "▶" else "Ⅱ", color = Color.White, fontSize = 48.sp, fontWeight = FontWeight.Black)
                }
                CircleAction(Icons.Default.Check, "Save") { vm.stopForSaveDialog(); showSaveDialog = true }
            }
            Spacer(Modifier.height(28.dp))
            Text("You can continue recording for up to ${if (vm.currentMode.id == "standard") "9 h 54 min" else "9 h 52 min"}.", color = Muted, fontSize = 19.sp)
            Spacer(Modifier.height(18.dp))
        }
        AdController.Banner(enabled = !vm.removeAds)
    }
    if (showSaveDialog) {
        SaveDialog(
            defaultTitle = vm.defaultTitle(),
            onCancel = { showSaveDialog = false },
            onSave = { title, tag ->
                val item = vm.commitSave(title, tag)
                showSaveDialog = false
                if (item != null) {
                    val activity = context as? Activity
                    if (!vm.removeAds && activity != null) {
                        AdController.showAfterSaveOnly(activity) { nav.navigate("saved") { popUpTo("home") } }
                    } else {
                        nav.navigate("saved") { popUpTo("home") }
                    }
                }
            }
        )
    }
}

@Composable
fun RecordingWaveform(elapsedMs: Long, markers: List<Long>, modifier: Modifier) {
    Canvas(modifier = modifier) {
        val centerX = size.width / 2f
        val centerY = size.height * .48f
        val barCount = 90
        val spacing = size.width / barCount
        val shift = (elapsedMs / 80f) % spacing
        drawLine(Color(0xFF2B2C33), Offset(0f, 36f), Offset(size.width, 36f), 2f)
        for (i in 0..10) {
            val x = i * size.width / 10f - (elapsedMs / 120f % (size.width / 10f))
            drawLine(Color(0xFF363740), Offset(x, 36f), Offset(x, 58f), 2f)
            drawContext.canvas.nativeCanvas.drawText(formatSeconds((elapsedMs / 1000 + i).toLong()), x, 26f, android.graphics.Paint().apply { color = android.graphics.Color.rgb(130,132,146); textSize = 30f; textAlign = android.graphics.Paint.Align.CENTER })
        }
        for (i in -barCount..barCount) {
            val x = centerX + i * spacing - shift
            val seed = abs(sin((i + elapsedMs / 400.0) * 0.44))
            val height = (16 + seed * 92).toFloat()
            val played = x < centerX
            drawLine(if (played) Color.White else Color(0xFF36373E), Offset(x, centerY - height / 2), Offset(x, centerY + height / 2), strokeWidth = 5f, cap = StrokeCap.Round)
        }
        markers.forEachIndexed { idx, m ->
            val deltaSec = ((m - elapsedMs) / 1000f)
            val x = centerX + deltaSec * 70f
            if (x > -40 && x < size.width + 40) {
                drawLine(BlueMarker, Offset(x, centerY + 8), Offset(x, centerY + 130), strokeWidth = 2f)
                drawCircle(BlueMarker, 12f, Offset(x, centerY + 132))
                drawContext.canvas.nativeCanvas.drawText("${idx + 1}", x, centerY + 138, android.graphics.Paint().apply { color = android.graphics.Color.WHITE; textSize = 20f; textAlign = android.graphics.Paint.Align.CENTER; isFakeBoldText = true })
            }
        }
        drawLine(Accent, Offset(centerX, 58f), Offset(centerX, size.height - 20f), strokeWidth = 5f, cap = StrokeCap.Round)
    }
}

@Composable
fun SmallPlaybackButton(label: String, onClick: () -> Unit) {
    Box(Modifier.width(78.dp).height(48.dp).clip(RoundedCornerShape(22.dp)).background(Color(0xFF202128)).clickable(onClick = onClick), contentAlignment = Alignment.Center) {
        Text(label, color = TextWhite, fontSize = 24.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun CircleAction(icon: ImageVector, label: String, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(Modifier.size(82.dp).clip(CircleShape).background(Color(0xFF20232B)).clickable(onClick = onClick), contentAlignment = Alignment.Center) {
            Icon(icon, null, tint = Color.White, modifier = Modifier.size(38.dp))
        }
        Spacer(Modifier.height(8.dp))
        Text(label, color = TextWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun SaveDialog(defaultTitle: String, onCancel: () -> Unit, onSave: (String, String) -> Unit) {
    var title by remember { mutableStateOf(defaultTitle) }
    var expanded by remember { mutableStateOf(false) }
    var tag by remember { mutableStateOf("Untagged") }
    AlertDialog(
        onDismissRequest = onCancel,
        containerColor = CardBg,
        shape = RoundedCornerShape(28.dp),
        title = { Text("Save", color = TextWhite, fontSize = 34.sp, fontWeight = FontWeight.Black) },
        text = {
            Column(Modifier.imePadding()) {
                Text("Title", color = TextWhite, fontSize = 18.sp)
                TextField(
                    value = title,
                    onValueChange = { if (it.length <= 50) title = it },
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                    colors = TextFieldDefaults.colors(focusedContainerColor = Bg2, unfocusedContainerColor = Bg2, focusedTextColor = TextWhite, unfocusedTextColor = TextWhite, cursorColor = Accent),
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)),
                    trailingIcon = { Text("${title.length}/50", color = Muted) }
                )
                Spacer(Modifier.height(22.dp))
                Text("Tag", color = TextWhite, fontSize = 18.sp)
                Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(Bg2).clickable { expanded = true }.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(tag, color = TextWhite, fontSize = 24.sp, modifier = Modifier.weight(1f))
                        Icon(Icons.Default.KeyboardArrowDown, null, tint = TextWhite)
                    }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }, modifier = Modifier.background(Bg2)) {
                        listOf("Untagged", "Voice note", "Meeting", "Lecture", "Music", "Live transcription", "+ Add tag").forEach {
                            DropdownMenuItem(text = { Text(it, color = if (it.startsWith("+")) Accent else TextWhite, fontSize = 22.sp) }, onClick = { tag = it; expanded = false })
                        }
                    }
                }
            }
        },
        confirmButton = { Button(onClick = { onSave(title, tag) }, colors = ButtonDefaults.buttonColors(containerColor = Accent), shape = RoundedCornerShape(24.dp), modifier = Modifier.width(160.dp).height(58.dp)) { Text("Save", fontSize = 24.sp, fontWeight = FontWeight.Bold) } },
        dismissButton = { OutlinedButton(onClick = onCancel, shape = RoundedCornerShape(24.dp), modifier = Modifier.width(150.dp).height(58.dp)) { Text("Cancel", fontSize = 22.sp, color = Muted) } }
    )
}

@Composable
fun SavedScreen(nav: NavHostController, vm: RecorderViewModel) {
    val item = vm.lastSaved
    var showRate by remember { mutableStateOf(item != null && !vm.removeAds) }
    Column(Modifier.fillMaxSize().background(Bg).statusBarsPadding()) {
        Row(Modifier.fillMaxWidth().height(72.dp).padding(horizontal = 22.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { nav.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = TextWhite, modifier = Modifier.size(38.dp)) }
            Text(Strings.t("saved"), color = TextWhite, fontSize = 34.sp, fontWeight = FontWeight.Black, modifier = Modifier.weight(1f))
            IconButton(onClick = { vm.startNewSession(); nav.navigate("home") { popUpTo("home") { inclusive = true } } }) { Icon(Icons.Default.Home, null, tint = TextWhite, modifier = Modifier.size(36.dp)) }
        }
        if (item == null) {
            EmptyState("No saved recording", "Start a new recording from the home screen.")
        } else {
            Column(Modifier.weight(1f).padding(horizontal = 28.dp)) {
                RecordingMiniCard(item, vm, large = true)
                Spacer(Modifier.height(34.dp))
                GradientButton(Strings.t("startNew")) { vm.startNewSession(); nav.navigate("home") { popUpTo("home") { inclusive = true } } }
                Spacer(Modifier.height(16.dp))
                OutlinedAccentButton(Strings.t("list")) { nav.navigate("list") }
                Spacer(Modifier.height(48.dp))
                Text("Guess you might need", color = TextWhite, fontSize = 27.sp)
                Spacer(Modifier.height(24.dp))
                QuickActionsGrid(nav, vm, item)
            }
            AdController.Banner(enabled = !vm.removeAds)
        }
    }
    if (showRate) RateDialog(onDismiss = { showRate = false })
}

@Composable
fun RecordingMiniCard(item: RecordingItem, vm: RecorderViewModel, large: Boolean = false) {
    Card(shape = RoundedCornerShape(28.dp), colors = CardDefaults.cardColors(containerColor = CardBg), modifier = Modifier.fillMaxWidth().height(if (large) 172.dp else 108.dp)) {
        Column(Modifier.fillMaxSize().padding(22.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(58.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFF30313A)), contentAlignment = Alignment.Center) { MiniWaveIcon() }
                Spacer(Modifier.width(16.dp))
                Column(Modifier.weight(1f)) {
                    Text(item.title, color = TextWhite, fontSize = 24.sp, fontWeight = FontWeight.Black, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text("${item.sizeLabel}  ${item.format}", color = Muted, fontSize = 18.sp)
                }
            }
            if (large) {
                Spacer(Modifier.height(18.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { vm.togglePlay(item) }) { Icon(Icons.Default.PlayArrow, null, tint = TextWhite) }
                    Box(Modifier.size(22.dp).clip(CircleShape).background(Color.White), contentAlignment = Alignment.Center) { Box(Modifier.size(10.dp).clip(CircleShape).background(Accent)) }
                    ProgressLine(Modifier.weight(1f).padding(horizontal = 16.dp))
                    Text(formatMmSs(item.durationMs), color = Muted)
                }
            }
        }
    }
}

@Composable
fun MiniWaveIcon() {
    Canvas(Modifier.size(34.dp)) {
        for (i in 0..8) {
            val h = (10 + abs(sin(i.toDouble())) * 25).toFloat()
            val x = i * size.width / 9f
            drawLine(Accent, Offset(x, size.height / 2 - h / 2), Offset(x, size.height / 2 + h / 2), 4f, StrokeCap.Round)
        }
    }
}

@Composable
fun ProgressLine(modifier: Modifier = Modifier) {
    Canvas(modifier.height(18.dp)) {
        drawLine(Color(0xFF34353D), Offset(0f, size.height / 2), Offset(size.width, size.height / 2), 8f, StrokeCap.Round)
        drawCircle(BlueMarker, 5f, Offset(size.width * .42f, size.height / 2))
        drawCircle(BlueMarker, 5f, Offset(size.width * .78f, size.height / 2))
    }
}

@Composable
fun QuickActionsGrid(nav: NavHostController, vm: RecorderViewModel, item: RecordingItem) {
    val context = LocalContext.current
    val actions = listOf(
        "Share" to { shareFile(context, item) },
        "Share via link" to { Toast.makeText(context, "Local share link is not available offline", Toast.LENGTH_SHORT).show() },
        "Back up to\nDrive" to { backupToDrive(context, item) },
        "Delete" to { vm.deleteRecording(item); nav.navigate("home") },
        "Trim" to { nav.navigate("trim") },
        "Voice changer" to { nav.navigate("voice") },
        "Continue\nrecording" to { vm.startRecording(vm.currentMode); nav.navigate("recording") },
        "Background\nmusic" to { Toast.makeText(context, "Background music tool", Toast.LENGTH_SHORT).show() }
    )
    Column(verticalArrangement = Arrangement.spacedBy(22.dp)) {
        actions.chunked(4).forEach { row ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                row.forEach { (title, action) ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(76.dp).clickable(onClick = action)) {
                        Box(Modifier.size(66.dp).clip(RoundedCornerShape(18.dp)).background(CardBg), contentAlignment = Alignment.Center) {
                            Text(actionIcon(title), fontSize = 28.sp, color = Accent)
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(title, color = Muted, fontSize = 16.sp, textAlign = TextAlign.Center, lineHeight = 18.sp)
                    }
                }
            }
        }
    }
}

private fun actionIcon(title: String): String = when {
    title.startsWith("Share") -> "↗"
    title.startsWith("Back") -> "☁"
    title.startsWith("Delete") -> "⌫"
    title.startsWith("Trim") -> "✂"
    title.startsWith("Voice") -> "♬"
    title.startsWith("Continue") -> "Ū"
    else -> "♪"
}

@Composable
fun GradientButton(text: String, onClick: () -> Unit) {
    Box(Modifier.fillMaxWidth().height(72.dp).clip(RoundedCornerShape(36.dp)).background(Brush.horizontalGradient(listOf(Accent2, Accent))).clickable(onClick = onClick), contentAlignment = Alignment.Center) {
        Text(text, color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Black)
    }
}

@Composable
fun OutlinedAccentButton(text: String, onClick: () -> Unit) {
    Box(Modifier.fillMaxWidth().height(64.dp).clip(RoundedCornerShape(32.dp)).border(2.dp, Accent, RoundedCornerShape(32.dp)).clickable(onClick = onClick), contentAlignment = Alignment.Center) {
        Text(text, color = Accent, fontSize = 24.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun RateDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CardBg,
        shape = RoundedCornerShape(28.dp),
        title = { Text("😀", fontSize = 64.sp) },
        text = {
            Column {
                Text("We are working hard for a better user experience.\nWe’d greatly appreciate if you can rate us.", color = TextWhite, fontSize = 25.sp, fontWeight = FontWeight.Bold, lineHeight = 32.sp)
                Spacer(Modifier.height(24.dp))
                Text("The best we can get :)", color = Accent, fontSize = 24.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                Text("★ ★ ★ ★ ★", color = Accent, fontSize = 42.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = { GradientButton("RATE") { onDismiss() } }
    )
}

@Composable
fun RecordingListScreen(nav: NavHostController, vm: RecorderViewModel) {
    val recordingItems = remember(vm.lastSaved, vm.playingPath) { vm.recordings() }
    Column(Modifier.fillMaxSize().background(Bg).statusBarsPadding()) {
        TopBar("Recordings", onBack = { nav.popBackStack() }, right = {
            IconButton(onClick = { nav.navigate("home") }) { Icon(Icons.Default.Home, null, tint = TextWhite) }
        })
        if (recordingItems.isEmpty()) {
            EmptyState("No recordings", "Tap the red button to create your first recording.")
        } else {
            LazyColumn(Modifier.weight(1f).padding(22.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                items(recordingItems) { item ->
                    Box(Modifier.clickable { vm.selectRecording(item); nav.navigate("player") }) {
                        RecordingMiniCard(item, vm)
                    }
                }
            }
        }
        AdController.Banner(enabled = !vm.removeAds)
        BottomHomeBar(selectedRecord = false, onRecord = { nav.navigate("home") }, onList = {})
    }
}

@Composable
fun PlayerScreen(nav: NavHostController, vm: RecorderViewModel, item: RecordingItem?) {
    if (item == null) { EmptyFull(nav); return }
    Column(Modifier.fillMaxSize().background(Bg).statusBarsPadding()) {
        TopBar(item.title, onBack = { nav.popBackStack() }, right = { IconButton(onClick = {}) { Icon(Icons.Default.MoreVert, null, tint = TextWhite) } })
        Canvas(Modifier.fillMaxWidth().height(360.dp)) {
            val centerY = size.height * .46f
            for (i in 0..80) {
                val x = i * size.width / 80f
                val h = if (i < 45) 10f else 54f * abs(sin(i * .31)).toFloat() + 8f
                drawLine(if (i < 45) Color.White else Color(0xFF35363E), Offset(x, centerY - h / 2), Offset(x, centerY + h / 2), 5f, StrokeCap.Round)
            }
            drawLine(Accent, Offset(size.width / 2, 0f), Offset(size.width / 2, size.height), 5f, StrokeCap.Round)
            item.markers.ifEmpty { listOf(2000L, 5000L) }.forEach { m ->
                val x = (m.toFloat() / max(item.durationMs, 1L)) * size.width
                drawLine(BlueMarker, Offset(x, centerY), Offset(x, centerY + 130), 2f)
                drawCircle(BlueMarker, 9f, Offset(x, centerY + 132))
            }
        }
        Column(Modifier.weight(1f).padding(horizontal = 28.dp), verticalArrangement = Arrangement.Bottom) {
            Card(shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp), colors = CardDefaults.cardColors(containerColor = CardBg), modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(28.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                        Text("◩", color = TextWhite, fontSize = 34.sp)
                        Text("▢", color = TextWhite, fontSize = 34.sp)
                        Text("✂", color = Accent, fontSize = 34.sp, modifier = Modifier.clickable { nav.navigate("trim") })
                        Text("↶", color = TextWhite, fontSize = 34.sp)
                        Text("1.0X", color = TextWhite, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.height(24.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("00:00", color = TextWhite, fontSize = 20.sp)
                        ProgressLine(Modifier.weight(1f).padding(horizontal = 16.dp))
                        Text(formatMmSs(item.durationMs), color = TextWhite, fontSize = 20.sp)
                    }
                    Spacer(Modifier.height(42.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
                        Text("↶5", color = TextWhite, fontSize = 30.sp, fontWeight = FontWeight.Bold)
                        Box(Modifier.size(92.dp).clip(CircleShape).background(Brush.horizontalGradient(listOf(Accent2, Accent))).clickable { vm.togglePlay(item) }, contentAlignment = Alignment.Center) {
                            Text(if (vm.playingPath == item.path) "Ⅱ" else "▶", color = Color.White, fontSize = 40.sp, fontWeight = FontWeight.Bold)
                        }
                        Text("5↷", color = TextWhite, fontSize = 30.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        AdController.Banner(enabled = !vm.removeAds)
    }
}

@Composable
fun VoiceChangerScreen(nav: NavHostController, vm: RecorderViewModel, recording: RecordingItem?) {
    if (recording == null) { EmptyFull(nav); return }
    var selected by remember { mutableStateOf("Normal") }
    var pitch by remember { mutableStateOf(0f) }
    var speed by remember { mutableStateOf(1.0f) }
    val top = listOf("Normal", "Girl", "Man", "Child")
    val rest = listOf("Squirrel", "Tenor singer", "Megaphone", "Nervous", "Drunk", "Robot", "Death", "Monster", "Alien")
    Column(Modifier.fillMaxSize().background(Bg).statusBarsPadding()) {
        TopBar("Voice changer", onBack = { nav.popBackStack() }, right = {
            Box(Modifier.size(62.dp).clip(RoundedCornerShape(26.dp)).background(CardBg), contentAlignment = Alignment.Center) { Icon(Icons.Default.Check, null, tint = Muted, modifier = Modifier.size(36.dp)) }
        })
        LazyColumn(Modifier.weight(1f).padding(horizontal = 28.dp)) {
            item { Text("Voice effects", color = TextWhite, fontSize = 30.sp, fontWeight = FontWeight.Bold); Spacer(Modifier.height(28.dp)) }
            item { EffectsRow(top, selected) { selected = it } }
            item {
                if (selected in top) {
                    Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = CardBg), modifier = Modifier.fillMaxWidth().padding(top = 28.dp)) {
                        Column(Modifier.padding(28.dp)) {
                            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                Text("Adjust effect", color = TextWhite, fontSize = 28.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                                Text("Reset", color = Muted, fontSize = 22.sp, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { pitch = 0f; speed = 1f })
                            }
                            SliderRow("Pitch", pitch, -15f, 15f) { pitch = it }
                            SliderRow("Speed", speed, .5f, 1.5f) { speed = it }
                        }
                    }
                }
                Spacer(Modifier.height(28.dp))
                EffectsGrid(rest, selected) { selected = it }
                Spacer(Modifier.height(20.dp))
            }
        }
        Card(shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp), colors = CardDefaults.cardColors(containerColor = CardBg), modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                StaticWavePreview()
                Spacer(Modifier.height(14.dp))
                ProgressLine(Modifier.fillMaxWidth().padding(horizontal = 30.dp))
                Row(Modifier.fillMaxWidth().padding(top = 32.dp, bottom = 24.dp), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
                    Text("↗", color = TextWhite, fontSize = 30.sp)
                    Box(Modifier.size(86.dp).clip(CircleShape).background(Brush.horizontalGradient(listOf(Accent2, Accent))).clickable { vm.togglePlay(recording) }, contentAlignment = Alignment.Center) { Text(if (vm.playingPath == recording.path) "Ⅱ" else "▶", color = Color.White, fontSize = 38.sp, fontWeight = FontWeight.Bold) }
                    Text("1.0X", color = TextWhite, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun EffectsRow(items: List<String>, selected: String, onSelect: (String) -> Unit) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        items.forEach { EffectIcon(it, selected == it, onClick = { onSelect(it) }) }
    }
}

@Composable
fun EffectsGrid(items: List<String>, selected: String, onSelect: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(28.dp)) {
        items.chunked(4).forEach { row ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                row.forEach { EffectIcon(it, selected == it, onClick = { onSelect(it) }) }
                repeat(4 - row.size) { Spacer(Modifier.width(82.dp)) }
            }
        }
    }
}

@Composable
fun EffectIcon(label: String, selected: Boolean, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(82.dp).clickable(onClick = onClick)) {
        Canvas(Modifier.size(76.dp).clip(CircleShape).background(colorForEffect(label)).border(if (selected) 3.dp else 0.dp, Accent, CircleShape)) {
            if (label == "Normal") {
                for (i in 0..5) drawLine(Accent, Offset(size.width * (.28f + i * .09f), size.height * .35f), Offset(size.width * (.28f + i * .09f), size.height * .65f), 5f, StrokeCap.Round)
            } else {
                drawCircle(Color(0xFF15161B), size.minDimension * .10f, Offset(size.width * .38f, size.height * .42f))
                drawCircle(Color(0xFF15161B), size.minDimension * .10f, Offset(size.width * .62f, size.height * .42f))
                drawArc(Color(0xFF15161B), 20f, 140f, false, topLeft = Offset(size.width * .31f, size.height * .48f), size = Size(size.width * .38f, size.height * .25f), style = Stroke(5f, cap = StrokeCap.Round))
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(label, color = if (selected) Accent else TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold, maxLines = 2, textAlign = TextAlign.Center)
    }
}

private fun colorForEffect(label: String): Color = when (label) {
    "Girl" -> Color(0xFFC05B86)
    "Man" -> Color(0xFF5865B0)
    "Child" -> Color(0xFF58B976)
    "Squirrel" -> Color(0xFFFFC7A8)
    "Tenor singer" -> Color(0xFF5448B8)
    "Megaphone" -> Color(0xFFA3D8C8)
    "Nervous" -> Color(0xFFA33B67)
    "Robot" -> Color(0xFF119CE5)
    "Death" -> Color(0xFF8E6AD2)
    "Monster" -> Color(0xFFE8C46D)
    "Alien" -> Color(0xFF5B43A8)
    else -> Color(0xFF24252C)
}

@Composable
fun SliderRow(label: String, value: Float, min: Float, max: Float, onValueChange: (Float) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 18.dp)) {
        Text(label, color = TextWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(72.dp))
        Text(if (label == "Pitch") "${min.toInt()}" else String.format(Locale.US, "%.1f", min), color = Muted, modifier = Modifier.width(42.dp))
        Slider(value = value, onValueChange = onValueChange, valueRange = min..max, modifier = Modifier.weight(1f))
        Text(if (label == "Pitch") "${max.toInt()}" else String.format(Locale.US, "%.1f", max), color = Muted, modifier = Modifier.width(42.dp), textAlign = TextAlign.End)
    }
}

@Composable
fun StaticWavePreview() {
    Canvas(Modifier.fillMaxWidth().height(82.dp).clip(RoundedCornerShape(16.dp)).background(Bg2)) {
        val center = size.height / 2f
        for (i in 0..60) {
            val x = i * size.width / 60f
            val h = (6f + abs(cos(i * .4)) * 46f).toFloat()
            drawLine(if (i < 32) Color.White else Color(0xFF4B4C55), Offset(x, center - h / 2), Offset(x, center + h / 2), 4f, StrokeCap.Round)
        }
        drawLine(Accent, Offset(size.width * .50f, 0f), Offset(size.width * .50f, size.height), 4f, StrokeCap.Round)
    }
}

@Composable
fun TrimCutScreen(nav: NavHostController, vm: RecorderViewModel, item: RecordingItem?) {
    if (item == null) { EmptyFull(nav); return }
    var tab by remember { mutableStateOf("Trim") }
    var discard by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxSize().background(Bg).statusBarsPadding()) {
        TopBar(item.title, onBack = { discard = true }, right = {
            IconButton(onClick = { nav.popBackStack() }) { Icon(Icons.Default.Done, null, tint = Accent, modifier = Modifier.size(34.dp)) }
        })
        Row(Modifier.fillMaxWidth().padding(top = 24.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
            TabText("Trim", tab == "Trim") { tab = "Trim" }
            TabText("Cut", tab == "Cut") { tab = "Cut" }
        }
        Row(Modifier.fillMaxWidth().padding(horizontal = 28.dp, vertical = 18.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            TimeAdjust("00:00.7")
            Column(horizontalAlignment = Alignment.CenterHorizontally) { Text(if (tab == "Trim") "00:06.3" else "00:01.5", color = TextWhite, fontSize = 24.sp); Text("Total", color = Muted, fontSize = 18.sp, fontWeight = FontWeight.Bold) }
            TimeAdjust("00:07.0")
        }
        TrimWaveform(tab, Modifier.fillMaxWidth().height(390.dp))
        Row(Modifier.fillMaxWidth().padding(horizontal = 58.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("◩", color = TextWhite, fontSize = 34.sp)
            Text("|‹", color = TextWhite, fontSize = 40.sp)
            Box(Modifier.size(78.dp).clip(CircleShape).background(Brush.horizontalGradient(listOf(Accent2, Accent))).clickable { vm.togglePlay(item) }, contentAlignment = Alignment.Center) { Text(if (vm.playingPath == item.path) "Ⅱ" else "▶", color = Color.White, fontSize = 34.sp, fontWeight = FontWeight.Bold) }
            Text("›|", color = TextWhite, fontSize = 40.sp)
            Text("1.0X", color = TextWhite, fontSize = 24.sp, fontWeight = FontWeight.Black)
        }
        Spacer(Modifier.weight(1f))
        AdController.Banner(enabled = !vm.removeAds)
    }
    if (discard) {
        AlertDialog(
            onDismissRequest = { discard = false },
            containerColor = CardBg,
            title = { Text("Discard your unsaved changes?", color = TextWhite, fontSize = 30.sp, fontWeight = FontWeight.Black) },
            confirmButton = { Button(onClick = { discard = false; nav.popBackStack() }, colors = ButtonDefaults.buttonColors(containerColor = Accent), shape = RoundedCornerShape(28.dp)) { Text("Discard", fontSize = 22.sp, fontWeight = FontWeight.Bold) } },
            dismissButton = { OutlinedButton(onClick = { discard = false }, shape = RoundedCornerShape(28.dp)) { Text("Cancel", color = Muted, fontSize = 22.sp) } }
        )
    }
}

@Composable
fun TabText(text: String, selected: Boolean, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable(onClick = onClick)) {
        Text(text, color = if (selected) TextWhite else Muted, fontSize = 28.sp, fontWeight = FontWeight.Black)
        Spacer(Modifier.height(6.dp))
        Box(Modifier.width(34.dp).height(5.dp).clip(RoundedCornerShape(5.dp)).background(if (selected) Accent else Color.Transparent))
    }
}

@Composable
fun TimeAdjust(time: String) {
    Row(Modifier.width(136.dp).height(48.dp).clip(RoundedCornerShape(24.dp)).background(CardBg), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceAround) {
        Text("−", color = TextWhite, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Text(time, color = TextWhite, fontSize = 18.sp)
        Text("+", color = TextWhite, fontSize = 24.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun TrimWaveform(tab: String, modifier: Modifier) {
    Canvas(modifier = modifier.background(CardBg)) {
        val centerY = size.height * .45f
        val start = size.width * .12f
        val end = size.width * .88f
        drawRect(Color(0xFF3A1412).copy(.7f), topLeft = Offset(if (tab == "Cut") start else 0f, 40f), size = Size(if (tab == "Cut") end - start else start, size.height * .55f))
        drawRect(Color(0xFF3A1412).copy(.7f), topLeft = Offset(if (tab == "Cut") 0f else end, 40f), size = Size(if (tab == "Cut") size.width else size.width - end, size.height * .55f))
        for (i in 0..75) {
            val x = i * size.width / 75f
            val h = (16f + abs(sin(i * .22)) * 75f).toFloat()
            drawLine(Color.White.copy(if (tab == "Cut" && x in start..end) .25f else 1f), Offset(x, centerY - h / 2), Offset(x, centerY + h / 2), 5f, StrokeCap.Round)
        }
        listOf(start, end).forEachIndexed { idx, x ->
            drawLine(Accent, Offset(x, 40f), Offset(x, size.height * .84f), 4f)
            drawRoundRect(Accent, topLeft = Offset(x - 22f, if (idx == 0) size.height * .76f else 40f), size = Size(44f, 44f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f, 8f))
        }
        listOf(size.width * .55f, size.width * .76f).forEach { x ->
            drawLine(BlueMarker, Offset(x, 30f), Offset(x, size.height * .84f), 2f)
            drawCircle(BlueMarker, 10f, Offset(x, size.height * .84f))
        }
    }
}

@Composable
fun SettingsScreen(nav: NavHostController, vm: RecorderViewModel) {
    val context = LocalContext.current
    LazyColumn(Modifier.fillMaxSize().background(Bg).statusBarsPadding()) {
        item { TopBar(Strings.t("settings"), onBack = { nav.popBackStack() }) }
        item {
            SettingsCard("Backup & Restore", "Never lose your recordings", "△") { Toast.makeText(context, "Backup screen", Toast.LENGTH_SHORT).show() }
            SettingsCard(Strings.t("removeAds"), "Upgrade", "AD") { nav.navigate("pro") }
            Text(Strings.t("recording"), color = Accent, fontSize = 30.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(28.dp, 22.dp, 28.dp, 8.dp))
            SettingsRow("Storage path", "/storage/emulated/0/Music/VoiceProRecorder/", true)
            SettingsRow("Recording quality", "High (CD)", true)
            SettingsRow("Recording format", "M4A", true)
            SettingsRow("Audio source", "Main", true)
            SettingsSwitch("Use when Bluetooth mic is available", false)
            SettingsRow("Sampling rate", "44.1kHz (CD)", true)
            SettingsRow("Encoder bitrate", "128 kbps", true)
            Text(Strings.t("language"), color = Accent, fontSize = 30.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(28.dp, 22.dp, 28.dp, 8.dp))
            listOf("System default", "English", "العربية", "Türkçe", "Português", "Español", "Français", "Deutsch").forEach { SettingsRow(it, if (it == "System default") "Uses phone language automatically" else "", false) }
            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
fun SettingsCard(title: String, subtitle: String, icon: String, onClick: () -> Unit) {
    Row(Modifier.fillMaxWidth().padding(horizontal = 0.dp, vertical = 10.dp).clip(RoundedCornerShape(24.dp)).background(CardBg).clickable(onClick = onClick).padding(28.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(icon, color = Accent, fontSize = 32.sp, fontWeight = FontWeight.Black, modifier = Modifier.width(64.dp))
        Column(Modifier.weight(1f)) {
            Text(title, color = TextWhite, fontSize = 28.sp)
            Text(subtitle, color = Muted, fontSize = 18.sp)
        }
        Text("›", color = TextWhite, fontSize = 46.sp)
    }
}

@Composable
fun SettingsRow(title: String, subtitle: String, arrow: Boolean) {
    Row(Modifier.fillMaxWidth().padding(horizontal = 28.dp, vertical = 18.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text(title, color = TextWhite, fontSize = 26.sp)
            if (subtitle.isNotBlank()) Text(subtitle, color = Muted, fontSize = 18.sp)
        }
        if (arrow) Text("›", color = TextWhite, fontSize = 44.sp)
    }
}

@Composable
fun SettingsSwitch(title: String, checked: Boolean) {
    var value by remember { mutableStateOf(checked) }
    Row(Modifier.fillMaxWidth().padding(horizontal = 28.dp, vertical = 18.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(title, color = TextWhite, fontSize = 26.sp, modifier = Modifier.weight(1f))
        Switch(checked = value, onCheckedChange = { value = it })
    }
}

@Composable
fun ProScreen(nav: NavHostController, vm: RecorderViewModel) {
    val context = LocalContext.current
    Column(Modifier.fillMaxSize().background(Bg).statusBarsPadding().padding(28.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { nav.popBackStack() }) { Icon(Icons.Default.Close, null, tint = TextWhite, modifier = Modifier.size(34.dp)) }
            Spacer(Modifier.weight(1f))
            Text("Restore", color = Muted, fontSize = 24.sp)
        }
        Spacer(Modifier.height(80.dp))
        Row(verticalAlignment = Alignment.CenterVertically) { BrandTitle(); Text(" PRO", color = Color.Black, fontSize = 24.sp, fontWeight = FontWeight.Black, modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(Accent2).padding(horizontal = 10.dp, vertical = 4.dp)) }
        Spacer(Modifier.height(34.dp))
        listOf("Real-time transcription", "AI noise reduction", "Remove all ads", "Bluetooth recording").forEach { feature ->
            Row(Modifier.padding(vertical = 13.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(46.dp).clip(CircleShape).background(Brush.horizontalGradient(listOf(Accent2, Accent))), contentAlignment = Alignment.Center) { Text("✓", color = Color.Black, fontWeight = FontWeight.Black) }
                Spacer(Modifier.width(20.dp))
                Text(feature, color = TextWhite, fontSize = 27.sp)
            }
        }
        Spacer(Modifier.height(30.dp))
        SubscriptionBox("Monthly", "$2.49", false)
        SubscriptionBox("Yearly", "7-day free trial, then $9.99/year", true)
        SubscriptionBox("Lifetime - Best value", "$19.99", false)
        Spacer(Modifier.weight(1f))
        Text("Cancel anytime", color = Muted, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
        Spacer(Modifier.height(20.dp))
        GradientButton("Subscribe Now") { vm.updateRemoveAds(true); Toast.makeText(context, "Ads removed", Toast.LENGTH_SHORT).show(); nav.popBackStack() }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
fun SubscriptionBox(title: String, subtitle: String, selected: Boolean) {
    Row(Modifier.fillMaxWidth().padding(vertical = 8.dp).height(82.dp).clip(RoundedCornerShape(20.dp)).border(2.dp, if (selected) Accent else Color(0xFF4A211D), RoundedCornerShape(20.dp)).background(if (selected) Color(0xFF311D1C) else Bg).padding(horizontal = 22.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) { Text(title, color = TextWhite, fontSize = 24.sp, fontWeight = FontWeight.Bold); Text(subtitle, color = Muted, fontSize = 18.sp) }
        Box(Modifier.size(32.dp).clip(CircleShape).border(3.dp, if (selected) Accent else Color(0xFF4A211D), CircleShape).background(if (selected) Accent else Color.Transparent), contentAlignment = Alignment.Center) { if (selected) Text("✓", color = Color.White, fontWeight = FontWeight.Bold) }
    }
}

@Composable
fun TrashScreen(nav: NavHostController, vm: RecorderViewModel) {
    Column(Modifier.fillMaxSize().background(Bg).statusBarsPadding()) {
        TopBar("Trash", onBack = { nav.popBackStack() })
        EmptyState("Trash is empty", "Deleted recordings will appear here when trash mode is enabled.")
    }
}

@Composable
fun LiveTranscribeScreen(nav: NavHostController) {
    Column(Modifier.fillMaxSize().background(Bg).statusBarsPadding()) {
        TopBar("Live transcribe", onBack = { nav.popBackStack() })
        EmptyState("Real-time text transcription", "This screen is prepared for the transcription engine and keeps the same dark design.")
    }
}

@Composable
fun TopBar(title: String, onBack: () -> Unit, right: @Composable () -> Unit = {}) {
    Row(Modifier.fillMaxWidth().height(76.dp).padding(horizontal = 18.dp), verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = TextWhite, modifier = Modifier.size(38.dp)) }
        Text(title, color = TextWhite, fontSize = 32.sp, fontWeight = FontWeight.Black, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
        right()
    }
}

@Composable
fun EmptyState(title: String, subtitle: String) {
    Box(Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(title, color = TextWhite, fontSize = 28.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            Spacer(Modifier.height(12.dp))
            Text(subtitle, color = Muted, fontSize = 18.sp, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun EmptyFull(nav: NavHostController) {
    Column(Modifier.fillMaxSize().background(Bg).statusBarsPadding()) {
        TopBar("Recorder", onBack = { nav.popBackStack() })
        EmptyState("No recording selected", "Open a recording from the list first.")
    }
}

fun formatHms(ms: Long): String {
    val s = ms / 1000
    return String.format(Locale.US, "%02d : %02d : %02d", s / 3600, (s % 3600) / 60, s % 60)
}

fun formatMmSs(ms: Long): String {
    val s = ms / 1000
    return String.format(Locale.US, "%02d:%02d", s / 60, s % 60)
}

fun formatSeconds(sec: Long): String = String.format(Locale.US, "%02d:%02d", sec / 60, sec % 60)

fun shareFile(context: Context, item: RecordingItem) {
    val file = File(item.path)
    if (!file.exists()) return
    val uri = FileProvider.getUriForFile(context, context.packageName + ".fileprovider", file)
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "audio/mp4"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(intent, "Share recording"))
}

fun backupToDrive(context: Context, item: RecordingItem) {
    val file = File(item.path)
    if (!file.exists()) return
    val uri = FileProvider.getUriForFile(context, context.packageName + ".fileprovider", file)
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "audio/mp4"
        putExtra(Intent.EXTRA_STREAM, uri)
        putExtra(Intent.EXTRA_TITLE, item.title)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        `package` = "com.google.android.apps.docs"
    }
    runCatching { context.startActivity(intent) }.onFailure { shareFile(context, item) }
}
