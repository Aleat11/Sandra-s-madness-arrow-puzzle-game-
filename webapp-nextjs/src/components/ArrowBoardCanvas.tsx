"use client";

import React, { useEffect, useRef } from "react";
import {
  ArrowPinStyleId,
  BOARD_BG_STYLES,
  BlockedArrowAnim,
  BoardBackgroundStyleId,
  DIRECTIONS,
  ExitingArrowAnim,
  GridCell,
  LaunchExplosionAnim,
  PuzzleArrow,
  cellKey
} from "@/types/game";

interface ArrowBoardCanvasProps {
  gridCols: number;
  gridRows: number;
  arrows: PuzzleArrow[];
  exitingArrows: ExitingArrowAnim[];
  launchExplosions: LaunchExplosionAnim[];
  clearedCells: Set<string>;
  bossHazardCells: Set<string>;
  hintedArrowId: number | null;
  xrayArrowIds: Set<number>;
  isSpatulaModeActive: boolean;
  blockedAnim: BlockedArrowAnim | null;
  showRayGuide: boolean;
  showYarnStitchRibs: boolean;
  arrowPinStyle: ArrowPinStyleId;
  boardBgStyle: BoardBackgroundStyleId;
  isDarkTheme: boolean;
  onCellTapped: (cell: GridCell) => void;
}

