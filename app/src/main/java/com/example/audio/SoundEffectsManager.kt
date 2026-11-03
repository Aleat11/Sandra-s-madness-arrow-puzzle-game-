package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.speech.tts.TextToSpeech
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin
import kotlin.math.sign
import kotlin.random.Random

enum class HilariousArrowSound(val comicText: String, val badgeColorHex: Long) {
    BOING("BOINGGG!! 🌀", 0xFF10B981),
    CASH_REGISTER("CA-CHING!! 💸", 0xFFF59E0B),
    HORN("HONK HONK!! 📯", 0xFFEC4899),
    SWOOP("SWOOOSH!! 🚀", 0xFF0EA5E9),
    SCREAM("AAAHHH!! 😱", 0xFF8B5CF6),
    BRING_HOME_THE_JERKY("MOMMA'S BRINGIN' HOME THE JERKY!! 🥩", 0xFFE53935)
}

enum class MusicTrack(val title: String, val vibe: String) {
    CROCHET_LOFI("Cozy Crochet Cafe Lo-Fi", "Warm Rhodes Chords"),
    BURGER_BOUNCE("Sizzling Burger Diner Groove", "Upbeat Marimba Bounce"),
    BOOKWORM_WALTZ("Midnight Bookworm Music Box", "Dreamy Celestial Waltz"),
    JERKY_POLKA("Momma's Jerky & Bakery Jam", "Playful Arcade Melody")
}

/**
 * Hilarious Comedy Sound Effects Synthesizer + Android TextToSpeech ("Momma's bringin' home the jerky!")
 * + Auto-Switching 4-Track Calming Music Jukebox.
 */
