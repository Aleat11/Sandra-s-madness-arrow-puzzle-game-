"use client";

import React from "react";
import { BOSS_TYPES, BossTypeId } from "@/types/game";

interface BossArenaCardProps {
  bossType: BossTypeId;
  currentHp: number;
  maxHp: number;
  movesLeftInTurn: number;
  lastDamage: number | null;
  statusBanner: string | null;
}

export const BossArenaCard: React.FC<BossArenaCardProps> = ({
  bossType,
  currentHp,
  maxHp,
  movesLeftInTurn,
  lastDamage,
  statusBanner
}) => {
  const boss = BOSS_TYPES[bossType];
  const hpPercent = Math.max(0, Math.min(100, Math.round((currentHp / Math.max(1, maxHp)) * 100)));

  return (
    <div
      className="w-full rounded-2xl border-2 p-3.5 shadow-md transition-all"
      style={{
        borderColor: boss.badgeColor,
        backgroundColor: `${boss.badgeColor}14`
      }}
    >
      <div className="flex items-center justify-between gap-3 flex-wrap">
        <div className="flex items-center gap-3">
          <div
            className="w-12 h-12 rounded-2xl flex items-center justify-center text-2xl shadow-sm"
            style={{ backgroundColor: boss.badgeColor, color: "#fff" }}
          >
            {bossType === "BROKEN_AC" ? "❄️" : bossType === "MESSY_HOUSE" ? "🏠" : bossType === "LAUNDRY_YARN_TORNADO" ? "🌪️" : "🔥"}
          </div>
          <div>
            <div className="flex items-center gap-2">
              <span className="font-extrabold text-base" style={{ color: boss.badgeColor }}>
                {boss.bossName}
              </span>
              {lastDamage !== null && (
                <span className="px-2 py-0.5 text-xs font-bold rounded-full bg-red-600 text-white animate-bounce">
                  -{lastDamage} HP!
                </span>
              )}
            </div>
            <p className="text-xs opacity-80">{boss.subtitle}</p>
          </div>
        </div>

        <div className="text-right">
          <div className="text-xs font-bold uppercase tracking-wider opacity-75">
            Next Boss Attack In: {movesLeftInTurn} Move{movesLeftInTurn === 1 ? "" : "s"}
          </div>
          <div className="text-sm font-extrabold">
            {currentHp} / {maxHp} HP ({hpPercent}%)
          </div>
        </div>
      </div>

      <div className="w-full h-3.5 bg-black/10 rounded-full overflow-hidden mt-2.5 border border-black/10">
        <div
          className="h-full transition-all duration-300 rounded-full"
          style={{
            width: `${hpPercent}%`,
            background: `linear-gradient(90deg, ${boss.badgeColor}, #EF4444)`
          }}
        />
      </div>

      {statusBanner && (
        <p className="mt-2 text-xs font-semibold text-amber-950 dark:text-amber-200">
          ⚡ {statusBanner}
        </p>
      )}
    </div>
  );
};
