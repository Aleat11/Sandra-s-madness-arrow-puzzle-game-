export interface HilariousSoundMeta {
  id: string;
  comicText: string;
  badgeColor: string;
  wavFile: string;
}

export const HILARIOUS_SOUNDS: HilariousSoundMeta[] = [
  {
    id: "BOING",
    comicText: "🌀 BOING-OING-OING!",
    badgeColor: "#8B5CF6",
    wavFile: "/sounds/boing.wav"
  },
  {
    id: "CASH_REGISTER",
    comicText: "💰 CA-CHING!!",
    badgeColor: "#16A34A",
    wavFile: "/sounds/caching.wav"
  },
  {
    id: "PARTY_HORN",
    comicText: "📯 HONK-HONK!!",
    badgeColor: "#F59E0B",
    wavFile: "/sounds/horn.wav"
  },
  {
    id: "SLIDE_WHISTLE",
    comicText: "🎢 SWOOOSH!!",
    badgeColor: "#0EA5E9",
    wavFile: "/sounds/swoop.wav"
  },
  {
    id: "COMIC_SCREAM",
    comicText: "😱 WILHELM AAAH!!",
    badgeColor: "#EC4899",
    wavFile: "/sounds/scream.wav"
  },
  {
    id: "MOMMAS_BRINGIN_HOME_THE_JERKY",
    comicText: "🥩 MOMMA'S BRINGIN' HOME THE JERKY!!",
    badgeColor: "#E53935",
    wavFile: "/sounds/jerky_fanfare.wav"
  }
];

export interface MusicTrackMeta {
  id: string;
  title: string;
  vibe: string;
  notes: number[];
}

export const MUSIC_TRACKS: MusicTrackMeta[] = [
  {
    id: "CROCHET_LOFI",
    title: "Cozy Crochet Cafe Lofi",
    vibe: "Warm Acoustic Plucks",
    notes: [261.63, 329.63, 392.00, 493.88, 440.00, 392.00, 329.63, 293.66]
  },
  {
    id: "BURGER_BOUNCE",
    title: "Sizzling Burger Bounce",
    vibe: "Upbeat Diner Groove",
    notes: [293.66, 369.99, 440.00, 587.33, 493.88, 440.00, 369.99, 329.63]
  },
  {
    id: "BOOKWORM_WALTZ",
    title: "Naty's Bookworm Waltz",
    vibe: "Dreamy Reading Nook",
    notes: [220.00, 261.63, 329.63, 440.00, 392.00, 329.63, 261.63, 246.94]
  },
  {
    id: "JERKY_POLKA",
    title: "Sandra's Jerky Polka",
    vibe: "Playful Bakery Jam",
    notes: [349.23, 440.00, 523.25, 698.46, 587.33, 523.25, 440.00, 392.00]
  }
];

class SoundEffectsController {
  private audioCtx: AudioContext | null = null;
  private sfxEnabled = true;
  private musicEnabled = true;
  private cycleIndex = 0;
  private currentTrackIndex = 0;
  private noteIndex = 0;
  private musicTimer: ReturnType<typeof setInterval> | null = null;
  private onTrackChange?: (track: MusicTrackMeta) => void;

  private ensureContext(): AudioContext | null {
    if (typeof window === "undefined") return null;
    if (!this.audioCtx) {
      const AudioCtx =
        window.AudioContext ||
        (window as unknown as { webkitAudioContext: typeof AudioContext }).webkitAudioContext;
      if (AudioCtx) this.audioCtx = new AudioCtx();
    }
    if (this.audioCtx && this.audioCtx.state === "suspended") {
      this.audioCtx.resume().catch(() => {});
    }
    return this.audioCtx;
  }

  public initMusicLoop(onTrackChange: (track: MusicTrackMeta) => void) {
    this.onTrackChange = onTrackChange;
    if (this.musicTimer) clearInterval(this.musicTimer);
    this.musicTimer = setInterval(() => {
      if (!this.musicEnabled) return;
      const ctx = this.audioCtx;
      if (!ctx || ctx.state !== "running") return;

      const track = MUSIC_TRACKS[this.currentTrackIndex];
      const freq = track.notes[this.noteIndex % track.notes.length];
      this.noteIndex++;

      // Periodically auto-switch song every 28 notes
      if (this.noteIndex > 0 && this.noteIndex % 28 === 0) {
        this.switchToNextSong();
      }

      this.playSynthTone(freq, freq * 1.004, 0.32, "sine", 0.06);
    }, 620);
  }