export const ArrowBoardCanvas: React.FC<ArrowBoardCanvasProps> = ({
  gridCols,
  gridRows,
  arrows,
  exitingArrows,
  launchExplosions,
  clearedCells,
  bossHazardCells,
  hintedArrowId,
  xrayArrowIds,
  isSpatulaModeActive,
  blockedAnim,
  showRayGuide,
  showYarnStitchRibs,
  arrowPinStyle,
  boardBgStyle,
  isDarkTheme,
  onCellTapped
}) => {
  const canvasRef = useRef<HTMLCanvasElement | null>(null);

  useEffect(() => {
    let animFrameId: number;
    const canvas = canvasRef.current;
    if (!canvas) return;
    const ctx = canvas.getContext("2d");
    if (!ctx) return;

    const render = () => {
      const now = performance.now();
      const width = canvas.width;
      const height = canvas.height;
      ctx.clearRect(0, 0, width, height);

      const bgInfo = BOARD_BG_STYLES.find(b => b.id === boardBgStyle) || BOARD_BG_STYLES[0];
      const bgFill = isDarkTheme ? bgInfo.darkBg : bgInfo.lightBg;
      const borderStroke = isDarkTheme ? bgInfo.borderDark : bgInfo.borderLight;

      // Board Mat Background
      ctx.fillStyle = bgFill;
      ctx.strokeStyle = isSpatulaModeActive ? "#F59E0B" : borderStroke;
      ctx.lineWidth = isSpatulaModeActive ? 6 : 3;
      ctx.beginPath();
      ctx.roundRect(6, 6, width - 12, height - 12, 26);
      ctx.fill();
      ctx.stroke();

      const pad = 34;
      const boardW = width - pad * 2;
      const boardH = height - pad * 2;
      const cellSize = Math.min(boardW / gridCols, boardH / gridRows);
      const originX = (width - cellSize * gridCols) / 2;
      const originY = (height - cellSize * gridRows) / 2;

      // 1. Draw Waffle-Stitch Crochet Grid Cells & Cleared Burger Pattern
      for (let r = 0; r < gridRows; r++) {
        for (let c = 0; c < gridCols; c++) {
          const cx = originX + c * cellSize;
          const cy = originY + r * cellSize;
          const k = `${c},${r}`;
          const isCleared = clearedCells.has(k);
          const isHazard = bossHazardCells.has(k);

          ctx.fillStyle = isCleared
            ? isDarkTheme
              ? "rgba(245, 158, 11, 0.16)"
              : "rgba(254, 243, 199, 0.85)"
            : (r + c) % 2 === 0
            ? isDarkTheme
              ? "rgba(255,255,255,0.04)"
              : "rgba(217, 155, 82, 0.10)"
            : isDarkTheme
            ? "rgba(255,255,255,0.07)"
            : "rgba(217, 155, 82, 0.16)";

          ctx.beginPath();
          ctx.roundRect(cx + 3, cy + 3, cellSize - 6, cellSize - 6, 9);
          ctx.fill();

          if (isHazard) {
            ctx.strokeStyle = "#EF4444";
            ctx.lineWidth = 2.5;
            ctx.strokeRect(cx + 5, cy + 5, cellSize - 10, cellSize - 10);
          }
        }
      }

      // 2. Optional Apprentice Ray Guide (Levels 1-3)
      if (showRayGuide) {
        const occupied = new Set<string>();
        arrows.forEach(a => a.cells.forEach(cell => occupied.add(cellKey(cell))));
        arrows.forEach(arrow => {
          const head = arrow.cells[arrow.cells.length - 1];
          const d = DIRECTIONS[arrow.headDirection];
          let cc = head.col + d.dx;
          let rr = head.row + d.dy;
          let blocked = false;
          while (cc >= 0 && cc < gridCols && rr >= 0 && rr < gridRows) {
            if (occupied.has(`${cc},${rr}`)) {
              blocked = true;
              break;
            }
            cc += d.dx;
            rr += d.dy;
          }
          const hx = originX + (head.col + 0.5) * cellSize;
          const hy = originY + (head.row + 0.5) * cellSize;
          const ex = originX + (cc + 0.5) * cellSize;
          const ey = originY + (rr + 0.5) * cellSize;
          ctx.save();
          ctx.setLineDash([5, 5]);
          ctx.strokeStyle = blocked ? "rgba(239, 68, 68, 0.35)" : "rgba(16, 185, 129, 0.55)";
          ctx.lineWidth = 2;
          ctx.beginPath();
          ctx.moveTo(hx, hy);
          ctx.lineTo(ex, ey);
          ctx.stroke();
          ctx.restore();
        });
      }

      // 3. Draw Spot Launch Explosions & Combo Starbursts
      launchExplosions.forEach(exp => {
        const elapsed = Date.now() - exp.startTimeMs;
        const progress = Math.min(1, Math.max(0, elapsed / 650));
        const inv = 1 - progress;

        exp.cells.forEach((cell, idx) => {
          const cx = originX + (cell.col + 0.5) * cellSize;
          const cy = originY + (cell.row + 0.5) * cellSize;

          // Expanding shockwave ring
          ctx.strokeStyle = exp.primaryColor;
          ctx.globalAlpha = inv * 0.85;
          ctx.lineWidth = Math.max(1.5, 5 * inv);
          ctx.beginPath();
          ctx.arc(cx, cy, cellSize * (0.2 + progress * 0.9), 0, Math.PI * 2);
          ctx.stroke();

          // 8 Radial Confetti Sparks
          for (let s = 0; s < 8; s++) {
            const angle = (s * Math.PI) / 4 + idx * 0.25;
            const dist = cellSize * (0.15 + progress * 0.95);
            const sx = cx + Math.cos(angle) * dist;
            const sy = cy + Math.sin(angle) * dist;
            ctx.fillStyle = s % 2 === 0 ? "#FBBF24" : exp.highlightColor;
            ctx.beginPath();
            ctx.arc(sx, sy, Math.max(1.5, 4.5 * inv), 0, Math.PI * 2);
            ctx.fill();
          }
        });

        // 12-Ray Combo Starburst at head cell
        if (exp.comboCount >= 2) {
          const hx = originX + (exp.headCell.col + 0.5) * cellSize;
          const hy = originY + (exp.headCell.row + 0.5) * cellSize;
          ctx.strokeStyle = "#F59E0B";
          ctx.lineWidth = 2.5 * inv;
          for (let ray = 0; ray < 12; ray++) {
            const ang = (ray * Math.PI) / 6 + progress * 0.6;
            const r1 = cellSize * 0.2 * progress;
            const r2 = cellSize * 1.35 * progress;
            ctx.beginPath();
            ctx.moveTo(hx + Math.cos(ang) * r1, hy + Math.sin(ang) * r1);
            ctx.lineTo(hx + Math.cos(ang) * r2, hy + Math.sin(ang) * r2);
            ctx.stroke();
          }
        }
        ctx.globalAlpha = 1;
      });

      // Helper to draw a narrow arrow
      const drawNarrowArrow = (arrow: PuzzleArrow, slideDistancePx: number, isExiting: boolean) => {
        const d = DIRECTIONS[arrow.headDirection];
        let bumpOffsetX = 0;
        let bumpOffsetY = 0;
        if (blockedAnim && blockedAnim.arrowId === arrow.id) {
          const shake = Math.sin((now / 28) * Math.PI) * 5;
          bumpOffsetX = d.dx * shake;
          bumpOffsetY = d.dy * shake;
        }

        const points = arrow.cells.map(c => ({
          x: originX + (c.col + 0.5) * cellSize + d.dx * slideDistancePx + bumpOffsetX,
          y: originY + (c.row + 0.5) * cellSize + d.dy * slideDistancePx + bumpOffsetY
        }));

        const isHinted = arrow.id === hintedArrowId || xrayArrowIds.has(arrow.id);
        const primary = isHinted ? "#10B981" : arrow.yarnColor.primaryColor;
        const highlight = isHinted ? "#A7F3D0" : arrow.yarnColor.highlightColor;
        const shadow = arrow.yarnColor.deepShadowColor;

        const shaftWidth = Math.max(7, cellSize * 0.21);

        // Afterburner trail for exiting arrows
        if (isExiting && points.length > 0) {
          const tailPt = points[0];
          ctx.save();
          ctx.strokeStyle = "#FBBF24";
          ctx.globalAlpha = 0.65;
          ctx.lineWidth = shaftWidth * 0.85;
          ctx.lineCap = "round";
          ctx.beginPath();
          ctx.moveTo(tailPt.x, tailPt.y);
          ctx.lineTo(tailPt.x - d.dx * cellSize * 1.4, tailPt.y - d.dy * cellSize * 1.4);
          ctx.stroke();
          ctx.restore();
        }

        // Deep shadow casing
        ctx.strokeStyle = shadow;
        ctx.lineWidth = shaftWidth + 4;
        ctx.lineCap = "round";
        ctx.lineJoin = "round";
        ctx.beginPath();
        points.forEach((pt, idx) => (idx === 0 ? ctx.moveTo(pt.x, pt.y) : ctx.lineTo(pt.x, pt.y)));
        ctx.stroke();

        // Main narrow yarn shaft
        ctx.strokeStyle = primary;
        ctx.lineWidth = shaftWidth;
        ctx.beginPath();
        points.forEach((pt, idx) => (idx === 0 ? ctx.moveTo(pt.x, pt.y) : ctx.lineTo(pt.x, pt.y)));
        ctx.stroke();

        // Center yarn highlight rib
        if (showYarnStitchRibs) {
          ctx.save();
          ctx.strokeStyle = highlight;
          ctx.lineWidth = Math.max(2, shaftWidth * 0.28);
          ctx.setLineDash([4, 5]);
          ctx.beginPath();
          points.forEach((pt, idx) => (idx === 0 ? ctx.moveTo(pt.x, pt.y) : ctx.lineTo(pt.x, pt.y)));
          ctx.stroke();
          ctx.restore();
        }

        // Tail Pin Ornament for Crochet Hook / Burger Skewer styles
        const tail = points[0];
        if (arrowPinStyle === "CROCHET_HOOK_PIN") {
          ctx.fillStyle = "#FBBF24";
          ctx.beginPath();
          ctx.arc(tail.x, tail.y, shaftWidth * 0.58, 0, Math.PI * 2);
          ctx.fill();
        } else if (arrowPinStyle === "BURGER_SKEWER_PIN") {
          ctx.fillStyle = "#D97706";
          ctx.beginPath();
          ctx.arc(tail.x, tail.y, shaftWidth * 0.68, 0, Math.PI * 2);
          ctx.fill();
        }

        // Narrow Minimalist Arrowhead Chevron
        const head = points[points.length - 1];
        const wing = cellSize * 0.23;
        const tipX = head.x + d.dx * (cellSize * 0.24);
        const tipY = head.y + d.dy * (cellSize * 0.24);
        const baseCenterX = head.x - d.dx * (cellSize * 0.05);
        const baseCenterY = head.y - d.dy * (cellSize * 0.05);
        const perpX = -d.dy * wing;
        const perpY = d.dx * wing;

        ctx.fillStyle = shadow;
        ctx.beginPath();
        ctx.moveTo(tipX + d.dx * 2, tipY + d.dy * 2);
        ctx.lineTo(baseCenterX + perpX * 1.12, baseCenterY + perpY * 1.12);
        ctx.lineTo(baseCenterX - perpX * 1.12, baseCenterY - perpY * 1.12);
        ctx.closePath();
        ctx.fill();

        ctx.fillStyle = primary;
        ctx.beginPath();
        ctx.moveTo(tipX, tipY);
        ctx.lineTo(baseCenterX + perpX, baseCenterY + perpY);
        ctx.lineTo(baseCenterX - perpX, baseCenterY - perpY);
        ctx.closePath();
        ctx.fill();
      };

      // 4. Draw Active Arrows
      arrows.forEach(a => drawNarrowArrow(a, 0, false));

      // 5. Draw Exiting Arrows
      exitingArrows.forEach(ex => {
        const elapsed = Date.now() - ex.startTimeMs;
        const dist = (elapsed / 420) * (Math.max(width, height) * 0.95);
        drawNarrowArrow(ex.arrow, dist, true);
      });

      // 6. Draw Blocked Cell X Indicator if bumped
      if (blockedAnim) {
        const bx = originX + (blockedAnim.blockingCell.col + 0.5) * cellSize;
        const by = originY + (blockedAnim.blockingCell.row + 0.5) * cellSize;
        ctx.strokeStyle = "#EF4444";
        ctx.lineWidth = 4;
        const r = cellSize * 0.24;
        ctx.beginPath();
        ctx.moveTo(bx - r, by - r);
        ctx.lineTo(bx + r, by + r);
        ctx.moveTo(bx + r, by - r);
        ctx.lineTo(bx - r, by + r);
        ctx.stroke();
      }

      animFrameId = requestAnimationFrame(render);
    };

    animFrameId = requestAnimationFrame(render);
    return () => cancelAnimationFrame(animFrameId);
  }, [
    gridCols,
    gridRows,
    arrows,
    exitingArrows,
    launchExplosions,
    clearedCells,
    bossHazardCells,
    hintedArrowId,
    xrayArrowIds,
    isSpatulaModeActive,
    blockedAnim,
    showRayGuide,
    showYarnStitchRibs,
    arrowPinStyle,
    boardBgStyle,
    isDarkTheme
  ]);

  const handleCanvasClick = (e: React.MouseEvent<HTMLCanvasElement>) => {
    const canvas = canvasRef.current;
    if (!canvas) return;
    const rect = canvas.getBoundingClientRect();
    const clickX = ((e.clientX - rect.left) / rect.width) * canvas.width;
    const clickY = ((e.clientY - rect.top) / rect.height) * canvas.height;

    const pad = 34;
    const boardW = canvas.width - pad * 2;
    const boardH = canvas.height - pad * 2;
    const cellSize = Math.min(boardW / gridCols, boardH / gridRows);
    const originX = (canvas.width - cellSize * gridCols) / 2;
    const originY = (canvas.height - cellSize * gridRows) / 2;

    const col = Math.floor((clickX - originX) / cellSize);
    const row = Math.floor((clickY - originY) / cellSize);
    if (col >= 0 && col < gridCols && row >= 0 && row < gridRows) {
      onCellTapped({ col, row });
    }
  };

  return (
    <canvas
      ref={canvasRef}
      width={800}
      height={800}
      onClick={handleCanvasClick}
      className="game-board-canvas w-full max-w-[min(94vw,780px)] aspect-square cursor-pointer select-none rounded-[2rem] shadow-2xl"
    />
  );
};
