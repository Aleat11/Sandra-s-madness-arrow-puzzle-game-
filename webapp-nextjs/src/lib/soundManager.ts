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
    id: "BRING_HOME_THE_JERKY",
    comicText: "🥩 BRING HOME THE JERKY!!",
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
      const AudioCtx = window.AudioContext || (window as unknown as { webkitAudioContext: typeof AudioContext }).webkitAudioContext;
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

      // Periodically auto-switch song every 28 notes!
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
      chosen = HILARIOUS_SOUNDS[5]; // BRING_HOME_THE_JERKY
    } else {
      chosen = HILARIOUS_SOUNDS[this.cycleIndex % HILARIOUS_SOUNDS.length];
      this.cycleIndex++;
    }

    if (!this.sfxEnabled) return chosen;

    // Play bundled .wav asset and/or Web Audio synthesis
    if (typeof Audio !== "undefined") {
      const audio = new Audio(chosen.wavFile);
      audio.volume = 0.55;
      audio.play().catch(() => {});
    }

    if (chosen.id === "BRING_HOME_THE_JERKY") {
      this.speakBringHomeTheJerky();
    }
    return chosen;
  }

  public speakBringHomeTheJerky(): HilariousSoundMeta {
    this.ensureContext();
    const meta = HILARIOUS_SOUNDS[5];
    if (!this.sfxEnabled) return meta;

    if (typeof window !== "undefined" && "speechSynthesis" in window) {
      window.speechSynthesis.cancel();
      const utter = new SpeechSynthesisUtterance("Bring home the jerky!");
      utter.pitch = 1.18;
      utter.rate = 1.08;
      window.speechSynthesis.speak(utter);
    }
    this.playSynthTone(523.25, 783.99, 0.24, "triangle", 0.18);
    return meta;
  }

  public playBlockedBump() {
    if (!this.sfxEnabled) return;
    this.ensureContext();
    if (typeof Audio !== "undefined") {
      const audio = new Audio("/sounds/blocked.wav");
      audio.volume = 0.55;
      audio.play().catch(() => {});
    }
    this.playSynthTone(165, 92, 0.22, "sawtooth", 0.2);
  }

  public playVictory() {
    if (!this.sfxEnabled) return;
    this.ensureContext();
    if (typeof Audio !== "undefined") {
      const audio = new Audio("/sounds/victory.wav");
      audio.volume = 0.6;
      audio.play().catch(() => {});
    }
    this.speakBringHomeTheJerky();
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
  }
}

export const soundController = new SoundEffectsController();
