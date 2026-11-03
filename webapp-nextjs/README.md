# Sandra's Madness featuring Naty & Ally (Next.js + React + TypeScript + Tailwind)

A progressive, strategic browser-based arrow puzzle game inspired by Easybrain's *Arrows - Puzzle Game*, blending a **crochet aesthetic** with **hamburger motifs**, **reading & baking accents**, **turn-based boss battles**, and **hilarious sound effects**.

## Included in This Project
- **`package.json`**, **`tsconfig.json`**, **`tailwind.config.ts`**, **`postcss.config.mjs`**, **`next.config.mjs`**
- **`public/assets/sandra-photo.jpg`** — Sandra's real photo avatar
- **`public/sounds/*.wav`** — Pre-synthesized hilarious sound effects (`boing.wav`, `caching.wav`, `horn.wav`, `swoop.wav`, `scream.wav`, `jerky_fanfare.wav`, `blocked.wav`, `victory.wav`) + Web Audio API & SpeechSynthesis (`"Bring home the jerky!"`)
- **`src/types/game.ts`** — Complete TypeScript data models for narrow arrows, pin styles, board backgrounds, power-ups, bosses, and collectibles
- **`src/lib/levelCatalog.ts`** — 20 progressive Story Levels (`5×5` up to `10×10`), 4 Turn-Based Boss Battles (*The Cluttered Messy House*, *Broken Air Conditioner 89°F*, *Messy Closet & Yarn Tornado*, *Mega Chore Duo 10×10*), Endless Madness, and the **Single-Keyhole Strategic Interlocking Arrow Generator**
- **`src/lib/soundManager.ts`** — Audio controller with 6 hilarious arrow sounds, 4-track auto-switching background music, and Sandra's *"Bring home the jerky!"* voice callout
- **`src/components/ArrowBoardCanvas.tsx`** — Interactive HTML5 Canvas rendering narrow crochet arrows, pin styles, radial launch explosions, 12-ray combo starbursts, and afterburner flight trails
- **`src/components/BossArenaCard.tsx`** — Turn-Based Boss Arena header with HP bar, turn counter, and counter-heal alerts
- **`src/app/page.tsx`** — Complete 4-tab application with `localStorage` progression persistence

## How to Run Locally or in a Web Environment

1. Install dependencies:
   ```bash
   npm install
   ```
2. Start the development server:
   ```bash
   npm run dev
   ```
3. Open [http://localhost:3000](http://localhost:3000) in your browser.

## Building for Production
```bash
npm run build
npm run start
```
