import type { Metadata } from "next";
import "./globals.css";

export const metadata: Metadata = {
  title: "Sandra's Madness: Naty & Ally | Crochet & Hamburger Arrow Puzzle",
  description:
    "Progressive strategic crochet & hamburger arrow puzzle game featuring Sandra, Naty, and Ally, up to 10x10 grids, turn-based boss fights, and hilarious sound effects."
};

export default function RootLayout({
  children
}: {
  children: React.ReactNode;
}) {
  return (
    <html lang="en">
      <body className="antialiased selection:bg-amber-300 selection:text-amber-950">
        {children}
      </body>
    </html>
  );
}