  public setSfxEnabled(enabled: boolean) {
    this.sfxEnabled = enabled;
  }

  public setMusicEnabled(enabled: boolean) {
    this.musicEnabled = enabled;
    if (enabled) this.ensureContext();
  }

  public getCurrentTrack(): MusicTrackMeta {
    return MUSIC_TRACKS[this.currentTrackIndex];
  }

  public switchToNextSong(): MusicTrackMeta {
    this.ensureContext();
    this.currentTrackIndex = (this.currentTrackIndex + 1) % MUSIC_TRACKS.length;
    this.noteIndex = 0;
    const next = MUSIC_TRACKS[this.currentTrackIndex];
    if (this.onTrackChange) this.onTrackChange(next);
    return next;
  }

  public playHilariousLaunch(combo: number): HilariousSoundMeta {
    this.ensureContext();
    let chosen: HilariousSoundMeta;
    if (combo > 0 && combo % 4 === 0) {
      chosen = HILARIOUS_SOUNDS[5]; // MOMMAS_BRINGIN_HOME_THE_JERKY
    } else {
      chosen = HILARIOUS_SOUNDS[this.cycleIndex % HILARIOUS_SOUNDS.length];
      this.cycleIndex++;
    }

    if (!this.sfxEnabled) return chosen;

    // Play crisp synthesized bowstring arrow twang
    this.playArrowLaunchTwang();

    // Play bundled .wav asset if available
    if (typeof Audio !== "undefined") {
      const audio = new Audio(chosen.wavFile);
      audio.volume = 0.55;
      audio.play().catch(() => {});
    }

    if (chosen.id === "MOMMAS_BRINGIN_HOME_THE_JERKY") {
      this.speakMommasBringinHomeTheJerky();
    }
    return chosen;
  }

  /**
   * Bowstring twang + wind whoosh for crisp arrow launches
   */
  public playArrowLaunchTwang() {
    if (!this.sfxEnabled) return;
    const ctx = this.ensureContext();
    if (!ctx) return;

    try {
      // Snappy bow twang (pitch glide downwards)
      const osc = ctx.createOscillator();
      const gain = ctx.createGain();
      osc.type = "triangle";
      osc.frequency.setValueAtTime(680, ctx.currentTime);
      osc.frequency.exponentialRampToValueAtTime(140, ctx.currentTime + 0.18);
      gain.gain.setValueAtTime(0.18, ctx.currentTime);
      gain.gain.exponentialRampToValueAtTime(0.001, ctx.currentTime + 0.18);
      osc.connect(gain);
      gain.connect(ctx.destination);
      osc.start();
      osc.stop(ctx.currentTime + 0.18);

      // Whimsical harmonic ring
      const osc2 = ctx.createOscillator();
      const gain2 = ctx.createGain();
      osc2.type = "sine";
      osc2.frequency.setValueAtTime(1040, ctx.currentTime);
      osc2.frequency.exponentialRampToValueAtTime(520, ctx.currentTime + 0.12);
      gain2.gain.setValueAtTime(0.09, ctx.currentTime);
      gain2.gain.exponentialRampToValueAtTime(0.001, ctx.currentTime + 0.12);
      osc2.connect(gain2);
      gain2.connect(ctx.destination);
      osc2.start();
      osc2.stop(ctx.currentTime + 0.12);
    } catch {}
  }

  public speakMommasBringinHomeTheJerky(): HilariousSoundMeta {
    this.ensureContext();
    const meta = HILARIOUS_SOUNDS[5];
    if (!this.sfxEnabled) return meta;

    if (typeof window !== "undefined" && "speechSynthesis" in window) {
      try {
        window.speechSynthesis.cancel();
        const utter = new SpeechSynthesisUtterance("Momma's bringin' home the jerky!");
        utter.pitch = 1.25;
        utter.rate = 1.12;
        window.speechSynthesis.speak(utter);
      } catch {}
    }

    // Celebratory brass fanfare triad (C5 - E5 - G5 - C6)
    this.playFanfareArpeggio();
    return meta;
  }

  // Alias for backward compatibility
  public speakBringHomeTheJerky(): HilariousSoundMeta {
    return this.speakMommasBringinHomeTheJerky();
  }

