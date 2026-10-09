/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

import React, { useState } from 'react';
import { RotateCcw, RotateCw, Sparkles, Trophy, ArrowRight, Play, Info } from 'lucide-react';
import { GAME_INFO } from '../data/gameData';

type ColorType = 'orange' | 'blue' | 'purple' | 'emerald';

interface TubeState {
  colors: ColorType[];
}

const COLOR_MAP: Record<ColorType, { bg: string; border: string; name: string }> = {
  orange: { bg: 'bg-orange-500', border: 'border-orange-400', name: 'Orange' },
  blue: { bg: 'bg-sky-500', border: 'border-sky-400', name: 'Blue' },
  purple: { bg: 'bg-purple-600', border: 'border-purple-400', name: 'Purple' },
  emerald: { bg: 'bg-emerald-500', border: 'border-emerald-400', name: 'Green' },
};

const INITIAL_TUBES: TubeState[] = [
  { colors: ['purple', 'orange', 'blue', 'orange'] },
  { colors: ['blue', 'purple', 'emerald', 'emerald'] },
  { colors: ['emerald', 'blue', 'purple', 'orange'] },
  { colors: ['emerald', 'purple', 'blue', 'orange'] },
  { colors: [] },
  { colors: [] },
];

const MAX_CAPACITY = 4;