class SoundEffectsManager(context: Context? = null) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var musicJob: Job? = null
    private var tts: TextToSpeech? = null
    @Volatile
    private var isTtsReady: Boolean = false

    private val _currentTrack = MutableStateFlow(MusicTrack.CROCHET_LOFI)
    val currentTrack: StateFlow<MusicTrack> = _currentTrack.asStateFlow()

    @Volatile
    var sfxEnabled: Boolean = true

    @Volatile
    var musicEnabled: Boolean = true
        private set

    private var arrowSoundCycleIndex = 0

    init {
        if (context != null) {
            runCatching {
                tts = TextToSpeech(context.applicationContext) { status ->
                    if (status == TextToSpeech.SUCCESS) {
                        tts?.language = Locale.US
                        tts?.setPitch(1.12f)
                        tts?.setSpeechRate(1.08f)
                        isTtsReady = true
                    }
                }
            }
        }
    }

    fun setSoundEffectsEnabled(enabled: Boolean) {
        sfxEnabled = enabled
    }

    fun setCalmingMusicEnabled(enabled: Boolean) {
        musicEnabled = enabled
        if (enabled) {
            startMusicLoopIfNeeded()
        } else {
            musicJob?.cancel()
            musicJob = null
        }
    }

    fun switchToNextSong() {
        val nextOrd = (_currentTrack.value.ordinal + 1) % MusicTrack.entries.size
        _currentTrack.value = MusicTrack.entries[nextOrd]
    }

    /**
     * Picks and plays a hilarious sound effect for a launched arrow, returning the
     * [HilariousArrowSound] so the canvas can spawn a matching comic explosion callout!
     */
    fun playHilariousArrowLaunch(comboStreak: Int): HilariousArrowSound {
        // Every 4th combo or when cycling hits index 5, Sandra shouts "Momma's bringin' home the jerky!"
        val chosen = if (comboStreak >= 3 && comboStreak % 3 == 0) {
            HilariousArrowSound.BRING_HOME_THE_JERKY
        } else {
            val all = HilariousArrowSound.entries
            val sound = all[arrowSoundCycleIndex % all.size]
            arrowSoundCycleIndex++
            sound
        }

        if (!sfxEnabled) return chosen

        when (chosen) {
            HilariousArrowSound.BOING -> scope.launch { playCartoonBoing() }
            HilariousArrowSound.CASH_REGISTER -> scope.launch { playCashRegisterCaChing() }
            HilariousArrowSound.HORN -> scope.launch { playClownPartyHorn() }
            HilariousArrowSound.SWOOP -> scope.launch { playSlideWhistleSwoop() }
            HilariousArrowSound.SCREAM -> scope.launch { playComicScream() }
            HilariousArrowSound.BRING_HOME_THE_JERKY -> {
                speakBringHomeTheJerky()
                scope.launch { playJerkyFanfare() }
            }
        }
        return chosen
    }

    /**
     * Explicitly triggers Sandra saying "Momma's bringin' home the jerky!" via TTS + celebratory riff.
     */
    fun speakBringHomeTheJerky() {
        if (!sfxEnabled) return
        runCatching {
            if (isTtsReady) {
                tts?.speak(
                    "Momma's bringin' home the jerky!",
                    TextToSpeech.QUEUE_FLUSH,
                    null,
                    "sandra_jerky_${System.currentTimeMillis()}"
                )
            }
        }
    }

    /**
     * 1. BOING!! — Cartoon spring jaw-harp bounce with rapid pitch wobble.
     */
    private fun playCartoonBoing() {
        runCatching {
            val sampleRate = 22050
            val durationMs = 340
            val numSamples = (sampleRate * durationMs) / 1000
            val samples = ShortArray(numSamples)
            var phase = 0.0

            for (i in 0 until numSamples) {
                val t = i.toDouble() / numSamples.toDouble()
                // Springy upward glide from 190Hz to 580Hz with 28Hz wobble
                val wobble = sin(2.0 * PI * 28.0 * t) * 75.0 * (1.0 - t * 0.5)
                val freq = 190.0 + (390.0 * t) + wobble
                phase += (2.0 * PI * freq) / sampleRate

                val env = exp(-3.2 * t)
                val wave = 0.65 * sin(phase) + 0.25 * sin(phase * 2.0) + 0.10 * sin(phase * 3.0)
                samples[i] = (wave * env * 0.48 * Short.MAX_VALUE).toInt()
                    .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }
            playShortBuffer(samples, sampleRate)
        }
    }

    /**
     * 2. CASH REGISTER ("CA-CHING!!") — Mechanical drawer click-clack + dual high silver bells.
     */
    private fun playCashRegisterCaChing() {
        runCatching {
            val sampleRate = 22050
            val durationMs = 360
            val numSamples = (sampleRate * durationMs) / 1000
            val samples = ShortArray(numSamples)
            var phase1 = 0.0
            var phase2 = 0.0
            val rng = Random(42)

            for (i in 0 until numSamples) {
                val t = i.toDouble() / numSamples.toDouble()
                val sampleVal: Double = if (t < 0.16) {
                    // Drawer "CA-" mechanical ratchet click
                    val noise = (rng.nextDouble() * 2.0 - 1.0) * exp(-18.0 * t)
                    val lowThunk = sin(2.0 * PI * 320.0 * t) * exp(-14.0 * t)
                    (noise * 0.45 + lowThunk * 0.55) * 0.45
                } else {
                    // "-CHING!!" high metallic twin bells (C7 + E7)
                    val bt = (t - 0.16) / 0.84
                    phase1 += (2.0 * PI * 2093.0) / sampleRate
                    phase2 += (2.0 * PI * 2637.0) / sampleRate
                    val bellEnv = exp(-4.0 * bt)
                    (0.55 * sin(phase1) + 0.45 * sin(phase2)) * bellEnv * 0.46
                }
                samples[i] = (sampleVal * Short.MAX_VALUE).toInt()
                    .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }
            playShortBuffer(samples, sampleRate)
        }
    }

    /**
     * 3. CLOWN / PARTY HORN ("HONK-HONK!!") — Brassy double-honk reed sound.
     */
    private fun playClownPartyHorn() {
        runCatching {
            val sampleRate = 22050
            val durationMs = 310
            val numSamples = (sampleRate * durationMs) / 1000
            val samples = ShortArray(numSamples)
            var phase1 = 0.0
            var phase2 = 0.0

            for (i in 0 until numSamples) {
                val t = i.toDouble() / numSamples.toDouble()
                // Two distinct honks: [0..0.42] and [0.50..1.0]
                val inFirstHonk = t < 0.44
                val inSecondHonk = t > 0.50
                if (!inFirstHonk && !inSecondHonk) {
                    samples[i] = 0
                    continue
                }
                val baseF = if (inFirstHonk) 349.23 else 415.30
                phase1 += (2.0 * PI * baseF) / sampleRate
                phase2 += (2.0 * PI * (baseF * 1.26)) / sampleRate

                // Brassy odd/even harmonics
                val reed = 0.50 * sin(phase1) + 0.30 * sin(phase1 * 2.0) + 0.20 * sign(sin(phase2))
                val localT = if (inFirstHonk) t / 0.44 else (t - 0.50) / 0.50
                val env = (1.0 - localT * 0.35)
                samples[i] = (reed * env * 0.38 * Short.MAX_VALUE).toInt()
                    .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }
            playShortBuffer(samples, sampleRate)
        }
    }

    /**
     * 4. SWOOP WHISTLE ("SWOOOSH!!") — Fast upward cartoon slide-whistle swoop.
     */
    private fun playSlideWhistleSwoop() {
        runCatching {
            val sampleRate = 22050
            val durationMs = 280
            val numSamples = (sampleRate * durationMs) / 1000
            val samples = ShortArray(numSamples)
            var phase = 0.0

            for (i in 0 until numSamples) {
                val t = i.toDouble() / numSamples.toDouble()
                // Exponential swoop from 320Hz up to 1680Hz
                val freq = 320.0 * Math.pow(5.25, t)
                phase += (2.0 * PI * freq) / sampleRate
                val env = sin(PI * t)
                val wave = 0.85 * sin(phase) + 0.15 * sin(phase * 2.0)
                samples[i] = (wave * env * 0.44 * Short.MAX_VALUE).toInt()
                    .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }
            playShortBuffer(samples, sampleRate)
        }
    }

    /**
     * 5. COMIC SCREAM ("AAAHHH!!") — Hilarious high-pitched cartoon vocal arc with vibrato.
     */
    private fun playComicScream() {
        runCatching {
            val sampleRate = 22050
            val durationMs = 370
            val numSamples = (sampleRate * durationMs) / 1000
            val samples = ShortArray(numSamples)
            var phase = 0.0

            for (i in 0 until numSamples) {
                val t = i.toDouble() / numSamples.toDouble()
                // Arching vocal scream contour: 680Hz -> 1080Hz -> 490Hz with fast vibrato
                val contour = if (t < 0.35) {
                    680.0 + (400.0 * (t / 0.35))
                } else {
                    1080.0 - (590.0 * ((t - 0.35) / 0.65))
                }
                val vibrato = sin(2.0 * PI * 19.0 * t) * 28.0
                val freq = contour + vibrato
                phase += (2.0 * PI * freq) / sampleRate

                // Formant-like harmonics ("AAAH")
                val vocal = 0.55 * sin(phase) + 0.30 * sin(phase * 2.0) + 0.15 * sin(phase * 3.0)
                val env = sin(PI * t)
                samples[i] = (vocal * env * 0.40 * Short.MAX_VALUE).toInt()
                    .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }
            playShortBuffer(samples, sampleRate)
        }
    }

    /**
     * 6. Jerky Fanfare Riff accompanying Sandra's "Momma's bringin' home the jerky!" voice line.
     */
    private fun playJerkyFanfare() {
        val notes = doubleArrayOf(523.25, 659.25, 783.99, 1046.50)
        for (freq in notes) {
            playPluckBurst(baseFreq = freq, durationMs = 110, volume = 0.34f, slideUpRatio = 1.03)
        }
    }

    fun playBossHit(isCritical: Boolean) {
        if (!sfxEnabled) return
        scope.launch {
            delay(90)
            val freq = if (isCritical) 987.77 else 740.0
            playPluckBurst(
                baseFreq = freq,
                durationMs = 190,
                volume = 0.48f,
                slideUpRatio = 1.12
            )
        }
    }

    fun playBlockedBump() {
        if (!sfxEnabled) return
        scope.launch {
            playPluckBurst(baseFreq = 155.0, durationMs = 95, volume = 0.38f, slideUpRatio = 0.82)
            delay(70)
            playPluckBurst(baseFreq = 120.0, durationMs = 115, volume = 0.36f, slideUpRatio = 0.78)
        }
    }

    fun playPowerUp() {
        if (!sfxEnabled) return
        scope.launch {
            val chord = doubleArrayOf(659.25, 830.61, 987.77, 1318.51)
            for (freq in chord) {
                launch {
                    playPluckBurst(baseFreq = freq, durationMs = 210, volume = 0.30f, slideUpRatio = 1.01)
                }
                delay(55)
            }
        }
    }

    fun playBossTurnAction() {
        if (!sfxEnabled) return
        scope.launch {
            playPluckBurst(baseFreq = 293.66, durationMs = 130, volume = 0.32f, slideUpRatio = 0.96)
            delay(90)
            playPluckBurst(baseFreq = 261.63, durationMs = 160, volume = 0.32f, slideUpRatio = 0.94)
        }
    }

    fun playLevelVictory() {
        if (!sfxEnabled) return
        speakBringHomeTheJerky()
        scope.launch {
            val notes = doubleArrayOf(523.25, 659.25, 783.99, 987.77, 1046.50)
            for (freq in notes) {
                launch {
                    playPluckBurst(baseFreq = freq, durationMs = 280, volume = 0.36f, slideUpRatio = 1.0)
                }
                delay(80)
            }
        }
    }

    /**
     * Periodically switches between 4 distinct calming & playful background songs!
     */
    private fun startMusicLoopIfNeeded() {
        if (musicJob?.isActive == true) return
        musicJob = scope.launch {
            val trackPatterns: Map<MusicTrack, DoubleArray> = mapOf(
                MusicTrack.CROCHET_LOFI to doubleArrayOf(
                    349.23, 440.00, 523.25, 659.25,
                    329.63, 392.00, 493.88, 587.33,
                    293.66, 349.23, 440.00, 523.25,
                    261.63, 329.63, 392.00, 493.88
                ),
                MusicTrack.BURGER_BOUNCE to doubleArrayOf(
                    392.00, 493.88, 587.33, 783.99,
                    329.63, 440.00, 523.25, 659.25,
                    349.23, 440.00, 587.33, 698.46,
                    392.00, 587.33, 493.88, 392.00
                ),
                MusicTrack.BOOKWORM_WALTZ to doubleArrayOf(
                    261.63, 392.00, 659.25,
                    293.66, 440.00, 698.46,
                    329.63, 493.88, 783.99,
                    261.63, 392.00, 523.25
                ),
                MusicTrack.JERKY_POLKA to doubleArrayOf(
                    523.25, 659.25, 587.33, 783.99,
                    659.25, 880.00, 783.99, 523.25,
                    587.33, 698.46, 659.25, 783.99,
                    880.00, 783.99, 659.25, 523.25
                )
            )

            var stepCount = 0
            while (isActive && musicEnabled) {
                // Switch the song automatically every 32 notes (~20 seconds)!
                if (stepCount > 0 && stepCount % 32 == 0) {
                    switchToNextSong()
                }
                val activeTrack = _currentTrack.value
                val notes = trackPatterns[activeTrack] ?: trackPatterns.values.first()
                val freq = notes[stepCount % notes.size]
                val tempoDelay = when (activeTrack) {
                    MusicTrack.CROCHET_LOFI -> 640L
                    MusicTrack.BURGER_BOUNCE -> 460L
                    MusicTrack.BOOKWORM_WALTZ -> 580L
                    MusicTrack.JERKY_POLKA -> 420L
                }
                launch {
                    playSoftAmbientPadNote(freq = freq, durationMs = (tempoDelay + 140).toInt(), volume = 0.11f)
                }
                stepCount++
                delay(tempoDelay)
            }
        }
    }

    fun release() {
        musicJob?.cancel()
        musicJob = null
        runCatching {
            tts?.stop()
            tts?.shutdown()
        }
    }

    private fun playPluckBurst(
        baseFreq: Double,
        durationMs: Int,
        volume: Float,
        slideUpRatio: Double
    ) {
        runCatching {
            val sampleRate = 22050
            val numSamples = (sampleRate * durationMs) / 1000
            val samples = ShortArray(numSamples)
            var phase = 0.0

            for (i in 0 until numSamples) {
                val t = i.toDouble() / numSamples.toDouble()
                val freq = baseFreq * (1.0 + (slideUpRatio - 1.0) * t)
                phase += (2.0 * PI * freq) / sampleRate

                val attack = (i / (sampleRate * 0.004)).coerceAtMost(1.0)
                val decay = exp(-5.2 * t)
                val env = attack * decay

                val wave = 0.78 * sin(phase) + 0.22 * sin(phase * 2.0)
                val sampleVal = (wave * env * volume * Short.MAX_VALUE).toInt()
                    .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
                samples[i] = sampleVal.toShort()
            }

            playShortBuffer(samples, sampleRate)
        }
    }

    private fun playSoftAmbientPadNote(
        freq: Double,
        durationMs: Int,
        volume: Float
    ) {
        runCatching {
            val sampleRate = 22050
            val numSamples = (sampleRate * durationMs) / 1000
            val samples = ShortArray(numSamples)
            var phase = 0.0

            for (i in 0 until numSamples) {
                val t = i.toDouble() / numSamples.toDouble()
                phase += (2.0 * PI * freq) / sampleRate
                val attack = (t / 0.15).coerceAtMost(1.0)
                val decay = exp(-2.8 * t)
                val env = attack * decay
                val wave = 0.85 * sin(phase) + 0.15 * sin(phase * 3.0)
                val sampleVal = (wave * env * volume * Short.MAX_VALUE).toInt()
                    .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
                samples[i] = sampleVal.toShort()
            }

            playShortBuffer(samples, sampleRate)
        }
    }

    private fun playShortBuffer(samples: ShortArray, sampleRate: Int) {
        val byteCount = samples.size * 2
        val track = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(byteCount.coerceAtLeast(1024))
            .setTransferMode(AudioTrack.MODE_STATIC)
            .build()

        track.write(samples, 0, samples.size)
        track.play()
        Thread.sleep(((samples.size * 1000L) / sampleRate) + 30L)
        track.stop()
        track.release()
    }
}