  public playArcherQuipTone() {
    if (!this.sfxEnabled) return;
    const ctx = this.ensureContext();
    if (!ctx) return;

    try {
      // Whimsical Robin Hood style double pluck
      const notes = [440, 554.37, 659.25];
      notes.forEach((freq, idx) => {
        const osc = ctx.createOscillator();
        const gain = ctx.createGain();
        osc.type = "triangle";
        const start = ctx.currentTime + idx * 0.08;
        osc.frequency.setValueAtTime(freq, start);
        gain.gain.setValueAtTime(0.14, start);
        gain.gain.exponentialRampToValueAtTime(0.001, start + 0.15);
        osc.connect(gain);
        gain.connect(ctx.destination);
        osc.start(start);
        osc.stop(start + 0.16);
      });
    } catch {}
  }

  public playBlockedBump() {
    if (!this.sfxEnabled) return;
    const ctx = this.ensureContext();
    if (!ctx) return;

    if (typeof Audio !== "undefined") {
      const audio = new Audio("/sounds/blocked.wav");
      audio.volume = 0.55;
      audio.play().catch(() => {});
    }

    // Wooden deflection impact: sharp dual click
    try {
      const osc = ctx.createOscillator();
      const gain = ctx.createGain();
      osc.type = "sawtooth";
      osc.frequency.setValueAtTime(220, ctx.currentTime);
      osc.frequency.exponentialRampToValueAtTime(70, ctx.currentTime + 0.15);
      gain.gain.setValueAtTime(0.22, ctx.currentTime);
      gain.gain.exponentialRampToValueAtTime(0.001, ctx.currentTime + 0.15);
      osc.connect(gain);
      gain.connect(ctx.destination);
      osc.start();
      osc.stop(ctx.currentTime + 0.15);
    } catch {}
  }

  public playVictory() {
    if (!this.sfxEnabled) return;
    this.ensureContext();
    if (typeof Audio !== "undefined") {
      const audio = new Audio("/sounds/victory.wav");
      audio.volume = 0.6;
      audio.play().catch(() => {});
    }
    this.playVictoryFanfare();
    this.speakMommasBringinHomeTheJerky();
  }

  private playFanfareArpeggio() {
    const ctx = this.audioCtx;
    if (!ctx) return;
    const notes = [523.25, 659.25, 783.99, 1046.5]; // C5, E5, G5, C6
    notes.forEach((freq, idx) => {
      try {
        const osc = ctx.createOscillator();
        const gain = ctx.createGain();
        osc.type = "triangle";
        const start = ctx.currentTime + idx * 0.07;
        osc.frequency.setValueAtTime(freq, start);
        gain.gain.setValueAtTime(0.18, start);
        gain.gain.exponentialRampToValueAtTime(0.001, start + 0.28);
        osc.connect(gain);
        gain.connect(ctx.destination);
        osc.start(start);
        osc.stop(start + 0.29);
      } catch {}
    });
  }

  private playVictoryFanfare() {
    const ctx = this.audioCtx;
    if (!ctx) return;
    const chords = [
      [392.0, 493.88, 587.33], // G major
      [440.0, 554.37, 659.25], // A major
      [523.25, 659.25, 783.99, 1046.5] // C major triumphant
    ];
    chords.forEach((chord, stepIdx) => {
      chord.forEach(freq => {
        try {
          const osc = ctx.createOscillator();
          const gain = ctx.createGain();
          osc.type = "sine";
          const start = ctx.currentTime + stepIdx * 0.14;
          osc.frequency.setValueAtTime(freq, start);
          gain.gain.setValueAtTime(0.12, start);
          gain.gain.exponentialRampToValueAtTime(0.001, start + 0.35);
          osc.connect(gain);
          gain.connect(ctx.destination);
          osc.start(start);
          osc.stop(start + 0.36);
        } catch {}
      });
    });
  }

  private playSynthTone(
    startFreq: number,
    endFreq: number,
    durationSec: number,
    type: OscillatorType,
    gainLevel: number
  ) {
    const ctx = this.audioCtx;
    if (!ctx) return;
    try {
      const osc = ctx.createOscillator();
      const gain = ctx.createGain();
      osc.type = type;
      osc.frequency.setValueAtTime(startFreq, ctx.currentTime);
      osc.frequency.exponentialRampToValueAtTime(Math.max(40, endFreq), ctx.currentTime + durationSec);
      gain.gain.setValueAtTime(gainLevel, ctx.currentTime);
      gain.gain.exponentialRampToValueAtTime(0.001, ctx.currentTime + durationSec);
      osc.connect(gain);
      gain.connect(ctx.destination);
      osc.start();
      osc.stop(ctx.currentTime + durationSec);
    } catch {}
  }
}

export const soundController = new SoundEffectsController();