export const InteractiveMiniDemo: React.FC = () => {
  const [tubes, setTubes] = useState<TubeState[]>(() => JSON.parse(JSON.stringify(INITIAL_TUBES)));
  const [selectedTubeIndex, setSelectedTubeIndex] = useState<number | null>(null);
  const [moveCount, setMoveCount] = useState<number>(0);
  const [history, setHistory] = useState<TubeState[][]>([]);
  const [message, setMessage] = useState<string>('Select a tube to lift its top liquid, then select another tube to pour.');

  // Check victory condition: each tube is either empty or contains 4 of the same color
  const isWon = tubes.every((tube) => {
    if (tube.colors.length === 0) return true;
    if (tube.colors.length !== MAX_CAPACITY) return false;
    const first = tube.colors[0];
    return tube.colors.every((c) => c === first);
  }) && tubes.filter(t => t.colors.length > 0).length >= 4;

  const handleTubeClick = (index: number) => {
    if (isWon) return;

    if (selectedTubeIndex === null) {
      // First click: select if tube has colors
      if (tubes[index].colors.length > 0) {
        setSelectedTubeIndex(index);
        const topColor = tubes[index].colors[tubes[index].colors.length - 1];
        setMessage(`Selected ${COLOR_MAP[topColor].name}. Now click a recipient tube.`);
      } else {
        setMessage('That tube is empty. Choose a tube with liquid first.');
      }
    } else if (selectedTubeIndex === index) {
      // Deselect clicked tube
      setSelectedTubeIndex(null);
      setMessage('Selection cancelled. Tap any tube to start.');
    } else {
      // Second click: attempt transfer from selectedTubeIndex to index
      const sourceTube = tubes[selectedTubeIndex];
      const targetTube = tubes[index];

      if (sourceTube.colors.length === 0) {
        setSelectedTubeIndex(null);
        return;
      }

      const topColor = sourceTube.colors[sourceTube.colors.length - 1];

      // Target must have space (< MAX_CAPACITY)
      // Target must be empty OR target top color matches source top color
      const targetTopColor = targetTube.colors.length > 0 ? targetTube.colors[targetTube.colors.length - 1] : null;

      if (targetTube.colors.length < MAX_CAPACITY && (targetTopColor === null || targetTopColor === topColor)) {
        // Valid move! Count how many consecutive matching colors to move
        let matchCount = 0;
        for (let i = sourceTube.colors.length - 1; i >= 0; i--) {
          if (sourceTube.colors[i] === topColor) {
            matchCount++;
          } else {
            break;
          }
        }

        const spaceAvailable = MAX_CAPACITY - targetTube.colors.length;
        const countToMove = Math.min(matchCount, spaceAvailable);

        if (countToMove > 0) {
          // Save history for undo
          setHistory((prev) => [...prev, JSON.parse(JSON.stringify(tubes))]);

          const newTubes = JSON.parse(JSON.stringify(tubes));
          const movedColors = newTubes[selectedTubeIndex].colors.splice(
            newTubes[selectedTubeIndex].colors.length - countToMove,
            countToMove
          );
          newTubes[index].colors.push(...movedColors);

          setTubes(newTubes);
          setSelectedTubeIndex(null);
          setMoveCount((prev) => prev + 1);
          setMessage(`Poured ${COLOR_MAP[topColor].name} successfully!`);
        } else {
          setSelectedTubeIndex(null);
          setMessage('No space in recipient tube.');
        }
      } else {
        // Can't pour into this tube: if clicked tube has liquid, switch selection to it
        if (targetTube.colors.length > 0) {
          setSelectedTubeIndex(index);
          const newTopColor = targetTube.colors[targetTube.colors.length - 1];
          setMessage(`Selected ${COLOR_MAP[newTopColor].name} tube instead.`);
        } else {
          setSelectedTubeIndex(null);
          setMessage("Colors don't match. Liquid only pours into matching colors or empty tubes.");
        }
      }
    }
  };

  const handleUndo = () => {
    if (history.length === 0) return;
    const lastState = history[history.length - 1];
    setTubes(lastState);
    setHistory((prev) => prev.slice(0, prev.length - 1));
    setSelectedTubeIndex(null);
    setMoveCount((prev) => Math.max(0, prev - 1));
    setMessage('Move undone.');
  };

  const handleReset = () => {
    setTubes(JSON.parse(JSON.stringify(INITIAL_TUBES)));
    setSelectedTubeIndex(null);
    setHistory([]);
    setMoveCount(0);
    setMessage('Puzzle reset. Make your first move!');
  };

  return (
    <section className="py-20 bg-white">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        
        <div className="bg-gradient-to-b from-slate-900 to-slate-950 rounded-3xl p-6 sm:p-10 lg:p-12 text-white shadow-2xl relative overflow-hidden border border-slate-800">
          
          {/* Subtle background glow */}
          <div className="absolute top-0 right-1/4 w-96 h-96 bg-purple-500/10 rounded-full blur-3xl pointer-events-none" />
          <div className="absolute bottom-0 left-1/4 w-96 h-96 bg-orange-500/10 rounded-full blur-3xl pointer-events-none" />

          {/* Header */}
          <div className="relative z-10 flex flex-col md:flex-row md:items-end justify-between gap-6 pb-8 border-b border-slate-800">
            <div>
              <div className="inline-flex items-center gap-2 text-xs font-bold uppercase tracking-widest text-orange-400 mb-2">
                <Play className="w-3.5 h-3.5 fill-orange-400" />
                <span>Interactive Web Demo</span>
              </div>
              <h2 className="text-2xl sm:text-3xl font-extrabold tracking-tight">
                Try a Mini Puzzle Right Here
              </h2>
              <p className="text-slate-400 text-sm mt-1 max-w-lg">
                Click a tube to pick up its top liquid, then click another tube to pour. Try organizing all matching colors!
              </p>
            </div>

            {/* Score & Controls Bar */}
            <div className="flex items-center gap-3 shrink-0">
              <div className="px-3.5 py-1.5 bg-slate-800/80 rounded-xl border border-slate-700/80 text-xs font-mono">
                <span className="text-slate-400">Moves: </span>
                <span className="font-bold text-white tabular-nums">{moveCount}</span>
              </div>
              <button
                type="button"
                onClick={handleUndo}
                disabled={history.length === 0}
                className="px-3 py-1.5 bg-slate-800 hover:bg-slate-700 disabled:opacity-40 disabled:hover:bg-slate-800 text-slate-200 rounded-xl text-xs font-semibold flex items-center gap-1.5 border border-slate-700 transition-colors"
                title="Undo last move"
              >
                <RotateCcw className="w-3.5 h-3.5" />
                <span>Undo</span>
              </button>
              <button
                type="button"
                onClick={handleReset}
                className="px-3 py-1.5 bg-slate-800 hover:bg-slate-700 text-slate-200 rounded-xl text-xs font-semibold flex items-center gap-1.5 border border-slate-700 transition-colors"
                title="Reset puzzle"
              >
                <RotateCw className="w-3.5 h-3.5" />
                <span>Restart</span>
              </button>
            </div>
          </div>

          {/* Interactive Play Arena */}
          <div className="py-10 relative z-10">
            
            {/* Status Prompt */}
            <div className="text-center mb-8">
              <span className={`inline-block px-4 py-1.5 rounded-full text-xs font-medium ${
                isWon
                  ? 'bg-emerald-500/20 text-emerald-300 border border-emerald-500/40'
                  : 'bg-slate-800 text-slate-300 border border-slate-700'
              }`}>
                {message}
              </span>
            </div>

            {/* Tubes row */}
            <div className="flex flex-wrap items-end justify-center gap-4 sm:gap-6 lg:gap-8 min-h-[220px]">
              {tubes.map((tube, tIndex) => {
                const isSelected = selectedTubeIndex === tIndex;
                const isComplete = tube.colors.length === MAX_CAPACITY && tube.colors.every((c) => c === tube.colors[0]);

                return (
                  <div
                    key={tIndex}
                    onClick={() => handleTubeClick(tIndex)}
                    className={`relative cursor-pointer flex flex-col items-center group transition-transform duration-200 select-none ${
                      isSelected ? '-translate-y-4' : 'hover:-translate-y-1'
                    }`}
                  >
                    {/* Selected Indicator Arrow */}
                    {isSelected && (
                      <div className="absolute -top-6 text-orange-400 font-bold text-xs animate-bounce">
                        ▼ Pour
                      </div>
                    )}

                    {/* Tube Glass Container */}
                    <div
                      className={`w-12 sm:w-14 h-40 sm:h-44 rounded-b-3xl border-2 p-1 flex flex-col-reverse relative overflow-hidden backdrop-blur-md transition-all ${
                        isSelected
                          ? 'border-orange-400 ring-4 ring-orange-400/30 bg-slate-800/80 shadow-lg shadow-orange-500/20'
                          : isComplete
                          ? 'border-emerald-400 ring-2 ring-emerald-400/20 bg-emerald-950/20'
                          : 'border-slate-500/70 hover:border-slate-300 bg-slate-800/40'
                      }`}
                    >
                      {/* Glass shine highlight */}
                      <div className="absolute top-0 right-1.5 w-1 h-full bg-white/15 rounded-full pointer-events-none" />

                      {/* Render Liquid segments */}
                      {tube.colors.map((color, cIndex) => {
                        const isTop = cIndex === tube.colors.length - 1;
                        const isBottom = cIndex === 0;

                        return (
                          <div
                            key={cIndex}
                            className={`w-full h-1/4 ${COLOR_MAP[color].bg} transition-all duration-300 relative border-t border-white/20 ${
                              isBottom ? 'rounded-b-2xl' : ''
                            } ${isTop ? 'rounded-t-sm' : ''}`}
                          >
                            {/* Liquid surface meniscus */}
                            {isTop && (
                              <div className="absolute inset-x-0 -top-1 h-1.5 bg-white/40 rounded-full blur-[0.5px]" />
                            )}
                          </div>
                        );
                      })}

                      {/* Empty slots watermark */}
                      {tube.colors.length === 0 && (
                        <div className="h-full flex items-center justify-center text-[10px] text-slate-500 font-bold uppercase tracking-wider">
                          Empty
                        </div>
                      )}
                    </div>

                    {/* Tube base label */}
                    <span className="text-[11px] font-mono text-slate-400 mt-2">
                      #{tIndex + 1}
                    </span>
                  </div>
                );
              })}
            </div>

            {/* Victory Overlay if Won */}
            {isWon && (
              <div className="mt-8 p-6 bg-gradient-to-r from-emerald-900/60 via-slate-900 to-emerald-900/60 rounded-2xl border border-emerald-500/50 text-center space-y-3 animate-fade-in shadow-xl">
                <div className="flex items-center justify-center gap-2 text-amber-400 text-lg font-bold">
                  <Trophy className="w-6 h-6" />
                  <span>Level Completed! Outstanding Sort!</span>
                  <Trophy className="w-6 h-6" />
                </div>
                <p className="text-slate-300 text-sm max-w-md mx-auto">
                  You solved the demo in {moveCount} moves. The full Android game features 100+ progressively challenging puzzles, daily rewards, and custom themes!
                </p>
                <div className="pt-2">
                  <a
                    href={GAME_INFO.playStoreUrl}
                    target="_blank"
                    rel="noopener noreferrer"
                    className="inline-flex items-center gap-2 px-6 py-3 bg-gradient-to-r from-orange-500 to-amber-500 hover:from-orange-600 hover:to-amber-600 text-white font-bold text-sm rounded-xl shadow-lg transition-transform hover:scale-105"
                  >
                    <span>Download Full Game on Google Play</span>
                    <ArrowRight className="w-4 h-4" />
                  </a>
                </div>
              </div>
            )}

          </div>

          {/* Bottom Tip Strip */}
          <div className="relative z-10 pt-4 flex flex-col sm:flex-row items-center justify-between gap-3 text-xs text-slate-400 border-t border-slate-800/80">
            <div className="flex items-center gap-2">
              <Info className="w-4 h-4 text-orange-400 shrink-0" />
              <span>Full game has undo assistance, hints, streak challenges, and tube customization.</span>
            </div>
            <a
              href={GAME_INFO.playStoreUrl}
              target="_blank"
              rel="noopener noreferrer"
              className="text-orange-400 hover:text-orange-300 font-semibold flex items-center gap-1"
            >
              <span>Get Android App</span>
              <ArrowRight className="w-3.5 h-3.5" />
            </a>
          </div>

        </div>

      </div>
    </section>
  );
};
